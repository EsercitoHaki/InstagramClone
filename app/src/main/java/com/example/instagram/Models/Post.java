package com.example.instagram.Models;

public class Post {
    private String postUrl, caption, uid, time;
    public Post() {

    }
    public Post(String postUrl, String caption) {
        this.postUrl = postUrl;
        this.caption = caption;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public Post(String postUrl, String caption, String name, String time) {
        this.postUrl = postUrl;
        this.caption = caption;
        this.uid = name;
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
