package com.pfiev.englishcontest.roomdb.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.pfiev.englishcontest.roomdb.AppDatabase;

@Entity(tableName = AppDatabase.APP_CONFIG_TABLE)
public class AppConfig {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "field")
    public String field;

    @ColumnInfo(name = "value")
    public String value;
}
