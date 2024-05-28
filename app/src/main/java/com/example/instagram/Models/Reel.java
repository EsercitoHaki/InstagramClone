package com.example.instagram.Models;

public class Reel {
    private String reelUrl, caption;
    private String profileLink = null;

    public Reel(String reelUrl, String caption, String profileLink) {
        this.reelUrl = reelUrl;
        this.caption = caption;
        this.profileLink = profileLink;
    }

    public Reel(String reelUrl, String caption) {
        this.reelUrl = reelUrl;
        this.caption = caption;
    }

    public String getReelUrl() {
        return reelUrl;
    }

    public void setReelUrl(String reelUrl) {
        this.reelUrl = reelUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
