package com.pfiev.englishcontest.utils;

import android.app.Activity;

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
}
