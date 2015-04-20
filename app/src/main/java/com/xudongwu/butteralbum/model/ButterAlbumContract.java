package com.xudongwu.butteralbum.model;

import android.net.Uri;
import android.provider.BaseColumns;

public class ButterAlbumContract {
    public static class Tables {
        public static final String FEED = "feed";
    }

    public static final String AUTHORITY = "com.xudongwu.butteralbum";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY + "/");

    public static class Feed implements BaseColumns {
        public static final String DIRECTORY = "feed";
        public static final Uri CONTENT_URI = Uri.parse(BASE_URI + DIRECTORY);

        public static final String IMAGE_ID = "imgid";
        public static final String URL = "url";
        public static final String TIMESTAMP = "timestamp";
        public static final String USER_NAME = "username";
        public static final String USER_AVATAR = "user_avatar";
    }
}
