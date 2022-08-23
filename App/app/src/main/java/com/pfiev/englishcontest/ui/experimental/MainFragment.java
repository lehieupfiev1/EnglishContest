package com.pfiev.englishcontest.ui.experimental;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.pfiev.englishcontest.ExperimentalActivity;
import com.pfiev.englishcontest.LoginActivity;
import com.pfiev.englishcontest.PlayGameActivity;
import com.pfiev.englishcontest.ProfileActivity;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.databinding.FragmentExperimentalMainBinding;
import com.pfiev.englishcontest.model.UserItem;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainFragment extends Fragment {

    private final String TAG = "MainFragment";
    private FragmentExperimentalMainBinding binding;
    private LottieAnimationView lottie;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentExperimentalMainBinding.inflate(inflater, container, false);
        Log.i(TAG, binding.getClass().getClasses().toString());

        // Change to Leaderboard Screen using fragment
        binding.experimentalLeaderboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.experimental_fullscreen_content, LeaderboardFragment.newInstance())
                        .commitNow();
            }
        });

        // Change to Settings Screen using fragment
        binding.experimentalSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.experimental_fullscreen_content, SettingsFragment.newInstance("", ""))
                        .commitNow();
            }
        });

        // Change to About Screen using fragment
        binding.experimentalAboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.experimental_fullscreen_content, AboutFragment.newInstance("", ""))
                        .commitNow();
            }
        });

        // Change to Profile Activity
        binding.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Move to Profile Activity");
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.experimentalNewGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.experimental_fullscreen_content, FindingMatchFragment.newInstance())
                        .commitNow();
            }
        });

        binding.logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogOutDialog(getContext());
            }
        });



        // Create lottie animation background
        lottie = binding.lottieExBg;
        lottie.setRepeatCount(LottieDrawable.INFINITE);
        lottie.playAnimation();

        return binding.getRoot();

    }

    private void initUserData() {
        UserItem userItem = SharePreferenceUtils.getUserData(getActivity());
        Log.i(TAG, userItem.getName());
        binding.avatar.load(getContext(),userItem.getUserPhotoUrl());
        binding.userName.setText(userItem.getName());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        initUserData();
    }

    public void showLogOutDialog(Context context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Exit game");
        builder1.setMessage("Do you want to exit game ?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        signOut();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void signOut() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getActivity(), "Sign Out", Toast.LENGTH_SHORT);
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}