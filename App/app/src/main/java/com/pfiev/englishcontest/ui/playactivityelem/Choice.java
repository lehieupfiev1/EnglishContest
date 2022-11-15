package com.pfiev.englishcontest.ui.playactivityelem;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.utils.Utility;

public class Choice extends androidx.appcompat.widget.AppCompatTextView {
    private long orderIndex;
    private int mState;
    private Context context;
    private final int DEFAULT_ANIM_DURATION = 2000;
    private final int DEFAULT_ANIM_RATE = 400;

    public static class STATE {
        public static final int DEFAULT = 0;
        public static final int SELECTED = 1;
        public static final int RIGHT = 2;
        public static final int WRONG = 3;
    }

    public Choice(Context context) {
        super(context);
        init(context);
    }

    public Choice(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Choice(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Init params when create
     *
     * @param mContext context
     */
    private void init(Context mContext) {
        context = mContext;
        setDisplayParams();
    }

    /**
     * Set display parameters like margin, padding, ...
     */
    private void setDisplayParams() {
        setDecoration(STATE.DEFAULT);
        // Set layout padding and margin
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginBottom = getDimension(R.dimen.play_activity_choice_margin_bottom);
        int paddingVertical = getDimension(R.dimen.play_activity_choice_pd_vertical);
        int paddingHorizontal = getDimension(R.dimen.play_activity_choice_pd_horizontal);
        params.setMargins(0, 0, 0, marginBottom);
        this.setPadding(
                paddingVertical, paddingHorizontal, paddingVertical, paddingHorizontal
        );
        setLayoutParams(params);
        this.requestLayout();
        // Set text color, set text size
        setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getDimension(R.dimen.play_activity_choice_text_size));
        // Set default font with mali_bold
        Typeface typeface = getResources().getFont(R.font.mali_bold);
        setTypeface(typeface);
        // Set text gravity
        setGravity(Gravity.CENTER_HORIZONTAL);
    }


    public void setContentDisplay(String content) {
        this.setText(content);
    }

    public long getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(long orderIndex) {
        this.orderIndex = orderIndex;
    }

    /**
     * Delete margin bottom
     */
    public void deleteMarginBottom() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.bottomMargin = 0;
        this.setLayoutParams(params);
    }

    /**
     * Set action when click
     *
     * @param callback callback when click
     */
    public void setOnClickCallback(ChoiceClick callback) {
        setOnClickListener(view -> {
            setDecoration(STATE.SELECTED);
            if (callback != null) callback.run(this);
        });
    }

    /**
     * Set text view ui
     *
     * @param state state of tv
     */
    public void setDecoration(int state) {
        int resBackground = -1;
        int resTextColor = -1;
        mState = state;
        switch (state) {
            case STATE.DEFAULT:
                resBackground = R.drawable.bg_play_activity_choice;
                resTextColor = R.color.play_activity_choice_text;
                break;
            case STATE.SELECTED:
                resBackground = R.drawable.bg_play_activity_choice_selected;
                resTextColor = R.color.play_activity_choice_selected_text;
                break;
            case STATE.RIGHT:
                resBackground = R.drawable.bg_play_activity_choice_right;
                resTextColor = R.color.play_activity_choice_right_text;
                break;
            case STATE.WRONG:
                resBackground = R.drawable.bg_play_activity_choice_wrong;
                resTextColor = R.color.play_activity_choice_wrong_text;
                break;
            default:
                break;
        }
        if (resBackground != -1)
            setBackground(AppCompatResources.getDrawable(context, resBackground));
        if (resTextColor != -1)
            setTextColor(ContextCompat.getColor(context, resTextColor));
    }

    /**
     * Show blink effect when change to state with duration and refresh rate use default
     * @param newState new state
     */
    public void showBlinkEffectToState(int newState) {
        showBlinkEffectToState(newState, DEFAULT_ANIM_DURATION, DEFAULT_ANIM_RATE);
    }

    /**
     * Show blink effect when change to state
     * @param newState new sate
     * @param duration animation duration
     * @param rate refresh rate
     */
    public void showBlinkEffectToState(int newState, int duration, int rate) {
        new CountDownTimer(duration, rate) {
            int currentState = mState;
            final int oldState = mState;

            @Override
            public void onTick(long l) {
                currentState = (currentState != newState) ? newState : oldState;
                setDecoration(currentState);
            }

            @Override
            public void onFinish() {
                setDecoration(newState);
            }
        }.start();
    }

    /**
     * Get dimension
     *
     * @param resId resource id
     * @return dimension
     */
    private int getDimension(int resId) {
        return Utility.getDimensionFromRes(context, resId);
    }

    public static interface ChoiceClick {
        public void run(Choice choice);
    }
}
