package com.pfiev.englishcontest.model;

import java.time.Instant;

public class WaitingItem extends BaseFriendListItem {
    public WaitingItem() {}

    public WaitingItem(String uid, String name, String userPhotoUrl, String status) {
        this.uid = uid;
        this.name = name;
        this.userPhotoUrl = userPhotoUrl;
        this.status = status;
        this.timestamp = Instant.now().getEpochSecond();
    }

}
