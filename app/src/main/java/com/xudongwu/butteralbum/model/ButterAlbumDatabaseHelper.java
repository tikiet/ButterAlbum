package com.xudongwu.butteralbum.model;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xudongwu.butteralbum.model.ButterAlbumContract.*;

public class ButterAlbumDatabaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "ButterAlbum.sqlite3";
    private static ButterAlbumDatabaseHelper INSTANCE;
    private Context mContext;

    public ButterAlbumDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION, null);
        mContext = context;
    }

    public static synchronized ButterAlbumDatabaseHelper getInstance(Context context) {
       if (INSTANCE == null) {
           INSTANCE = new ButterAlbumDatabaseHelper(context);
       }
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createFeedTable(db);
    }

    private void createFeedTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.FEED);
        db.execSQL("CREATE TABLE " + Tables.FEED + "(" +
                Feed._ID + " INTEGER PRIMARY KEY, " +
                Feed.IMAGE_ID + " TEXT NOT NULL UNIQUE, " +
                Feed.URL + " TEXT NOT NULL, " +
                Feed.TIMESTAMP + " INTEGER NOT NULL DEFAULT 0," +
                Feed.USER_AVATAR + " TEXT NOT NULL, " +
                Feed.USER_NAME + " TEXT NOT NULL" +
                ")");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
