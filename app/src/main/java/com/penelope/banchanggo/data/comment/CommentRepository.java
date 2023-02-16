package com.penelope.banchanggo.data.comment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.penelope.banchanggo.data.chat.Chat;
import com.penelope.banchanggo.data.chat.ChatRepository;
import com.penelope.banchanggo.data.user.User;
import com.penelope.banchanggo.data.user.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class CommentRepository {

    private final CollectionReference chatsCollection;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Inject
    public CommentRepository(FirebaseFirestore firestore,
                             UserRepository userRepository,
                             ChatRepository chatRepository) {
        chatsCollection = firestore.collection("chats");
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    public LiveData<List<Comment>> getComments(String chatId) {

        CollectionReference commentsCollection = chatsCollection.document(chatId).collection("comments");

        MutableLiveData<List<Comment>> comments = new MutableLiveData<>();

        // chatId 로 검색된 커멘트 리스트 획득

        commentsCollection
                .orderBy("created", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value == null || error != null) {
                        comments.setValue(null);
                        return;
                    }

                    List<Comment> commentList = new ArrayList<>();
                    for (DocumentSnapshot snapshot : value) {
                        Comment comment = snapshot.toObject(Comment.class);
                        if (comment != null) {
                            commentList.add(comment);
                        }
                    }

                    comments.setValue(commentList);
                });

        return comments;
    }

    public void addComment(Comment comment, String receiverId) {

        String chatId = comment.getChatId();
        CollectionReference commentsCollection = chatsCollection.document(chatId).collection("comments");
        commentsCollection.document(comment.getId()).set(comment);
    }


    public LiveData<List<DetailedComment>> getDetailedComments(String chatId) {

        LiveData<List<Comment>> comments = getComments(chatId);
        LiveData<Map<String, User>> userMap = userRepository.getUserMap();

        return Transformations.switchMap(comments, commentList ->
                Transformations.map(userMap, map -> {
                    if (commentList == null || map == null) {
                        return null;
                    }
                    List<DetailedComment> detailedCommentList = new ArrayList<>();
                    for (Comment comment : commentList) {
                        User user = map.get(comment.getUserId());
                        detailedCommentList.add(new DetailedComment(comment, user));
                    }
                    return detailedCommentList;
                })
        );
    }


    public LiveData<Map<String, Comment>> getLastComments(String userId) {

        MutableLiveData<Map<String, Comment>> lastComments = new MutableLiveData<>();
        LiveData<List<Chat>> chats = chatRepository.getChats(userId);

        chats.observeForever(chatList -> {
            if (chatList == null) {
                lastComments.setValue(null);
                return;
            }

            Map<String, Comment> lastCommentMap = new HashMap<>();

            for (Chat chat : chatList) {
                LiveData<List<Comment>> comments = getComments(chat.getId());
                comments.observeForever(commentList -> {
                    if (commentList != null && !commentList.isEmpty()) {
                        Comment lastComment = commentList.get(commentList.size() - 1);
                        lastCommentMap.put(chat.getId(), lastComment);
                    }
                    if (chat == chatList.get(chatList.size() - 1)) {
                        lastComments.setValue(lastCommentMap);
                    }
                });
            }
        });

        return lastComments;
    }

}
