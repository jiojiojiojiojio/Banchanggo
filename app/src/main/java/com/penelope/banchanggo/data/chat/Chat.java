package com.penelope.banchanggo.data.chat;

public class Chat {

    private String id;
    private String postId;
    private String hostId;      // 호스트 아이디
    private String guestId;     // 게스트 아이디
    private long created;       // 생성 시간

    public Chat() {
    }

    public Chat(String postId, String hostId, String guestId) {
        this.postId = postId;
        this.hostId = hostId;
        this.guestId = guestId;
        this.created = System.currentTimeMillis();
        this.id = postId + "#" + hostId + "#" + guestId;
    }

    public Chat(Chat other) {
        this.id = other.id;
        this.postId = other.postId;
        this.hostId = other.hostId;
        this.guestId = other.guestId;
        this.created = other.created;
    }

    public String getId() {
        return id;
    }

    public String getPostId() {
        return postId;
    }

    public String getHostId() {
        return hostId;
    }

    public String getGuestId() {
        return guestId;
    }

    public long getCreated() {
        return created;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}
