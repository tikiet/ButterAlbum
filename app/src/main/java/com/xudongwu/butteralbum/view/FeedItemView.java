package com.xudongwu.butteralbum.view;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xudongwu.butteralbum.R;

public class FeedItemView extends FrameLayout {
    private static final String TAG = "FeedItemView";
    private static OnClickListener sListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle(R.string.download)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DownloadManager dm = (DownloadManager) v.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                            String url = (String) v.getTag();
                            Uri uri = Uri.parse(url.substring(0, url.indexOf("-")));
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, uri.getLastPathSegment());
                            dm.enqueue(request);
                        }
                    }).setNegativeButton(android.R.string.cancel, null).show();
        }
    };

    private SimpleDraweeView mImage;
    private SimpleDraweeView mAvatar;
    private TextView mName;
    private View mDownload;

    public FeedItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeedItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mImage = (SimpleDraweeView) findViewById(R.id.feed_image);
        mAvatar = (SimpleDraweeView) findViewById(R.id.user_avatar);
        mName = (TextView) findViewById(R.id.user_name);
        mDownload = findViewById(R.id.download);
    }

    public void onBind(Cursor cursor) {
        String url = cursor.getString(1);
        Uri image = Uri.parse(url);
        Uri avatar = Uri.parse(cursor.getString(2));
        String name = cursor.getString(3);

        Log.d(TAG, "binding " + image);
        mImage.setImageURI(image);
        mAvatar.setImageURI(avatar);
        mName.setText(name);
        mDownload.setTag(url);
        mDownload.setOnClickListener(sListener);
    }
}
