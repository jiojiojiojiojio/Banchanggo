package com.penelope.banchanggo.data.post;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Post implements Serializable {

    private String id;
    private String writerId;
    private String title;
    private String content;
    private int price;
    private List<String> categories;
    private List<String> likes;
    private String address;
    private long created;

    public Post() {

    }

    public Post(String writerId, String title, String content, int price, List<String> categories, String address) {
        this.writerId = writerId;
        this.title = title;
        this.content = content;
        this.price = price;
        this.categories = categories;
        this.likes = new ArrayList<>();
        this.address = address;
        this.created = System.currentTimeMillis();
        this.id = writerId + "#" + created;
    }

    public String getId() {
        return id;
    }

    public String getWriterId() {
        return writerId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getPrice() {
        return price;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<String> getLikes() {
        return likes;
    }

    public String getAddress() {
        return address;
    }

    public long getCreated() {
        return created;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return price == post.price && created == post.created && id.equals(post.id) && writerId.equals(post.writerId) && title.equals(post.title) && content.equals(post.content) && categories.equals(post.categories) && likes.equals(post.likes) && address.equals(post.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, writerId, title, content, price, categories, likes, address, created);
    }
}
