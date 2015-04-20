package com.xudongwu.butteralbum.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.xudongwu.butteralbum.model.ButterAlbumContract.*;

public class ButterAlbumProvider extends ContentProvider {
    private static SQLiteOpenHelper sDBHelper;
    private static UriMatcher sMatcher;

    private static final int URI_FEED = 1;
    static {
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sMatcher.addURI(ButterAlbumContract.AUTHORITY, Feed.DIRECTORY, URI_FEED);
    }

    @Override
    public boolean onCreate() {
        sDBHelper = ButterAlbumDatabaseHelper.getInstance(getContext().getApplicationContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = sDBHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (sMatcher.match(uri)) {
            case URI_FEED:
                cursor = db.query(Tables.FEED, projection, selection, selectionArgs, null, null, sortOrder);
                break;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        long id = 0;
        switch (sMatcher.match(uri)) {
            case URI_FEED:
                db.replace(Tables.FEED, null, values);
                break;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
