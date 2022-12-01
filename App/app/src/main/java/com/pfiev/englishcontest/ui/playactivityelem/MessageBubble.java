package com.pfiev.englishcontest.ui.playactivityelem;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.content.res.AppCompatResources;

import com.airbnb.lottie.LottieAnimationView;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.utils.Utility;

/**
 * Message bubble is wrap to message send by user
 */
public class MessageBubble extends RelativeLayout {

    private static final int ANIM_DURATION = 500;
    private int displayDuration = 3000;
    private CountDownTimer fadeInCountDown = null;

    public MessageBubble(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackground(AppCompatResources.getDrawable(context, R.drawable.bg_message_bubble));
        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        setLayoutParams(layoutParams);
        int padding = Utility.getDimensionFromRes(context, R.dimen.message_bubble_padding);
        setPadding(padding, padding, padding, padding);
    }

    /**
     * Add sticker lottie
     *
     * @param lottie sticker lottie type
     */
    public void addSticker(LottieAnimationView lottie) {
        LayoutParams layoutParams = new LayoutParams(
                getStickerLottieSize(true), getStickerLottieSize(false)
        );
        lottie.setLayoutParams(layoutParams);
        addView(lottie);
    }

    /**
     * Add sticker image
     *
     * @param imageView sticker type is image
     */
    public void addSticker(ImageView imageView) {

    }

    /**
     * Get sticker size
     * @return size
     */
    private int getStickerLottieSize(boolean getWidth) {
        int STICKER_SIZE = R.dimen.message_bubble_sticker_size_width;
        if (!getWidth) STICKER_SIZE = R.dimen.message_bubble_sticker_size_height;
        return Utility.getDimensionFromRes(getContext(), STICKER_SIZE);
    }

    /**
     * Fade out message display
     * @param callback call when complete animation
     */
    public void fadeOut(Runnable callback) {
        this.animate().alpha(0f).setDuration(ANIM_DURATION).withEndAction(
                () -> {
                    if (callback != null) callback.run();
                }
        );
    }

    /**
     * Fade in message and set count down to fade out
     * @param callback call when complete animation
     * @param fadeOutCallback call when fade out complete
     */
    public void fadeIn(Runnable callback, Runnable fadeOutCallback ) {
        this.setAlpha(0f);
        this.animate().alpha(1f).setDuration(ANIM_DURATION).withEndAction(
                () -> {
                    if (displayDuration > 0) {
                        fadeInCountDown = new CountDownTimer(displayDuration, 1000) {

                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                fadeOut(fadeOutCallback);
                            }
                        };
                        fadeInCountDown.start();
                    }
                    if (callback != null) callback.run();
                }
        );
    }

    /**
     * Get display duration
     * @return duration
     */
    public int getDisplayDuration() {
        return displayDuration;
    }

    /**
     * Set display duration
     * @param displayDuration duration
     */
    public void setDisplayDuration(int displayDuration) {
        this.displayDuration = displayDuration;
    }

    /**
     * Cancel fade in countdown
     */
    public void cancelFadeInCountDown() {
        if (fadeInCountDown != null) fadeInCountDown.cancel();
    }
}
