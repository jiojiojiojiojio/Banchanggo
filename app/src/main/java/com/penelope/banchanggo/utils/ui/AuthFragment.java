package com.penelope.banchanggo.utils.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public abstract class AuthFragment extends Fragment implements FirebaseAuth.AuthStateListener {

    protected Context context;
    private final FirebaseAuth auth;

    public AuthFragment(int layout) {
        super(layout);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        auth.addAuthStateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        auth.removeAuthStateListener(this);
    }
}