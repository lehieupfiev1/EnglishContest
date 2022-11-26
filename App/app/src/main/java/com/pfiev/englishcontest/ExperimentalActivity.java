package com.pfiev.englishcontest;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;

import com.google.firebase.auth.FirebaseAuth;
import com.pfiev.englishcontest.databinding.ActivityExperimentalBinding;
import com.pfiev.englishcontest.model.EmotionIconItem;
import com.pfiev.englishcontest.model.PackEmotionItem;
import com.pfiev.englishcontest.ui.experimental.FindingMatchFragment;
import com.pfiev.englishcontest.ui.experimental.MainFragment;
import com.pfiev.englishcontest.utils.EmotionIconDBHelper;
import com.pfiev.englishcontest.utils.ListEmotionsDBHelper;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ExperimentalActivity extends AppCompatActivity {

    private final String TAG = "ExperimentalActivity";
    public static final String START_FRAGMENT_KEY = "start_fragment";

    public static interface FRAGMENT {
        public static final String FINDING_MATCH = "findingMatch";
    }

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };

    private final Runnable mShowPart2Runnable = () -> {
        // Delayed display of UI elements
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = this::hide;
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = (view, motionEvent) -> {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (AUTO_HIDE) {
                    delayedHide(AUTO_HIDE_DELAY_MILLIS);
                }
                break;
            case MotionEvent.ACTION_UP:
                view.performClick();
                break;
            default:
                break;
        }
        return false;
    };
    private ActivityExperimentalBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityExperimentalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle bundle = getIntent().getExtras();
        mContentView = binding.experimentalFullscreenContent;
        mVisible = true;

        if (bundle != null) {
            String startFragment = bundle.getString(START_FRAGMENT_KEY);
            if (startFragment != null && startFragment.equals(FRAGMENT.FINDING_MATCH)) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.experimental_fullscreen_content,
                                FindingMatchFragment.class,
                                bundle
                        )
                        .commitNow();

                return;
            }
        }
        boolean isInit = SharePreferenceUtils.getBoolean(getApplicationContext(),GlobalConstant.INIT_DATA_BASE, false);
        if (!isInit) {
            Log.d("LeHieu", "isInit false");
            initData();
            SharePreferenceUtils.putBoolean(getApplicationContext(),GlobalConstant.INIT_DATA_BASE, true);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.experimental_fullscreen_content, MainFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    public void initData() {
        // Init pack data
        Log.i("LeHieu", "Init data emotion ");
        ListEmotionsDBHelper packDB = ListEmotionsDBHelper.getInstance(getApplicationContext());
        packDB.addListEmotion(new PackEmotionItem("recent_emotion", "0", "descrip about recent", "recent_emotion"));

        packDB.addListEmotion(new PackEmotionItem("cat", "11", "descrip about cat", "cat_emotion"));
        packDB.addListEmotion(new PackEmotionItem("diggy", "12", "descrip about dyggy", "diggy_emotion"));
        packDB.addListEmotion(new PackEmotionItem("girl", "11", "descrip about girl", "girl_emotion"));
        packDB.addListEmotion(new PackEmotionItem("jooey", "12", "descrip about jooey", "jooey_emotion"));
        packDB.addListEmotion(new PackEmotionItem("senya", "11", "descrip about senya", "senya_emotion"));
        packDB.addListEmotion(new PackEmotionItem("perchick", "11", "descrip about perchick", "perchick_emotion"));
        packDB.addListEmotion(new PackEmotionItem("mysticise", "11", "descrip about mysticise", "mysticise_emotion"));
        packDB.addListEmotion(new PackEmotionItem("towelie", "10", "descrip about towelie_emotion", "towelie_emotion"));
        packDB.addListEmotion(new PackEmotionItem("pepetopanim", "8", "descrip about pepe", "pepetopanim_emotion"));
        packDB.addListEmotion(new PackEmotionItem("orangoutang", "6", "descrip about orangoutang", "orangoutang_emotion"));

        //Init emotion icon list
        EmotionIconDBHelper iconDB = EmotionIconDBHelper.getInstance(getApplicationContext());

        iconDB.addEmotionIcon(new EmotionIconItem("cat","sticker0","loti","cat_sticker0"));
        iconDB.addEmotionIcon(new EmotionIconItem("cat","sticker1","loti","cat_sticker1"));
        iconDB.addEmotionIcon(new EmotionIconItem("cat","sticker2","loti","cat_sticker2"));
        iconDB.addEmotionIcon(new EmotionIconItem("cat","sticker3","loti","cat_sticker3"));
        iconDB.addEmotionIcon(new EmotionIconItem("cat","sticker4","loti","cat_sticker4"));
        iconDB.addEmotionIcon(new EmotionIconItem("cat","sticker5","loti","cat_sticker5"));
        iconDB.addEmotionIcon(new EmotionIconItem("cat","sticker6","loti","cat_sticker6"));
        iconDB.addEmotionIcon(new EmotionIconItem("cat","sticker7","loti","cat_sticker7"));
        iconDB.addEmotionIcon(new EmotionIconItem("cat","sticker8","loti","cat_sticker8"));
        iconDB.addEmotionIcon(new EmotionIconItem("cat","sticker9","loti","cat_sticker9"));
        iconDB.addEmotionIcon(new EmotionIconItem("cat","sticker10","loti","cat_sticker10"));

        iconDB.addEmotionIcon(new EmotionIconItem("diggy","sticker0","loti","diggy_sticker0"));
        iconDB.addEmotionIcon(new EmotionIconItem("diggy","sticker1","loti","diggy_sticker1"));
        iconDB.addEmotionIcon(new EmotionIconItem("diggy","sticker2","loti","diggy_sticker2"));
        iconDB.addEmotionIcon(new EmotionIconItem("diggy","sticker3","loti","diggy_sticker3"));
        iconDB.addEmotionIcon(new EmotionIconItem("diggy","sticker4","loti","diggy_sticker4"));
        iconDB.addEmotionIcon(new EmotionIconItem("diggy","sticker5","loti","diggy_sticker5"));
        iconDB.addEmotionIcon(new EmotionIconItem("diggy","sticker6","loti","diggy_sticker6"));
        iconDB.addEmotionIcon(new EmotionIconItem("diggy","sticker7","loti","diggy_sticker7"));
        iconDB.addEmotionIcon(new EmotionIconItem("diggy","sticker8","loti","diggy_sticker8"));
        iconDB.addEmotionIcon(new EmotionIconItem("diggy","sticker9","loti","diggy_sticker9"));
        iconDB.addEmotionIcon(new EmotionIconItem("diggy","sticker10","loti","diggy_sticker10"));
        iconDB.addEmotionIcon(new EmotionIconItem("diggy","sticker11","loti","diggy_sticker11"));

        iconDB.addEmotionIcon(new EmotionIconItem("girl","sticker0","loti","girl_sticker0"));
        iconDB.addEmotionIcon(new EmotionIconItem("girl","sticker1","loti","girl_sticker1"));
        iconDB.addEmotionIcon(new EmotionIconItem("girl","sticker2","loti","girl_sticker2"));
        iconDB.addEmotionIcon(new EmotionIconItem("girl","sticker3","loti","girl_sticker3"));
        iconDB.addEmotionIcon(new EmotionIconItem("girl","sticker4","loti","girl_sticker4"));
        iconDB.addEmotionIcon(new EmotionIconItem("girl","sticker5","loti","girl_sticker5"));
        iconDB.addEmotionIcon(new EmotionIconItem("girl","sticker6","loti","girl_sticker6"));
        iconDB.addEmotionIcon(new EmotionIconItem("girl","sticker7","loti","girl_sticker7"));
        iconDB.addEmotionIcon(new EmotionIconItem("girl","sticker8","loti","girl_sticker8"));
        iconDB.addEmotionIcon(new EmotionIconItem("girl","sticker9","loti","girl_sticker9"));
        iconDB.addEmotionIcon(new EmotionIconItem("girl","sticker10","loti","girl_sticker10"));

        iconDB.addEmotionIcon(new EmotionIconItem("jooey","sticker0","loti","jooey_sticker0"));
        iconDB.addEmotionIcon(new EmotionIconItem("jooey","sticker1","loti","jooey_sticker1"));
        iconDB.addEmotionIcon(new EmotionIconItem("jooey","sticker2","loti","jooey_sticker2"));
        iconDB.addEmotionIcon(new EmotionIconItem("jooey","sticker3","loti","jooey_sticker3"));
        iconDB.addEmotionIcon(new EmotionIconItem("jooey","sticker4","loti","jooey_sticker4"));
        iconDB.addEmotionIcon(new EmotionIconItem("jooey","sticker5","loti","jooey_sticker5"));
        iconDB.addEmotionIcon(new EmotionIconItem("jooey","sticker6","loti","jooey_sticker6"));
        iconDB.addEmotionIcon(new EmotionIconItem("jooey","sticker7","loti","jooey_sticker7"));
        iconDB.addEmotionIcon(new EmotionIconItem("jooey","sticker8","loti","jooey_sticker8"));
        iconDB.addEmotionIcon(new EmotionIconItem("jooey","sticker9","loti","jooey_sticker9"));
        iconDB.addEmotionIcon(new EmotionIconItem("jooey","sticker10","loti","jooey_sticker10"));
        iconDB.addEmotionIcon(new EmotionIconItem("jooey","sticker11","loti","jooey_sticker11"));

        iconDB.addEmotionIcon(new EmotionIconItem("senya","sticker0","loti","senya_sticker0"));
        iconDB.addEmotionIcon(new EmotionIconItem("senya","sticker1","loti","senya_sticker1"));
        iconDB.addEmotionIcon(new EmotionIconItem("senya","sticker2","loti","senya_sticker2"));
        iconDB.addEmotionIcon(new EmotionIconItem("senya","sticker3","loti","senya_sticker3"));
        iconDB.addEmotionIcon(new EmotionIconItem("senya","sticker4","loti","senya_sticker4"));
        iconDB.addEmotionIcon(new EmotionIconItem("senya","sticker5","loti","senya_sticker5"));
        iconDB.addEmotionIcon(new EmotionIconItem("senya","sticker6","loti","senya_sticker6"));
        iconDB.addEmotionIcon(new EmotionIconItem("senya","sticker7","loti","senya_sticker7"));
        iconDB.addEmotionIcon(new EmotionIconItem("senya","sticker8","loti","senya_sticker8"));
        iconDB.addEmotionIcon(new EmotionIconItem("senya","sticker9","loti","senya_sticker9"));
        iconDB.addEmotionIcon(new EmotionIconItem("senya","sticker10","loti","senya_sticker10"));

        iconDB.addEmotionIcon(new EmotionIconItem("perchick","sticker0","loti","perchick_sticker0"));
        iconDB.addEmotionIcon(new EmotionIconItem("perchick","sticker1","loti","perchick_sticker1"));
        iconDB.addEmotionIcon(new EmotionIconItem("perchick","sticker2","loti","perchick_sticker2"));
        iconDB.addEmotionIcon(new EmotionIconItem("perchick","sticker3","loti","perchick_sticker3"));
        iconDB.addEmotionIcon(new EmotionIconItem("perchick","sticker4","loti","perchick_sticker4"));
        iconDB.addEmotionIcon(new EmotionIconItem("perchick","sticker5","loti","perchick_sticker5"));
        iconDB.addEmotionIcon(new EmotionIconItem("perchick","sticker6","loti","perchick_sticker6"));
        iconDB.addEmotionIcon(new EmotionIconItem("perchick","sticker7","loti","perchick_sticker7"));
        iconDB.addEmotionIcon(new EmotionIconItem("perchick","sticker8","loti","perchick_sticker8"));
        iconDB.addEmotionIcon(new EmotionIconItem("perchick","sticker9","loti","perchick_sticker9"));
        iconDB.addEmotionIcon(new EmotionIconItem("perchick","sticker10","loti","perchick_sticker10"));

        iconDB.addEmotionIcon(new EmotionIconItem("mysticise","sticker0","loti","mysticise_sticker0"));
        iconDB.addEmotionIcon(new EmotionIconItem("mysticise","sticker1","loti","mysticise_sticker1"));
        iconDB.addEmotionIcon(new EmotionIconItem("mysticise","sticker2","loti","mysticise_sticker2"));
        iconDB.addEmotionIcon(new EmotionIconItem("mysticise","sticker3","loti","mysticise_sticker3"));
        iconDB.addEmotionIcon(new EmotionIconItem("mysticise","sticker4","loti","mysticise_sticker4"));
        iconDB.addEmotionIcon(new EmotionIconItem("mysticise","sticker5","loti","mysticise_sticker5"));
        iconDB.addEmotionIcon(new EmotionIconItem("mysticise","sticker6","loti","mysticise_sticker6"));
        iconDB.addEmotionIcon(new EmotionIconItem("mysticise","sticker7","loti","mysticise_sticker7"));
        iconDB.addEmotionIcon(new EmotionIconItem("mysticise","sticker8","loti","mysticise_sticker8"));
        iconDB.addEmotionIcon(new EmotionIconItem("mysticise","sticker9","loti","mysticise_sticker9"));
        iconDB.addEmotionIcon(new EmotionIconItem("mysticise","sticker10","loti","mysticise_sticker10"));

        iconDB.addEmotionIcon(new EmotionIconItem("shark","sticker0","loti","shark_sticker0"));
        iconDB.addEmotionIcon(new EmotionIconItem("shark","sticker1","loti","shark_sticker1"));
        iconDB.addEmotionIcon(new EmotionIconItem("shark","sticker2","loti","shark_sticker2"));
        iconDB.addEmotionIcon(new EmotionIconItem("shark","sticker3","loti","shark_sticker3"));
        iconDB.addEmotionIcon(new EmotionIconItem("shark","sticker4","loti","shark_sticker4"));
        iconDB.addEmotionIcon(new EmotionIconItem("shark","sticker5","loti","shark_sticker5"));
        iconDB.addEmotionIcon(new EmotionIconItem("shark","sticker6","loti","shark_sticker6"));
        iconDB.addEmotionIcon(new EmotionIconItem("shark","sticker7","loti","shark_sticker7"));
        iconDB.addEmotionIcon(new EmotionIconItem("shark","sticker8","loti","shark_sticker8"));
        iconDB.addEmotionIcon(new EmotionIconItem("shark","sticker9","loti","shark_sticker9"));

        iconDB.addEmotionIcon(new EmotionIconItem("towelie","sticker0","loti","towelie_sticker0"));
        iconDB.addEmotionIcon(new EmotionIconItem("towelie","sticker1","loti","towelie_sticker1"));
        iconDB.addEmotionIcon(new EmotionIconItem("towelie","sticker2","loti","towelie_sticker2"));
        iconDB.addEmotionIcon(new EmotionIconItem("towelie","sticker3","loti","towelie_sticker3"));
        iconDB.addEmotionIcon(new EmotionIconItem("towelie","sticker4","loti","towelie_sticker4"));
        iconDB.addEmotionIcon(new EmotionIconItem("towelie","sticker5","loti","towelie_sticker5"));
        iconDB.addEmotionIcon(new EmotionIconItem("towelie","sticker6","loti","towelie_sticker6"));
        iconDB.addEmotionIcon(new EmotionIconItem("towelie","sticker7","loti","towelie_sticker7"));
        iconDB.addEmotionIcon(new EmotionIconItem("towelie","sticker8","loti","towelie_sticker8"));
        iconDB.addEmotionIcon(new EmotionIconItem("towelie","sticker9","loti","towelie_sticker9"));

        iconDB.addEmotionIcon(new EmotionIconItem("orangoutang","sticker0","loti","orangoutang_sticker0"));
        iconDB.addEmotionIcon(new EmotionIconItem("orangoutang","sticker1","loti","orangoutang_sticker1"));
        iconDB.addEmotionIcon(new EmotionIconItem("orangoutang","sticker2","loti","orangoutang_sticker2"));
        iconDB.addEmotionIcon(new EmotionIconItem("orangoutang","sticker3","loti","orangoutang_sticker3"));
        iconDB.addEmotionIcon(new EmotionIconItem("orangoutang","sticker4","loti","orangoutang_sticker4"));
        iconDB.addEmotionIcon(new EmotionIconItem("orangoutang","sticker5","loti","orangoutang_sticker5"));

        iconDB.addEmotionIcon(new EmotionIconItem("pepetopanim","sticker0","loti","pepetopanim_sticker0"));
        iconDB.addEmotionIcon(new EmotionIconItem("pepetopanim","sticker1","loti","pepetopanim_sticker1"));
        iconDB.addEmotionIcon(new EmotionIconItem("pepetopanim","sticker2","loti","pepetopanim_sticker2"));
        iconDB.addEmotionIcon(new EmotionIconItem("pepetopanim","sticker3","loti","pepetopanim_sticker3"));
        iconDB.addEmotionIcon(new EmotionIconItem("pepetopanim","sticker4","loti","pepetopanim_sticker4"));
        iconDB.addEmotionIcon(new EmotionIconItem("pepetopanim","sticker5","loti","pepetopanim_sticker5"));
        iconDB.addEmotionIcon(new EmotionIconItem("pepetopanim","sticker6","loti","pepetopanim_sticker6"));
        iconDB.addEmotionIcon(new EmotionIconItem("pepetopanim","sticker7","loti","pepetopanim_sticker7"));

    }

    @Override
    public void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}