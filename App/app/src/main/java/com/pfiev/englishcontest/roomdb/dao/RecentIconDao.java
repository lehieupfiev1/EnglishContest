package com.pfiev.englishcontest.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;
import com.pfiev.englishcontest.roomdb.AppDatabase;
import com.pfiev.englishcontest.roomdb.entity.EmotionIcon;
import com.pfiev.englishcontest.roomdb.entity.RecentIcon;

@Dao
public interface RecentIconDao {

    @Query("SELECT emotionIcon.*  FROM recentIcon INNER JOIN emotionIcon " +
            "ON recentIcon.icon_id = emotionIcon.id ORDER BY recentIcon.time_created DESC")
    ListenableFuture<EmotionIcon[]> getAllRecentIcon();

    @Query("SELECT count(*) FROM " + AppDatabase.RECENT_ICON_TABLE)
    ListenableFuture<Integer> countAllInRecentPack();

    @Insert
    ListenableFuture<Long> insertIconsToRecentPack(RecentIcon icon);

    @Query("SELECT * FROM recentIcon ORDER BY time_created asc limit 1")
    ListenableFuture<RecentIcon> getOldestIconInPack();

    @Update
    void updateIcon(RecentIcon icon);

    @Query("SELECT * FROM recentIcon WHERE icon_id = :iconId")
    ListenableFuture<RecentIcon> getRecentIcon(int iconId);
}
