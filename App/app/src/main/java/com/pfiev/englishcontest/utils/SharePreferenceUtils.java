package com.pfiev.englishcontest.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.pfiev.englishcontest.GlobalConstant;
import com.pfiev.englishcontest.model.UserItem;

public class SharePreferenceUtils {
    private static final String TAG = SharePreferenceUtils.class.getSimpleName();
    private static final String MY_SHARE_FREF = "com.pfiev.english.preferences";
    private static final String DEVICE_DISPLAY_HASH = "device_display_hash";

    public static void updateUserData(Context mContext, UserItem userItem) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARE_FREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(GlobalConstant.USER_ID, userItem.getUserId());
        myEdit.putString(GlobalConstant.USER_NAME, userItem.getName());
        myEdit.putString(GlobalConstant.USER_EMAIL, userItem.getEmail());
        myEdit.putString(GlobalConstant.USER_GENDER, userItem.getUserGender());
        myEdit.putString(GlobalConstant.USER_PHONE_NUMBER, userItem.getUserPhoneNumber());
        myEdit.putString(GlobalConstant.USER_PROFILE_IMAGE, userItem.getUserPhotoUrl());
        myEdit.commit();
    }

    public static UserItem getUserData(Context mContext) {
        UserItem userItem = new UserItem();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARE_FREF, Context.MODE_PRIVATE);
        userItem.setUserId(sharedPreferences.getString(GlobalConstant.USER_ID,null));
        userItem.setName(sharedPreferences.getString(GlobalConstant.USER_NAME,null));
        userItem.setEmail(sharedPreferences.getString(GlobalConstant.USER_EMAIL,null));
        userItem.setUserGender(sharedPreferences.getString(GlobalConstant.USER_GENDER,null));
        userItem.setUserPhoneNumber(sharedPreferences.getString(GlobalConstant.USER_PHONE_NUMBER,null));
        userItem.setUserPhotoUrl(sharedPreferences.getString(GlobalConstant.USER_PROFILE_IMAGE,null));
        return userItem;
    }

    public static String getDeviceDisplayHash(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARE_FREF, Context.MODE_PRIVATE);
        String deviceHash = sharedPreferences.getString(DEVICE_DISPLAY_HASH, null);
        if (deviceHash == null) {
            deviceHash = Utility.randomString(8);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString(DEVICE_DISPLAY_HASH, deviceHash);
            myEdit.commit();
        }
        return deviceHash;
    }

    public static void putInt(Context mContext,String key, int value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARE_FREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putInt(key, value);
        myEdit.apply();
    }

    public static int getInt(Context mContext, String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARE_FREF, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public static int getInt(Context mContext, String key, int defaultValue) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARE_FREF, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void putString(Context mContext,String key, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARE_FREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(key, value);
        myEdit.apply();
    }

    public static String getString(Context mContext, String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARE_FREF, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, "");
        return value;
    }

    public static String getString(Context mContext, String key, String defaultValue) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARE_FREF, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, defaultValue);
        return value;
    }
    public static void putBoolean(Context mContext,String key, boolean value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARE_FREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putBoolean(key, value);
        myEdit.apply();
    }

    public static boolean getBoolean(Context mContext, String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARE_FREF, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static boolean getBoolean(Context mContext, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARE_FREF, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void putLong(Context mContext,String key, long value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARE_FREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putLong(key, value);
        myEdit.apply();
    }

    public static long getLong(Context mContext, String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SHARE_FREF, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, 0);
    }

}
