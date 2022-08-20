package com.pfiev.englishcontest;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.pfiev.englishcontest.utils.AppConfig;
import com.pfiev.englishcontest.utils.LangUtils;

import java.util.Locale;

public class EnglishApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Application init", "On Create");
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
