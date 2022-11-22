package com.pfiev.englishcontest.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.pfiev.englishcontest.ExperimentalActivity;
import com.pfiev.englishcontest.GlobalConstant;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.firestore.FireStoreClass;
import com.pfiev.englishcontest.model.BaseFriendListItem;
import com.pfiev.englishcontest.model.FriendItem;
import com.pfiev.englishcontest.realtimedb.FriendList;
import com.pfiev.englishcontest.ui.dialog.CustomToast;
import com.pfiev.englishcontest.ui.dialog.UnFriendDialog;
import com.pfiev.englishcontest.ui.experimental.FindingMatchFragment;
import com.pfiev.englishcontest.ui.wiget.RoundedAvatarImageView;
import com.pfiev.englishcontest.utils.TextUtils;

import java.util.List;
import java.util.Map;

public class FriendListAdapter extends RecyclerView.Adapter
        implements FriendList.ItemEventListener {
    private final String TAG = "FriendListAdapter";

    private List<FriendItem> friendsLists;
    private Context mContext;
    private RecyclerView recyclerView;
    private Runnable startLoadingAnim;
    private Runnable endLoadingAnim;

    public FriendListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setRecyclerView(RecyclerView mRecyclerView) {
        this.recyclerView = mRecyclerView;
    }

    public void setData(List friendsLists) {
        this.friendsLists = friendsLists;
        Log.d("initialize friendlist", "" + this.friendsLists.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutView = R.layout.recycler_view_friends_list_friends;
        View friendListView = inflater.inflate(layoutView, parent, false);
        return new ViewHolderFriends(friendListView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FriendItem friend = friendsLists.get(position);
        if (holder instanceof ViewHolderFriends) {
            ((ViewHolderFriends) holder).holderUid = friend.getUid();
            ((ViewHolderFriends) holder).avatar.load(mContext, friend.getUserPhotoUrl());
            ((ViewHolderFriends) holder).userTextMore.setText(friend.getStatus());
            ((ViewHolderFriends) holder).username.setText(friend.getName());
            if (friend.getStatus().equals(BaseFriendListItem.STATUS.ONLINE)) {
                ((ViewHolderFriends) holder).requestCombatBtn.setVisibility(View.VISIBLE);
            } else ((ViewHolderFriends) holder).requestCombatBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return friendsLists.size();
    }

    public void swapItem(List<FriendItem> mList, int fromPosition, int toPosition, FriendItem item) {
        mList.remove(fromPosition);
        mList.add(toPosition, item);
//        Collections.swap(mList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public int getItemPositionByUid(List<FriendItem> mList, String uid) {
        if (mList != null)
            if (mList.size() > 0) {
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).getUid().equals(uid))
                        return i;
                }
            }
        return -1;
    }

    public List<FriendItem> getFriendsLists() {
        return this.friendsLists;
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot) {
        FriendItem data = snapshot.getValue(FriendItem.class);
        List<FriendItem> mFriendsList = ((FriendListAdapter) recyclerView.getAdapter())
                .getFriendsLists();
        int position = getItemPositionByUid(mFriendsList, data.getUid());
        mFriendsList.set(position, data);
        notifyItemChanged(position, data);

        ViewHolderFriends viewHolderFriends = ((ViewHolderFriends)
                recyclerView.findViewHolderForAdapterPosition(position));
        if (viewHolderFriends != null) {
            position = viewHolderFriends.getAdapterPosition();
            if (data.getStatus().equals(FriendItem.STATUS.ONLINE)) {
                viewHolderFriends.requestCombatBtn.setVisibility(View.VISIBLE);
                swapItem(mFriendsList, position, 0, data);
            } else {
                viewHolderFriends.requestCombatBtn.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setLongRequestStartAnim(Runnable runnable) {
        startLoadingAnim = runnable;
    }

    public void setLongRequestEndAnim(Runnable runnable) {
        endLoadingAnim = runnable;
    }

    /**
     * View holder for friends list type friends
     */
    public class ViewHolderFriends extends RecyclerView.ViewHolder {
        public RoundedAvatarImageView avatar;
        public TextView username;
        public TextView userTextMore;
        public ImageView requestCombatBtn;
        public ImageView unfriendBtn;
        public String holderUid;

        private ViewHolderFriends instance;

        public ViewHolderFriends(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.recycler_view_friends_user_avatar);
            username = itemView.findViewById(R.id.recycler_view_friends_user_name);
            userTextMore = itemView.findViewById(R.id.recycler_view_friends_user_text_more);
            requestCombatBtn = itemView.findViewById(R.id.recycler_view_friends_request_combat);
            unfriendBtn = itemView.findViewById(R.id.recycler_view_friends_unfriend);
            instance = this;


            requestCombatBtn.setOnClickListener(view -> {
                startLoadingAnim.run();
                FireStoreClass.requestCombat(holderUid).addOnCompleteListener(task -> {
                    endLoadingAnim.run();
                    if (!task.isSuccessful()) {
                        Exception e = task.getException();
                        Log.i(TAG, " task is not success :" + e.getMessage());
                    } else {
                        Log.i(TAG, " Result request match :" + task.getResult());
                        Map data = TextUtils.convertHashMap(task.getResult());
                        Intent intent = new Intent(mContext, ExperimentalActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(
                                ExperimentalActivity.START_FRAGMENT_KEY,
                                ExperimentalActivity.FRAGMENT.FINDING_MATCH);
                        bundle.putString(
                                FindingMatchFragment.MATCH_ID_FIELD,
                                (String) data.get(GlobalConstant.MATCH_ID)
                        );
                        bundle.putBoolean(FindingMatchFragment.IS_OWNER_FIELD, true);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent, bundle);
                    }
                });
            });

            unfriendBtn.setOnClickListener(view -> {

                UnFriendDialog dialog = new UnFriendDialog(mContext);
                dialog.setData(new FriendItem(
                        holderUid,
                        username.getText().toString(),
                        avatar.getSourceUrl(),
                        BaseFriendListItem.STATUS.ONLINE
                ));
                dialog.setAcceptCallBack(() -> {
                    String message = mContext.getString(
                            R.string.toast_custom_unfriend_success);
                    CustomToast.makeText(mContext, message, CustomToast.WARNING,
                            CustomToast.LENGTH_SHORT).show();
                    deleteItemInList();
                });
                dialog.show();
            });
        }

        private void deleteItemInList() {
            int position = instance.getAdapterPosition();
            FriendListAdapter.this.friendsLists.remove(position);
            notifyItemRemoved(position);
        }
    }
}
