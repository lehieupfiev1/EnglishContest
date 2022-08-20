package com.pfiev.englishcontest.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.model.LeaderboardItem;
import com.pfiev.englishcontest.databinding.ListviewExperimentalLeaderboardBinding;

import java.util.List;

public class LeaderboardAdapter extends BaseAdapter {

    private final Context context;
    private final int idLayout;
    private final List<LeaderboardItem> listLeaderboard;
    private ListviewExperimentalLeaderboardBinding binding;

    public LeaderboardAdapter(Context context, int idLayout, List<LeaderboardItem> listLeaderboard) {
        this.context = context;
        this.idLayout = idLayout;
        this.listLeaderboard = listLeaderboard;
    }

    @Override
    public int getCount() {
        if (listLeaderboard.size() != 0 && !listLeaderboard.isEmpty()) {
            return listLeaderboard.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        final LeaderboardItem user = listLeaderboard.get(index);
        binding = ListviewExperimentalLeaderboardBinding
                .inflate(LayoutInflater.from(context),viewGroup, false);

        binding.leaderboardUserOrder.setText(user.getOrderId());
        binding.leaderboardUserAvatar.load(this.context, user.getAvatar());
        binding.leaderboardUserName.setText(user.getUsername());
        binding.leaderboardUserScore.setText(user.getScore());

        switch (index) {
            case 0:
                binding.leaderboardItemRootLayout.setBackgroundResource(R.color.leaderboard_item_main_first_bg);
                break;
            case 1:
                binding.leaderboardItemRootLayout.setBackgroundResource(R.color.leaderboard_item_main_second_bg);
                break;
            case 2:
                binding.leaderboardItemRootLayout.setBackgroundResource(R.color.leaderboard_item_main_third_bg);
                break;
            default:
                break;
        }

        return binding.getRoot();
    }
}
