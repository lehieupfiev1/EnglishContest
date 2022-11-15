package com.pfiev.englishcontest.ui.playactivityelem;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.ui.wiget.RoundedAvatarImageView;
import com.pfiev.englishcontest.utils.Utility;

public class OrderRow extends ConstraintLayout {
    public static final int VIEW_PARAMS_SCORE = 1;
    public static final int VIEW_PARAMS_NAME = 2;
    private final RoundedAvatarImageView avatar;
    private final TextView userParams;
    // Order id to know position, smaller is higher
    public int orderId = 1;
    // Need that set up last Y Axis
    public boolean hasSetLastYAxis = false;
    public float lastYAxis = 0;
    // Animate duration, default is 500 milliseconds
    private int animateDuration = 500;
    // User info
    private final UserInfo userInfo;

    public OrderRow(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ConstraintLayout mainLayout = (ConstraintLayout) LayoutInflater.from(context)
                .inflate(R.layout.play_game_order_row, this);
        avatar = mainLayout.findViewById(R.id.order_row_avatar);
        userParams = mainLayout.findViewById(R.id.order_row_user_params);
        // Set id to view element
        avatar.setId(View.generateViewId());
        userParams.setId(View.generateViewId());
        this.setId(View.generateViewId());
        userInfo = new UserInfo();

    }

    /**
     * Set avatar to display
     *
     * @param context   context
     * @param avatarUrl avatar of user
     */
    public void setAvatar(Context context, String avatarUrl) {
        avatar.load(context, avatarUrl);
    }

    /**
     * Set user params to display
     *
     * @param text display text
     */
    public void setUserParams(String text) {
        userParams.setText(text);
    }

    /**
     * Set user params display username or score
     * @param context context
     * @param type type display
     */
    public void showUserParams(Context context, int type) {
        if (type == VIEW_PARAMS_SCORE)
            userParams.setText(
                    context.getString(R.string.play_activity_user_score_params,
                            userInfo.totalRightAnswer, userInfo.totalTimeAnswer)
            );
        if (type == VIEW_PARAMS_NAME)
            userParams.setText(userInfo.name);
    }

    /**
     * Swap position of two order row
     *
     * @param swapRow the swap row
     */
    public void swapPosition(OrderRow swapRow) {
        // swap value of orderId
        int temp = orderId;
        orderId = swapRow.orderId;
        swapRow.orderId = temp;
        // swap view position
        // set last Y axis first time if not set
        if (!hasSetLastYAxis) {
            hasSetLastYAxis = true;
            lastYAxis = getY();
        }
        if (!swapRow.hasSetLastYAxis) {
            swapRow.hasSetLastYAxis = true;
            swapRow.lastYAxis = swapRow.getY();
        }
        float xSwapAxis = swapRow.getX();
        float xCurAxis = this.getX();
        // swap last axis
        float tempAxis = lastYAxis;
        this.lastYAxis = swapRow.lastYAxis;
        swapRow.lastYAxis = tempAxis;
        // Translation
        this.animate().x(xSwapAxis).y(lastYAxis)
                .setDuration(animateDuration).start();
        swapRow.animate().x(xCurAxis).y(swapRow.lastYAxis)
                .setDuration(animateDuration).start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        lastYAxis = getY();
    }

    /**
     * Set animate duration
     *
     * @param duration duration by milliseconds
     */
    public void setAnimateDuration(int duration) {
        animateDuration = duration;
    }

    /**
     * Show new message bubble
     *
     * @param message new message
     */
    public void showNewMessage(MessageBubble message) {
        // Set view id to message
        message.setId(View.generateViewId());
        this.addView(message);
        // Set constraint to message display near avatar
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);
        constraintSet.connect(
                message.getId(), ConstraintSet.BOTTOM, this.getId(), ConstraintSet.TOP,
                getMessageMarginBottom());
        // Don't know why but can't constraint direct to avatar elem so must use this
        constraintSet.connect(
                message.getId(), ConstraintSet.START, this.getId(), ConstraintSet.START,
                avatar.getWidth() + getMessageMarginStart());
        constraintSet.applyTo(this);
        // Show animation
        userParams.setVisibility(INVISIBLE);
        message.fadeIn(null, () -> {
            userParams.setVisibility(VISIBLE);
            removeView(message);
        });
    }

    /**
     * Get message margin start
     *
     * @return margin
     */
    private int getMessageMarginStart() {
        return Utility.getDimensionFromRes(getContext(), R.dimen.message_bubble_margin_start);
    }

    /**
     * Get message margin start
     *
     * @return margin
     */
    private int getMessageMarginBottom() {
        return Utility.getDimensionFromRes(getContext(), R.dimen.message_bubble_margin_bottom);
    }

    /**
     * Get user
     *
     * @return user info
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    public class UserInfo {

        public UserInfo() {
        }

        // Uid of user
        public String uid;
        // User name
        public String name;
        // User's avatar url
        public String avatarUrl;
        // Total right answers
        private int totalRightAnswer = 0;
        // Total time answer
        private long totalTimeAnswer = 0;

//        /**
//         * Get total right answer
//         *
//         * @return total
//         */
//        public int getTotalRightAnswer() {
//            return totalRightAnswer;
//        }

        /**
         * Increase total right answer by one
         */
        public void addRightAnswer() {
            ++totalRightAnswer;
        }

//        /**
//         * Get total time answer
//         *
//         * @return total
//         */
//        public long getTotalTimeAnswer() {
//            return totalTimeAnswer;
//        }

        /**
         * Increase total time answer
         *
         * @param timeAnswer additional time
         */
        public void increaseTotalTimeAnswer(long timeAnswer) {
            totalTimeAnswer += timeAnswer;
        }

        /**
         * Check if has better result than competitor
         * @param competitor competitor info
         * @return int 1 if better, -1 if competitor is better and 0 is draw
         */
        public int hasBetterResult(UserInfo competitor) {
            if (this.totalRightAnswer > competitor.totalRightAnswer) return 1;
            if (this.totalRightAnswer < competitor.totalTimeAnswer) return -1;
            if (this.totalTimeAnswer < competitor.totalRightAnswer) return 1;
            if (this.totalTimeAnswer > competitor.totalRightAnswer) return -1;
            return 0;
        }

    }
}
