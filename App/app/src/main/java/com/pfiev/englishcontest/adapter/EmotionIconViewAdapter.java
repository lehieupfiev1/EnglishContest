package com.pfiev.englishcontest.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.model.EmotionIconItem;

import java.util.List;

public class EmotionIconViewAdapter extends RecyclerView.Adapter<EmotionIconViewAdapter.ViewHolder> {
    private List<EmotionIconItem> mListEmotion;
    private LayoutInflater mInflater;
    private EmotionItemClickListener mClickListener;
    private Context mContext;

    // data is passed into the constructor
    public EmotionIconViewAdapter(Context context, List<EmotionIconItem> emotionItems) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mListEmotion = emotionItems;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.emotion_icon_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.animationView.setVisibility(View.VISIBLE);
            String urlImage = mListEmotion.get(position).getUrl();
            String urI = "@raw/" + urlImage;
            Log.d("LeHieu", "EmotionIconViewAdapter urI " + urlImage + " " + urI);
            int imageResource = mContext.getResources().getIdentifier(urI, null, mContext.getApplicationContext().getPackageName());
            holder.animationView.setAnimation(imageResource);
            holder.animationView.setVisibility(View.VISIBLE);
            holder.animationView.playAnimation();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mListEmotion.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LottieAnimationView animationView;

        ViewHolder(View itemView) {
            super(itemView);
            animationView = (LottieAnimationView) itemView.findViewById(R.id.emotionIconView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                int pos = getAdapterPosition();
                String urlImage = mListEmotion.get(pos).getUrl();
                int iconId = mListEmotion.get(pos).getId();
                String rawUrI = "@raw/"+urlImage;
                Log.d("LeHieu"," OnClick in UrI"+ rawUrI);
                mClickListener.onEmotionItemClick(view, iconId, urlImage, rawUrI);
            }
        }
    }

    // convenience method for getting data at click position
    public EmotionIconItem getItem(int id) {
        return mListEmotion.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(EmotionItemClickListener itemClickListener) {

        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface EmotionItemClickListener {
        void onEmotionItemClick(View view, int stickerId, String url, String rawUrI);
    }
}
