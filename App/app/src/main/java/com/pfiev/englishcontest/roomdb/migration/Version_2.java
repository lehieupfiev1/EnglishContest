package com.pfiev.englishcontest.roomdb.migration;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.pfiev.englishcontest.roomdb.AppDatabase;
import com.pfiev.englishcontest.roomdb.entity.EmotionIcon;
import com.pfiev.englishcontest.roomdb.entity.EmotionPack;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class Version_2 implements DatabaseMigration.DbVersion {

    public void importData(AppDatabase dbInstance) {
        long startTime = Instant.now().toEpochMilli();
        List<EmotionPack> listPacks = new ArrayList<>();
        listPacks.add(new EmotionPack("perchick", 11, "description about perchick", "perchick_emotion"));
        listPacks.add(new EmotionPack("mysticise", 11, "description about mysticise", "mysticise_emotion"));
        listPacks.add(new EmotionPack("towelie", 10, "description about towelie_emotion", "towelie_emotion"));
        listPacks.add(new EmotionPack("pepetopanim", 8, "description about pepe", "pepetopanim_emotion"));
        listPacks.add(new EmotionPack("orangoutang", 6, "description about orangoutang", "orangoutang_emotion"));
        ListenableFuture<Long[]> insertResult = dbInstance.emotionPackDao()
                .bulkInsertPack(listPacks);
        Futures.addCallback(
                insertResult,
                new FutureCallback<Long[]>() {
                    @Override
                    public void onSuccess(@Nullable Long[] result) {
                        List<EmotionIcon> listIcons = new ArrayList<>();
                        // Add perChick icons
                        assert result != null;
                        int perChickPackId = result[0].intValue();
                        listIcons.add(new EmotionIcon(perChickPackId, "sticker0", "loti", "perchick_sticker0"));
                        listIcons.add(new EmotionIcon(perChickPackId, "sticker1", "loti", "perchick_sticker1"));
                        listIcons.add(new EmotionIcon(perChickPackId, "sticker2", "loti", "perchick_sticker2"));
                        listIcons.add(new EmotionIcon(perChickPackId, "sticker3", "loti", "perchick_sticker3"));
                        listIcons.add(new EmotionIcon(perChickPackId, "sticker4", "loti", "perchick_sticker4"));
                        listIcons.add(new EmotionIcon(perChickPackId, "sticker5", "loti", "perchick_sticker5"));
                        listIcons.add(new EmotionIcon(perChickPackId, "sticker6", "loti", "perchick_sticker6"));
                        listIcons.add(new EmotionIcon(perChickPackId, "sticker7", "loti", "perchick_sticker7"));
                        listIcons.add(new EmotionIcon(perChickPackId, "sticker8", "loti", "perchick_sticker8"));
                        listIcons.add(new EmotionIcon(perChickPackId, "sticker9", "loti", "perchick_sticker9"));
                        listIcons.add(new EmotionIcon(perChickPackId, "sticker10", "loti", "perchick_sticker10"));

                        // Add mysticise icons
                        int mysticPackId = result[1].intValue();
                        listIcons.add(new EmotionIcon(mysticPackId, "sticker0", "loti", "mysticise_sticker0"));
                        listIcons.add(new EmotionIcon(mysticPackId, "sticker1", "loti", "mysticise_sticker1"));
                        listIcons.add(new EmotionIcon(mysticPackId, "sticker2", "loti", "mysticise_sticker2"));
                        listIcons.add(new EmotionIcon(mysticPackId, "sticker3", "loti", "mysticise_sticker3"));
                        listIcons.add(new EmotionIcon(mysticPackId, "sticker4", "loti", "mysticise_sticker4"));
                        listIcons.add(new EmotionIcon(mysticPackId, "sticker5", "loti", "mysticise_sticker5"));
                        listIcons.add(new EmotionIcon(mysticPackId, "sticker6", "loti", "mysticise_sticker6"));
                        listIcons.add(new EmotionIcon(mysticPackId, "sticker7", "loti", "mysticise_sticker7"));
                        listIcons.add(new EmotionIcon(mysticPackId, "sticker8", "loti", "mysticise_sticker8"));
                        listIcons.add(new EmotionIcon(mysticPackId, "sticker9", "loti", "mysticise_sticker9"));
                        listIcons.add(new EmotionIcon(mysticPackId, "sticker10", "loti", "mysticise_sticker10"));

                        // Add towel icons
                        int towelPackId = result[2].intValue();
                        listIcons.add(new EmotionIcon(towelPackId, "sticker0", "loti", "towelie_sticker0"));
                        listIcons.add(new EmotionIcon(towelPackId, "sticker1", "loti", "towelie_sticker1"));
                        listIcons.add(new EmotionIcon(towelPackId, "sticker2", "loti", "towelie_sticker2"));
                        listIcons.add(new EmotionIcon(towelPackId, "sticker3", "loti", "towelie_sticker3"));
                        listIcons.add(new EmotionIcon(towelPackId, "sticker4", "loti", "towelie_sticker4"));
                        listIcons.add(new EmotionIcon(towelPackId, "sticker5", "loti", "towelie_sticker5"));
                        listIcons.add(new EmotionIcon(towelPackId, "sticker6", "loti", "towelie_sticker6"));
                        listIcons.add(new EmotionIcon(towelPackId, "sticker7", "loti", "towelie_sticker7"));
                        listIcons.add(new EmotionIcon(towelPackId, "sticker8", "loti", "towelie_sticker8"));
                        listIcons.add(new EmotionIcon(towelPackId, "sticker9", "loti", "towelie_sticker9"));

                        // Add pepe icons
                        int pepePackId = result[3].intValue();
                        listIcons.add(new EmotionIcon(pepePackId, "sticker0", "loti", "pepetopanim_sticker0"));
                        listIcons.add(new EmotionIcon(pepePackId, "sticker1", "loti", "pepetopanim_sticker1"));
                        listIcons.add(new EmotionIcon(pepePackId, "sticker2", "loti", "pepetopanim_sticker2"));
                        listIcons.add(new EmotionIcon(pepePackId, "sticker3", "loti", "pepetopanim_sticker3"));
                        listIcons.add(new EmotionIcon(pepePackId, "sticker4", "loti", "pepetopanim_sticker4"));
                        listIcons.add(new EmotionIcon(pepePackId, "sticker5", "loti", "pepetopanim_sticker5"));
                        listIcons.add(new EmotionIcon(pepePackId, "sticker6", "loti", "pepetopanim_sticker6"));
                        listIcons.add(new EmotionIcon(pepePackId, "sticker7", "loti", "pepetopanim_sticker7"));

                        // Add orangoutang icons
                        int oranPackId = result[4].intValue();
                        listIcons.add(new EmotionIcon(oranPackId, "sticker0", "loti", "orangoutang_sticker0"));
                        listIcons.add(new EmotionIcon(oranPackId, "sticker1", "loti", "orangoutang_sticker1"));
                        listIcons.add(new EmotionIcon(oranPackId, "sticker2", "loti", "orangoutang_sticker2"));
                        listIcons.add(new EmotionIcon(oranPackId, "sticker3", "loti", "orangoutang_sticker3"));
                        listIcons.add(new EmotionIcon(oranPackId, "sticker4", "loti", "orangoutang_sticker4"));
                        listIcons.add(new EmotionIcon(oranPackId, "sticker5", "loti", "orangoutang_sticker5"));

                        // Insert to db
                        dbInstance.emotionIconDao().bulkInsertIcons(listIcons);
                        long duration = Instant.now().toEpochMilli() - startTime;
                        Log.d("Version 2", " total time: " + duration);
                    }

                    @Override
                    public void onFailure(Throwable t) {}
                },
                Executors.newSingleThreadExecutor()
        );
    }
}
