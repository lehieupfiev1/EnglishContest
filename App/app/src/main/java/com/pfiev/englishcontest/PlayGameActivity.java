package com.pfiev.englishcontest;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pfiev.englishcontest.model.QuestionItem;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class PlayGameActivity extends AppCompatActivity {
    private static final String TAG = "PlayGameActivity";
    public int mTime;
    public int mTimeChoose;
    public int mAnswerChoose = 0;
    public Button chooser1;
    public Button chooser2;
    public Button chooser3;
    public Button chooser4;
    public Button timeCountBtn;
    public TextView questionTV;
    public ProgressBar mProgressBar;
    public TextView mProgressBarTv;
    public ArrayList<QuestionItem> mListQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBarTv = (TextView) findViewById(R.id.progress_bar_tv);
        chooser1 = (Button) findViewById(R.id.chooser_1);
        chooser2 = (Button) findViewById(R.id.chooser_2);
        chooser3 = (Button) findViewById(R.id.chooser_3);
        chooser4 = (Button) findViewById(R.id.chooser_4);
        timeCountBtn = (Button) findViewById(R.id.time_count);
        questionTV = (TextView) findViewById(R.id.questionTv);
        chooser1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Choose A
                mTimeChoose = mTime;
                mAnswerChoose = 0;
                Toast.makeText(getApplicationContext(), "Choose A at time :" + mTimeChoose, Toast.LENGTH_SHORT).show();
            }
        });
        chooser2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Choose B
                mTimeChoose = mTime;
                mAnswerChoose = 1;
                Toast.makeText(getApplicationContext(), "Choose B at time :" + mTimeChoose, Toast.LENGTH_SHORT).show();
            }
        });
        chooser3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Choose B
                mTimeChoose = mTime;
                mAnswerChoose = 2;
                Toast.makeText(getApplicationContext(), "Choose C at time :" + mTimeChoose, Toast.LENGTH_SHORT).show();
            }
        });
        chooser4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Choose B
                mTimeChoose = mTime;
                mAnswerChoose = 3;
                Toast.makeText(getApplicationContext(), "Choose D at time :" + mTimeChoose, Toast.LENGTH_SHORT).show();
            }
        });

        initData();
        runData(0);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void initData() {
        mListQuestion = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
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
                timeCountBtn.setText("" + l / 1000);
                mTime = (int) l / 1000;

            }

            @Override
            public void onFinish() {
                timeCountBtn.setText("0");
                updateButtonChoose(false);
                Toast.makeText(getApplicationContext(), " onFinish :" + index, Toast.LENGTH_SHORT).show();
                pushAnswerToServer();
                //displayCorrectAnswer(index, mListQuestion.get(index).correctAnswer);
                try {
                    TimeUnit.SECONDS.sleep(3);
                    int next = index + 1;
                    runData(next);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    public void updateQuestion(int index) {
        QuestionItem questionItem = mListQuestion.get(index);
        mAnswerChoose = -1;
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
        ObjectAnimator animator = ObjectAnimator.ofInt(object, "backgroundColor", Color.WHITE, Color.RED, color);
        animator.setDuration(100);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(2);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void pushAnswerToServer() {
        Log.i(TAG, "pushAnswerToServer");
    }


}