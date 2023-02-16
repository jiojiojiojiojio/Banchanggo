package com.penelope.banchanggo.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final FirebaseAuth auth;


    @Inject
    public HomeViewModel() {
        auth = FirebaseAuth.getInstance();
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        }
    }

    public void onBackClick() {
        event.setValue(new Event.ConfirmSignOut("로그아웃 하시겠습니까?"));
    }

    public void onSignOutConfirmed() {
        auth.signOut();
    }


    public static class Event {

        public static class NavigateBack extends Event {
        }

        public static class ConfirmSignOut extends Event {
            public final String message;
            public ConfirmSignOut(String message) {
                this.message = message;
            }
        }
    }

}