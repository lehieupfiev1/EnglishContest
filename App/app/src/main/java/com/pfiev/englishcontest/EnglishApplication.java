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

import com.facebook.FacebookActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.pfiev.englishcontest.realtimedb.Status;
import com.pfiev.englishcontest.service.ListenCombatRequestService;
import com.pfiev.englishcontest.service.SoundBackgroundService;
import com.pfiev.englishcontest.setup.FacebookSignInActivity;
import com.pfiev.englishcontest.setup.GoogleSignInActivity;
import com.pfiev.englishcontest.setup.TwitterSignInActivity;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

import java.util.ArrayList;

public class EnglishApplication extends Application implements LifecycleObserver {
    public ActivityManager activityManager;
    ListenCombatRequestService mService;
    boolean mBound;
    private static boolean isSetOnDisconnectAction;
    private static Activity currentActivity;
    private static String currentUserStatus;
    private static boolean isShowNotification;
    private ArrayList<Class> excludeClass;

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    /**
     * Check if allow show notification
     * @return
     */
    public static Boolean isAllowedShowNotification() {
        return !currentUserStatus.equals(Status.STATE_PLAYING);
    }

    /**
     * Set current status state of user
     * @param status
     */
    public static void setCurrentUserStatus(String status) {
        currentUserStatus = status;
    }

    public static boolean isSetOnDisconnectEvent() {
        return isSetOnDisconnectAction;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        currentUserStatus = Status.STATE_OFFLINE;
        Log.d("Application init", "On Create");
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        excludeClass = new ArrayList<>();
        excludeClass.add(LoginActivity.class);
        excludeClass.add(PlayGameActivity.class);
        excludeClass.add(FacebookSignInActivity.class);
        excludeClass.add(FacebookActivity.class);
        excludeClass.add(TwitterSignInActivity.class);
        excludeClass.add(GoogleSignInActivity.class);
        excludeClass.add(TestActivity.class);
        isSetOnDisconnectAction = false;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Status.getInstance().destroyListener();
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
            if (excludeClass.contains(activity.getClass())) {
                return;
            }
            String ownUid = SharePreferenceUtils.getString(
                    getApplicationContext(), GlobalConstant.USER_ID);
            if (!isSetOnDisconnectAction) {
                Status.getInstance().setOwnUid(ownUid);
                Status.getInstance().setOnDisconnectAction(activity, new Status.StateCallBack() {
                    @Override
                    public void afterSetOnDisconnectAction() {
                        isSetOnDisconnectAction = true;
                    }

                    @Override
                    public void notSameHashCallback() {
                        isSetOnDisconnectAction = false;
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(activity, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }
            Intent intent = new Intent(activity, ListenCombatRequestService.class);
            intent.putExtra(GlobalConstant.USER_ID, ownUid);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            if (excludeClass.contains(activity.getClass())) {
                return;
            }
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