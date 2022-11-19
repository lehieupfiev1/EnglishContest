package com.pfiev.englishcontest.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pfiev.englishcontest.ExperimentalActivity;
import com.pfiev.englishcontest.GlobalConstant;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.firestore.FireStoreClass;
import com.pfiev.englishcontest.model.NotificationItem;
import com.pfiev.englishcontest.ui.experimental.FindingMatchFragment;
import com.pfiev.englishcontest.ui.wiget.RoundedAvatarImageView;

import java.time.Instant;

public class RequestCombatDialog extends Dialog implements
        View.OnClickListener {

    private NotificationItem notificationItem;
    public TextView acceptBtn, rejectBtn;
    private RoundedAvatarImageView avatar;
    private TextView username, timeRemaining;
    private int timeRemain;
    private CountDownTimer countDownTimer;

    private long mLastClickTime;
    private static final long MIN_CLICK_INTERVAL = 600;

    public RequestCombatDialog(@NonNull Context context) {
        super(context);
    }

    public void setData(NotificationItem notificationItem) {
        this.notificationItem = notificationItem;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_request_combat);
        // Need this to display background corner radius
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        avatar = findViewById(R.id.dialog_request_combat_avatar);
        avatar.load(getContext(), notificationItem.getUserPhotoUrl());

        username = findViewById(R.id.dialog_request_combat_username);
        username.setText(notificationItem.getName());

        acceptBtn = findViewById(R.id.dialog_request_combat_accept_btn);
        rejectBtn = findViewById(R.id.dialog_request_combat_reject_btn);
        acceptBtn.setOnClickListener(this);
        rejectBtn.setOnClickListener(this);

        timeRemaining = findViewById(R.id.dialog_request_combat_time_remaining);
        this.setOnShowListener(dialogInterface -> {
            float timePassed = Instant.now().getEpochSecond() - notificationItem.getTimestamp();
            timeRemain = Math.round(GlobalConstant.REQUEST_COMBAT_WAITING_JOIN_TIME - timePassed);
            timeRemain = timeRemain * 1000;
            countDownTimer = new CountDownTimer(timeRemain, 1000) {
                @Override
                public void onTick(long l) {
                    String mess = getContext().getString(
                            R.string.dialog_request_combat_time_remaining, "" + l/1000);
                    timeRemaining.setText(mess);
                }

                @Override
                public void onFinish() {
                    RequestCombatDialog.this.dismiss();
                }
            };
            countDownTimer.start();
        });
    }

    @Override
    public void onClick(View view) {
        if (isDoubleClick()) return;
        switch (view.getId()) {
            case R.id.dialog_request_combat_accept_btn:
                countDownTimer.cancel();
                Intent intent = new Intent(getContext(), ExperimentalActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(
                        ExperimentalActivity.START_FRAGMENT_KEY,
                        ExperimentalActivity.FRAGMENT.FINDING_MATCH);
                bundle.putString(
                        FindingMatchFragment.MATCH_ID_FIELD,
                        notificationItem.getMatchId()
                );
                bundle.putBoolean(FindingMatchFragment.IS_OWNER_FIELD, false);
                intent.putExtras(bundle);
                getContext().startActivity(intent, bundle);
                break;
            case R.id.dialog_request_combat_reject_btn:
                FireStoreClass.rejectCombatRequest(notificationItem.getMatchId());
                countDownTimer.cancel();
                break;
            default:
                break;
        }
        dismiss();
    }

    private boolean isDoubleClick() {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        mLastClickTime = currentClickTime;
        // Return if double tap to prevent error in position;
        return elapsedTime <= MIN_CLICK_INTERVAL;
    }

}
