package com.penelope.banchanggo.ui.post;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.penelope.banchanggo.R;
import com.penelope.banchanggo.databinding.FragmentPostBinding;
import com.penelope.banchanggo.utils.TimeUtils;
import com.penelope.banchanggo.utils.ui.AuthFragment;
import com.penelope.banchanggo.utils.ui.CardsAdapter;

import java.text.NumberFormat;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PostFragment extends AuthFragment {

    private FragmentPostBinding binding;
    private PostViewModel viewModel;


    public PostFragment() {
        super(R.layout.fragment_post);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentPostBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(PostViewModel.class);

        binding.textViewPostTitle.setText(viewModel.getPostTitle());
        binding.textViewPostContent.setText(viewModel.getPostContent());

        String strPrice;
        if (viewModel.getPostPrice() == -1) {
            strPrice = "협의 가능";
        } else {
            strPrice = NumberFormat.getInstance()
                    .format(viewModel.getPostPrice()) + " 원";
        }
        binding.textViewPostPrice.setText(strPrice);

        binding.textViewPostCreated.setText(
                TimeUtils.getDateString(viewModel.getPostCreated())
        );

        CardsAdapter adapter = new CardsAdapter();
        binding.recyclerCategory.setAdapter(adapter);
        binding.recyclerCategory.setHasFixedSize(true);
        adapter.submitList(viewModel.getPostCategories());

        binding.imageViewLike.setOnClickListener(v -> viewModel.onLikeClick());
        binding.textViewChat.setOnClickListener(v -> viewModel.onChatClick());

        viewModel.getPostImage().observe(getViewLifecycleOwner(), image -> {
            if (image != null) {
                binding.imageViewPost.setImageBitmap(image);
            }
        });

        viewModel.hasLiked().observe(getViewLifecycleOwner(), hasLiked ->
                binding.imageViewLike.setColorFilter(getResources().getColor(
                        hasLiked ? R.color.purple_500 : android.R.color.darker_gray, null))
        );

        viewModel.isChatable().observe(getViewLifecycleOwner(), isChatable ->
                binding.textViewChat.setBackgroundResource(isChatable ? R.drawable.blue_btn : R.drawable.disabled_btn)
        );

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof PostViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof PostViewModel.Event.NavigateToChatScreen) {
                String chatId = ((PostViewModel.Event.NavigateToChatScreen) event).chatId;
                String postId = ((PostViewModel.Event.NavigateToChatScreen) event).postId;
                String hostId = ((PostViewModel.Event.NavigateToChatScreen) event).hostId;
                String guestId = ((PostViewModel.Event.NavigateToChatScreen) event).guestId;
                String uid = ((PostViewModel.Event.NavigateToChatScreen) event).userId;
                NavDirections action = PostFragmentDirections.actionGlobalChatRoomFragment(
                        chatId, postId, hostId, guestId, uid);
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

}