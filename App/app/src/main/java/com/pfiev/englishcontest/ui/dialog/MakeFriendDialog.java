package com.pfiev.englishcontest.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.model.BaseFriendListItem;
import com.pfiev.englishcontest.model.FriendItem;
import com.pfiev.englishcontest.model.NotificationItem;
import com.pfiev.englishcontest.model.UserItem;
import com.pfiev.englishcontest.realtimedb.FriendList;
import com.pfiev.englishcontest.realtimedb.Status;
import com.pfiev.englishcontest.realtimedb.WaitingList;
import com.pfiev.englishcontest.ui.wiget.RoundedAvatarImageView;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

import java.util.HashMap;
import java.util.Map;

public class MakeFriendDialog extends Dialog implements
        View.OnClickListener {

    private FriendItem friendItem;
    private Context mContext;
    public Button acceptBtn, laterBtn, rejectBtn;
    public String friendId;
    private RoundedAvatarImageView avatar;
    private TextView username;

    private long mLastClickTime;
    private static final long MIN_CLICK_INTERVAL = 600;

    private OnAcceptCallBack onAcceptCb;
    private OnRejectCallBack onRejectCb;

    public MakeFriendDialog(@NonNull Context context) {
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
        setContentView(R.layout.dialog_make_friend);

        avatar = (RoundedAvatarImageView) findViewById(R.id.dialog_make_friend_avatar);
        avatar.load(getContext(), friendItem.getUserPhotoUrl());
        username = (TextView) findViewById(R.id.dialog_make_friend_username);
        username.setText(friendItem.getName());
        friendId = friendItem.getUid();

        acceptBtn = (Button) findViewById(R.id.dialog_make_friend_accept_btn);
        laterBtn = (Button) findViewById(R.id.dialog_make_friend_later_btn);
        rejectBtn = (Button) findViewById(R.id.dialog_make_friend_reject_btn);
        acceptBtn.setOnClickListener(this);
        laterBtn.setOnClickListener(this);
        rejectBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (isDoubleClick()) return;
        UserItem ownItem = SharePreferenceUtils.getUserData(mContext);
        switch (view.getId()) {
            case R.id.dialog_make_friend_accept_btn:
                // Update status state of new friend
                Status.getInstance().getStatusByUid(friendId).addOnCompleteListener(
                        new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                // Put data to create two new friend item
                                friendItem.setStatus(task.getResult());
                                Map<String, FriendItem> friends = new HashMap<String, FriendItem>();
                                friends.put(ownItem.getUserId(), friendItem);
                                friends.put(friendId, new FriendItem(
                                        ownItem.getUserId(),
                                        ownItem.getName(),
                                        ownItem.getUserPhotoUrl(),
                                        BaseFriendListItem.STATUS.ONLINE
                                ));
                                FriendList.getInstance().addFriend(friends);
                                if (onAcceptCb != null) onAcceptCb.acceptCallback();
                            }
                        }
                );
                break;
            case R.id.dialog_make_friend_reject_btn:
                WaitingList.getInstance().setUid(ownItem.getUserId());
                WaitingList.getInstance().deleteInvitation(friendId);
                if (onRejectCb != null) onRejectCb.rejectCallback();
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
        if (elapsedTime <= MIN_CLICK_INTERVAL) return true;
        return false;
    }

    public interface OnAcceptCallBack {
        public void acceptCallback();
    }

    public interface OnRejectCallBack {
        public void rejectCallback();
    }
}
