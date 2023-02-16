package com.penelope.banchanggo.data.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ChatRepository {

    private final CollectionReference chatsCollection;

    @Inject
    public ChatRepository(FirebaseFirestore firestore) {
        this.chatsCollection = firestore.collection("chats");
    }

    public LiveData<List<Chat>> getChats(String userId) {

        MutableLiveData<List<Chat>> chats = new MutableLiveData<>();

        // userId 로 검색된 채팅 리스트 획득

        chatsCollection.addSnapshotListener((value, error) -> {
            if (value == null || error != null) {
                chats.setValue(null);
                return;
            }

            List<Chat> chatList = new ArrayList<>();
            for (DocumentSnapshot snapshot : value) {
                Chat chat = snapshot.toObject(Chat.class);
                if (chat == null) {
                    continue;
                }
                if (chat.getHostId().equals(userId) || chat.getGuestId().equals(userId)) {
                    chatList.add(chat);
                }
            }

            chats.setValue(chatList);
        });

        return chats;
    }

    public LiveData<Chat> getChat(String id) {

        MutableLiveData<Chat> chat = new MutableLiveData<>();

        chatsCollection.whereEqualTo("id", id)
                .addSnapshotListener((value, error) -> {
                    if (value == null || error != null || value.isEmpty()) {
                        chat.setValue(null);
                        return;
                    }

                    DocumentSnapshot snapshot = value.getDocuments().get(0);
                    chat.setValue(snapshot.toObject(Chat.class));
                });

        return chat;
    }

    public void addChat(Chat chat, OnSuccessListener<Void> onSuccessListener) {

        chatsCollection.document(chat.getId()).set(chat)
                .addOnSuccessListener(onSuccessListener);
    }

}


