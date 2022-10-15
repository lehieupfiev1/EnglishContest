package com.pfiev.englishcontest.model;

public class NotificationItem {
    private String matchId, name, userPhotoUrl, type;
    private float timestamp;
    private boolean hasRead;

    public NotificationItem() {
        // Default constructor required for calls to
        // DataSnapshot.getValue(User.class)
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(float timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isHasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    public final static String COLLECTION_NAME = "";

    public static class FIELD_NAME {
        public final static String TYPE = "type";
        public final static String USER_NAME = "name";
        public final static String MATCH_ID = "matchId";
        public final static String USER_PHOTO_URL = "userPhotoUrl";
        public final static String HAS_READ = "hasRead";
        public final static String TIMESTAMP = "timestamp";
    }

    public static class TYPE_VALUE {
        public final static String REQUEST_COMBAT = "request_combat";
    }
}
