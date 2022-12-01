package com.pfiev.englishcontest.roomdb.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.pfiev.englishcontest.roomdb.AppDatabase;

import java.time.Instant;

@Entity(tableName = AppDatabase.EMOTION_ICON_TABLE)
public class EmotionIcon {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "pack_id")
    public int packId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "time_created")
    public long timeCreated;

    public EmotionIcon(int packId, String name, String type, String url) {
        this.packId = packId;
        this.name = name;
        this.type = type;
        this.url = url;
        this.timeCreated = Instant.now().getEpochSecond();
    }
}
