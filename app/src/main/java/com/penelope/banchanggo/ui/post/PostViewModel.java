package com.penelope.banchanggo.ui.post;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.penelope.banchanggo.data.image.ImageRepository;
import com.penelope.banchanggo.data.post.Post;
import com.penelope.banchanggo.data.post.PostRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PostViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<String> uid = new MutableLiveData<>();

    private final Post post;
    private final LiveData<Bitmap> image;

    private final MutableLiveData<List<String>> likes = new MutableLiveData<>();
    private final LiveData<Boolean> hasLiked;

    private final LiveData<Boolean> isChatable;

    private final PostRepository postRepository;


    @Inject
    public PostViewModel(SavedStateHandle savedStateHandle, PostRepository postRepository, ImageRepository imageRepository) {

        post = savedStateHandle.get("post");
        assert post != null;

        image = imageRepository.getPostImageLiveData(post.getId());

        likes.setValue(post.getLikes());
        hasLiked = Transformations.switchMap(uid, uidValue ->
                Transformations.map(likes, likeList -> likeList.contains(uidValue))
        );

        isChatable = Transformations.map(uid, uidValue -> !uidValue.equals(post.getWriterId()));

        this.postRepository = postRepository;
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public String getPostTitle() {
        return post.getTitle();
    }

    public String getPostContent() {
        return post.getContent();
    }

    public int getPostPrice() {
        return post.getPrice();
    }

    public List<String> getPostCategories() {
        return post.getCategories();
    }

    public long getPostCreated() {
        return post.getCreated();
    }

    public LiveData<Bitmap> getPostImage() {
        return image;
    }

    public LiveData<Boolean> hasLiked() {
        return hasLiked;
    }

    public LiveData<Boolean> isChatable() {
        return isChatable;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        } else {
            uid.setValue(firebaseAuth.getUid());
        }
    }

    public void onLikeClick() {
        if (uid.getValue() == null) {
            return;
        }
        postRepository.likeOrUnlikePost(post.getId(), uid.getValue(), likes::setValue);
    }

    public void onChatClick() {

        String uidValue = uid.getValue();
        Boolean isChatableValue = isChatable.getValue();
        if (isChatableValue == null || !isChatableValue || uidValue == null) {
            return;
        }

        String postId = post.getId();
        String hostId = post.getWriterId();
        String guestId = uidValue;
        String chatId = postId + "#" + hostId + "#" + guestId;
        event.setValue(new Event.NavigateToChatScreen(chatId, postId, hostId, guestId, uidValue));
    }


    public static class Event {

        public static class NavigateBack extends Event {
        }

        public static class NavigateToChatScreen extends Event {
            public final String chatId;
            public final String postId;
            public final String hostId;
            public final String guestId;
            public final String userId;
            public NavigateToChatScreen(String chatId, String postId, String hostId, String guestId, String userId) {
                this.chatId = chatId;
                this.postId = postId;
                this.hostId = hostId;
                this.guestId = guestId;
                this.userId = userId;
            }
        }
    }

}