package com.pfiev.englishcontest.ui.experimental;

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
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.databinding.FragmentExperimentalMainBinding;

import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainFragment extends Fragment {

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
        Log.d("TAG Fragment", binding.getClass().getClasses().toString());

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


        // Create lottie animation background
        lottie = binding.lottieExBg;
        lottie.setRepeatCount(LottieDrawable.INFINITE);
        lottie.playAnimation();

        return binding.getRoot();

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}