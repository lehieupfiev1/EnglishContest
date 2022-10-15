package com.pfiev.englishcontest.ui.experimental;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.adapter.LeaderboardAdapter;
import com.pfiev.englishcontest.databinding.FragmentExperimentalLeaderboardBinding;
import com.pfiev.englishcontest.model.LeaderboardItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaderboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaderboardFragment extends Fragment {


    private FragmentExperimentalLeaderboardBinding binding;
    private List<LeaderboardItem> listLeaderboard;

    interface bindingListViewCallback {
        void bindingList(String result);
    }

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LeaderboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LeaderboardFragment newInstance() {
        return new LeaderboardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExperimentalLeaderboardBinding.inflate(inflater, container, false);
        this.bindingBackButton();
        this.bindingListView();
        return binding.getRoot();
    }

    private void bindingBackButton() {
        // Back to main when touch return button
        binding.experimentalLeaderboardBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.experimental_fullscreen_content, MainFragment.newInstance())
                        .commitNow();
            }
        });
    }

    private void bindingListView() {
        listLeaderboard = new ArrayList<>();

        getLeaderboardFromServer(new bindingListViewCallback() {
            @Override
            public void bindingList(String result) {

                for (int i = 1; i < 9; i++) {
                    LeaderboardItem user = new LeaderboardItem();
                    user.setOrderId(String.valueOf(i));
                    user.setAvatar("https://scontent.fhan2-2.fna.fbcdn.net/v/t1.6435-9/97085964_2967579086666431_472265966688927744_n.jpg?stp=cp0_dst-jpg_e15_fr_q65&_nc_cat=110&ccb=1-7&_nc_sid=85a577&_nc_ohc=hl49kIFuzmUAX-4NKVH&_nc_ht=scontent.fhan2-2.fna&oh=00_AT9lOdHFHzSqC6XUU9Ke84dqlUyr84VeSYegnrdqEIThiA&oe=6323286F");
                    user.setUsername("Candy Milton");
                    user.setScore(String.valueOf(1402));
                    listLeaderboard.add(user);
                }
                LeaderboardAdapter adapter = new LeaderboardAdapter(
                        getContext(), R.layout.listview_experimental_leaderboard, listLeaderboard);
                binding.experimentalLeaderboardLv.setAdapter(adapter);
            }
        });

    }

    public void getLeaderboardFromServer(bindingListViewCallback callback) {
        //Todo Implement get data from server.
        String result = "";
        callback.bindingList("");
    }
}