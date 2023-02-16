package com.penelope.banchanggo.ui.chat.chatroom;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.penelope.banchanggo.R;
import com.penelope.banchanggo.databinding.DialogRequestContractBinding;
import com.penelope.banchanggo.databinding.DialogRequestContractNegoBinding;
import com.penelope.banchanggo.databinding.FragmentChatRoomBinding;
import com.penelope.banchanggo.utils.OnTextChangedListener;
import com.penelope.banchanggo.utils.ui.AuthFragment;

import java.text.NumberFormat;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChatRoomFragment extends AuthFragment {

    private FragmentChatRoomBinding binding;
    private ChatRoomViewModel viewModel;


    public ChatRoomFragment() {
        super(R.layout.fragment_chat_room);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentChatRoomBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        binding.editTextMessage.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onMessageChange(text);
            }
        });

        binding.buttonSubmit.setOnClickListener(v -> {
            viewModel.onSubmitClick();
            binding.editTextMessage.setText("");
            hideKeyboard(requireView());
        });

        binding.buttonRequestContract.setOnClickListener(v -> viewModel.onRequestContractClick());
        binding.buttonCompleteContract.setOnClickListener(v -> viewModel.onCompleteContractClick());

        CommentsAdapter adapter = new CommentsAdapter(viewModel.getUserId());
        binding.recyclerComment.setAdapter(adapter);
        binding.recyclerComment.setHasFixedSize(true);

        viewModel.getComments().observe(getViewLifecycleOwner(), comments -> {
            adapter.submitList(comments);
            binding.recyclerComment.postDelayed(() ->
                    binding.recyclerComment.smoothScrollToPosition(adapter.getItemCount() - 1),
                    500);
        });

        viewModel.isRequestableByUser().observe(getViewLifecycleOwner(), isRequestable ->
                binding.buttonRequestContract.setVisibility(isRequestable ? View.VISIBLE : View.GONE));

        viewModel.isCompletableByUser().observe(getViewLifecycleOwner(), isCompletable ->
                binding.buttonCompleteContract.setVisibility(isCompletable ? View.VISIBLE : View.GONE));

        viewModel.getCompletedContract().observe(getViewLifecycleOwner(), completed ->
                binding.textViewContractCompleted.setVisibility(completed != null ? View.VISIBLE : View.GONE));

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof ChatRoomViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof ChatRoomViewModel.Event.ShowRequestContractScreen) {
                int price = ((ChatRoomViewModel.Event.ShowRequestContractScreen) event).price;
                if (price != -1) {
                    showRequestContractDialog(price);
                } else {
                    showRequestContractNegoDialog();
                }
            } else if (event instanceof ChatRoomViewModel.Event.ShowCompleteContractScreen) {
                int price = ((ChatRoomViewModel.Event.ShowCompleteContractScreen) event).price;
                showCompleteContractDialog(price);
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

    private void showRequestContractDialog(int price) {

        DialogRequestContractBinding dialogBinding = DialogRequestContractBinding.inflate(getLayoutInflater());

        String strPrice = NumberFormat.getInstance().format(price) + "원에 구매를 요청하시겠습니까?";
        dialogBinding.textViewPrice.setText(strPrice);

        new AlertDialog.Builder(requireContext())
                .setTitle("구매 요청")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("요청", (dialogInterface, i) -> viewModel.onRequestContractConfirm(price))
                .setNegativeButton("취소", null)
                .show();
    }

    private void showRequestContractNegoDialog() {

        DialogRequestContractNegoBinding dialogBinding = DialogRequestContractNegoBinding.inflate(getLayoutInflater());

        new AlertDialog.Builder(requireContext())
                .setTitle("구매 요청")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("요청", (dialogInterface, i) -> {
                    String strPrice = dialogBinding.editTextPrice.getText().toString().trim();
                    if (strPrice.isEmpty()) {
                        Snackbar.make(requireView(), "가격을 입력해주세요", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    int price = Integer.parseInt(strPrice);
                    viewModel.onRequestContractConfirm(price);
                })
                .setNegativeButton("취소", null)
                .show();
    }

    public void showCompleteContractDialog(int price) {

        DialogRequestContractBinding dialogBinding = DialogRequestContractBinding.inflate(getLayoutInflater());

        String strPrice = NumberFormat.getInstance().format(price) + "원에 거래를 완료하시겠습니까?";
        dialogBinding.textViewPrice.setText(strPrice);

        new AlertDialog.Builder(requireContext())
                .setTitle("거래 완료")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("완료", (dialogInterface, i) -> viewModel.onCompleteContractConfirm())
                .setNegativeButton("취소", null)
                .show();
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}