package com.pfiev.englishcontest.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.model.FriendItem;
import com.pfiev.englishcontest.model.UserItem;
import com.pfiev.englishcontest.realtimedb.FriendList;
import com.pfiev.englishcontest.ui.wiget.RoundedAvatarImageView;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

public class UnFriendDialog extends Dialog implements
        View.OnClickListener {

    private FriendItem friendItem;
    private Context mContext;
    public TextView acceptBtn, rejectBtn;
    public String friendId;
    private RoundedAvatarImageView avatar;
    private TextView username;

    private long mLastClickTime;
    private static final long MIN_CLICK_INTERVAL = 600;

    private OnAcceptCallBack onAcceptCb;
    private OnRejectCallBack onRejectCb;

    public UnFriendDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public void setData(FriendItem friendItem) {
        this.friendItem = friendItem;
    }

    public void setAcceptCallBack(OnAcceptCallBack onAcceptCb) {
        this.onAcceptCb = onAcceptCb;
    }

    public void setRejectCallBack(OnRejectCallBack onRejectCb) {
        this.onRejectCb = onRejectCb;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_unfriend);
        // Need this to display background corner radius
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        avatar = findViewById(R.id.dialog_unfriend_avatar);
        avatar.load(getContext(), friendItem.getUserPhotoUrl());
        username = findViewById(R.id.dialog_unfriend_username);
        username.setText(friendItem.getName());
        friendId = friendItem.getUid();

        acceptBtn = findViewById(R.id.dialog_unfriend_accept_btn);
        rejectBtn = findViewById(R.id.dialog_unfriend_reject_btn);
        acceptBtn.setOnClickListener(this);
        rejectBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        UserItem ownItem = SharePreferenceUtils.getUserData(mContext);
        if (isDoubleClick()) return;
        switch (view.getId()) {
            case R.id.dialog_unfriend_accept_btn:
                FriendList.getInstance().deleteFriend(ownItem.getUserId(), friendItem.getUid());
                if (onAcceptCb != null) onAcceptCb.acceptCallback();
                break;
            case R.id.dialog_unfriend_reject_btn:
                if (onRejectCb != null) onRejectCb.rejectCallback();
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

    public interface OnAcceptCallBack {
        public void acceptCallback();
    }

    public interface OnRejectCallBack {
        public void rejectCallback();
    }
}
