package com.example.instagram.Models;

public class Post {
    private String postUrl, caption, name, time;

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
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
