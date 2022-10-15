package com.pfiev.englishcontest;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.pfiev.englishcontest.service.ListenCombatRequestService;
import com.pfiev.englishcontest.service.SoundBackgroundService;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

public class EnglishApplication extends Application implements LifecycleObserver {
    public ActivityManager activityManager;
    ListenCombatRequestService mService;
    boolean mBound;
    private static Activity currentActivity;
    private static boolean isShowNotification;

    public static Activity getCurrentActivity() {
        return currentActivity;
    }
//    public static void setIsShowNotification(Boolean isShow) {
////        isShowNotification = isShow;
//        if (isShow)
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Application init", "On Create");
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        //App in background
        boolean sound_turnOn = SharePreferenceUtils.getBoolean(
                getApplicationContext(), this.getString(R.string.preferences_bg_music_status),
                false);
        if (sound_turnOn) {
            stopService(new Intent(getApplicationContext(),
                    SoundBackgroundService.class));
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // App in foreground
        boolean sound_turnOn = SharePreferenceUtils.getBoolean(
                getApplicationContext(), this.getString(R.string.preferences_bg_music_status),
                false);
        if (sound_turnOn) {
            startService(new Intent(getApplicationContext(), SoundBackgroundService.class));
        }
        this.activityManager = new ActivityManager(this);
    }


    public class ActivityManager implements Application.ActivityLifecycleCallbacks {

        public ActivityManager(Application myApplication) {
            myApplication.registerActivityLifecycleCallbacks(this);
        }

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
            currentActivity = activity;
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            currentActivity = activity;


        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            currentActivity = activity;
            if (activity instanceof PlayGameActivity) return;
            if (activity instanceof LoginActivity) return;
            Intent intent = new Intent(activity, ListenCombatRequestService.class);
            intent.putExtra(GlobalConstant.USER_ID,
                    SharePreferenceUtils.getString(getApplicationContext(), GlobalConstant.USER_ID)
            );
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            if (activity instanceof PlayGameActivity) return;
            if (activity instanceof LoginActivity) return;
            if (mBound) unbindService(connection);
            mBound = false;

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    }

    /**
     * Service connection of listen request service
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            ListenCombatRequestService.ListenLocalBinder binder =
                    (ListenCombatRequestService.ListenLocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };
}