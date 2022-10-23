package com.pfiev.englishcontest.adapter;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.firestore.NotificationCollection;
import com.pfiev.englishcontest.model.BaseFriendListItem;
import com.pfiev.englishcontest.model.UserItem;
import com.pfiev.englishcontest.model.WaitingItem;
import com.pfiev.englishcontest.realtimedb.WaitingList;
import com.pfiev.englishcontest.ui.dialog.CustomToast;
import com.pfiev.englishcontest.ui.wiget.RoundedAvatarImageView;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter {
    private List<UserItem> usersList;
    private Context mContext;

    private String lastNameDisplay;

    public UsersListAdapter(Context mContext) {
        this.mContext = mContext;
        usersList = new ArrayList<>();
    }

    public void addData(List<UserItem> newList) {
        if (usersList != null) {
            int oldSize = usersList.size();
            usersList.addAll(newList);
            notifyItemRangeInserted(oldSize, usersList.size());
        } else this.usersList = newList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutView = R.layout.recycler_view_friends_list_all_users;
        View allUsersView = inflater.inflate(layoutView, parent, false);
        ViewHolderUsers viewHolder = new ViewHolderUsers(allUsersView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserItem user = usersList.get(position);
        if (holder instanceof UsersListAdapter.ViewHolderUsers) {
            ((UsersListAdapter.ViewHolderUsers) holder).avatar
                    .load(mContext, user.getUserPhotoUrl());
            ((UsersListAdapter.ViewHolderUsers) holder).userTextMore
                    .setText(user.getUserGender());
            ((UsersListAdapter.ViewHolderUsers) holder).username
                    .setText(user.getName());
            ((ViewHolderUsers) holder).holderId = user.getUserId();
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    /**
     * Get last name display
     * @return
     */
    public String getLastNameDisplay() {
        return lastNameDisplay;
    }

    /**
     * Set last name display
     * @param lastNameDisplay
     */
    public void setLastNameDisplay(String lastNameDisplay) {
        this.lastNameDisplay = lastNameDisplay;
    }

    /**
     * View holder for friends list type friends
     */
    public class ViewHolderUsers extends RecyclerView.ViewHolder {
        private View itemView;
        public RoundedAvatarImageView avatar;
        public TextView username;
        public TextView userTextMore;
        public ImageView action;
        public String holderId;
        private long mLastClickTime;
        private static final long MIN_CLICK_INTERVAL = 600;

        public ViewHolderUsers(View itemView) {
            super(itemView);
            itemView = itemView;
            avatar = itemView.findViewById(R.id.recycler_view_friends_user_avatar);
            username = itemView.findViewById(R.id.recycler_view_friends_user_name);
            userTextMore = itemView.findViewById(R.id.recycler_view_friends_user_text_more);
            action = itemView.findViewById(R.id.recycler_view_friends_view_detail);


            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long currentClickTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentClickTime - mLastClickTime;
                    mLastClickTime = currentClickTime;
                    // Return if double tap to prevent error in position;
                    if (elapsedTime <= MIN_CLICK_INTERVAL) return;

                    WaitingList waitingList = WaitingList.getInstance();
                    UserItem userItem = SharePreferenceUtils.getUserData(mContext);
                    waitingList.setUid(userItem.getUserId());
                    // New waiting Item
                    waitingList.sendInvitation(holderId, new WaitingItem(
                            userItem.getUserId(),
                            userItem.getName(),
                            userItem.getUserPhotoUrl(),
                            BaseFriendListItem.STATUS.ONLINE
                    )).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.getException() == null) {
                                NotificationCollection collection = new NotificationCollection();
                                collection.sendFriendInvitation(holderId);
                            }
                        }
                    });
                    String message = mContext.getString(R.string.toast_custom_invite_friend);
                    CustomToast.makeText(mContext, message, CustomToast.SUCCESS,
                            CustomToast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
