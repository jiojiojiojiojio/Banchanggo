package com.penelope.banchanggo.data.comment;

import com.penelope.banchanggo.data.user.User;

import java.util.Objects;

public class DetailedComment extends Comment {

    private final User user;

    public DetailedComment(Comment comment, User user) {
        super(comment);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DetailedComment that = (DetailedComment) o;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user);
    }
}
