package com.pfiev.englishcontest.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.model.BaseFriendListItem;
import com.pfiev.englishcontest.model.BlockItem;
import com.pfiev.englishcontest.model.FriendItem;
import com.pfiev.englishcontest.model.UserItem;
import com.pfiev.englishcontest.model.WaitingItem;
import com.pfiev.englishcontest.realtimedb.BlockList;
import com.pfiev.englishcontest.realtimedb.FriendList;
import com.pfiev.englishcontest.realtimedb.WaitingList;
import com.pfiev.englishcontest.ui.dialog.CustomToast;
import com.pfiev.englishcontest.ui.dialog.MakeFriendDialog;
import com.pfiev.englishcontest.ui.wiget.RoundedAvatarImageView;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaitingListAdapter extends RecyclerView.Adapter
        implements WaitingList.ItemEventListener {
    private List<WaitingItem> waitingList;
    private Context mContext;
    private String startAtName;
    private Long startAtTime;

    public WaitingListAdapter(Context mContext) {
        this.mContext = mContext;
        startAtName = "";
        startAtTime = 0l;
    }

    public void addData(List<WaitingItem> newList) {
        if (waitingList != null) {
            int oldSize = waitingList.size();
            waitingList.addAll(newList);
            notifyItemRangeInserted(oldSize, waitingList.size());
        } else this.waitingList = newList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutView = R.layout.recycler_view_friends_list_waiting;
        View studentView = inflater.inflate(layoutView, parent, false);
        ViewHolderWaiting viewHolder = new ViewHolderWaiting(studentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        WaitingItem friend = waitingList.get(position);
        if (holder instanceof ViewHolderWaiting) {
            ((ViewHolderWaiting) holder).holderUid = friend.getUid();
            ((ViewHolderWaiting) holder).avatar.load(mContext, friend.getUserPhotoUrl());
            ((ViewHolderWaiting) holder).userTextMore.setText(friend.getStatus());
            ((ViewHolderWaiting) holder).username.setText(friend.getName());
        }
    }

    @Override
    public int getItemCount() {
        if (waitingList != null)
            return waitingList.size();
        else return 0;
    }

    public void swapItem(int fromPosition, int toPosition) {
        Collections.swap(this.waitingList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public int getItemPositionByUid(String uid) {
        if (this.waitingList != null)
            if (this.waitingList.size() > 0) {
                for (int i = 0; i < waitingList.size(); i++) {
                    if (waitingList.get(i).getUid().equals(uid))
                        return i;
                }
            }
        return -1;
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot) {
        WaitingItem data = snapshot.getValue(WaitingItem.class);
        int position = getItemPositionByUid(data.getUid());
        this.waitingList.set(position, data);
        notifyItemChanged(position);
        if (data.getStatus().equals(WaitingItem.STATUS.ONLINE)) {
            swapItem(position, 0);
        }
    }

    /**
     * View holder for waiting list
     */
    public class ViewHolderWaiting extends RecyclerView.ViewHolder
            implements MakeFriendDialog.OnAcceptCallBack, MakeFriendDialog.OnRejectCallBack {
        public RoundedAvatarImageView avatar;
        public TextView username;
        public TextView userTextMore;
        public ImageView replyBtn;
        public ImageView blockBtn;

        public String holderUid;
        private WaitingListAdapter.ViewHolderWaiting instance;
        private long mLastClickTime;
        private static final long MIN_CLICK_INTERVAL = 600;

        public ViewHolderWaiting(View itemView) {
            super(itemView);
            itemView = itemView;
            avatar = itemView.findViewById(R.id.recycler_view_friends_user_avatar);
            username = itemView.findViewById(R.id.recycler_view_friends_user_name);
            userTextMore = itemView.findViewById(R.id.recycler_view_friends_user_text_more);
            replyBtn = itemView.findViewById(R.id.recycler_view_friends_waiting_reply);
            blockBtn = itemView.findViewById(R.id.recycler_view_friends_waiting_block);
            instance = this;

            /**
             * reply friends invitation
             */
            replyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isDoubleClick()) return;
                    MakeFriendDialog makeFriendDialog = new MakeFriendDialog(mContext);
                    makeFriendDialog.setData(new FriendItem(
                            holderUid,
                            username.getText().toString(),
                            avatar.getSourceUrl(),
                            BaseFriendListItem.STATUS.OFFLINE
                    ));
                    makeFriendDialog.setAcceptCallBack(ViewHolderWaiting.this);
                    makeFriendDialog.setRejectCallBack(ViewHolderWaiting.this);
                    makeFriendDialog.show();
                }
            });
            /**
             * block user
             */
            blockBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isDoubleClick()) return;
                    BlockItem blockItem = new BlockItem(
                            holderUid,
                            username.getText().toString(),
                            avatar.getSourceUrl(),
                            BaseFriendListItem.STATUS.OFFLINE

                    );
                    blockItem.setTimestamp(System.currentTimeMillis() / 1000);
                    // Set uid to insert new
                    BlockList.getInstance().setUid(
                            SharePreferenceUtils.getUserData(mContext).getUserId()
                    );
                    BlockList.getInstance().addBlockUser(holderUid, blockItem);
                    // Delete invitation
                    WaitingList.getInstance().deleteInvitation(holderUid);
                    deleteItemInList();
                    String message = mContext.getString(R.string.toast_custom_block_message);
                    CustomToast.makeText(mContext, message, CustomToast.WARNING,
                            CustomToast.LENGTH_SHORT).show();
                }
            });
        }

        private boolean isDoubleClick() {
            long currentClickTime = SystemClock.uptimeMillis();
            long elapsedTime = currentClickTime - mLastClickTime;
            mLastClickTime = currentClickTime;
            // Return if double tap to prevent error in position;
            if (elapsedTime <= MIN_CLICK_INTERVAL) return true;
            return false;
        }

        private void deleteItemInList() {
            int position = instance.getAdapterPosition();
            WaitingListAdapter.this.waitingList.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public void acceptCallback() {
            WaitingList.getInstance().deleteInvitation(holderUid);
            deleteItemInList();
            String message = mContext.getString(R.string.toast_custom_accept_friend);
            CustomToast.makeText(mContext, message, CustomToast.SUCCESS,
                    CustomToast.LENGTH_SHORT).show();
        }

        @Override
        public void rejectCallback() {
            WaitingList.getInstance().deleteInvitation(holderUid);
            deleteItemInList();
            String message = mContext.getString(R.string.toast_custom_reject_friend);
            CustomToast.makeText(mContext, message, CustomToast.SUCCESS,
                    CustomToast.LENGTH_SHORT).show();
        }
    }

    public String getStartAtName() {
        return startAtName;
    }

    public void setStartAtName(String startAtName) {
        this.startAtName = startAtName;
    }

    public Long getStartAtTime() {
        return startAtTime;
    }

    public void setStartAtTime(Long startAtTime) {
        this.startAtTime = startAtTime;
    }


}
