package com.pfiev.englishcontest.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.pfiev.englishcontest.model.EmotionIconItem;

import java.util.ArrayList;

public class EmotionIconDBHelper extends SQLiteOpenHelper {
    public static String TAG = "EmotionIconDBHelper";
    public static final String EMOTION_DATABASE_NAME = "EmotionManager.db";
    public static final String DATABASE_NAME = "EnglishContest.db";
    public static final String EMOTION_TABLE_NAME = "emotionDB";
    public static final String EMOTION_COLUMN_ID = "id";
    public static final String EMOTION_COLUMN_PACK_NAME = "pack_name";
    public static final String EMOTION_COLUMN_NAME = "name";
    public static final String EMOTION_COLUMN_TYPE = "type";
    public static final String EMOTION_COLUMN_URL = "url";
    private static EmotionIconDBHelper instance = null;


    public EmotionIconDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static synchronized EmotionIconDBHelper getInstance(Context context) {
        Log.i(TAG, "getInstance ListEmotionsDBHelper");
        if (instance == null) {
            instance = new EmotionIconDBHelper(context, EMOTION_DATABASE_NAME, null, 1);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_emotion_table = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT)", EMOTION_TABLE_NAME, EMOTION_COLUMN_ID, EMOTION_COLUMN_PACK_NAME, EMOTION_COLUMN_NAME, EMOTION_COLUMN_TYPE, EMOTION_COLUMN_URL);
        sqLiteDatabase.execSQL(create_emotion_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String drop_emotion_table = String.format("DROP TABLE IF EXISTS %s", EMOTION_TABLE_NAME);
        sqLiteDatabase.execSQL(drop_emotion_table);

        onCreate(sqLiteDatabase);
    }

    public void addEmotionIcon(EmotionIconItem iconItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EMOTION_COLUMN_PACK_NAME, iconItem.getPack_name());
        values.put(EMOTION_COLUMN_NAME, iconItem.getName());
        values.put(EMOTION_COLUMN_TYPE, iconItem.getType());
        values.put(EMOTION_COLUMN_URL, iconItem.getUrl());

        db.insert(EMOTION_TABLE_NAME, null, values);
        db.close();
    }

    // Get Emotion detail  based on pack name

    public ArrayList<EmotionIconItem> getEmotionListByPackName(String packName){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<EmotionIconItem> emotionList = new ArrayList<>();
        String query = "SELECT id, name, type, url FROM "+ EMOTION_TABLE_NAME;
        Cursor cursor = db.query(EMOTION_TABLE_NAME, new String[]{EMOTION_COLUMN_ID, EMOTION_COLUMN_NAME, EMOTION_COLUMN_TYPE,EMOTION_COLUMN_URL }, EMOTION_COLUMN_PACK_NAME+ "=?",new String[]{packName},null, null, null, null);
        while (cursor.moveToNext()){
            EmotionIconItem emotionItem = new EmotionIconItem();
            emotionItem.setId(cursor.getString(0));
            emotionItem.setName(cursor.getString(1));
            emotionItem.setType(cursor.getString(2));
            emotionItem.setUrl(cursor.getString(3));
            emotionItem.setPack_name(packName);
            emotionList.add(emotionItem);
            Log.i(TAG, "getEmotionListByPackName "+ emotionItem.toString());
        }
        return emotionList;
    }

    public void deleteEmotionById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(EMOTION_TABLE_NAME, EMOTION_COLUMN_ID + " = ?",
                new String[] {id});
        db.close();
    }

    public int updateEmotionIcon(EmotionIconItem emotionItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EMOTION_COLUMN_NAME, emotionItem.getName());
        values.put(EMOTION_COLUMN_PACK_NAME, emotionItem.getPack_name());
        values.put(EMOTION_COLUMN_TYPE, emotionItem.getType());
        values.put(EMOTION_COLUMN_URL, emotionItem.getUrl());
        // updating row
        return db.update(EMOTION_TABLE_NAME, values, EMOTION_COLUMN_ID + " = ?",
                new String[] { emotionItem.getId() });
    }

}
