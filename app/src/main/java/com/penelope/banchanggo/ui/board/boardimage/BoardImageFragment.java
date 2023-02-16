package com.penelope.banchanggo.ui.board.boardimage;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.penelope.banchanggo.R;
import com.penelope.banchanggo.data.post.Post;
import com.penelope.banchanggo.databinding.FragmentBoardImageBinding;
import com.penelope.banchanggo.ui.board.PostImagesAdapter;
import com.penelope.banchanggo.ui.board.PostsAdapter;
import com.penelope.banchanggo.ui.board.boardtext.BoardTextFragmentDirections;
import com.penelope.banchanggo.utils.OnTextChangedListener;
import com.penelope.banchanggo.utils.ui.AuthFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BoardImageFragment extends AuthFragment {

    private FragmentBoardImageBinding binding;
    private BoardImageViewModel viewModel;


    public BoardImageFragment() {
        super(R.layout.fragment_board_image);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentBoardImageBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(BoardImageViewModel.class);

        // 드로워 열고 닫기
        binding.homeMenuBtn.setOnClickListener(v ->
                binding.homeDrawerLayout.openDrawer(binding.drawer.drawer));
        binding.drawer.btnClose.setOnClickListener(v ->
                binding.homeDrawerLayout.closeDrawers());

        binding.editTextAddress.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onAddressQueryChange(text);
            }
        });

        binding.imageViewTextType.setOnClickListener(v -> viewModel.onTextTypeClick());

        binding.recyclerPostRealtime.setHasFixedSize(true);
        binding.recyclerPostPopular.setHasFixedSize(true);

        viewModel.getAlbumRealtime().observe(getViewLifecycleOwner(), album -> {

            PostImagesAdapter adapter = new PostImagesAdapter(Glide.with(this), album);
            binding.recyclerPostRealtime.setAdapter(adapter);

            adapter.setOnItemSelectedListener(position -> {
                Post post = adapter.getCurrentList().get(position);
                viewModel.onPostClick(post);
            });

            viewModel.getPostsRealtime().observe(getViewLifecycleOwner(), postList -> {
                if (postList != null) {
                    adapter.submitList(postList);
                }
            });
        });

        viewModel.getAlbumPopular().observe(getViewLifecycleOwner(), album -> {

            PostImagesAdapter adapter = new PostImagesAdapter(Glide.with(this), album);
            binding.recyclerPostPopular.setAdapter(adapter);

            adapter.setOnItemSelectedListener(position -> {
                Post post = adapter.getCurrentList().get(position);
                viewModel.onPostClick(post);
            });

            viewModel.getPostsPopular().observe(getViewLifecycleOwner(), postList -> {
                if (postList != null) {
                    adapter.submitList(postList);
                }
            });
        });
        
        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof BoardImageViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof BoardImageViewModel.Event.NavigateToPostScreen) {
                Post post = ((BoardImageViewModel.Event.NavigateToPostScreen) event).post;
                NavDirections action = BoardImageFragmentDirections.actionGlobalPostFragment(post);
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof BoardImageViewModel.Event.NavigateToBoardTextScreen) {
                NavDirections action = BoardImageFragmentDirections.actionBoardImageFragmentToBoardTextFragment();
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