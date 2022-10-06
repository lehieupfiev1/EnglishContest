package com.pfiev.englishcontest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.pfiev.englishcontest.R;

public class AppConfig {

    public final Context context;
    public static String locale = "";

    public AppConfig(Context context) {
        this.context = context;
        AppConfig.locale = this.getDisplayLanguage();
    }


    /**
     * Get Display Language
     * @return display_language ex:vi_VN
     */
    public String getDisplayLanguage() {
        return SharePreferenceUtils.getString(context, context.getString(R.string.preferences_display_language),
                context.getString(R.string.preferences_locale_default));
    }

    /**
     * Set Display language
     * @param displayLanguage ex: vi_VN
     */
    public void setDisplayLanguage(String displayLanguage) {
        SharePreferenceUtils.putString(context,context.getString(R.string.preferences_display_language),
                displayLanguage);
    }

    /**
     *
     * @return All locale in Array of String Ex: en_US,vi_VN
     */
    public String[] getAllLocale() {

        return SharePreferenceUtils.getString(context,
                context.getString(R.string.preferences_all_locale_key),
                context.getString(R.string.preferences_all_locale_values)
        ).split(context.getString(R.string.preferences_all_locale_delimiter));
    }

    /**
     *
     * @return All locale in Array of String Ex: English, Vietnamese
     */
    public String[] getAllLocaleShow() {

        return SharePreferenceUtils.getString(context,
                context.getString(R.string.preferences_all_locale_show_key),
                context.getString(R.string.preferences_all_locale_show_values)
        ).split(context.getString(R.string.preferences_all_locale_delimiter));
    }

    /**
     * Check if sound is on
     * @return True if sound is on otherwise will be false
     */
    public Boolean isSoundOn() {
        return SharePreferenceUtils.getBoolean(context,
                context.getString(R.string.preferences_bg_music_status), false);
    }

    /**
     * Set sound status
     * @param isOn true if want on sound
     */
    public void setSoundOn(Boolean isOn) {
        SharePreferenceUtils.putBoolean(context, context.getString(R.string.preferences_sound_status), isOn);
    }

    /**
     * Check if background music is on
     * @return True if sound is on otherwise will be false
     */
    public boolean isBackgroundMusicEffectOn() {
        return SharePreferenceUtils.getBoolean(context,
                context.getString(R.string.preferences_bg_music_status), true);
    }

    /**
     * Set background music status
     * @param isOn true if want on sound
     */
    public void setBackgroundMusicEffectOn(boolean isOn) {
        SharePreferenceUtils.putBoolean(context, context.getString(R.string.preferences_bg_music_status), isOn);
    }
}
