package com.penelope.banchanggo.ui.auth.signin;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SignInViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<String> email = new MutableLiveData<>("");       // 유저 아이디 값
    private final MutableLiveData<String> password = new MutableLiveData<>("");     // 비밀번호 값

    private final FirebaseAuth auth;


    @Inject
    public SignInViewModel() {
        auth = FirebaseAuth.getInstance();
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getPassword() {
        return password;
    }


    public void onEmailChanged(String value) {
        // 이메일 입력값이 변경되었을 때
        email.setValue(value);
    }

    public void onPasswordChanged(String value) {
        // 비밀번호 입력값이 변경되었을 때
        password.setValue(value);
    }


    public void onSignInClicked() {

        // 로그인을 요청했을 때 : 로그인 시도하기
        String emailValue = email.getValue();
        String passwordValue = password.getValue();

        assert emailValue != null && passwordValue != null;

        if (emailValue.length() < 4) {
            // 에러 : 짧은 아이디
            event.setValue(new Event.ShowShortUserIdMessage("아이디를 4글자 이상 입력해주세요"));
            return;
        }
        if (passwordValue.length() < 6) {
            // 에러 : 짧은 패스워드
            event.setValue(new Event.ShowShortPasswordMessage("비밀번호를 6글자 이상 입력해주세요"));
            return;
        }

        auth.signInWithEmailAndPassword(emailValue, passwordValue)
                .addOnFailureListener(
                        e -> event.setValue(new Event.ShowSignInFailureMessage("회원정보를 확인해주세요"))
                );
    }

    public void onSignUpClicked() {
        event.setValue(new Event.NavigateToSignUpScreen());
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() != null) {
            event.setValue(new Event.NavigateToHomeScreen());
        }
    }

    // 프래그먼트에 전송될 이벤트

    static class Event {

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

        public static class ShowSignInFailureMessage extends Event {
            public final String message;
            public ShowSignInFailureMessage(String message) {
                this.message = message;
            }
        }

        public static class NavigateToSignUpScreen extends Event {}

        public static class NavigateToHomeScreen extends Event {}
    }

}