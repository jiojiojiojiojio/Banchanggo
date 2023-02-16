package com.penelope.banchanggo.data.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.penelope.banchanggo.data.comment.Comment;
import com.penelope.banchanggo.data.comment.CommentRepository;
import com.penelope.banchanggo.data.image.ImageRepository;
import com.penelope.banchanggo.data.user.User;
import com.penelope.banchanggo.data.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class DetailedChatRepository {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;

    @Inject
    public DetailedChatRepository(ChatRepository chatRepository,
                                  UserRepository userRepository,
                                  CommentRepository commentRepository,
                                  ImageRepository imageRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.imageRepository = imageRepository;
    }

    public LiveData<List<DetailedChat>> getDetailedChats(String userId) {

        LiveData<List<Chat>> chats = chatRepository.getChats(userId);
        LiveData<Map<String, User>> userMap = userRepository.getUserMap();
        LiveData<Map<String, Comment>> lastComments = commentRepository.getLastComments(userId);

        // userId 로 검색된 채팅 리스트 획득

        return Transformations.switchMap(chats, chatList ->
                Transformations.switchMap(lastComments, commentMap ->
                        Transformations.map(userMap, uMap -> {
                            if (chatList == null || uMap == null || commentMap == null) {
                                return null;
                            }

                            List<DetailedChat> detailedChatList = new ArrayList<>();
                            for (Chat chat : chatList) {
                                User host = uMap.get(chat.getHostId());
                                User guest = uMap.get(chat.getGuestId());
                                Comment lastComment = commentMap.get(chat.getId());
                                if (host != null && guest != null) {
                                    DetailedChat detailedChat = new DetailedChat(
                                            chat, host, guest, lastComment
                                    );
                                    detailedChatList.add(detailedChat);
                                }
                            }

                            return detailedChatList;
                        })
                )
        );
    }

}
