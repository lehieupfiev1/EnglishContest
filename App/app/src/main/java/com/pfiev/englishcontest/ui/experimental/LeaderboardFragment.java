package com.pfiev.englishcontest.ui.experimental;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.adapter.LeaderboardAdapter;
import com.pfiev.englishcontest.databinding.FragmentExperimentalLeaderboardBinding;
import com.pfiev.englishcontest.firestore.FireStoreClass;
import com.pfiev.englishcontest.model.LeaderboardItem;
import com.pfiev.englishcontest.ui.dialog.CustomToast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaderboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaderboardFragment extends Fragment {

    private String TAG = getClass().getName();
    private FragmentExperimentalLeaderboardBinding binding;
    private LottieAnimationView loadingAnim;


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
        loadingAnim = binding.experimentalLeaderboardLoading;
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
        showLoadingAnim();
        FireStoreClass.getLeaderBoard().addOnCompleteListener(
                new OnCompleteListener<HashMap>() {
                    @Override
                    public void onComplete(@NonNull Task<HashMap> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }
                            String messageDisplay = getString(R.string.toast_custom_default_error_display);
                            CustomToast.makeText(getActivity(), messageDisplay,
                                    CustomToast.ERROR, Toast.LENGTH_LONG);
                        } else {
                            ArrayList data = (ArrayList) task.getResult().get(
                                    FireStoreClass.LEADERBOARD_FIELD);

                            ArrayList<LeaderboardItem> list = new ArrayList<>();
                            for(int i =0; i< data.size(); i++) {
                                LeaderboardItem item = new LeaderboardItem();
                                item.setProperties((HashMap) data.get(i));
                                int orderId = i +1;
                                item.setOrderId("" +orderId);
                                list.add(item);
                            }
                            if (getContext() != null) {
                                LeaderboardAdapter adapter = new LeaderboardAdapter(
                                        getContext(), R.layout.listview_experimental_leaderboard, list);
                                binding.experimentalLeaderboardLv.setAdapter(adapter);
                                binding.experimentalLeaderboardLv.setDivider(
                                        new ColorDrawable(
                                                ContextCompat.getColor(getContext(),
                                                        R.color.leaderboard_item_divider))
                                );
                                binding.experimentalLeaderboardLv.setDividerHeight(10);
                            }
                        }
                        hideLoadingAnim();
                    }
                }
        );

    }

    /**
     * Show loading animation
     */
    private void showLoadingAnim() {
        loadingAnim.setVisibility(View.VISIBLE);
        loadingAnim.playAnimation();
    }

    /**
     * Hide loading animation
     */
    private void hideLoadingAnim() {
        loadingAnim.setVisibility(View.INVISIBLE);
        loadingAnim.pauseAnimation();
    }


}