package com.pfiev.englishcontest;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieAnimationView;
import com.pfiev.englishcontest.firestore.FireStoreClass;
import com.pfiev.englishcontest.model.AnswerItem;
import com.pfiev.englishcontest.model.QuestionItem;

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
    public int mAnswerChoose = 0;
    public static long pressedTime = 0;
    public Button chooser1;
    public Button chooser2;
    public Button chooser3;
    public Button chooser4;
    public Button timeCountBtn;
    public TextView questionTV;
    public ProgressBar mProgressBar;
    public TextView mProgressBarTv;
    public ArrayList<QuestionItem> mListQuestion;
    public Context mContext;
    public Animation myAnim;
    private LottieAnimationView lottie_count_down;
    private LinearLayout Button_layout;
    private RelativeLayout Progress_layout;
    private FrameLayout Question_layout;
    private LinearLayout Result_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        Progress_layout = (RelativeLayout) findViewById(R.id.progress_bar_layout);
        Button_layout = (LinearLayout) findViewById(R.id.button_layout);
        Question_layout = (FrameLayout) findViewById(R.id.question_layout);
        Result_layout = (LinearLayout) findViewById(R.id.layout_result);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBarTv = (TextView) findViewById(R.id.progress_bar_tv);
        chooser1 = (Button) findViewById(R.id.chooser_1);
        chooser2 = (Button) findViewById(R.id.chooser_2);
        chooser3 = (Button) findViewById(R.id.chooser_3);
        chooser4 = (Button) findViewById(R.id.chooser_4);
        timeCountBtn = (Button) findViewById(R.id.time_count);
        questionTV = (TextView) findViewById(R.id.questionTv);
        lottie_count_down = (LottieAnimationView) findViewById(R.id.count_down_display);

        setListenners();
        initData();

        FireStoreClass.updateDataOtherPlayer("5kOnZZlf2LQBvrrHAmGO","1");
    }

    private void setListenners() {
        chooser1.setOnClickListener(this);
        chooser2.setOnClickListener(this);
        chooser3.setOnClickListener(this);
        chooser4.setOnClickListener(this);
        myAnim = AnimationUtils.loadAnimation(this, R.anim.button_animation);
        chooser1.setAnimation(myAnim);
        chooser2.setAnimation(myAnim);
        chooser3.setAnimation(myAnim);
        chooser4.setAnimation(myAnim);
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
        Button_layout.setVisibility(View.VISIBLE);
        Progress_layout.setVisibility(View.VISIBLE);
        Question_layout.setVisibility(View.VISIBLE);
        Result_layout.setVisibility(View.VISIBLE);
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
        mListQuestion = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            QuestionItem questionItem = new QuestionItem();
            questionItem.setQuestionId("" + i);
            questionItem.setQuestionContent("Cau hoi so " + i + " là : ");
            ArrayList<String> listAnswer = new ArrayList<>(Arrays.asList("Dap an A cau " + i, "Dap an B cau " + i, "Dap an C cau " + i, "Dap an D cau " + i));
            questionItem.setListAnswer(listAnswer);
            questionItem.setCorrectAnswer(i % 4);
            questionItem.setTimeAnswer(10);
            mListQuestion.add(questionItem);
        }
        Toast.makeText(getApplicationContext(), " Make ListQuestion :" + mListQuestion.size(), Toast.LENGTH_SHORT).show();
        mProgressBar.setMax(mListQuestion.size());
    }

    public void runData(int index) {

        if (index >= mListQuestion.size()) return;
        int timeCount = mListQuestion.get(index).getTimeAnswer() * 1000;
        updateQuestion(index);

        CountDownTimer countDownTimer = new CountDownTimer(timeCount, 1000) {
            @Override
            public void onTick(long l) {
                timeCountBtn.setText( l / 1000 +" s");
                mTime = (int) l / 1000;

            }

            @Override
            public void onFinish() {
                timeCountBtn.setText("0 s");
                displayCorrectAnswer(index, mListQuestion.get(index).correctAnswer);
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
        mProgressBar.setProgress(indexQues);
        mProgressBarTv.setText(indexQues + "/" + mListQuestion.size());
        questionTV.setText(questionItem.getQuestionContent());
        chooser1.setText(questionItem.getListAnswer().get(0));
        chooser2.setText(questionItem.getListAnswer().get(1));
        chooser3.setText(questionItem.getListAnswer().get(2));
        chooser4.setText(questionItem.getListAnswer().get(3));
        updateButtonChoose(true);
    }


    public void updateButtonChoose(boolean isEnable) {
        chooser1.setEnabled(isEnable);
        chooser2.setEnabled(isEnable);
        chooser3.setEnabled(isEnable);
        chooser4.setEnabled(isEnable);
    }

    public void displayCorrectAnswer(int questionIndex, int correctAnswer) {
        switch (correctAnswer) {
            case 0:
                //mBinding.chooser1.setBackgroundColor(R.);
                blinkEffectAnimation(chooser1, Color.parseColor("#FFFFFFFF"), questionIndex);
                break;
            case 1:
                blinkEffectAnimation(chooser2, Color.parseColor("#4CAF50"), questionIndex);
                break;
            case 2:
                blinkEffectAnimation(chooser3, Color.parseColor("#FFBB86FC"),questionIndex);
                break;
            case 3:
                blinkEffectAnimation(chooser4, Color.parseColor("#FF039BE5"),questionIndex);
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
        answerItem.setQuestion_id(index);
        answerItem.setChoice_id(mAnswerChoose);
        answerItem.setTime_answer(mTimeChoose);
        if ((mListQuestion.get(index).correctAnswer == mAnswerChoose) && mTimeChoose > 0) {
            answerItem.setIs_right(true);
        } else {
            answerItem.setIs_right(false);
        }
        FireStoreClass.pushAnswer(this, "5kOnZZlf2LQBvrrHAmGO","1", answerItem, Integer.toString(index));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chooser_1:
                // do something
                mTimeChoose = mTime;
                mAnswerChoose = 0;
                Toast.makeText(getApplicationContext(), "Choose A at time :" + mTimeChoose, Toast.LENGTH_SHORT).show();
                break;
            case R.id.chooser_2:
                // do something else
                mTimeChoose = mTime;
                mAnswerChoose = 1;
                Toast.makeText(getApplicationContext(), "Choose B at time :" + mTimeChoose, Toast.LENGTH_SHORT).show();
                break;
            case R.id.chooser_3:
                // i'm lazy, do nothing
                mTimeChoose = mTime;
                mAnswerChoose = 2;
                Toast.makeText(getApplicationContext(), "Choose C at time :" + mTimeChoose, Toast.LENGTH_SHORT).show();
                break;
            case R.id.chooser_4:
                // i'm lazy, do nothing
                mTimeChoose = mTime;
                mAnswerChoose = 3;
                Toast.makeText(getApplicationContext(), "Choose D at time :" + mTimeChoose, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}