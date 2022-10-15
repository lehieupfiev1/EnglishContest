package com.pfiev.englishcontest.model;

public class BlockItem extends BaseFriendListItem{
    public static String NAME_FIELD_NAME = "name";

    public BlockItem() {
        // Default constructor required for calls to
        // DataSnapshot.getValue(User.class)
    }

    public BlockItem(String uid, String name, String userPhotoUrl, String status) {
        this.uid = uid;
        this.name = name;
        this.userPhotoUrl = userPhotoUrl;
        this.status = status;
    }
}
