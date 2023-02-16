package com.penelope.banchanggo.ui.mypage.addnotice;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.penelope.banchanggo.R;
import com.penelope.banchanggo.databinding.FragmentAddNoticeBinding;
import com.penelope.banchanggo.utils.OnTextChangedListener;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddNoticeFragment extends Fragment {

    private FragmentAddNoticeBinding binding;
    private AddNoticeViewModel viewModel;


    public AddNoticeFragment() {
        super(R.layout.fragment_add_notice);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentAddNoticeBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(AddNoticeViewModel.class);

        binding.editTextNoticeTitle.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onTitleChange(text);
            }
        });

        binding.editTextNoticeContent.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onContentChange(text);
            }
        });

        binding.buttonSubmit.setOnClickListener(v -> viewModel.onSubmitClick());

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof AddNoticeViewModel.Event.NavigateBackWithResult) {
                Bundle result = new Bundle();
                result.putBoolean("result", true);
                getParentFragmentManager().setFragmentResult("add_notice_fragment", result);
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof AddNoticeViewModel.Event.ShowGeneralMessage) {
                String message = ((AddNoticeViewModel.Event.ShowGeneralMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}