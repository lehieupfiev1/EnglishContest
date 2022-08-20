package com.pfiev.englishcontest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.pfiev.englishcontest.R;

public class AppConfig {

    private final Context context;
    private SharedPreferences sharedPref;
    public static String locale = "";

    public AppConfig(Context context) {
        this.context = context;
        this.sharedPref = context.getSharedPreferences(
                context.getString(R.string.shared_preferences_key),
                Context.MODE_PRIVATE);
        AppConfig.locale = this.getDisplayLanguage();
    }

//    public SharedPreferences getSharedPref() {
//        return this.sharedPref;
//    }

    /**
     * Get Display Language
     * @return display_language ex:vi_VN
     */
    public String getDisplayLanguage() {
        return this.sharedPref.getString(
                context.getString(R.string.preferences_display_language),
                context.getString(R.string.preferences_locale_default));
    }

    /**
     * Set Display language
     * @param displayLanguage ex: vi_VN
     */
    public void setDisplayLanguage(String displayLanguage) {
        this.sharedPref.edit().putString(
                context.getString(R.string.preferences_display_language),
                displayLanguage
        ).apply();
    }

    /**
     *
     * @return All locale in Array of String Ex: en_US,vi_VN
     */
    public String[] getAllLocale() {

        return this.sharedPref.getString(
                context.getString(R.string.preferences_all_locale_key),
                context.getString(R.string.preferences_all_locale_values)
        ).split(context.getString(R.string.preferences_all_locale_delimiter));
    }

    /**
     *
     * @return All locale in Array of String Ex: English, Vietnamese
     */
    public String[] getAllLocaleShow() {

        return this.sharedPref.getString(
                context.getString(R.string.preferences_all_locale_show_key),
                context.getString(R.string.preferences_all_locale_show_values)
        ).split(context.getString(R.string.preferences_all_locale_delimiter));
    }

//    public void setAllLocale() {
//        this.sharedPref.edit().putString(
//                context.getString(R.string.preferences_all_locale_key),
//                context.getString(R.string.preferences_all_locale_values)
//        ).apply();
//    }

    /**
     * Check if sound is on
     * @return True if sound is on otherwise will be false
     */
    public Boolean isSoundOn() {
        String status = this.sharedPref.getString(
                context.getString(R.string.preferences_sound_status),
                context.getString(R.string.preferences_sound_status_on)
        );
        return status.equals(context.getString(R.string.preferences_sound_status_on));
    }

    /**
     * Set sound status
     * @param isOn true if want on sound
     */
    public void setSoundOn(Boolean isOn) {
        String sound_status;
        if (isOn) sound_status = context.getString(R.string.preferences_sound_status_on);
        else sound_status = context.getString(R.string.preferences_sound_status_off);
        this.sharedPref.edit().putString(
                context.getString(R.string.preferences_sound_status),
                sound_status
        ).apply();
    }

}
