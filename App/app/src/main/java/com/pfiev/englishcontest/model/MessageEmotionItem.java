package com.pfiev.englishcontest.model;

public class MessageEmotionItem {
    String message;
    String senderId;
    String timeCreated;
    String hasRead;
    String type;

    public MessageEmotionItem() {
    }

    public MessageEmotionItem(String message, String senderId, String timeCreated, String hasRead, String type) {
        this.message = message;
        this.senderId = senderId;
        this.timeCreated = timeCreated;
        this.hasRead = hasRead;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getHasRead() {
        return hasRead;
    }

    public void setHasRead(String hasRead) {
        this.hasRead = hasRead;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MessageEmotionItem{" +
                "message='" + message + '\'' +
                ", senderId='" + senderId + '\'' +
                ", timeCreated='" + timeCreated + '\'' +
                ", hasRead='" + hasRead + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
