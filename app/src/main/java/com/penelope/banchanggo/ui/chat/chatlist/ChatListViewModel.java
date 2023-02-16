package com.penelope.banchanggo.ui.chat.chatlist;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.penelope.banchanggo.data.chat.Chat;
import com.penelope.banchanggo.data.chat.DetailedChat;
import com.penelope.banchanggo.data.chat.DetailedChatRepository;
import com.penelope.banchanggo.data.image.ImageRepository;
import com.penelope.banchanggo.data.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatListViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<String> uid = new MutableLiveData<>();

    private final LiveData<List<DetailedChat>> chats;

    private final LiveData<Map<String, Bitmap>> album;


    @Inject
    public ChatListViewModel(DetailedChatRepository chatRepository,
                             ImageRepository imageRepository) {

        chats = Transformations.switchMap(uid, chatRepository::getDetailedChats);

        // 프로필 이미지 앨범 획득

        album = Transformations.switchMap(uid, uidValue ->
                Transformations.switchMap(chats, chatList -> {
                    List<String> idList = new ArrayList<>();
                    for (Chat chat : chatList) {
                        String partnerId = uidValue.equals(chat.getHostId()) ? chat.getGuestId() : chat.getHostId();
                        idList.add(partnerId);
                    }
                    return imageRepository.getAlbum(idList);
                })
        );
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<DetailedChat>> getChats() {
        return chats;
    }

    public LiveData<String> getUid() {
        return uid;
    }

    public LiveData<Map<String, Bitmap>> getAlbum() {
        return album;
    }


    public void onChatClick(Chat chat) {
        String uidValue = uid.getValue();
        if (uidValue == null) {
            return;
        }
        event.setValue(new Event.NavigateToChatScreen(
                chat.getId(), chat.getPostId(), chat.getHostId(), chat.getGuestId(), uidValue
        ));
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        } else {
            uid.setValue(firebaseAuth.getUid());
        }
    }


    public static class Event {

        public static class NavigateBack extends Event {
        }

        public static class NavigateToChatScreen extends Event {
            public final String chatId;
            public final String postId;
            public final String hostId;
            public final String guestId;
            public final String uid;

            public NavigateToChatScreen(String chatId, String postId, String hostId, String guestId, String uid) {
                this.chatId = chatId;
                this.postId = postId;
                this.hostId = hostId;
                this.guestId = guestId;
                this.uid = uid;
            }
        }
    }

}