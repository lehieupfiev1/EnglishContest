package com.pfiev.englishcontest.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserItem implements Parcelable {
    String userName;
    String userId;
    String userEmail;
    String userPhotoUrl;
    String userPhoneNumber;
    String userGender;

    public UserItem() {
    }

    public UserItem(String userName, String userId, String userEmail, String userPhotoUrl, String userPhoneNumber, String userGender) {
        this.userName = userName;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPhotoUrl = userPhotoUrl;
        this.userPhoneNumber = userPhoneNumber;
        this.userGender = userGender;
    }

    protected UserItem(Parcel in) {
        userName = in.readString();
        userId = in.readString();
        userEmail = in.readString();
        userPhotoUrl = in.readString();
        userPhoneNumber = in.readString();
        userGender = in.readString();
    }

    public static final Creator<UserItem> CREATOR = new Creator<UserItem>() {
        @Override
        public UserItem createFromParcel(Parcel in) {
            return new UserItem(in);
        }

        @Override
        public UserItem[] newArray(int size) {
            return new UserItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userName);
        parcel.writeString(userId);
        parcel.writeString(userEmail);
        parcel.writeString(userPhotoUrl);
        parcel.writeString(userPhoneNumber);
        parcel.writeString(userGender);
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getName() {
        return userName;
    }

    public void setName(String name) {
        this.userName = name;
    }

    public String getEmail() {
        return userEmail;
    }

    public void setEmail(String email) {
        this.userEmail = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }


}
