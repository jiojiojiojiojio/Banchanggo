package com.penelope.banchanggo.ui.auth.signup;

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
import com.penelope.banchanggo.databinding.FragmentSignUpBinding;
import com.penelope.banchanggo.utils.ui.AuthFragment;
import com.penelope.banchanggo.utils.OnTextChangedListener;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignUpFragment extends AuthFragment {

    private FragmentSignUpBinding binding;
    private SignUpViewModel viewModel;


    public SignUpFragment() {
        super(R.layout.fragment_sign_up);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentSignUpBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

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
        binding.editTextPasswordConfirm.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onPasswordConfirmChanged(text);
            }
        });
        binding.editTextName.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onNameChanged(text);
            }
        });
        binding.editTextPhone.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onPhoneChanged(text);
            }
        });
        binding.buttonSignUp.setOnClickListener(v -> viewModel.onSignUpClicked());

        // 뷰모델에서 전송한 이벤트 처리

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof SignUpViewModel.Event.ShowShortUserIdMessage) {
                hideKeyboard(requireView());
                Snackbar.make(requireView(),
                        ((SignUpViewModel.Event.ShowShortUserIdMessage)event).message,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else if (event instanceof SignUpViewModel.Event.ShowShortPasswordMessage) {
                hideKeyboard(requireView());
                Snackbar.make(requireView(),
                        ((SignUpViewModel.Event.ShowShortPasswordMessage)event).message,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else if (event instanceof SignUpViewModel.Event.ShowIncorrectPasswordConfirmMessage) {
                hideKeyboard(requireView());
                Snackbar.make(requireView(),
                        ((SignUpViewModel.Event.ShowIncorrectPasswordConfirmMessage)event).message,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else if (event instanceof SignUpViewModel.Event.ShowSignUpFailureMessage) {
                hideKeyboard(requireView());
                Snackbar.make(requireView(),
                        ((SignUpViewModel.Event.ShowSignUpFailureMessage)event).message,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else if (event instanceof SignUpViewModel.Event.NavigateToHomeScreen) {
                NavDirections action = SignUpFragmentDirections.actionSignUpFragmentToHomeFragment();
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