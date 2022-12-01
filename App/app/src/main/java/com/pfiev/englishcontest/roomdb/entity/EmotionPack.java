package com.pfiev.englishcontest.roomdb.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.pfiev.englishcontest.roomdb.AppDatabase;

import java.time.Instant;

@Entity(tableName = AppDatabase.EMOTION_PACK_TABLE)
public class EmotionPack {

    public static final String RECENT_PACK_NAME = "recent_pack";
    public static final int MAX_RECENT_PACK_ICONS = 20;

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "size")
    public int size;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "time_created")
    public long timeCreated;

    public EmotionPack(String name, int size, String description, String url) {
        this.name = name;
        this.size = size;
        this.description = description;
        this.url = url;
        this.timeCreated = Instant.now().getEpochSecond();
    }

}
