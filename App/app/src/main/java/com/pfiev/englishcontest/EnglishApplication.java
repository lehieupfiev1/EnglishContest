package com.pfiev.englishcontest;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.pfiev.englishcontest.service.SoundBackgroundService;
import com.pfiev.englishcontest.utils.AppConfig;
import com.pfiev.englishcontest.utils.LangUtils;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

import java.util.Locale;

public class EnglishApplication extends Application implements LifecycleObserver {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Application init", "On Create");
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        //App in background
        boolean sound_turnOn = SharePreferenceUtils.getBoolean(getApplicationContext(), this.getString(R.string.preferences_bg_music_status), false);
        if (sound_turnOn) {
            stopService(new Intent(getApplicationContext(), SoundBackgroundService.class));
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // App in foreground
        boolean sound_turnOn = SharePreferenceUtils.getBoolean(getApplicationContext(), this.getString(R.string.preferences_bg_music_status), false);
        if (sound_turnOn) {
            startService(new Intent(getApplicationContext(), SoundBackgroundService.class));
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        LangUtils langUtils = new LangUtils();
        langUtils.onAttach(this.getApplicationContext(), AppConfig.locale);
        Log.d("Application", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void attachBaseContext(Context base) {
        AppConfig appConfig = new AppConfig(base);
        LangUtils langUtils = new LangUtils();
        Log.d("Application attach ", appConfig.getDisplayLanguage());
        super.attachBaseContext(langUtils.onAttach(base, appConfig.getDisplayLanguage()));
    }
}
