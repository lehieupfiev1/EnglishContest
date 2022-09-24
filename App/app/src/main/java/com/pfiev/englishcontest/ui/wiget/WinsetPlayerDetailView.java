package com.pfiev.englishcontest.ui.wiget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pfiev.englishcontest.R;

public class WinsetPlayerDetailView extends RelativeLayout {
    private Context mContext;
    RoundedAvatarImageView mAvatar;
    TextView mInfoTexView;



    public WinsetPlayerDetailView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.winset_player_detail_layout, this);

        mAvatar = findViewById(R.id.avatarUser);
        mInfoTexView = findViewById(R.id.point_tv);
    }

    public TextView getInfoTexView() {
        return mInfoTexView;
    }

    public void setInfoTextView(CharSequence text) {
        mInfoTexView.setText(text);
    }

    public RoundedAvatarImageView getAvatarView() {
        return mAvatar;
    }
}
