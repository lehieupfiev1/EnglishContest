package com.pfiev.englishcontest.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.model.PackEmotionItem;

import java.util.ArrayList;
import java.util.List;

public class PackEmotionViewAdapter extends RecyclerView.Adapter<PackEmotionViewAdapter.ViewHolder> {
    private List<PackEmotionItem> mListPack;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;
    List<LinearLayout> mListLayoutView = new ArrayList<>();
    private int row_select = -1;
    private boolean isRecent;

    // data is passed into the constructor
    public PackEmotionViewAdapter(Context context, List<PackEmotionItem> packEmotionItems, boolean isRecent) {
        Log.d("LeHieu", "PackEmotionViewAdapter init");
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mListPack = packEmotionItems;
        this.isRecent = isRecent;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.pack_emotion_item, parent, false);
        Log.d("LeHieu", "PackEmotionViewAdapter onCreateViewHolder ");
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d("LeHieu", "PackEmotionViewAdapter onBindViewHolder ");
        String urlImage = mListPack.get(position).getUrl();
        String urI = "@drawable/"+urlImage;
        Log.d("LeHieu", "PackEmotionViewAdapter urI "+urlImage + " "+urI);
        int imageResource =  mContext.getResources().getIdentifier(urI, null, mContext.getApplicationContext().getPackageName());
        Log.d("LeHieu", "PackEmotionViewAdapter imageResource "+imageResource);
        Drawable res = mContext.getResources().getDrawable(imageResource);
        holder.imageView.setImageDrawable(res);
        holder.item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_select = position;
                mClickListener.onItemClick(position);
                notifyDataSetChanged();
            }
        });

        if (row_select == position) {
            holder.item_layout.setBackgroundResource(R.drawable.button_shape_grey);
        } else if (row_select == -1) {
            //First time init
            if (isRecent) {
                if (position == 0) {
                    holder.item_layout.setBackgroundResource(R.drawable.button_shape_grey);
                } else {
                    holder.item_layout.setBackgroundResource(0);;
                }
            } else {
                if (position == 1) {
                    holder.item_layout.setBackgroundResource(R.drawable.button_shape_grey);
                } else {
                    holder.item_layout.setBackgroundResource(0);;
                }
            }

        } else {
            holder.item_layout.setBackgroundResource(0);;
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mListPack.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        LinearLayout  item_layout;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.packImageView);
            item_layout = itemView.findViewById(R.id.pack_item_layout);
        }

    }

    // convenience method for getting data at click position
    public PackEmotionItem getItem(int id) {
        return mListPack.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(int position);
    }
}