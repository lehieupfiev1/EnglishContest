package com.pfiev.englishcontest.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.pfiev.englishcontest.ExperimentalActivity;
import com.pfiev.englishcontest.GlobalConstant;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.firestore.FireStoreClass;
import com.pfiev.englishcontest.model.BaseFriendListItem;
import com.pfiev.englishcontest.model.FriendItem;
import com.pfiev.englishcontest.realtimedb.FriendList;
import com.pfiev.englishcontest.ui.experimental.FindingMatchFragment;
import com.pfiev.englishcontest.ui.wiget.RoundedAvatarImageView;
import com.pfiev.englishcontest.utils.TextUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FriendListAdapter extends RecyclerView.Adapter
        implements FriendList.ItemEventListener {
    private String TAG = "FriendListAdapter";

    private List<FriendItem> friendsLists;
    private Context mContext;
    private RecyclerView recyclerView;

    public FriendListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setRecyclerView(RecyclerView mRecyclerView) {
        this.recyclerView = mRecyclerView;
    }

    public void setData(List friendsLists) {
        this.friendsLists = friendsLists;
        Log.d("initialize friendlist", ""+ this.friendsLists.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutView = R.layout.recycler_view_friends_list_friends;
        View friendListView = inflater.inflate(layoutView, parent, false);
        ViewHolderFriends viewHolder = new ViewHolderFriends(friendListView);
        return viewHolder;
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

    public void swapItem(List<FriendItem> mList, int fromPosition, int toPosition) {
        mList.add(toPosition, mList.remove(fromPosition));
//        Collections.swap(mList, fromPosition, toPosition);
        notifyDataSetChanged();
//        notifyItemMoved(fromPosition, toPosition);
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

        notifyItemChanged(position);
        if (data.getStatus().equals(FriendItem.STATUS.ONLINE)) {
            swapItem(mFriendsList, position, 0);
            position = 0;
        }
        ViewHolderFriends viewHolderFriends = ((ViewHolderFriends)
                recyclerView.findViewHolderForAdapterPosition(position));
        if (viewHolderFriends != null) {
            if (data.getStatus().equals(FriendItem.STATUS.ONLINE)) {
                viewHolderFriends.requestCombatBtn.setVisibility(View.VISIBLE);

            } else {
                viewHolderFriends.requestCombatBtn.setVisibility(View.INVISIBLE);
            }
        }
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

        private ViewHolderFriends instance;
        public String holderUid;

        public ViewHolderFriends(View itemView) {
            super(itemView);
            instance = this;
            avatar = itemView.findViewById(R.id.recycler_view_friends_user_avatar);
            username = itemView.findViewById(R.id.recycler_view_friends_user_name);
            userTextMore = itemView.findViewById(R.id.recycler_view_friends_user_text_more);
            requestCombatBtn = itemView.findViewById(R.id.recycler_view_friends_request_combat);
            unfriendBtn = itemView.findViewById(R.id.recycler_view_friends_unfriend);


            requestCombatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FireStoreClass.requestCombat(holderUid).addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Exception e = task.getException();
                                if (e instanceof FirebaseFunctionsException) {
                                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                    FirebaseFunctionsException.Code code = ffe.getCode();
                                    Object details = ffe.getDetails();
                                }
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
                        }
                    });
                }
            });

            unfriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    String mess = view.getResources().
                            getString(R.string.friend_list_unfriend_confirm_mess);
                    builder.setMessage(mess + " " + username.getText().toString() + "?");
                    builder.setPositiveButton(
                            R.string.friend_list_unfriend_confirm_accept,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FriendList.getInstance().deleteFriend(holderUid);
                                    int position = instance.getAdapterPosition();
                                    FriendListAdapter.this.friendsLists.remove(position);
                                    notifyItemRemoved(position);
                                }
                            }
                    ).setNegativeButton(
                            R.string.friend_list_unfriend_confirm_reject,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }
                    ).show();
                }
            });
        }
    }
}
