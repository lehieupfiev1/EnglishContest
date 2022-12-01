package com.pfiev.englishcontest.roomdb;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.pfiev.englishcontest.roomdb.dao.AppConfigDao;
import com.pfiev.englishcontest.roomdb.dao.EmotionIconDao;
import com.pfiev.englishcontest.roomdb.dao.EmotionPackDao;
import com.pfiev.englishcontest.roomdb.dao.RecentIconDao;
import com.pfiev.englishcontest.roomdb.entity.AppConfig;
import com.pfiev.englishcontest.roomdb.entity.EmotionIcon;
import com.pfiev.englishcontest.roomdb.entity.EmotionPack;
import com.pfiev.englishcontest.roomdb.entity.RecentIcon;

import java.time.Instant;
import java.util.concurrent.Executors;


@Database(entities = {EmotionIcon.class, EmotionPack.class,
        AppConfig.class, RecentIcon.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase dbInstance = null;

    private static final String DB_NAME = "MyEnglishDb";
    public static final String EMOTION_ICON_TABLE = "emotionIcon";
    public static final String EMOTION_PACK_TABLE = "emotionPack";
    public static final String RECENT_ICON_TABLE = "recentIcon";
    public static final String APP_CONFIG_TABLE = "appConfig";

    public static AppDatabase getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                    .build();
        }
        return dbInstance;
    }

    /**
     * Get all Emotion pack
     *
     * @param context
     * @param callback
     */
    public static void getAllEmotionPack(Context context, AllEmotionPackCallback callback) {
        if (dbInstance == null) getInstance(context);
        ListenableFuture<EmotionPack[]> listPacks = dbInstance.emotionPackDao().getAllPack();
        Futures.addCallback(
                listPacks,
                new FutureCallback<EmotionPack[]>() {
                    @Override
                    public void onSuccess(@Nullable EmotionPack[] result) {
                        callback.run(result);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                },
                Executors.newSingleThreadExecutor()
        );
    }

    /**
     * Get all emotion icons in packs
     *
     * @param context
     * @param packIds
     * @param callback
     */
    public static void getEmotionsInPacks(Context context, int[] packIds,
                                          EmotionsInPackCallback callback) {
        if (dbInstance == null) getInstance(context);
        ListenableFuture<EmotionIcon[]> listPacks = dbInstance.emotionIconDao()
                .getEmotionsInPacks(packIds);
        Futures.addCallback(
                listPacks,
                new FutureCallback<EmotionIcon[]>() {
                    @Override
                    public void onSuccess(@Nullable EmotionIcon[] result) {
                        callback.run(result);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                },
                Executors.newSingleThreadExecutor()
        );
    }

    public static void getAllRecentIcons(Context context, EmotionsInPackCallback callback) {
        if (dbInstance == null) getInstance(context);
        ListenableFuture<EmotionIcon[]> recentIcons = dbInstance.recentIconDao()
                .getAllRecentIcon();
        Futures.addCallback(
                recentIcons,
                new FutureCallback<EmotionIcon[]>() {
                    @Override
                    public void onSuccess(@Nullable EmotionIcon[] result) {
                        callback.run(result);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                },
                Executors.newSingleThreadExecutor()
        );
    }

    /**
     * Add icon to recent emotions used
     *
     * @param context
     * @param emotionIcon
     */
    public static void addToRecentEmotionPack(Context context, EmotionIcon emotionIcon) {
        if (dbInstance == null) getInstance(context);
        RecentIconDao recentDao = dbInstance.recentIconDao();
        ListenableFuture<RecentIcon> recentIcon = recentDao.getRecentIcon(emotionIcon.id);
        Futures.addCallback(
                recentIcon,
                new FutureCallback<RecentIcon>() {
                    @Override
                    public void onSuccess(@Nullable RecentIcon result) {
                        if (result == null) {
                            dbInstance.insertRecentIconNotExists(recentDao, emotionIcon);
                        } else {
                            result.timeCreated = Instant.now().getEpochSecond();
                            recentDao.updateIcon(result);
                        }
                        ;
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                },
                Executors.newSingleThreadExecutor()
        );
    }

    /**
     * Insert icon not exists to recent icons
     *
     * @param recentDao
     * @param emotionIcon
     */
    private void insertRecentIconNotExists(RecentIconDao recentDao, EmotionIcon emotionIcon) {
        ListenableFuture<Integer> countAll = recentDao.countAllInRecentPack();
        Futures.addCallback(
                countAll,
                new FutureCallback<Integer>() {
                    @Override
                    public void onSuccess(@Nullable Integer result) {
                        if (result.intValue() < EmotionPack.MAX_RECENT_PACK_ICONS) {
                            // If not great than max recent pack icon then insert new one
                            recentDao.insertIconsToRecentPack(
                                    new RecentIcon(emotionIcon.id));
                        } else {
                            // Otherwise will update to oldest icon
                            ListenableFuture<RecentIcon> oldestIconInPack = recentDao
                                    .getOldestIconInPack();
                            Futures.addCallback(
                                    oldestIconInPack,
                                    new FutureCallback<RecentIcon>() {
                                        @Override
                                        public void onSuccess(@Nullable RecentIcon icon) {
                                            icon.iconId = emotionIcon.id;
                                            icon.timeCreated = Instant.now().getEpochSecond();
                                            recentDao.updateIcon(icon);
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                        }
                                    },
                                    Executors.newSingleThreadExecutor()
                            );
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                },
                Executors.newSingleThreadExecutor()
        );
    }

    /**
     * Call back when get all emotion pack
     */
    public interface AllEmotionPackCallback {
        void run(EmotionPack[] result);
    }

    /**
     * Call back when get all icons
     */
    public interface EmotionsInPackCallback {
        void run(EmotionIcon[] result);
    }

    public abstract EmotionIconDao emotionIconDao();

    public abstract EmotionPackDao emotionPackDao();

    public abstract AppConfigDao appConfigDao();

    public abstract RecentIconDao recentIconDao();

}
