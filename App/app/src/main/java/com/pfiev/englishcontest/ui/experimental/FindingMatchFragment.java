package com.pfiev.englishcontest.ui.experimental;

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
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.pfiev.englishcontest.PlayGameActivity;
import com.pfiev.englishcontest.databinding.FragmentExperimentalFindingmatchBinding;


public class FindingMatchFragment extends Fragment {

    private final String TAG = "FindingMatchFragment";
    private FragmentExperimentalFindingmatchBinding mBinding;
    private LottieAnimationView finding_lottie;


    public static FindingMatchFragment newInstance() {
        return new FindingMatchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentExperimentalFindingmatchBinding.inflate(inflater, container, false);
        finding_lottie = mBinding.findingProgress;
        mBinding.findingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.findingBtn.setEnabled(false);
                finding_lottie.setRepeatCount(LottieDrawable.INFINITE);
                finding_lottie.playAnimation();
                sendRequestFindMatch();
                waitingFindMatch();
            }
        });

        return mBinding.getRoot();
    }

    private void sendRequestFindMatch() {
        Log.i(TAG, "sendRequestFindMatch ");
        Toast.makeText(getContext(),"Finding match", Toast.LENGTH_SHORT).show();
    }

    private void waitingFindMatch() {
        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long l) {
                mBinding.statusFinding.setText("Finding........");
            }

            @Override
            public void onFinish() {
                navigatePlayActivity();
            }
        }.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.findingBtn.setEnabled(true);
        finding_lottie.pauseAnimation();
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void navigatePlayActivity() {
        //Add data info player
        Intent intent = new Intent(getActivity(), PlayGameActivity.class);
        startActivity(intent);
    }
}
