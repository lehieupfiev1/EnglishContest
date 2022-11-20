package com.pfiev.englishcontest.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.pfiev.englishcontest.model.PackEmotionItem;

import java.util.ArrayList;

public class ListEmotionsDBHelper extends SQLiteOpenHelper {
    public static String TAG = "ListEmotionsDBHelper";
    public static final String PACK_DATABASE_NAME = "PackManager.db";
    public static final String LIST_EMOTION_TABLE_NAME = "ListEmotionDB";
    public static final String LIST_EMOTION_COLUMN_ID = "id";
    public static final String LIST_EMOTION_COLUMN_NAME = "name";
    public static final String LIST_EMOTION_COLUMN_SIZE = "size";
    public static final String LIST_EMOTION_COLUMN_DESCRIPTION= "description";
    public static final String LIST_EMOTION_COLUMN_URL= "url";
    public static final int EMOTION_MAX_COLUMN_SIZE = 10;
    private static ListEmotionsDBHelper instance = null;

    public ListEmotionsDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static synchronized ListEmotionsDBHelper getInstance(Context context) {
        Log.i(TAG, "getInstance ListEmotionsDBHelper");
        if (instance == null) {
            instance = new ListEmotionsDBHelper(context, PACK_DATABASE_NAME, null, 1);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String list_emotion_table = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT)", LIST_EMOTION_TABLE_NAME, LIST_EMOTION_COLUMN_ID, LIST_EMOTION_COLUMN_NAME, LIST_EMOTION_COLUMN_SIZE, LIST_EMOTION_COLUMN_DESCRIPTION, LIST_EMOTION_COLUMN_URL);
        sqLiteDatabase.execSQL(list_emotion_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String drop_list_emotion_table = String.format("DROP TABLE IF EXISTS %s", LIST_EMOTION_TABLE_NAME);
        sqLiteDatabase.execSQL(drop_list_emotion_table);

        onCreate(sqLiteDatabase);
    }

    public void addListEmotion(PackEmotionItem packItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LIST_EMOTION_COLUMN_NAME, packItem.getName());
        values.put(LIST_EMOTION_COLUMN_SIZE, packItem.getSize());
        values.put(LIST_EMOTION_COLUMN_DESCRIPTION, packItem.getDescription());
        values.put(LIST_EMOTION_COLUMN_URL, packItem.getUrl());

        db.insert(LIST_EMOTION_TABLE_NAME, null, values);
        Log.i(TAG, "addListEmotion : PackEmotionItem"+packItem.getUrl());
        db.close();
    }

    public ArrayList<PackEmotionItem> getAllPackEmotion(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<PackEmotionItem> packList = new ArrayList<>();
        String query = "SELECT id, name, size, description,url FROM "+ LIST_EMOTION_TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){

            PackEmotionItem packItem = new PackEmotionItem();
            packItem.setId(cursor.getString(0));
            packItem.setName(cursor.getString(1));
            packItem.setSize(cursor.getString(2));
            packItem.setDescription(cursor.getString(3));
            packItem.setUrl(cursor.getString(4));
            Log.i(TAG, "getAllPackEmotion : Url"+ cursor.getString(4));
            packList.add(packItem);
        }
        return  packList;
    }

//    public ArrayList<PackEmotionItem> getAllPackEmotionWithRecentPack(){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ArrayList<PackEmotionItem> packList = new ArrayList<>();
//        //Add Recent Pack
//        PackEmotionItem recentPackItem = new PackEmotionItem();
//        recentPackItem.setId("0");
//        recentPackItem.setName("recent_emotion");
//        recentPackItem.setSize("0");
//        recentPackItem.setDescription("Add recent emotion");
//        recentPackItem.setUrl("recent_emotion");
//        packList.add(recentPackItem);
//
//        String query = "SELECT id, name, size, description,url FROM "+ LIST_EMOTION_TABLE_NAME;
//        Cursor cursor = db.rawQuery(query,null);
//        while (cursor.moveToNext()){
//
//            PackEmotionItem packItem = new PackEmotionItem();
//            packItem.setId(cursor.getString(0));
//            packItem.setName(cursor.getString(1));
//            packItem.setSize(cursor.getString(2));
//            packItem.setDescription(cursor.getString(3));
//            packItem.setUrl(cursor.getString(4));
//            Log.i(TAG, "getAllPackEmotion : Url"+ cursor.getString(4));
//            packList.add(packItem);
//        }
//        return  packList;
//    }

    public void deletePackByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LIST_EMOTION_TABLE_NAME, LIST_EMOTION_COLUMN_NAME + " = ?",
                new String[] {name});
        db.close();
    }

    public int updatePackEmotion(PackEmotionItem packItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LIST_EMOTION_COLUMN_NAME, packItem.getName());
        values.put(LIST_EMOTION_COLUMN_SIZE, packItem.getSize());
        values.put(LIST_EMOTION_COLUMN_DESCRIPTION, packItem.getDescription());
        values.put(LIST_EMOTION_COLUMN_URL, packItem.getUrl());
        // updating row
        return db.update(LIST_EMOTION_TABLE_NAME, values, LIST_EMOTION_COLUMN_ID + " = ?",
                new String[] { packItem.getId() });
    }
}
