package com.pfiev.englishcontest.model;

import java.util.HashMap;

public class BaseFriendListItem {

    String uid;
    String name;
    String userPhotoUrl;
    String status;
    long timestamp;

    public static class STATUS {
        public static String ONLINE = "online";
        public static String OFFLINE = "offline";
        public static String FINDING = "finding";
        public static String PLAYING = "playing";
        public static HashMap<String, Integer> STATUS_PRIORITY = new HashMap<String, Integer>();

        static {
            STATUS_PRIORITY.put(com.pfiev.englishcontest.model.FriendItem.STATUS.ONLINE, 1);
            STATUS_PRIORITY.put(com.pfiev.englishcontest.model.FriendItem.STATUS.PLAYING, 2);
            STATUS_PRIORITY.put(com.pfiev.englishcontest.model.FriendItem.STATUS.FINDING, 3);
            STATUS_PRIORITY.put(com.pfiev.englishcontest.model.FriendItem.STATUS.OFFLINE, 4);
        }

        public static int getPriority(String status) {
            return STATUS_PRIORITY.get(status);
        }

    }

    public static String TIMESTAMP_FIELD_NAME = "timestamp";
    public static String STATUS_FIELD_NAME = "status";
    public static String NAME_FIELD_NAME = "name";

    public BaseFriendListItem() {
        // Default constructor required for calls to
        // DataSnapshot.getValue(User.class)
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public BaseFriendListItem(String uid, String name, String userPhotoUrl, String status) {
        this.uid = uid;
        this.name = name;
        this.userPhotoUrl = userPhotoUrl;
        this.status = status;
    }
}
