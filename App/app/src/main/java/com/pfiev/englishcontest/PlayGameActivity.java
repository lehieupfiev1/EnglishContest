package com.pfiev.englishcontest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.pfiev.englishcontest.adapter.EmotionIconViewAdapter;
import com.pfiev.englishcontest.adapter.PackEmotionViewAdapter;
import com.pfiev.englishcontest.databinding.ActivityPlayGameBinding;
import com.pfiev.englishcontest.firestore.MatchCollection;
import com.pfiev.englishcontest.firestore.UsersCollection;
import com.pfiev.englishcontest.model.AnswerItem;
import com.pfiev.englishcontest.model.ChoiceItem;
import com.pfiev.englishcontest.model.EmotionIconItem;
import com.pfiev.englishcontest.model.PackEmotionItem;
import com.pfiev.englishcontest.model.QuestionItem;
import com.pfiev.englishcontest.model.UserItem;
import com.pfiev.englishcontest.ui.playactivityelem.Choice;
import com.pfiev.englishcontest.ui.playactivityelem.MessageBubble;
import com.pfiev.englishcontest.ui.playactivityelem.OrderRow;
import com.pfiev.englishcontest.utils.EmotionIconDBHelper;
import com.pfiev.englishcontest.utils.ListEmotionsDBHelper;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;


public class PlayGameActivity extends AppCompatActivity implements PackEmotionViewAdapter.ItemClickListener, EmotionIconViewAdapter.EmotionItemClickListener {
    private static final String TAG = "PlayGameActivity";
    public int mTime;
    public int mTimeChoose;
    public long mAnswerChoose = 0;
    public static long pressedTime = 0;
    public ArrayList<QuestionItem> mListQuestion;
    public Activity mContext;
    public String ownUserId;
    public String competitorUserId;
    public String mMatchId;
    private ActivityPlayGameBinding mBinding;
    public ArrayList<Integer> mListChoose;
    public List<Integer> mListTimeChoose;
    public int ownerCorrectCount;
    public long ownerTotalTimeAnswer;
    public int competitorCorrectAnswer;
    public long competitorTotalTimeAnswer;
    private Dialog mResultDialog;
    private int mMaxTimeCount;
    static MediaPlayer rightSoundEffect;
    static MediaPlayer failSoundEffect;
    PackEmotionViewAdapter mPackAdapter;
    EmotionIconViewAdapter mEmotionAdapter;
    List<List<EmotionIconItem>> mListTotalEmotion = new ArrayList<>();
    List<PackEmotionItem> mListPack;

    private UpdateUI updateUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPlayGameBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        rightSoundEffect = MediaPlayer.create(this, R.raw.right_answer_sound_effect);
        failSoundEffect = MediaPlayer.create(this, R.raw.fail_answer_sound_effect);

        mContext = this;
        initData();
        updateUI = new UpdateUI(mBinding, mContext);
        updateUI.setParticipantsUid(ownUserId, competitorUserId);
        updateUI.initDefaultUI(mListQuestion.size());
        setListenners();
        // Set click choice listener
        for (int i = 0; i < 4; i++) {
            Objects.requireNonNull(updateUI.getChoice(i)).setOnClickCallback(
                    choice -> {
                        mTimeChoose = mTime;
                        mAnswerChoose = choice.getOrderIndex();
                        updateUI.updateOtherChoicesState(choice, Choice.STATE.DEFAULT);
                    });
        }
        mResultDialog = new Dialog(mContext);
        UsersCollection usersCollection = new UsersCollection();
        usersCollection.getBaseInfoByUid(competitorUserId).continueWith(
                task -> {
                    UserItem userItem = task.getResult();
                    getUserProfileSuccess(userItem);
                    return null;
                }
        );
        requestData();
        viewStickerData();

