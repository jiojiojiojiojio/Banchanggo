package com.penelope.banchanggo.ui.chat.chatlist;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.penelope.banchanggo.R;
import com.penelope.banchanggo.data.chat.DetailedChat;
import com.penelope.banchanggo.databinding.FragmentChatListBinding;
import com.penelope.banchanggo.ui.post.PostFragmentDirections;
import com.penelope.banchanggo.ui.post.PostViewModel;
import com.penelope.banchanggo.utils.ui.AuthFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChatListFragment extends AuthFragment {

    private FragmentChatListBinding binding;
    private ChatListViewModel viewModel;


    public ChatListFragment() {
        super(R.layout.fragment_chat_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentChatListBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(ChatListViewModel.class);

        // 채팅 리스트에 프로필 사진과 마지막 채팅내용 표시

        viewModel.getUid().observe(getViewLifecycleOwner(), uid ->
                viewModel.getAlbum().observe(getViewLifecycleOwner(), album -> {

                    DetailedChatsAdapter adapter = new DetailedChatsAdapter(uid, album, Glide.with(this));
                    binding.recyclerChat.setAdapter(adapter);
                    binding.recyclerChat.setHasFixedSize(true);

                    adapter.setOnItemSelectedListener(position -> {
                        DetailedChat chat = adapter.getCurrentList().get(position);
                        viewModel.onChatClick(chat);
                    });

                    viewModel.getChats().observe(getViewLifecycleOwner(), adapter::submitList);
                })
        );

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof ChatListViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof ChatListViewModel.Event.NavigateToChatScreen) {
                String chatId = ((ChatListViewModel.Event.NavigateToChatScreen) event).chatId;
                String postId = ((ChatListViewModel.Event.NavigateToChatScreen) event).postId;
                String hostId = ((ChatListViewModel.Event.NavigateToChatScreen) event).hostId;
                String guestId = ((ChatListViewModel.Event.NavigateToChatScreen) event).guestId;
                String uid = ((ChatListViewModel.Event.NavigateToChatScreen) event).uid;
                NavDirections action = ChatListFragmentDirections.actionGlobalChatRoomFragment(
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