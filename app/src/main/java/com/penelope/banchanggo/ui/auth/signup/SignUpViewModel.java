package com.penelope.banchanggo.ui.auth.signup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.penelope.banchanggo.data.user.User;
import com.penelope.banchanggo.data.user.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SignUpViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private String email = "";
    private String password = "";
    private String passwordConfirm = "";
    private String name = "";
    private String phone = "";

    private final FirebaseAuth auth;
    private final UserRepository userRepository;


    @Inject
    public SignUpViewModel(UserRepository userRepository) {
        auth = FirebaseAuth.getInstance();
        this.userRepository = userRepository;
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() != null) {
            event.setValue(new Event.NavigateToHomeScreen());
        }
    }

    public void onEmailChanged(String value) {
        // 이메일 입력값이 변경되었을 때
        email = value;
    }

    public void onPasswordChanged(String value) {
        // 비밀번호 입력값이 변경되었을 때
        password = value;
    }

    public void onPasswordConfirmChanged(String value) {
        // 비밀번호 확인값이 변경되었을 때
        passwordConfirm = value;
    }

    public void onNameChanged(String value) {
        name = value;
    }

    public void onPhoneChanged(String value) {
        phone = value;
    }


    public void onSignUpClicked() {

        // 회원가입 요청했을 때 : 회원가입 시도하기
        if (email.length() < 4) {
            // 에러 : 짧은 아이디
            event.setValue(new Event.ShowShortUserIdMessage("아이디를 4글자 이상 입력해주세요"));
            return;
        }
        if (password.length() < 6) {
            // 에러 : 짧은 패스워드
            event.setValue(new Event.ShowShortPasswordMessage("비밀번호를 6글자 이상 입력해주세요"));
            return;
        }
        if (!passwordConfirm.equals(password)) {
            // 에러 : 비밀번호 확인 불일치
            event.setValue(new Event.ShowIncorrectPasswordConfirmMessage("비밀번호를 정확하게 입력해주세요"));
            return;
        }
        if (name.length() < 2) {
            return;
        }
        if (email.length() < 5) {
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(this::createUserData)
                .addOnFailureListener(e -> event.postValue(
                        new Event.ShowSignUpFailureMessage("이미 존재하는 아이디입니다"))
                );
    }

    private void createUserData(AuthResult authResult) {

        if (authResult.getUser() == null) {
            return;
        }

        String uid = authResult.getUser().getUid();
        User user = new User(uid, email, password, name, phone);
        userRepository.addUser(user, unused -> auth.signInWithEmailAndPassword(email, password), e -> {});
    }


    // 프래그먼트에 전송될 이벤트

    public static class Event {

        public static class ShowShortUserIdMessage extends Event {
            public final String message;
            public ShowShortUserIdMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowShortPasswordMessage extends Event {
            public final String message;
            public ShowShortPasswordMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowIncorrectPasswordConfirmMessage extends Event {
            public final String message;
            public ShowIncorrectPasswordConfirmMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowSignUpFailureMessage extends Event {
            public final String message;
            public ShowSignUpFailureMessage(String message) {
                this.message = message;
            }
        }

        public static class NavigateToHomeScreen extends Event {}
    }

}