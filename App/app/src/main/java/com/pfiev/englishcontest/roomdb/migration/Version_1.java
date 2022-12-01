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

public class Version_1 implements DatabaseMigration.DbVersion {

    public void importData(AppDatabase dbInstance) {
        long startTime = Instant.now().toEpochMilli();
        List<EmotionPack> listPacks = new ArrayList<>();
        listPacks.add(new EmotionPack(EmotionPack.RECENT_PACK_NAME, 0, "Recent sticker use in here", "recent_emotion"));
        listPacks.add(new EmotionPack("cat", 11, "description about cat", "cat_emotion"));
        listPacks.add(new EmotionPack("diggy", 12, "description about dyggy", "diggy_emotion"));
        listPacks.add(new EmotionPack("girl", 11, "description about girl", "girl_emotion"));
        listPacks.add(new EmotionPack("joey", 12, "description about jooey", "jooey_emotion"));
        listPacks.add(new EmotionPack("senya", 11, "description about senya", "senya_emotion"));
        listPacks.add(new EmotionPack("shark", 10, "description about jooey", "shark_emotion"));
        ListenableFuture<Long[]> insertResult = dbInstance.emotionPackDao()
                .bulkInsertPack(listPacks);
        Futures.addCallback(
                insertResult,
                new FutureCallback<Long[]>() {
                    @Override
                    public void onSuccess(@Nullable Long[] result) {
//                        Long[] listIds = result;
                        List<EmotionIcon> listIcons = new ArrayList<>();
                        // Add shark icons
                        assert result != null;
                        int catPackId = result[1].intValue();
                        listIcons.add(new EmotionIcon(catPackId, "sticker0", "loti", "cat_sticker0"));
                        listIcons.add(new EmotionIcon(catPackId, "sticker1", "loti", "cat_sticker1"));
                        listIcons.add(new EmotionIcon(catPackId, "sticker2", "loti", "cat_sticker2"));
                        listIcons.add(new EmotionIcon(catPackId, "sticker3", "loti", "cat_sticker3"));
                        listIcons.add(new EmotionIcon(catPackId, "sticker4", "loti", "cat_sticker4"));
                        listIcons.add(new EmotionIcon(catPackId, "sticker5", "loti", "cat_sticker5"));
                        listIcons.add(new EmotionIcon(catPackId, "sticker6", "loti", "cat_sticker6"));
                        listIcons.add(new EmotionIcon(catPackId, "sticker7", "loti", "cat_sticker7"));
                        listIcons.add(new EmotionIcon(catPackId, "sticker8", "loti", "cat_sticker8"));
                        listIcons.add(new EmotionIcon(catPackId, "sticker9", "loti", "cat_sticker9"));
                        listIcons.add(new EmotionIcon(catPackId, "sticker10", "loti", "cat_sticker10"));

                        // Add diggy pack
                        int diggyPackId = result[2].intValue();
                        listIcons.add(new EmotionIcon(diggyPackId, "sticker0", "loti", "diggy_sticker0"));
                        listIcons.add(new EmotionIcon(diggyPackId, "sticker1", "loti", "diggy_sticker1"));
                        listIcons.add(new EmotionIcon(diggyPackId, "sticker2", "loti", "diggy_sticker2"));
                        listIcons.add(new EmotionIcon(diggyPackId, "sticker3", "loti", "diggy_sticker3"));
                        listIcons.add(new EmotionIcon(diggyPackId, "sticker4", "loti", "diggy_sticker4"));
                        listIcons.add(new EmotionIcon(diggyPackId, "sticker5", "loti", "diggy_sticker5"));
                        listIcons.add(new EmotionIcon(diggyPackId, "sticker6", "loti", "diggy_sticker6"));
                        listIcons.add(new EmotionIcon(diggyPackId, "sticker7", "loti", "diggy_sticker7"));
                        listIcons.add(new EmotionIcon(diggyPackId, "sticker8", "loti", "diggy_sticker8"));
                        listIcons.add(new EmotionIcon(diggyPackId, "sticker9", "loti", "diggy_sticker9"));
                        listIcons.add(new EmotionIcon(diggyPackId, "sticker10", "loti", "diggy_sticker10"));
                        listIcons.add(new EmotionIcon(diggyPackId, "sticker11", "loti", "diggy_sticker11"));

                        // Add girl pack
                        int girlPackId = result[3].intValue();
                        listIcons.add(new EmotionIcon(girlPackId, "sticker0", "loti", "girl_sticker0"));
                        listIcons.add(new EmotionIcon(girlPackId, "sticker1", "loti", "girl_sticker1"));
                        listIcons.add(new EmotionIcon(girlPackId, "sticker2", "loti", "girl_sticker2"));
                        listIcons.add(new EmotionIcon(girlPackId, "sticker3", "loti", "girl_sticker3"));
                        listIcons.add(new EmotionIcon(girlPackId, "sticker4", "loti", "girl_sticker4"));
                        listIcons.add(new EmotionIcon(girlPackId, "sticker5", "loti", "girl_sticker5"));
                        listIcons.add(new EmotionIcon(girlPackId, "sticker6", "loti", "girl_sticker6"));
                        listIcons.add(new EmotionIcon(girlPackId, "sticker7", "loti", "girl_sticker7"));
                        listIcons.add(new EmotionIcon(girlPackId, "sticker8", "loti", "girl_sticker8"));
                        listIcons.add(new EmotionIcon(girlPackId, "sticker9", "loti", "girl_sticker9"));
                        listIcons.add(new EmotionIcon(girlPackId, "sticker10", "loti", "girl_sticker10"));

                        // Add joey pack
                        int joeyPackId = result[4].intValue();
                        listIcons.add(new EmotionIcon(joeyPackId, "sticker0", "loti", "jooey_sticker0"));
                        listIcons.add(new EmotionIcon(joeyPackId, "sticker1", "loti", "jooey_sticker1"));
                        listIcons.add(new EmotionIcon(joeyPackId, "sticker2", "loti", "jooey_sticker2"));
                        listIcons.add(new EmotionIcon(joeyPackId, "sticker3", "loti", "jooey_sticker3"));
                        listIcons.add(new EmotionIcon(joeyPackId, "sticker4", "loti", "jooey_sticker4"));
                        listIcons.add(new EmotionIcon(joeyPackId, "sticker5", "loti", "jooey_sticker5"));
                        listIcons.add(new EmotionIcon(joeyPackId, "sticker6", "loti", "jooey_sticker6"));
                        listIcons.add(new EmotionIcon(joeyPackId, "sticker7", "loti", "jooey_sticker7"));
                        listIcons.add(new EmotionIcon(joeyPackId, "sticker8", "loti", "jooey_sticker8"));
                        listIcons.add(new EmotionIcon(joeyPackId, "sticker9", "loti", "jooey_sticker9"));
                        listIcons.add(new EmotionIcon(joeyPackId, "sticker10", "loti", "jooey_sticker10"));
                        listIcons.add(new EmotionIcon(joeyPackId, "sticker11", "loti", "jooey_sticker11"));

                        // Add senya pack
                        int senyaPackId = result[5].intValue();
                        listIcons.add(new EmotionIcon(senyaPackId, "sticker0", "loti", "senya_sticker0"));
                        listIcons.add(new EmotionIcon(senyaPackId, "sticker1", "loti", "senya_sticker1"));
                        listIcons.add(new EmotionIcon(senyaPackId, "sticker2", "loti", "senya_sticker2"));
                        listIcons.add(new EmotionIcon(senyaPackId, "sticker3", "loti", "senya_sticker3"));
                        listIcons.add(new EmotionIcon(senyaPackId, "sticker4", "loti", "senya_sticker4"));
                        listIcons.add(new EmotionIcon(senyaPackId, "sticker5", "loti", "senya_sticker5"));
                        listIcons.add(new EmotionIcon(senyaPackId, "sticker6", "loti", "senya_sticker6"));
                        listIcons.add(new EmotionIcon(senyaPackId, "sticker7", "loti", "senya_sticker7"));
                        listIcons.add(new EmotionIcon(senyaPackId, "sticker8", "loti", "senya_sticker8"));
                        listIcons.add(new EmotionIcon(senyaPackId, "sticker9", "loti", "senya_sticker9"));
                        listIcons.add(new EmotionIcon(senyaPackId, "sticker10", "loti", "senya_sticker10"));

                        // Add shark pack
                        int sharkPackId = result[6].intValue();
                        listIcons.add(new EmotionIcon(sharkPackId, "sticker0", "loti", "shark_sticker0"));
                        listIcons.add(new EmotionIcon(sharkPackId, "sticker1", "loti", "shark_sticker1"));
                        listIcons.add(new EmotionIcon(sharkPackId, "sticker2", "loti", "shark_sticker2"));
                        listIcons.add(new EmotionIcon(sharkPackId, "sticker3", "loti", "shark_sticker3"));
                        listIcons.add(new EmotionIcon(sharkPackId, "sticker4", "loti", "shark_sticker4"));
                        listIcons.add(new EmotionIcon(sharkPackId, "sticker5", "loti", "shark_sticker5"));
                        listIcons.add(new EmotionIcon(sharkPackId, "sticker6", "loti", "shark_sticker6"));
                        listIcons.add(new EmotionIcon(sharkPackId, "sticker7", "loti", "shark_sticker7"));
                        listIcons.add(new EmotionIcon(sharkPackId, "sticker8", "loti", "shark_sticker8"));
                        listIcons.add(new EmotionIcon(sharkPackId, "sticker9", "loti", "shark_sticker9"));

                        // Insert to db
                        dbInstance.emotionIconDao().bulkInsertIcons(listIcons);
                        long duration = Instant.now().toEpochMilli() - startTime;
                        Log.d("Version 1", " total time: " + duration);
                    }

                    @Override
                    public void onFailure(Throwable t) {}
                },
                Executors.newSingleThreadExecutor()
        );
    }
}
