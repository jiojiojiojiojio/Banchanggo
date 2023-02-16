package com.penelope.banchanggo.ui.favorite;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.penelope.banchanggo.R;
import com.penelope.banchanggo.data.PostFilter;
import com.penelope.banchanggo.data.post.Post;
import com.penelope.banchanggo.databinding.FragmentFavoriteBinding;
import com.penelope.banchanggo.ui.board.PostsAdapter;
import com.penelope.banchanggo.utils.NameUtils;
import com.penelope.banchanggo.utils.ui.AuthFragment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoriteFragment extends AuthFragment {

    private FragmentFavoriteBinding binding;
    private FavoriteViewModel viewModel;


    public FavoriteFragment() {
        super(R.layout.fragment_favorite);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentFavoriteBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        // 드로워 열고 닫기
        binding.favoriteMenuBtn.setOnClickListener(v ->
                binding.favoriteDrawerLayout.openDrawer(binding.drawer.drawer));
        binding.drawer.btnClose.setOnClickListener(v ->
                binding.favoriteDrawerLayout.closeDrawers());

        List<String> filterNames = Arrays.stream(PostFilter.values()).map(NameUtils::getPostFilterName).collect(Collectors.toList());
        binding.spinnerFilter.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                filterNames.toArray(new String[] {}))
        );
        binding.spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.onPostFilterSelected(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        binding.recyclerPost.setHasFixedSize(true);

        viewModel.getAlbum().observe(getViewLifecycleOwner(), album -> {

            PostsAdapter adapter = new PostsAdapter(Glide.with(this), album);
            binding.recyclerPost.setAdapter(adapter);

            adapter.setOnItemSelectedListener(position -> {
                Post post = adapter.getCurrentList().get(position);
                viewModel.onPostClick(post);
            });

            viewModel.getPosts().observe(getViewLifecycleOwner(), postList -> {
                if (postList != null) {
                    adapter.submitList(postList);
                }
            });
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof FavoriteViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof FavoriteViewModel.Event.NavigateToPostScreen) {
                Post post = ((FavoriteViewModel.Event.NavigateToPostScreen) event).post;
                NavDirections action = FavoriteFragmentDirections.actionGlobalPostFragment(post);
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