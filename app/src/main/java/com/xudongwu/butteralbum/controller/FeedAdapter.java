package com.xudongwu.butteralbum.controller;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.xudongwu.butteralbum.R;
import com.xudongwu.butteralbum.view.FeedItemView;

public class FeedAdapter extends CursorAdapter{
    public FeedAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.feed_item, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((FeedItemView) view).onBind(cursor);
    }
}
