package com.pfiev.englishcontest.ui.experimental;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.pfiev.englishcontest.EnglishApplication;
import com.pfiev.englishcontest.LoginActivity;
import com.pfiev.englishcontest.ProfileActivity;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.databinding.FragmentExperimentalMainBinding;
import com.pfiev.englishcontest.model.FriendItem;
import com.pfiev.englishcontest.model.UserItem;
import com.pfiev.englishcontest.realtimedb.FriendList;
import com.pfiev.englishcontest.realtimedb.Status;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainFragment extends Fragment {

    private final String TAG = "MainFragment";
    private FragmentExperimentalMainBinding binding;
    private static boolean isDisplay= false;

    public static MainFragment newInstance() {
        return new MainFragment();
    }
    public static boolean isDisplayNow() {
        return isDisplay;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentExperimentalMainBinding.inflate(inflater, container, false);

        // Change to Friends List Screen using fragment
        binding.experimentalFriendsListBtn.setOnClickListener(
                view -> getParentFragmentManager().beginTransaction()
                        .replace(
                                R.id.experimental_fullscreen_content,
                                FriendListFragment.newInstance(),
                                null
                        )
                        .commitAllowingStateLoss());

        // Change to Leaderboard Screen using fragment
        binding.experimentalLeaderboardBtn.setOnClickListener(
                view -> getParentFragmentManager().beginTransaction()
                        .replace(
                                R.id.experimental_fullscreen_content,
                                LeaderboardFragment.newInstance()
                        )
                        .commitNow());

        // Change to Settings Screen using fragment
        binding.experimentalSettingsBtn.setOnClickListener(
                view -> getParentFragmentManager().beginTransaction()
                        .replace(
                                R.id.experimental_fullscreen_content,
                                SettingsFragment.newInstance("", "")
                        )
                        .commitNow()
        );

        // Change to About Screen using fragment
        binding.experimentalAboutBtn.setOnClickListener(
                view -> getParentFragmentManager().beginTransaction()
                        .replace(
                                R.id.experimental_fullscreen_content,
                                AboutFragment.newInstance()
                        )
                        .commitNow());

        // Change to Profile Activity
        binding.profileBar.setOnClickListener(view -> {
            Log.i(TAG, "Move to Profile Activity");
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        // Finding new game
        binding.experimentalNewGameBtn.setOnClickListener(
                view -> getParentFragmentManager().beginTransaction()
                        .replace(
                                R.id.experimental_fullscreen_content,
                                FindingMatchFragment.newInstance()
                        )
                        .commitNow());
        //Load Ads
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);


        return binding.getRoot();

    }

    private void initUserData() {
        UserItem userItem = SharePreferenceUtils.getUserData(getActivity());
//        Log.i(TAG, userItem.getName());
        binding.avatar.load(getContext(), userItem.getUserPhotoUrl());
        binding.userName.setText(userItem.getName());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        isDisplay = true;
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            return;
        }
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        initUserData();
        // Update online status to friends and listen their status
        String ownerUid = SharePreferenceUtils.getUserData(getActivity()).getUserId();
        FriendList friendList = FriendList.getInstance();
        friendList.setUid(ownerUid);
        friendList.updateStatusToFriends(FriendItem.STATUS.ONLINE);
        // Set current state is online
        if (EnglishApplication.isSetOnDisconnectEvent())
            Status.getInstance().setState(Status.STATE_ONLINE);
        EnglishApplication.setCurrentUserStatus(Status.STATE_ONLINE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDisplay = false;
        binding = null;
    }
}