package com.example.instagram.Models;

public class Reel {
    private String reelUrl;
    private String caption;
    private String profileLink;

    // Empty constructor for Firebase
    public Reel() {}

    // Constructor with reelUrl and caption
    public Reel(String reelUrl, String caption) {
        this.reelUrl = reelUrl;
        this.caption = caption;
    }

    // Constructor with all three parameters
    public Reel(String reelUrl, String caption, String profileLink) {
        this.reelUrl = reelUrl;
        this.caption = caption;
        this.profileLink = profileLink;
    }

    // Getters and setters
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

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }
}
