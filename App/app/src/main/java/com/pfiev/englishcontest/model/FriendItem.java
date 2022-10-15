package com.pfiev.englishcontest.model;

import java.time.Instant;

public class FriendItem extends  BaseFriendListItem{;

    public FriendItem() {
        // Default constructor required for calls to
        // DataSnapshot.getValue(User.class)
    }

    public FriendItem(String uid, String name, String userPhotoUrl, String status) {
        this.uid = uid;
        this.name = name;
        this.userPhotoUrl = userPhotoUrl;
        this.status = status;
        this.timestamp = Instant.now().getEpochSecond();
    }
}
