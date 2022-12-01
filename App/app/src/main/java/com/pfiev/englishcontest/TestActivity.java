package com.pfiev.englishcontest;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.pfiev.englishcontest.adapter.EmotionIconViewAdapter;
import com.pfiev.englishcontest.adapter.PackEmotionViewAdapter;
import com.pfiev.englishcontest.databinding.ActivityPlayGameBinding;
import com.pfiev.englishcontest.model.EmotionIconItem;
import com.pfiev.englishcontest.model.PackEmotionItem;
import com.pfiev.englishcontest.roomdb.AppDatabase;
import com.pfiev.englishcontest.roomdb.entity.EmotionIcon;
import com.pfiev.englishcontest.roomdb.entity.EmotionPack;
import com.pfiev.englishcontest.roomdb.migration.DatabaseMigration;
import com.pfiev.englishcontest.ui.playactivityelem.Choice;
import com.pfiev.englishcontest.ui.playactivityelem.MessageBubble;
import com.pfiev.englishcontest.ui.playactivityelem.OrderRow;
import com.pfiev.englishcontest.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestActivity extends AppCompatActivity implements PackEmotionViewAdapter.ItemClickListener, EmotionIconViewAdapter.EmotionItemClickListener {
    private ActivityPlayGameBinding binding;

    PackEmotionViewAdapter mPackAdapter;
    EmotionIconViewAdapter mEmotionAdapter;
    List<List<EmotionIconItem>> mListTotalEmotion = new ArrayList<>();
    List<PackEmotionItem> mListPack;
    boolean isShowStickerPanel = false;

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
//        binding.playActivityChoices.addView(new Choice(getApplicationContext()));
//        binding.playActivityChoices.addView(new Choice(getApplicationContext()));
//        binding.playActivityChoices.addView(new Choice(getApplicationContext()));
        Choice lastChoice = new Choice(getApplicationContext());
        lastChoice.setContentDisplay("I wouldn’t have gone there");
        lastChoice.deleteMarginBottom();

        binding.playActivityChoices.addView(lastChoice);

        binding.playActivityProgressBar.setProgress(12);
        ((ViewGroup) binding.playActivityMainCombat.getParent().getParent()).setClipChildren(false);
        ((ViewGroup) binding.playActivityMainCombat.getParent().getParent()).setClipToPadding(false);
        AppDatabase dbInstance = AppDatabase.getInstance(getApplicationContext());
//        DatabaseMigration.getInstance().upgradeDatabase(dbInstance);

        OrderRow firstRow = new OrderRow(getApplicationContext(), null);
        OrderRow secondRow = new OrderRow(getApplicationContext(), null);
        firstRow.setUserParams("This first Row");
        secondRow.orderId = 2;
//        firstRow.setUserParams("There second Row");
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

                secondRow.showNewMessage(createMessage());
                firstRow.showNewMessage(createMessage());
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
                new CountDownTimer(5000, 500) {

                    @Override
                    public void onTick(long l) {
                        Log.d("Test progress", "" + l);
                        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
                            @Override
                            public void run() {
//                                firstRow.swapPosition(secondRow);
                            }
                        });
                        binding.playActivityCountdownProgressIndicator
                                .setProgress((int) (l / 100), true);
                    }

                    @Override
                    public void onFinish() {
                        binding.playActivityCountdownProgressIndicator
                                .setProgress(0, true);
                    }
                }.start();

            }
        });
//        CollectionReference colRef = FirebaseFirestore.getInstance().collection(GlobalConstant.USERS);

        binding.playActivityCountdownProgressIndicator
                .setProgress(100);
//        binding.playActivityShowStickerPanel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MaterialSharedAxis materialSharedAxis = new MaterialSharedAxis(MaterialSharedAxis.Y, true);
//                binding.playActivityMainCombat.animate().translationY(-400).start();
//            }
//        });


        requestData();
