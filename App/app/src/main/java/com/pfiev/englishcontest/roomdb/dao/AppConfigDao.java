package com.pfiev.englishcontest.roomdb.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;
import com.pfiev.englishcontest.roomdb.AppDatabase;
import com.pfiev.englishcontest.roomdb.entity.AppConfig;

@Dao
public interface AppConfigDao {

    @Query("SELECT * FROM " + AppDatabase.APP_CONFIG_TABLE + " WHERE field = :fieldName")
    ListenableFuture<AppConfig> getConfig(String fieldName);

    @Update
    ListenableFuture<Integer> updateConfig(AppConfig appConfig);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertConfig(AppConfig appConfig);
}