        MatchCollection.updateDataOtherPlayer(mMatchId, competitorUserId,
                this::updateCompetitorInfo);
    }

    private void setListenners() {
        runData(0);
    }

    public void updateCompetitorInfo(long correctTimeCount) {
        //Update from server
        competitorTotalTimeAnswer += correctTimeCount;
        competitorCorrectAnswer++;
        updateUI.updateOderRow(competitorUserId, correctTimeCount);
        updateUI.reorganizeOrderRow();
        updateUI.showScoreInLimitTime(3000);
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

    /**
     * Inititalize data to start play match
     */
    public void initData() {
        Intent intent = getIntent();
        mListQuestion = intent.getParcelableArrayListExtra("ListQuestion");
        competitorUserId = (String) intent.getStringExtra("CompetitorId");
        mMatchId = (String) intent.getStringExtra(GlobalConstant.MATCH_ID);
        ownUserId = SharePreferenceUtils.getString(getApplicationContext(), GlobalConstant.USER_ID);

        mMaxTimeCount = 10;
        mListChoose = new ArrayList<>();
        mListTimeChoose = new ArrayList<>();
        ownerCorrectCount = 0;
        ownerTotalTimeAnswer = 0;
        competitorCorrectAnswer = 0;
        competitorTotalTimeAnswer = 0;
    }

    public void showWinnerDialog() {
        mResultDialog.setContentView(R.layout.win_layout_dialog);
        mResultDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mResultDialog.setCancelable(false);
        TextView ok_btn = mResultDialog.findViewById(R.id.ok_btn);
        TextView detail_btn = mResultDialog.findViewById(R.id.show_result_btn);
        ok_btn.setOnClickListener(view -> mResultDialog.dismiss());
        detail_btn.setOnClickListener(view -> {
            mResultDialog.dismiss();
            navigateResultGameActivity();
        });
        mResultDialog.show();
    }

    public void showLoserDialog() {
        mResultDialog.setContentView(R.layout.lose_layout_dialog);
        mResultDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mResultDialog.setCancelable(false);
        TextView ok_btn = mResultDialog.findViewById(R.id.ok_btn);
        TextView detail_btn = mResultDialog.findViewById(R.id.show_result_btn);
        ok_btn.setOnClickListener(view -> mResultDialog.dismiss());
        detail_btn.setOnClickListener(view -> {
            mResultDialog.dismiss();
            navigateResultGameActivity();
        });
        mResultDialog.show();
    }

    private boolean checkWinner() {
        if (ownerCorrectCount > competitorCorrectAnswer) {
            return true;
        } else if (ownerCorrectCount == competitorCorrectAnswer) {
            return ownerTotalTimeAnswer <= competitorTotalTimeAnswer;
        } else {
            return false;
        }
    }

    public void getUserProfileSuccess(UserItem userItem) {
        UserItem ownerItem = SharePreferenceUtils.getUserData(this);
        updateUI.addOrderRow(ownerItem, 1);
        // Create order row to competitor with default order id is 2
        updateUI.addOrderRow(userItem, 2);

        mBinding.packEmotionRecyclerView.findViewHolderForAdapterPosition(2).itemView.performClick(); // cai nay cung the a. chac do no chua bind kip
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        failSoundEffect.release();
        rightSoundEffect.release();
    }

    public void runData(int index) {

        if (index >= mListQuestion.size()) {
            // if end game wait more 3 seconds to get data
            new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    //Show winner dialog
                    Log.d(TAG, "runData finish");
                    if (checkWinner()) {
                        showWinnerDialog();
                    } else {
                        showLoserDialog();
                    }
                }
            }.start();
            return;
        }

        int timeCount = mMaxTimeCount * 1000;
        updateQuestion(index);
        updateUI.updateCountdownTime(timeCount);

        new CountDownTimer(timeCount, 1000) {
            @Override
            public void onTick(long l) {
                updateUI.updateCountdownTime(l);
                int timeOp = (int) l / 1000;
                mTime = (mMaxTimeCount - timeOp);
            }

            @Override
            public void onFinish() {
                updateUI.updateCountdownTime(0);
                updateUI.displayCorrectAnswer(mAnswerChoose, (int) mListQuestion.get(index).answer);
                updateUI.setChoicesEnable(false);
                pushAnswerToServer(index);

                new CountDownTimer(3000, 1000) {
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
        // update ui
        updateUI.updateProgressBar(indexQues, mListQuestion.size());
        updateUI.displayQuestionContent(questionItem.getQuestionContent());
        questionItem.getListAnswer().forEach(new Consumer<ChoiceItem>() {
            int indexChoice = 0;

            @Override
            public void accept(ChoiceItem choiceItem) {
                if (indexChoice < updateUI.getListChoices().length) {
                    updateUI.setChoiceData(indexChoice, choiceItem);
                    indexChoice = indexChoice + 1;
                }
            }
        });
        updateUI.setChoicesEnable(true);
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
            ownerCorrectCount++;
            ownerTotalTimeAnswer += mTimeChoose;
            rightSoundEffect.start();
            updateUI.updateOderRow(ownUserId, mTimeChoose);
            updateUI.reorganizeOrderRow();
        } else {
            answerItem.setIs_right(false);
            failSoundEffect.start();
        }
        MatchCollection.pushAnswer(mMatchId, ownUserId, answerItem, Integer.toString(index));

    }

    private void navigateResultGameActivity() {
        //Add data info player
        Intent intent = new Intent(PlayGameActivity.this, ResultGameActivity.class);
        Bundle bundle = new Bundle();
        Log.i(TAG, "navigatePlayActivity mListQuestion :" + mListQuestion.size());
        bundle.putParcelableArrayList("ListQuestion", mListQuestion);
        intent.putExtras(bundle);
        intent.putIntegerArrayListExtra("ListChoose", mListChoose);
        intent.putExtra("numberCorrect", ownerCorrectCount);
        startActivity(intent);
    }

    private void  viewStickerData() {
        Log.d("LeHieu", "View Emtion data");
        //requestData();
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBinding.packEmotionRecyclerView.setLayoutManager(horizontalLayoutManager);
        Log.d("LeHieu", "ListEmotionsDBHelper mListPack size " + mListPack.size() + mListPack.get(0).getUrl());
        if (mListTotalEmotion.get(0).size() == 0) {
            mPackAdapter = new PackEmotionViewAdapter(this, mListPack, false);
        } else {
            mPackAdapter = new PackEmotionViewAdapter(this, mListPack, true);
        }
        Log.d("LeHieu", "ListEmotionsDBHelper mPackAdapter " + mPackAdapter);
        mPackAdapter.setClickListener(this);
        mBinding.packEmotionRecyclerView.setAdapter(mPackAdapter);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4,LinearLayoutManager.VERTICAL,false);
        mBinding.gridEmotionRecyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        // cai ham nay chi goi 1 lan thoi chu nhi
        List<EmotionIconItem> mListEmotion;
        if (mListTotalEmotion.get(0).size() == 0) {
            mListEmotion = mListTotalEmotion.get(1);
        } else {
            mListEmotion = mListTotalEmotion.get(0);
        }
        Log.d("LeHieu", "EmotionIconDBHelper mListEmotion size = "+mListEmotion.size());
        mEmotionAdapter = new EmotionIconViewAdapter(this, mListEmotion);
        mBinding.gridEmotionRecyclerView.setAdapter(mEmotionAdapter);
        mEmotionAdapter.setClickListener(this);

    }

    public void requestData() {
        ListEmotionsDBHelper packDB = ListEmotionsDBHelper.getInstance(getApplicationContext());
        EmotionIconDBHelper iconDB = EmotionIconDBHelper.getInstance(getApplicationContext());
        Log.d("LeHieu", "ListEmotionsDBHelper data"+packDB);
        mListTotalEmotion.clear();
        mListPack = packDB.getAllPackEmotion();
        for (int i =0; i<mListPack.size(); i++) {
            String packName = mListPack.get(i).getName();
            Log.d("LeHieu", "EmotionIconDBHelper packName "+packName);
            List<EmotionIconItem> listEmotion = iconDB.getEmotionListByPackName(packName);
            Log.d("LeHieu", "EmotionIconDBHelper mListEmotion "+listEmotion.size());
            mListTotalEmotion.add(listEmotion);
        }
    }
    @Override
    public void onItemClick(int position) {
        Log.i("LeHieu", "Click on position"+position);
        List<EmotionIconItem> mListEmotion = mListTotalEmotion.get(position);
        if (position == 0 && mListEmotion.size() == 0) {
            mBinding.noRecentTv.setVisibility(View.VISIBLE);
            mBinding.gridEmotionRecyclerView.setVisibility(View.GONE);
        } else {
            mBinding.noRecentTv.setVisibility(View.GONE);
            mBinding.gridEmotionRecyclerView.setVisibility(View.VISIBLE);
            Log.d("LeHieu", "EmotionIconDBHelper mListEmotion size = " + mListEmotion.size());
            mEmotionAdapter = new EmotionIconViewAdapter(this, mListEmotion);
            mEmotionAdapter.setClickListener(this);
            mBinding.gridEmotionRecyclerView.setAdapter(mEmotionAdapter);
        }
    }

    @Override
    public void onEmotionItemClick(View view, int position, String url, String rawUrI) {
        Log.i("LeHieu", "onEmotionItemClick on rawUrI"+rawUrI);
        updateRecentEmotion(url);
        int imageResource =  getApplicationContext().getResources().getIdentifier(rawUrI, null, getApplicationContext().getPackageName());
        LottieAnimationView message = new LottieAnimationView(getApplicationContext());
        message.setAnimation(imageResource);
        updateUI.addNewMessage(ownUserId, message);
//        mResultEmotion.cancelAnimation();
//        int imageResource =  getContext().getResources().getIdentifier(rawUrI, null, getContext().getApplicationContext().getPackageName());
//        mResultEmotion.setAnimation(imageResource);
//        mResultEmotion.setVisibility(View.VISIBLE);
//        mResultEmotion.playAnimation();
    }

    public void updateRecentEmotion(String url) {
        EmotionIconItem emotionIconItem = new EmotionIconItem("recent_emotion","sticker0","loti",url);
        //Save to sqlite
        EmotionIconDBHelper iconDB = EmotionIconDBHelper.getInstance(getApplicationContext());
        iconDB.addEmotionIcon(emotionIconItem);
        mListTotalEmotion.get(0).add(emotionIconItem);
    }

    private class UpdateUI {
        Context uIContext;
        ActivityPlayGameBinding mBinding;
        String ownerId, competitorId;
        Choice[] listChoices = new Choice[4];
        HashMap<String, OrderRow> listOrderRow = new HashMap<>();
        boolean isShowStickerPanel = false;

        public UpdateUI(ActivityPlayGameBinding binding, Context context) {
            mBinding = binding;
            uIContext = context;
        }

        /**
         * Set participants uid
         *
         * @param ownerId      owner user id
         * @param competitorId competitor id
         */
        public void setParticipantsUid(String ownerId, String competitorId) {
            this.ownerId = ownerId;
            this.competitorId = competitorId;
        }

        /**
         * Init default ui
         *
         * @param totalQuestion total question
         */
        public void initDefaultUI(int totalQuestion) {
            mBinding.playActivityProgressBar.setMax(totalQuestion);
            mBinding.playActivityCountdownProgressIndicator.setMax(100);
            mBinding.playActivityShowStickerPanel.setOnClickListener(view -> toggleStickerPanel());
            for (int i =0; i<4; ++i) {
                Choice choice = new Choice(uIContext);
                listChoices[i] = choice;
                mBinding.playActivityChoices.addView(choice);
            }

            // Demo show sticker
            mBinding.testInsertSticker.setOnClickListener(view -> {
                LottieAnimationView message = new LottieAnimationView(getApplicationContext());
                message.setAnimation(R.raw.sticker_demo);
                addNewMessage(ownerId, message);
            });
        }

        /**
         * Add new order row
         *
         * @param userItem user item
         * @param orderId  order id default
         */
        public void addOrderRow(UserItem userItem, int orderId) {
            OrderRow orderRow = new OrderRow(getApplicationContext(), null);
            orderRow.orderId = orderId;
            orderRow.getUserInfo().uid = userItem.getUserId();
            orderRow.getUserInfo().name = userItem.getName();
            orderRow.getUserInfo().avatarUrl = userItem.getUserPhotoUrl();
            // First time add will show user name
            orderRow.setAvatar(uIContext, userItem.getUserPhotoUrl());
            orderRow.setUserParams(userItem.getName());
            listOrderRow.put(userItem.getUserId(), orderRow);
            mBinding.playActivityOrderBoard.addView(orderRow);
        }

        /**
         * Reorder OrderRow
         */
        public void reorganizeOrderRow() {
            OrderRow ownRow = listOrderRow.get(ownUserId);
            OrderRow competitorRow = listOrderRow.get(competitorId);
            if (ownRow == null || competitorRow == null) return;
            int result = ownRow.getUserInfo().hasBetterResult(competitorRow.getUserInfo());
            result = result * (ownRow.orderId - competitorRow.orderId);
            if (result > 0) ownRow.swapPosition(competitorRow);
        }

        /**
         * Get Choice by index
         *
         * @param index index
         * @return choice
         */
        public Choice getChoice(int index) {
            return listChoices[index];
        }

        /**
         * Set choice data
         * @param index      choice's index
         * @param choiceItem choice item
         */
        public void setChoiceData(int index, ChoiceItem choiceItem) {
            Log.d(TAG, "index is: " +index);
            listChoices[index].setOrderIndex(choiceItem.getId());
            listChoices[index].setContentDisplay(choiceItem.getContent());
            listChoices[index].setDecoration(Choice.STATE.DEFAULT);
        }

        /**
         * Get list choices
         *
         * @return list choices
         */
        public Choice[] getListChoices() {
            return listChoices;
        }

        /**
         * Set choices enable or disable
         *
         * @param isEnable true if enable
         */
        public void setChoicesEnable(boolean isEnable) {
            for (Choice mChoice : listChoices) {
                mChoice.setEnabled(isEnable);
            }
        }

        /**
         * Display correct answer
         *
         * @param ownerChoice   owner choice
         * @param rightChoiceId true answer
         */
        public void displayCorrectAnswer(long ownerChoice, int rightChoiceId) {
            for (Choice choice : listChoices) {
                if (choice.getOrderIndex() == ownerChoice) {
                    if (ownerChoice != rightChoiceId)
                        choice.setDecoration(Choice.STATE.WRONG);
                }
                if (choice.getOrderIndex() == rightChoiceId) {
                    choice.showBlinkEffectToState(Choice.STATE.RIGHT);
                }
            }
        }

        /**
         * Display question content
         *
         * @param questionContent content
         */
        public void displayQuestionContent(String questionContent) {
            mBinding.playActivityQuestionContent.setText(questionContent);
        }

        /**
         * Update progress bar
         *
         * @param questionIndex question index
         * @param totalQuestion total question
         */
        public void updateProgressBar(int questionIndex, int totalQuestion) {
            mBinding.playActivityProgressBar.setProgress(questionIndex);
            mBinding.playActivityProgressBarTv.setText(
                    uIContext.getString(
                            R.string.play_activity_progress_bar_text,
                            questionIndex, totalQuestion)
            );
        }

        /**
         * Update order row
         *
         * @param userId     own choice
         * @param timeAnswer time choose
         */
        public void updateOderRow(String userId, long timeAnswer) {
            listOrderRow.get(userId).getUserInfo().addRightAnswer();
            listOrderRow.get(userId).getUserInfo().increaseTotalTimeAnswer(timeAnswer);
        }

        /**
         * Update count down time
         *
         * @param timeRemain time remain
         */
        public void updateCountdownTime(long timeRemain) {
            mBinding.playActivityCountdownProgressIndicator
                    .setProgress((int) (timeRemain / 100), true);
            mBinding.playActivityCountdownTv.setText(
                    uIContext.getString(R.string.play_activity_countdown_time,
                            (int) (timeRemain / 1000))
            );
        }

        /**
         * Show score in limit time
         * @param time limit time show
         */
        public void showScoreInLimitTime(long time) {
            if (time == 0) time = 3000;
            listOrderRow.get(ownUserId).showUserParams(
                    uIContext, OrderRow.VIEW_PARAMS_SCORE);
            listOrderRow.get(competitorId).showUserParams(
                    uIContext, OrderRow.VIEW_PARAMS_SCORE);
            new CountDownTimer(time, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    listOrderRow.get(ownUserId).showUserParams(
                            uIContext, OrderRow.VIEW_PARAMS_NAME);
                    listOrderRow.get(competitorId).showUserParams(
                            uIContext, OrderRow.VIEW_PARAMS_NAME);
                }
            }.start();
        }

        /**
         * Set state to all choices except one choice
         * @param exceptChoice except this
         * @param state new state
         */
        public void updateOtherChoicesState(Choice exceptChoice, int state) {
            for (Choice mChoice : listChoices) {
                if (mChoice != exceptChoice) mChoice.setDecoration(state);
            }
        }

        /**
         * Show or hide sticker panel
         */
        public void toggleStickerPanel() {
            float newYAxis = 0;
            // Todo get sticker panel height
            if (!isShowStickerPanel){
                newYAxis = -800;
                Log.i("LeHieu", "toggleStickerPanel -800");

            }
            isShowStickerPanel = (!isShowStickerPanel);
            mBinding.playActivityMainCombat.animate().y(newYAxis).setDuration(500)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
//                            if (isShowStickerPanel) viewStickerData(); // gio la bam vao thi no moi bind cai nay nhi
                        }
                    })
                    .start();
        }

        /**
         * Show new message
         *
         * @param senderId sender Id
         * @param message  Lottie sticker message
         */
        public void addNewMessage(String senderId, LottieAnimationView message) {
            MessageBubble messageBubble = new MessageBubble(uIContext, null);
            message.setRepeatCount(LottieDrawable.INFINITE);
            message.setRepeatMode(LottieDrawable.RESTART);
            message.playAnimation();
            messageBubble.addSticker(message);
            listOrderRow.get(senderId).showNewMessage(messageBubble);
        }
    }
}