package com.pfiev.englishcontest.adapter;

import android.content.Context;
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
import com.pfiev.englishcontest.model.BlockItem;
import com.pfiev.englishcontest.realtimedb.BlockList;
import com.pfiev.englishcontest.ui.wiget.RoundedAvatarImageView;

import java.util.Collections;
import java.util.List;

public class BlockListAdapter extends RecyclerView.Adapter
        implements BlockList.ItemEventListener {
    private List<BlockItem> blockList;
    private Context mContext;
    private String startAtName;
    private Long startAtTime;

    public BlockListAdapter(Context mContext) {
        this.mContext = mContext;
        startAtName = "";
        startAtTime = 0l;
    }

    public void addData(List<BlockItem> newList) {
        if (blockList != null) {
            int oldSize = blockList.size();
            blockList.addAll(newList);
            notifyItemRangeInserted(oldSize, blockList.size());
        } else this.blockList = newList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutView = R.layout.recycler_view_friends_list_block;
        View studentView = inflater.inflate(layoutView, parent, false);
        ViewHolderBlock viewHolder = new ViewHolderBlock(studentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BlockItem friend = blockList.get(position);
        if (holder instanceof ViewHolderBlock) {
            ((ViewHolderBlock) holder).holderUid = friend.getUid();
            ((ViewHolderBlock) holder).avatar.load(mContext, friend.getUserPhotoUrl());
            ((ViewHolderBlock) holder).userTextMore.setText(friend.getStatus());
            ((ViewHolderBlock) holder).username.setText(friend.getName());
        }
    }

    @Override
    public int getItemCount() {
        if (blockList != null)
            return blockList.size();
        else return 0;
    }

    public void swapItem(int fromPosition, int toPosition) {
        Collections.swap(this.blockList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public int getItemPositionByUid(String uid) {
        if (this.blockList != null)
            if (this.blockList.size() > 0) {
                for (int i = 0; i < blockList.size(); i++) {
                    if (blockList.get(i).getUid().equals(uid))
                        return i;
                }
            }
        return -1;
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot) {
        BlockItem data = snapshot.getValue(BlockItem.class);
        int position = getItemPositionByUid(data.getUid());
        this.blockList.set(position, data);
        notifyItemChanged(position);
        if (data.getStatus().equals(BlockItem.STATUS.ONLINE)) {
            swapItem(position, 0);
        }
    }

    /**
     * View holder for block list
     */
    public class ViewHolderBlock extends RecyclerView.ViewHolder {
        private View itemView;
        public RoundedAvatarImageView avatar;
        public TextView username;
        public TextView userTextMore;
        public ImageView unblockBtn;
        public String holderUid;
        private ViewHolderBlock instance;
        private long mLastClickTime;
        private static final long MIN_CLICK_INTERVAL = 600;

        public ViewHolderBlock(View itemView) {
            super(itemView);
            itemView = itemView;
            avatar = itemView.findViewById(R.id.recycler_view_friends_user_avatar);
            username = itemView.findViewById(R.id.recycler_view_friends_user_name);
            userTextMore = itemView.findViewById(R.id.recycler_view_friends_user_text_more);
            unblockBtn = itemView.findViewById(R.id.recycler_view_friends_user_unblock);
            instance = this;

            unblockBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long currentClickTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentClickTime - mLastClickTime;
                    mLastClickTime = currentClickTime;
                    // Return if double tap to prevent error in position;
                    if (elapsedTime <= MIN_CLICK_INTERVAL) return;

                    BlockList.getInstance().unBlockUser(holderUid);
                    int position = instance.getAdapterPosition();
                    BlockListAdapter.this.blockList.remove(position);
                    notifyItemRemoved(position);
                }
            });
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
