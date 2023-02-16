package com.penelope.banchanggo.data.user;

public class User {

    private String uid;
    private String email;
    private String password;
    private String name;
    private String phone;
    private long created;

    public User() {
        // Empty constructor
    }

    public User(String uid, String email, String password, String name, String phone) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.created = System.currentTimeMillis();
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public long getCreated() {
        return created;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}
