package com.penelope.banchanggo.data.chat;

import com.penelope.banchanggo.data.comment.Comment;
import com.penelope.banchanggo.data.user.User;

import java.util.Objects;

public class DetailedChat extends Chat {

    private final User host;            // 호스트 유저
    private final User guest;           // 게스트 유저
    private final Comment lastComment;  // 마지막 커멘트

    public DetailedChat(Chat chat, User host, User guest, Comment lastComment) {
        super(chat);
        this.host = host;
        this.guest = guest;
        this.lastComment = lastComment;
    }

    public User getHost() {
        return host;
    }

    public User getGuest() {
        return guest;
    }

    public Comment getLastComment() {
        return lastComment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetailedChat that = (DetailedChat) o;
        return host.equals(that.host) && guest.equals(that.guest) && Objects.equals(lastComment, that.lastComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, guest, lastComment);
    }
}
