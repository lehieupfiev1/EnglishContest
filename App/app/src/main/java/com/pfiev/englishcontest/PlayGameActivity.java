package com.pfiev.englishcontest;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieAnimationView;
import com.pfiev.englishcontest.databinding.ActivityPlayGameBinding;
import com.pfiev.englishcontest.databinding.ActivityProfileBinding;
import com.pfiev.englishcontest.firestore.FireStoreClass;
import com.pfiev.englishcontest.model.AnswerItem;
import com.pfiev.englishcontest.model.QuestionItem;
import com.pfiev.englishcontest.model.UserItem;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class PlayGameActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PlayGameActivity";
    public int mTime;
    public int mTimeChoose;
    public long mAnswerChoose = 0;
    public static long pressedTime = 0;
    public ArrayList<QuestionItem> mListQuestion;
    public Activity mContext;
    public Animation myAnim;
    private LottieAnimationView lottie_count_down;
    public static String UserId;
    public static String OtherUserId;
    public static String mMatchId;
    private String mMyAvatarUri;
    private String mOtherAvatarUri;
    private boolean mIsOwner;
    private ActivityPlayGameBinding mBinding;
    public ArrayList<Integer> mListChoose;
    public List<Integer> mListTimeChoose;
    public int mMyCorrectCount;
    public long mMyCorrectTimeCount;
    public int mOtherCorrectCount;
    public long mOtherCorrectTimeCount;
    private Dialog mResultDialog;
    private int mMaxTimeCount;
    private boolean isSoundOn;
    private boolean isSoundEffectOn;
    static MediaPlayer rightSoundEffect;
    static MediaPlayer failSoundEffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPlayGameBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        rightSoundEffect = MediaPlayer.create(this, R.raw.right_answer_sound_effect);
        failSoundEffect = MediaPlayer.create(this, R.raw.fail_answer_sound_effect);
        lottie_count_down = (LottieAnimationView) findViewById(R.id.count_down_display);

        mContext = this;
        mResultDialog = new Dialog(mContext);
        setListenners();
        initData();

        FireStoreClass.updateDataOtherPlayer(PlayGameActivity.this,mMatchId, OtherUserId);
    }

    private void setListenners() {
        mBinding.chooser1.setOnClickListener(this);
        mBinding.chooser2.setOnClickListener(this);
        mBinding.chooser3.setOnClickListener(this);
        mBinding.chooser4.setOnClickListener(this);
        myAnim = AnimationUtils.loadAnimation(this, R.anim.button_animation);
        mBinding.chooser1.setAnimation(myAnim);
        mBinding.chooser2.setAnimation(myAnim);
        mBinding.chooser3.setAnimation(myAnim);
        mBinding.chooser4.setAnimation(myAnim);
        lottie_count_down.playAnimation();
        lottie_count_down.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Log.i(TAG, "lottie_count_down finish");
                updatePlayUI();
                runData(0);
                updatePlayData();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void updatePlayUI() {
        lottie_count_down.setVisibility(View.GONE);
        mBinding.buttonLayout.setVisibility(View.VISIBLE);
        mBinding.progressBarLayout.setVisibility(View.VISIBLE);
        mBinding.questionLayout.setVisibility(View.VISIBLE);
        mBinding.layoutResult.setVisibility(View.VISIBLE);
    }

    public void updateDataOtherPlayerInfo(long correctTimeCount) {
        //Update from server
        mOtherCorrectTimeCount += correctTimeCount;
        mOtherCorrectCount++;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    public void initData() {
        Intent intent = getIntent();
        mListQuestion = intent.getParcelableArrayListExtra("ListQuestion");
        OtherUserId = (String) intent.getStringExtra("CompetitorId");
        mMatchId = (String) intent.getStringExtra(GlobalConstant.MATCH_ID);
        UserId = SharePreferenceUtils.getString(getApplicationContext(), GlobalConstant.USER_ID);
        mIsOwner = (Boolean) intent.getBooleanExtra("isOwner",false);
        mMyAvatarUri = SharePreferenceUtils.getString(getApplicationContext(), GlobalConstant.USER_PROFILE_IMAGE);
        Toast.makeText(getApplicationContext(), " Make ListQuestion :" + mListQuestion.size(), Toast.LENGTH_SHORT).show();
        mBinding.progressBar.setMax(mListQuestion.size());
        mMaxTimeCount = 10;
        mListChoose = new ArrayList<>();
        mListTimeChoose = new ArrayList<>();
        mMyCorrectCount =0;
        mMyCorrectTimeCount =0;
        mOtherCorrectCount = 0;
        mOtherCorrectTimeCount = 0;
    }

    public void updatePlayData() {
        Log.i(TAG, "loadAvatarUser mOtherAvatarUri"+mOtherAvatarUri+ " mMyAvatarUri"+mMyAvatarUri + "mMyCorrectCount "+mMyCorrectCount +" mMyCorrectTimeCount "+mMyCorrectTimeCount);
        String myDetailStr = getApplicationContext().getString(R.string.play_detail_result, mMyCorrectCount, mMyCorrectTimeCount);
        String otherDetailStr = getApplicationContext().getString(R.string.play_detail_result, mOtherCorrectCount, mOtherCorrectTimeCount);
        if (checkWinner()) {
            mBinding.player1.getAvatarView().load(this, mMyAvatarUri);
            mBinding.player1.setInfoTextView(myDetailStr);
            mBinding.player2.getAvatarView().load(this, mOtherAvatarUri);
            mBinding.player2.setInfoTextView(otherDetailStr);
        } else {
            mBinding.player1.getAvatarView().load(this, mOtherAvatarUri);
            mBinding.player1.setInfoTextView(otherDetailStr);
            mBinding.player2.getAvatarView().load(this, mMyAvatarUri);
            mBinding.player2.setInfoTextView(myDetailStr);
        }
    }

    public void showWinnerDialog() {
        mResultDialog.setContentView(R.layout.win_layout_dialog);
        mResultDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mResultDialog.setCancelable(false);
        ImageView close_btn = mResultDialog.findViewById(R.id.close_layout);
        Button ok_btn = mResultDialog.findViewById(R.id.ok_btn);
        Button detail_btn = mResultDialog.findViewById(R.id.show_result_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mResultDialog.dismiss();
                Log.i(TAG, " showWinerDialog : close dialog");
            }
        });
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i(TAG, " showWinerDialog : ok");
                mResultDialog.dismiss();
            }
        });
        detail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, " showWinerDialog : detail");
                mResultDialog.dismiss();
                navigateResultGameActivity();
            }
        });

        mResultDialog.show();

    }

    public void showLoserDialog() {
        mResultDialog.setContentView(R.layout.lose_layout_dialog);
        mResultDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mResultDialog.setCancelable(false);
        ImageView close_btn = mResultDialog.findViewById(R.id.close_lose_layout);
        Button ok_btn = mResultDialog.findViewById(R.id.ok_btn);
        Button detail_btn = mResultDialog.findViewById(R.id.show_result_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mResultDialog.dismiss();
                Log.i(TAG, " showLoserDialog : close dialog");
            }
        });
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i(TAG, " showLoserDialog : ok");
                mResultDialog.dismiss();
            }
        });
        detail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, " showLoserDialog : detail");
                mResultDialog.dismiss();
                navigateResultGameActivity();
            }
        });

        mResultDialog.show();

    }

    private boolean checkWinner() {
        if (mMyCorrectCount > mOtherCorrectCount) {
            return true;
        } else if (mMyCorrectCount == mOtherCorrectCount) {
            if (mMyCorrectTimeCount <= mOtherCorrectTimeCount) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void getUserProfileSuccess(UserItem userItem) {
        mOtherAvatarUri = userItem.getUserPhotoUrl();
        Log.i(TAG, "getUserProfileSuccess "+userItem.getName() + " photoUserUri :"+userItem.getUserPhotoUrl());
        String myDetailStr = getString(R.string.play_detail_result, mMyCorrectCount, mMyCorrectTimeCount);
        String otherDetailStr = getString(R.string.play_detail_result, mOtherCorrectCount, mOtherCorrectTimeCount);
        mBinding.player1.getAvatarView().load(this, mMyAvatarUri);
        mBinding.player1.setInfoTextView(myDetailStr);
        mBinding.player2.getAvatarView().load(this, mOtherAvatarUri);
        mBinding.player2.setInfoTextView(otherDetailStr);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FireStoreClass.requestUserInfo(PlayGameActivity.this, OtherUserId);
        //loadAvatarUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        failSoundEffect.release();
        rightSoundEffect.release();
    }

    public void runData(int index) {

        if (index >= mListQuestion.size()) {
            //Show winner dialog
            Log.d(TAG, "runData finish");
            if (checkWinner()) {
                showWinnerDialog();
            } else {
                showLoserDialog();
            }

            return;
        }

        int timeCount = mMaxTimeCount * 1000;
        updateQuestion(index);
        updatePlayData();

        CountDownTimer countDownTimer = new CountDownTimer(timeCount, 1000) {
            @Override
            public void onTick(long l) {
                mBinding.timeCount.setText( l / 1000 +" s");
                int timeOp= (int) l / 1000;
                mTime = (mMaxTimeCount - timeOp);
            }

            @Override
            public void onFinish() {
                mBinding.timeCount.setText("0 s");
                displayCorrectAnswer(index, (int) mListQuestion.get(index).answer);
                Toast.makeText(getApplicationContext(), " onFinish :" + index, Toast.LENGTH_SHORT).show();
                pushAnswerToServer(index);
                updateButtonChoose(false);
                Log.d("UI thread", "I am the UI thread");


                new CountDownTimer(3000,1000) {

                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        int next = index + 1;
                        runData(next);
                    }
                }.start();
            }
        }.start();

    }

    public void updateQuestion(int index) {
        QuestionItem questionItem = mListQuestion.get(index);
        mAnswerChoose = -1;
        mTimeChoose = 0;
        int indexQues = index + 1;
        mBinding.progressBar.setProgress(indexQues);
        mBinding.progressBarTv.setText(indexQues + "/" + mListQuestion.size());
        mBinding.questionTv.setText(questionItem.getQuestionContent());
        mBinding.chooser1.setText(questionItem.getListAnswer().get(0).content);
        mBinding.chooser2.setText(questionItem.getListAnswer().get(1).content);
        mBinding.chooser3.setText(questionItem.getListAnswer().get(2).content);
        mBinding.chooser4.setText(questionItem.getListAnswer().get(3).content);
        updateButtonChoose(true);
    }


    public void updateButtonChoose(boolean isEnable) {
        mBinding.chooser1.setEnabled(isEnable);
        mBinding.chooser2.setEnabled(isEnable);
        mBinding.chooser3.setEnabled(isEnable);
        mBinding.chooser4.setEnabled(isEnable);
    }

    public void displayCorrectAnswer(int questionIndex, int correctAnswer) {
        switch (correctAnswer) {
            case 1:
                //mBinding.chooser1.setBackgroundColor(R.);
                blinkEffectAnimation(mBinding.chooser1, Color.parseColor("#FFFFFFFF"), questionIndex);
                break;
            case 2:
                blinkEffectAnimation(mBinding.chooser2, Color.parseColor("#4CAF50"), questionIndex);
                break;
            case 3:
                blinkEffectAnimation(mBinding.chooser3, Color.parseColor("#FFBB86FC"),questionIndex);
                break;
            case 4:
                blinkEffectAnimation(mBinding.chooser4, Color.parseColor("#FF039BE5"),questionIndex);
                break;
            default:
                break;
        }
    }

    private void blinkEffectAnimation(Button object, int color, int index) {
//        ObjectAnimator animator = ObjectAnimator.ofInt(object, "backgroundColor", color, Color.RED, color);
//        animator.setDuration(800);
//        animator.setEvaluator(new ArgbEvaluator());
//        animator.setRepeatCount(ValueAnimator.INFINITE);
//        animator.setRepeatMode(ValueAnimator.REVERSE);
//        animator.start();
        object.startAnimation(myAnim);
    }

    public void pushAnswerToServer(int index) {
        Log.i(TAG, "pushAnswerToServer");
        AnswerItem answerItem = new AnswerItem();
        answerItem.setQuestion_id(mListQuestion.get(index).getQuestionId());
        answerItem.setChoice_id(mAnswerChoose);
        answerItem.setTime_answer(mTimeChoose);
        mListChoose.add((int) mAnswerChoose);
        mListTimeChoose.add(mTimeChoose);

        if ((mListQuestion.get(index).answer == mAnswerChoose) && mTimeChoose > 0) {
            answerItem.setIs_right(true);
            mMyCorrectCount++;
            mMyCorrectTimeCount += mTimeChoose;
            rightSoundEffect.start();
        } else {
            answerItem.setIs_right(false);
            failSoundEffect.start();
        }
        FireStoreClass.pushAnswer(this, mMatchId, UserId, answerItem, Integer.toString(index));

    }

    private void navigateResultGameActivity() {
        //Add data info player
        Intent intent = new Intent(PlayGameActivity.this, ResultGameActivity.class);
        Bundle bundle = new Bundle();
        Log.i(TAG, "navigatePlayActivity mListQuestion :"+mListQuestion.size());
        bundle.putParcelableArrayList("ListQuestion",  mListQuestion);
        intent.putExtras(bundle);
        intent.putIntegerArrayListExtra("ListChoose", mListChoose);
        intent.putExtra("numberCorrect", mMyCorrectCount);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chooser_1:
                // do something
                mTimeChoose = mTime;
                mAnswerChoose = 1;
                Toast.makeText(getApplicationContext(), "Choose A at time :" + mTimeChoose, Toast.LENGTH_SHORT).show();
                break;
            case R.id.chooser_2:
                // do something else
                mTimeChoose = mTime;
                mAnswerChoose = 2;
                Toast.makeText(getApplicationContext(), "Choose B at time :" + mTimeChoose, Toast.LENGTH_SHORT).show();
                break;
            case R.id.chooser_3:
                // i'm lazy, do nothing
                mTimeChoose = mTime;
                mAnswerChoose = 3;
                Toast.makeText(getApplicationContext(), "Choose C at time :" + mTimeChoose, Toast.LENGTH_SHORT).show();
                break;
            case R.id.chooser_4:
                // i'm lazy, do nothing
                mTimeChoose = mTime;
                mAnswerChoose = 4;
                Toast.makeText(getApplicationContext(), "Choose D at time :" + mTimeChoose, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}