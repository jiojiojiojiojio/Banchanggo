package com.penelope.banchanggo.ui.chat.chatroom;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.penelope.banchanggo.data.chat.Chat;
import com.penelope.banchanggo.data.chat.ChatRepository;
import com.penelope.banchanggo.data.comment.Comment;
import com.penelope.banchanggo.data.comment.CommentRepository;
import com.penelope.banchanggo.data.comment.DetailedComment;
import com.penelope.banchanggo.data.contract.Contract;
import com.penelope.banchanggo.data.contract.ContractRepository;
import com.penelope.banchanggo.data.post.Post;
import com.penelope.banchanggo.data.post.PostRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatRoomViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final String chatId;
    private final String userId;
    private final String postId;
    private final String hostId;
    private final String guestId;

    private final LiveData<Post> post;
    private final LiveData<Chat> chat;
    private final LiveData<List<DetailedComment>> comments;

    private final LiveData<Contract> requested;
    private final LiveData<Boolean> isRequestableByUser;
    private final LiveData<Boolean> isCompletableByUser;
    private final LiveData<Contract> completed;

    private final ChatRepository chatRepository;
    private final CommentRepository commentsRepository;
    private final ContractRepository contractRepository;
    private final PostRepository postRepository;

    private String message;


    @Inject
    public ChatRoomViewModel(SavedStateHandle savedStateHandle,
                             PostRepository postRepository,
                             ChatRepository chatRepository,
                             CommentRepository commentRepository,
                             ContractRepository contractRepository) {

        chatId = savedStateHandle.get("chatId");
        postId = savedStateHandle.get("postId");
        hostId = savedStateHandle.get("hostId");
        guestId = savedStateHandle.get("guestId");
        userId = savedStateHandle.get("userId");
        assert userId != null;

        post = postRepository.getPost(postId);

        // 커멘트 획득

        chat = chatRepository.getChat(chatId);
        comments = Transformations.switchMap(chat, chatValue -> {
            if (chatValue == null) {
                return null;
            }
            return commentRepository.getDetailedComments(chatValue.getId());
        });

        requested = contractRepository.getContractRequestedBy(postId, guestId);
        completed = contractRepository.getContractCompleted(postId);
        isRequestableByUser = Transformations.switchMap(requested, requestedContract ->
                Transformations.map(completed, completedContract ->
                        (completedContract == null && requestedContract == null && userId.equals(guestId))
                )
        );
        isCompletableByUser = Transformations.switchMap(requested, requestedContract ->
                Transformations.map(completed, completedContract ->
                        (completedContract == null && requestedContract != null && userId.equals(hostId))
                )
        );

        this.chatRepository = chatRepository;
        this.commentsRepository = commentRepository;
        this.contractRepository = contractRepository;
        this.postRepository = postRepository;
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public String getUserId() {
        return userId;
    }

    public LiveData<List<DetailedComment>> getComments() {
        return comments;
    }

    public LiveData<Boolean> isRequestableByUser() {
        return isRequestableByUser;
    }

    public LiveData<Boolean> isCompletableByUser() {
        return isCompletableByUser;
    }

    public LiveData<Contract> getCompletedContract() {
        return completed;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        }
    }

    public void onMessageChange(String text) {
        this.message = text;
    }

    public void onSubmitClick() {

        if (message.isEmpty()) {
            return;
        }

        // 확인 클릭 시 채팅 추가

        Chat chatValue = chat.getValue();
        Comment comment = new Comment(chatId, userId, message);

        if (chatValue == null) {
            if (postId != null && hostId != null && guestId != null) {
                Chat chat = new Chat(postId, hostId, guestId);
                String receiverId = userId.equals(hostId) ? guestId : hostId;
                chatRepository.addChat(chat, unused -> commentsRepository.addComment(comment, receiverId));
            }
        } else {
            String hostId = chatValue.getHostId();
            String guestId = chatValue.getGuestId();
            String receiverId = userId.equals(hostId) ? guestId : hostId;
            commentsRepository.addComment(comment, receiverId);
        }
    }

    public void onRequestContractClick() {

        Boolean isValue = isRequestableByUser.getValue();
        Post postValue = post.getValue();
        if (isValue == null || !isValue || postValue == null) {
            return;
        }

        event.setValue(new Event.ShowRequestContractScreen(postValue.getPrice()));
    }

    public void onRequestContractConfirm(int price) {

        Contract contract = new Contract(postId, hostId, guestId, price, false);
        contractRepository.addContract(contract, unused ->
                event.setValue(new Event.ShowGeneralMessage("구매 요청이 완료되었습니다"))
        );
    }

    public void onCompleteContractClick() {

        Boolean isValue = isCompletableByUser.getValue();
        Contract contract = requested.getValue();
        if (isValue == null || !isValue || contract == null) {
            return;
        }

        event.setValue(new Event.ShowCompleteContractScreen(contract.getPrice()));
    }

    public void onCompleteContractConfirm() {

        Contract contract = requested.getValue();
        if (contract == null) {
            return;
        }

        contractRepository.completeContract(contract.getId(), unused ->
                postRepository.deletePost(postId, unused1 ->
                        event.setValue(new Event.ShowGeneralMessage("거래가 완료되었습니다"))
                )
        );
    }


    public static class Event {

        public static class NavigateBack extends Event {
        }

        public static class ShowGeneralMessage extends Event {
            public final String message;

            public ShowGeneralMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowRequestContractScreen extends Event {
            public final int price;

            public ShowRequestContractScreen(int price) {
                this.price = price;
            }
        }

        public static class ShowCompleteContractScreen extends Event {
            public final int price;

            public ShowCompleteContractScreen(int price) {
                this.price = price;
            }
        }

    }

}



