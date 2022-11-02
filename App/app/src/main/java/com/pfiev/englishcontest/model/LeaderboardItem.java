package com.pfiev.englishcontest.model;

import java.util.HashMap;
import java.util.Objects;

public class LeaderboardItem {
    String userId;
    String orderId;
    String name;
    String email;
    int score;
    String userPhotoUrl;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public void setProperties(HashMap map) {
        if (map.get("email") != null)
            this.email = Objects.requireNonNull(map.get("email")).toString();
        if (map.get("name") != null)
            this.name = Objects.requireNonNull(map.get("name")).toString();
        if (map.get("userId") != null)
            this.userId = Objects.requireNonNull(map.get("userId")).toString();
        if (map.get("userPhotoUrl") != null)
            this.userPhotoUrl = Objects.requireNonNull(map.get("userPhotoUrl")).toString();
        if (map.get("score") != null)
            this.score = (int) map.get("score");
    }
}