//        viewStickerData();
    }

    private MessageBubble createMessage() {
        MessageBubble messageBubble = new MessageBubble(getApplicationContext(), null);
        LottieAnimationView lottie = new LottieAnimationView(getApplicationContext());
        lottie.setAnimation(R.raw.sticker_demo);
        lottie.setRepeatCount(LottieDrawable.INFINITE);
        lottie.setRepeatMode(LottieDrawable.RESTART);
        lottie.playAnimation();
        messageBubble.addSticker(lottie);
        return messageBubble;
    }

    private void viewStickerData() {
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.packEmotionRecyclerView.setLayoutManager(horizontalLayoutManager);
        if (mListTotalEmotion.get(0) == null || mListTotalEmotion.get(0).size() == 0) {
            mPackAdapter = new PackEmotionViewAdapter(this, mListPack, false);
        } else {
            mPackAdapter = new PackEmotionViewAdapter(this, mListPack, true);
        }
        mPackAdapter.setClickListener(this);
        binding.packEmotionRecyclerView.setAdapter(mPackAdapter);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
        binding.gridEmotionRecyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        List<EmotionIconItem> mListEmotion = new ArrayList<>();
        if (mListTotalEmotion.get(0) != null && mListTotalEmotion.get(0).size() == 0) {
            mListEmotion = mListTotalEmotion.get(0);
        }
        mEmotionAdapter = new EmotionIconViewAdapter(this, mListEmotion);
        binding.gridEmotionRecyclerView.setAdapter(mEmotionAdapter);
        mEmotionAdapter.setClickListener(this);
    }

    public void requestData() {
        mListTotalEmotion.clear();
        mListPack = new ArrayList<>();
        AppDatabase.getAllEmotionPack(getApplicationContext(),
                listPacks -> {
                    int[] packIds = new int[listPacks.length];
                    for (int i = 0; i < listPacks.length; i++) {
                        mListPack.add(new PackEmotionItem(listPacks[i]));
                        packIds[i] = listPacks[i].id;
                    }

                    AppDatabase.getEmotionsInPacks(
                            getApplicationContext(), packIds,
                            listIcons -> {
                                Map<Integer, List<EmotionIconItem>> map = new HashMap<>();
                                for (EmotionIcon icon : listIcons) {
                                    if (map.get(icon.packId) == null) {
                                        List<EmotionIconItem> listEmotion = new ArrayList<>();
                                        listEmotion.add(new EmotionIconItem(icon));
                                        map.put(icon.packId, listEmotion);
                                    } else
                                        map.get(icon.packId).add(new EmotionIconItem(icon));
                                }
                                for (EmotionPack listPack : listPacks) {
                                    int packId = listPack.id;
                                    mListTotalEmotion.add(map.get(packId));
                                }
                                AppDatabase.getAllRecentIcons(getApplicationContext(),
                                        listIcons1 -> {
                                            List<EmotionIconItem> listEmotion = new ArrayList<>();
                                            for (EmotionIcon icon : listIcons1) {
                                                listEmotion.add(new EmotionIconItem(icon));
                                            }
                                            mListTotalEmotion.set(0, listEmotion);

                                            TestActivity.this.runOnUiThread(() -> viewStickerData());
                                        });
                            });
                });


    }

    @Override
    public void onItemClick(int position) {
        Log.i("LeHieu", "Click on position" + position);
        List<EmotionIconItem> mListEmotion = mListTotalEmotion.get(position);
        if (position == 0 && mListEmotion.size() == 0) {
            binding.noRecentTv.setVisibility(View.VISIBLE);
            binding.gridEmotionRecyclerView.setVisibility(View.GONE);
        } else {
            binding.noRecentTv.setVisibility(View.GONE);
            binding.gridEmotionRecyclerView.setVisibility(View.VISIBLE);
            Log.d("LeHieu", "EmotionIconDBHelper mListEmotion size = " + mListEmotion.size());
            mEmotionAdapter = new EmotionIconViewAdapter(this, mListEmotion);
            mEmotionAdapter.setClickListener(this);
            binding.gridEmotionRecyclerView.setAdapter(mEmotionAdapter);
        }
    }

    @Override
    public void onEmotionItemClick(View view, int stickerId, String url, String rawUrI) {
        Log.i("LeHieu", "onEmotionItemClick on rawUrI" + rawUrI);
        updateRecentEmotion(url, stickerId);
        int imageResource = getApplicationContext().getResources().getIdentifier(rawUrI, null, getApplicationContext().getPackageName());
        LottieAnimationView message = new LottieAnimationView(getApplicationContext());
        message.setAnimation(imageResource);
//        mResultEmotion.cancelAnimation();
//        int imageResource =  getContext().getResources().getIdentifier(rawUrI, null, getContext().getApplicationContext().getPackageName());
//        mResultEmotion.setAnimation(imageResource);
//        mResultEmotion.setVisibility(View.VISIBLE);
//        mResultEmotion.playAnimation();
    }

    public void updateRecentEmotion(String url, int stickerId) {
        if (!Utility.isExistsEmotion(url,mListTotalEmotion.get(0))) {
            EmotionIcon emotionIcon = new EmotionIcon(0, "sticker0", "loti", url);
            emotionIcon.id = stickerId;
            EmotionIconItem emotionIconItem = new EmotionIconItem(emotionIcon);
            //Save to sqlite
            AppDatabase.addToRecentEmotionPack(getApplicationContext(), emotionIcon);
            mListTotalEmotion.get(0).add(emotionIconItem);
        }
    }
}