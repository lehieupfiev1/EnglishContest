package com.pfiev.englishcontest.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;
import com.pfiev.englishcontest.roomdb.AppDatabase;
import com.pfiev.englishcontest.roomdb.entity.EmotionPack;

import java.util.List;

@Dao
public interface EmotionPackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long[]> bulkInsertPack(List<EmotionPack> listEmotionPacks);

    @Query("SELECT * FROM " + AppDatabase.EMOTION_PACK_TABLE)
    ListenableFuture<EmotionPack[]> getAllPack();
}
