package com.penelope.banchanggo.data.contract;

public class Contract {

    private String id;
    private String postId;
    private String sellerId;
    private String buyerId;
    private int price;
    private boolean completed;
    private long created;

    public Contract() {
    }

    public Contract(String postId, String sellerId, String buyerId, int price, boolean completed) {
        this.postId = postId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.price = price;
        this.completed = completed;
        this.created = System.currentTimeMillis();
        this.id = postId + "#" + buyerId;
    }

    public String getId() {
        return id;
    }

    public String getPostId() {
        return postId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public int getPrice() {
        return price;
    }

    public boolean isCompleted() {
        return completed;
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

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setCreated(long created) {
        this.created = created;
    }

}
