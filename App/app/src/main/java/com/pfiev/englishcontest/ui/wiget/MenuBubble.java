package com.pfiev.englishcontest.ui.wiget;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.pfiev.englishcontest.R;

public class MenuBubble extends ConstraintLayout {
    private ConstraintLayout mainBubble;
    private final static long ANIM_DURATION = 500;

    public MenuBubble(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.custom_layout_menu_bubble, this);
        mainBubble = (ConstraintLayout) findViewById(R.id.menu_bubble_container);
        // Get custom attrs
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenuBubble);

        // Get sys username and set text
        String sysUsername = a.getString(R.styleable.MenuBubble_sysUserName);
        if (sysUsername != null && !sysUsername.isEmpty())
            ((TextView) findViewById(R.id.menu_bubble_sys_name)).setText(sysUsername);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)
                mainBubble.getLayoutParams();

        // Get and set custom width, margin end, padding horizontal for bubble container
        int bubbleWidth = a.getDimensionPixelSize(R.styleable.MenuBubble_bubble_width, 0);
        int bubbleMarginEnd = a.getDimensionPixelSize(R.styleable.MenuBubble_bubble_margin_end, 0);
        int bubblePaddingHorizontal = a.getDimensionPixelSize(
                R.styleable.MenuBubble_bubble_inner_elem_padding_horizontal, 0);
        int bubblePaddingBottom = a.getDimensionPixelSize(
                R.styleable.MenuBubble_bubble_inner_elem_padding_bottom, 0);

        if (bubbleWidth != 0) layoutParams.width = bubbleWidth;
        if (bubbleMarginEnd != 0) layoutParams.setMarginEnd(bubbleMarginEnd);
        mainBubble.setLayoutParams(layoutParams);
        if (bubblePaddingHorizontal != 0)
            mainBubble.setPadding(bubblePaddingHorizontal, 0,
                    bubblePaddingHorizontal, bubblePaddingBottom);
        mainBubble.requestLayout();

        // Get avatar
        Drawable sysUserAvatar = a.getDrawable(R.styleable.MenuBubble_sysUserAvatar);
        if (sysUserAvatar != null)
            ((RoundedAvatarImageView) findViewById(R.id.menu_bubble_sys_avatar))
                    .setImageDrawable(sysUserAvatar);

        // Show hide avatar
        boolean hideAvatar = a.getBoolean(R.styleable.MenuBubble_sysUserAvatarHide, false);
        if (hideAvatar)
            findViewById(R.id.menu_bubble_sys_avatar)
                    .setVisibility(INVISIBLE);

        a.recycle();
    }

    public MenuBubble(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        this(context, attrs);

    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mainBubble == null) {
            super.addView(child, index, params);
        } else {
            //Forward these calls to the content view
            mainBubble.addView(child, index, params);
        }
    }

    public void addViewToMainContent(View child) {
        mainBubble.addView(child);
    }

    public ConstraintLayout getMainContainer() {
        return mainBubble;
    }

    public void fadeOut(FadeOutCallback callback) {
        this.animate().alpha(0f).setDuration(ANIM_DURATION).withEndAction(
                new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) callback.onEnd();
                    }
                }
        );
    }

    public void fadeIn() {
        this.setAlpha(0f);
        this.animate().alpha(1f).setDuration(ANIM_DURATION);
    }

    public static interface FadeOutCallback {
        public void onEnd();
    }
}
