package com.penelope.banchanggo.ui.mypage.mypage;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.penelope.banchanggo.data.contract.Contract;
import com.penelope.banchanggo.data.contract.ContractRepository;
import com.penelope.banchanggo.data.image.ImageRepository;
import com.penelope.banchanggo.data.notice.Notice;
import com.penelope.banchanggo.data.notice.NoticeRepository;
import com.penelope.banchanggo.data.post.Post;
import com.penelope.banchanggo.data.post.PostRepository;
import com.penelope.banchanggo.data.user.User;
import com.penelope.banchanggo.data.user.UserRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MyPageViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private String uid;
    private FirebaseUser firebaseUser;

    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final LiveData<Boolean> isAdmin;

    private final MutableLiveData<Bitmap> bitmap = new MutableLiveData<>();

    private final LiveData<List<Notice>> notices;

    private final LiveData<Boolean> isSelling;
    private final LiveData<Integer> countSold;
    private final LiveData<Integer> countBought;

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final FirebaseAuth auth;


    @Inject
    public MyPageViewModel(UserRepository userRepository,
                           ImageRepository imageRepository,
                           NoticeRepository noticeRepository,
                           PostRepository postRepository,
                           ContractRepository contractRepository) {

        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        auth = FirebaseAuth.getInstance();

        notices = noticeRepository.getNotices();

        LiveData<List<Post>> posts = Transformations.switchMap(user, userValue ->
                postRepository.getPostsWrittenBy(userValue.getUid())
        );
        isSelling = Transformations.map(posts, postList -> postList != null && !postList.isEmpty());

        LiveData<List<Contract>> contractsSold = Transformations.switchMap(user, userValue ->
                contractRepository.getContractsSold(userValue.getUid())
        );
        countSold = Transformations.map(contractsSold, list -> list == null ? 0 : list.size());

        LiveData<List<Contract>> contractBought = Transformations.switchMap(user, userValue ->
                contractRepository.getContractBought(userValue.getUid())
        );
        countBought = Transformations.map(contractBought, list -> list == null ? 0 : list.size());

        isAdmin = Transformations.map(user, userValue -> userValue.getEmail().equals("admin@admin.com"));
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<Bitmap> getBitmap() {
        return bitmap;
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<List<Notice>> getNotices() {
        return notices;
    }

    public LiveData<Boolean> isAdmin() {
        return isAdmin;
    }

    public LiveData<Boolean> isSelling() {
        return isSelling;
    }

    public LiveData<Integer> getCountSold() {
        return countSold;
    }

    public LiveData<Integer> getCountBought() {
        return countBought;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        } else {
            firebaseUser = firebaseAuth.getCurrentUser();
            uid = firebaseAuth.getCurrentUser().getUid();
            userRepository.getUser(uid, user::setValue, e -> user.setValue(null));
            imageRepository.getProfileImage(uid,
                    bitmap::setValue,
                    e -> {
                        e.printStackTrace();
                        event.setValue(new Event.ShowGeneralMessage("이미지를 불러오지 못했습니다"));
                    }
            );
        }
    }

    public void onEditProfileClick() {
        event.setValue(new Event.PromptImage());
    }

    public void onImageSelected(Bitmap image) {
        if (image != null) {
            saveProfileImage(image);
        } else {
            event.setValue(new Event.ShowGeneralMessage("이미지 선택에 실패했습니다"));
        }
    }

    public void onSignOutClick() {
        event.setValue(new Event.ConfirmSignOut("로그아웃 하시겠습니까?"));
    }

    public void onSignOutConfirmed() {
        auth.signOut();
    }

    public void onUnregisterClick() {
        if (firebaseUser != null) {
            event.setValue(new Event.ConfirmUnregister("회원 탈퇴 하시겠습니까?"));
        }
    }

    public void onUnregisterConfirmed() {

        User userValue = user.getValue();
        if (firebaseUser == null || userValue == null) {
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(
                userValue.getEmail(), userValue.getPassword());

        userRepository.deleteUser(uid, unused ->
                firebaseUser.reauthenticate(credential)
                        .addOnCompleteListener(task -> firebaseUser.delete())
        );
    }

    public void onWriteNoticeClick() {

        Boolean isAdminValue = isAdmin.getValue();
        if (isAdminValue == null || !isAdminValue) {
            return;
        }

        event.setValue(new Event.NavigateToAddNoticeScreen());
    }

    public void onAddNoticeResult(boolean result) {
        if (result) {
            event.setValue(new Event.ShowGeneralMessage("공지사항이 작성되었습니다"));
        }
    }

    public void onNoticeClick(Notice notice) {
        event.setValue(new Event.ShowNoticeScreen(notice));
    }


    private void saveProfileImage(Bitmap image) {

        if (uid == null) {
            return;
        }

        imageRepository.setProfileImage(uid, image,
                unused -> bitmap.setValue(image),
                e -> event.setValue(new Event.ShowGeneralMessage("이미지 업로드에 실패했습니다"))
        );
    }


    public static class Event {
        public static class NavigateBack extends Event {
        }

        public static class PromptImage extends Event {
        }

        public static class ShowGeneralMessage extends Event {
            public final String message;

            public ShowGeneralMessage(String message) {
                this.message = message;
            }
        }

        public static class ConfirmSignOut extends Event {
            public final String message;

            public ConfirmSignOut(String message) {
                this.message = message;
            }
        }

        public static class ConfirmUnregister extends Event {
            public final String message;

            public ConfirmUnregister(String message) {
                this.message = message;
            }
        }

        public static class NavigateToAddNoticeScreen extends Event {

        }

        public static class ShowNoticeScreen extends Event {
            public final Notice notice;

            public ShowNoticeScreen(Notice notice) {
                this.notice = notice;
            }
        }
    }

}