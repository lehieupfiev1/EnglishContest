package com.pfiev.englishcontest.model;

public class LeaderboardItem {
    String orderId;
    String avatar;
    String username;
    String score;

    public String getOrderId() {
        return orderId;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }

    public String getScore() {
        return score;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
