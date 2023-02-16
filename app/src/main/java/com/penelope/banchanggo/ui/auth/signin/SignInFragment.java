package com.penelope.banchanggo.ui.auth.signin;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.penelope.banchanggo.R;
import com.penelope.banchanggo.databinding.FragmentSignInBinding;
import com.penelope.banchanggo.utils.ui.AuthFragment;
import com.penelope.banchanggo.utils.OnTextChangedListener;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignInFragment extends AuthFragment {

    private FragmentSignInBinding binding;
    private SignInViewModel viewModel;


    public SignInFragment() {
        super(R.layout.fragment_sign_in);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentSignInBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(SignInViewModel.class);

        // UI 에 리스너 부여

        binding.editTextEmail.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onEmailChanged(text);
            }
        });
        binding.editTextPassword.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onPasswordChanged(text);
            }
        });
        binding.buttonSignIn.setOnClickListener(v -> viewModel.onSignInClicked());
        binding.buttonSignUp.setOnClickListener(v -> viewModel.onSignUpClicked());

        // 뷰모델에서 전송한 이벤트 처리

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof SignInViewModel.Event.ShowShortUserIdMessage) {
                hideKeyboard(requireView());
                Snackbar.make(requireView(),
                        ((SignInViewModel.Event.ShowShortUserIdMessage)event).message,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else if (event instanceof SignInViewModel.Event.ShowShortPasswordMessage) {
                hideKeyboard(requireView());
                Snackbar.make(requireView(),
                        ((SignInViewModel.Event.ShowShortPasswordMessage)event).message,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else if (event instanceof SignInViewModel.Event.ShowSignInFailureMessage) {
                hideKeyboard(requireView());
                Snackbar.make(requireView(),
                        ((SignInViewModel.Event.ShowSignInFailureMessage)event).message,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else if (event instanceof SignInViewModel.Event.NavigateToSignUpScreen) {
                NavDirections action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment();
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof SignInViewModel.Event.NavigateToHomeScreen) {
                NavDirections action = SignInFragmentDirections.actionSignInFragmentToHomeFragment();
                Navigation.findNavController(requireView()).navigate(action);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        viewModel.onAuthStateChanged(firebaseAuth);
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}