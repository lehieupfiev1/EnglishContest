package com.pfiev.englishcontest.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;
import com.pfiev.englishcontest.roomdb.AppDatabase;
import com.pfiev.englishcontest.roomdb.entity.EmotionIcon;
import com.pfiev.englishcontest.roomdb.entity.EmotionPack;

import java.util.List;

@Dao
public interface EmotionIconDao {

    @Query("SELECT * FROM " + AppDatabase.EMOTION_ICON_TABLE + " WHERE pack_id IN (:listPackIds) ")
    ListenableFuture<EmotionIcon[]> getEmotionsInPacks(int[] listPackIds);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long[]> bulkInsertIcons(List<EmotionIcon> listEmotionIcons);

}
