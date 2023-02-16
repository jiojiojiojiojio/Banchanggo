package com.penelope.banchanggo.data.comment;

import java.util.Objects;

public class Comment {

    private String id;
    private String chatId;
    private String userId;      // 작성자 아이디
    private String contents;    // 커멘트 내용
    private long created;       // 작성 시간


    public Comment() { }

    public Comment(String chatId, String userId, String contents) {
        this.chatId = chatId;
        this.userId = userId;
        this.contents = contents;
        this.created = System.currentTimeMillis();
        this.id = chatId + "#" + userId + "#" + created;
    }

    public Comment(Comment other) {
        this.id = other.id;
        this.chatId = other.chatId;
        this.userId = other.userId;
        this.contents = other.contents;
        this.created = other.created;
    }

    public String getId() {
        return id;
    }

    public String getChatId() {
        return chatId;
    }

    public String getUserId() {
        return userId;
    }

    public String getContents() {
        return contents;
    }

    public long getCreated() {
        return created;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setCreated(long created) {
        this.created = created;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return created == comment.created && id.equals(comment.id) && chatId.equals(comment.chatId) && userId.equals(comment.userId) && contents.equals(comment.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, userId, contents, created);
    }
}
