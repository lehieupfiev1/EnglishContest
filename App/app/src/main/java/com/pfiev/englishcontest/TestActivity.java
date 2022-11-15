package com.pfiev.englishcontest;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.pfiev.englishcontest.databinding.ActivityPlayGameBinding;
import com.pfiev.englishcontest.ui.playactivityelem.Choice;
import com.pfiev.englishcontest.ui.playactivityelem.MessageBubble;
import com.pfiev.englishcontest.ui.playactivityelem.OrderRow;

public class TestActivity extends AppCompatActivity {
    private  ActivityPlayGameBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//
//        if (Build.VERSION.SDK_INT >= 30) {
//            getWindowInsetsController().hide(
//                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
//        } else {
//            // Note that some of these constants are new as of API 16 (Jelly Bean)
//            // and API 19 (KitKat). It is safe to use them, as they are inlined
//            // at compile-time and do nothing on earlier devices.
//            setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        }
        super.onCreate(savedInstanceState);
        binding = ActivityPlayGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Choice firstChoice = new Choice(getApplicationContext());
        binding.playActivityChoices.addView(firstChoice);
        Choice secondChoice = new Choice(getApplicationContext());
        binding.playActivityChoices.addView(secondChoice);
        binding.playActivityChoices.addView(new Choice(getApplicationContext()));
        Choice lastChoice = new Choice(getApplicationContext());
        lastChoice.setContentDisplay("I wouldn’t have gone there");
        lastChoice.deleteMarginBottom();

        binding.playActivityChoices.addView(lastChoice);
        OrderRow firstRow = new OrderRow(getApplicationContext(), null);
        OrderRow secondRow = new OrderRow(getApplicationContext(), null);
        secondRow.setAvatar(getApplicationContext(),
                "https://static.remove.bg/remove-bg-web/221525818b4ba04e9088d39cdcbd0c7bcdfb052e/assets/start_remove-c851bdf8d3127a24e2d137a55b1b427378cd17385b01aec6e59d5d4b5f39d2ec.png");
        secondRow.setUserParams("User second");
        binding.playActivityOrderBoard.addView(firstRow);
        binding.playActivityOrderBoard.addView(secondRow);

//        firstRow.lastYAxis = firstRow.getY();
//        Log.d(getClass().getName(), "first " + firstRow.lastYAxis + " " +firstRow.getX());
//        Toast.makeText(this, "first " + firstRow.lastYAxis + " " +firstRow.getX(), Toast.LENGTH_LONG ).show();
//        secondRow.lastYAxis = secondRow.getY();
//        Log.d(getClass().getName(), "second " + secondRow.lastYAxis + " " +secondRow.getX());
//        Toast.makeText(this, "second " + secondRow.lastYAxis + " " +secondRow.getX(), Toast.LENGTH_LONG ).show();

        lastChoice.setOnClickCallback(new Choice.ChoiceClick() {
            @Override
            public void run(Choice choice) {
                MessageBubble messageBubble = new MessageBubble(getApplicationContext(), null);
                LottieAnimationView lottie = new LottieAnimationView(getApplicationContext());
                lottie.setAnimation(R.raw.sticker_demo);
                lottie.setRepeatCount(LottieDrawable.INFINITE);
                lottie.setRepeatMode(LottieDrawable.RESTART);
                lottie.playAnimation();
                messageBubble.addSticker(lottie);
                secondRow.showNewMessage(messageBubble);
                firstChoice.setDecoration(Choice.STATE.WRONG);
                secondChoice.setDecoration(Choice.STATE.RIGHT);
                lastChoice.showBlinkEffectToState(Choice.STATE.RIGHT);
//                firstRow.lastYAxis = firstRow.getY();
//                Log.d(getClass().getName(), "first " + firstRow.lastYAxis + " " +firstRow.getX());
//                Toast.makeText(getApplicationContext(), "first " + firstRow.lastYAxis + " " +firstRow.getX(), Toast.LENGTH_LONG ).show();
//                secondRow.lastYAxis = secondRow.getY();
//                Log.d(getClass().getName(), "second " + secondRow.lastYAxis + " " +secondRow.getX());
//                Toast.makeText(getApplicationContext(), "second " + secondRow.lastYAxis + " " +secondRow.getX(), Toast.LENGTH_LONG ).show();


//                firstRow.swapPosition(secondRow);
                new CountDownTimer(10000, 200) {

                    @Override
                    public void onTick(long l) {
                        Log.d("Test progress", "" +l);
                        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
                            @Override
                            public void run() {
                                firstRow.swapPosition(secondRow);
                            }
                        });
                        binding.playActivityCountdownProgressIndicator
                                .setProgress((int) (l/100), true);
                    }

                    @Override
                    public void onFinish() {
                        binding.playActivityCountdownProgressIndicator
                                .setProgress(0, true);
                    }
                }.start();

            }
        });

        binding.playActivityCountdownProgressIndicator
                .setProgress(100);

        binding.playActivityShowStickerPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.playActivityMainCombat.animate().y(-400).setDuration(1000).start();
            }
        });
        binding.testInsertSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
