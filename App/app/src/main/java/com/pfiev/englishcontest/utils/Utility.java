package com.pfiev.englishcontest.utils;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

public  class Utility {
    public static boolean isValid(Activity activity) {
        if (activity == null) {
            return false;
        }

        if (activity.isDestroyed()) {
            return false;
        }

        return !activity.isFinishing();

    }

    public static boolean isValid(Fragment fragment) {
        if (fragment == null) {
            return false;
        }

        if (fragment.isDetached()) {
            return false;
        }

        if (fragment.isRemoving()) {
            return false;
        }

        return isValid(fragment.getActivity());
    }

    public static boolean isInvalid(Fragment fragment) {
        return !isValid(fragment);
    }

    public static String randomString(int length){

        char[] ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
                .toCharArray();

        StringBuilder random = new StringBuilder();

        for(int i =0; i < length; i++) {
            int index = (int) (Math.random() * ALPHANUMERIC.length);
            random.append(ALPHANUMERIC[index]);
        }
        return random.toString();
    }

    public static int getDimensionFromRes(Context context, int resId) {
        return Math.round(context.getResources().getDimension(resId));
    }

}
