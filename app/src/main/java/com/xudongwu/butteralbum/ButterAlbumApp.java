package com.xudongwu.butteralbum;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class ButterAlbumApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
