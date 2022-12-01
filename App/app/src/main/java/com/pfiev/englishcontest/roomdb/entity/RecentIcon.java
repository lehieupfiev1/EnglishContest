package com.pfiev.englishcontest.roomdb.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.pfiev.englishcontest.roomdb.AppDatabase;

import java.time.Instant;

@Entity(tableName = AppDatabase.RECENT_ICON_TABLE)
public class RecentIcon {

    public static final int MAX_RECENT_ICONS = 20;

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "icon_id")
    public int iconId;

    @ColumnInfo(name = "time_created")
    public long timeCreated;

    public RecentIcon(int iconId) {
        this.iconId = iconId;
        this.timeCreated = Instant.now().getEpochSecond();
    }
}