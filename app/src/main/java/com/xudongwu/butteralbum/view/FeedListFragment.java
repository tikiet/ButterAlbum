package com.xudongwu.butteralbum.view;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.xudongwu.butteralbum.R;
import com.xudongwu.butteralbum.controller.FeedAdapter;
import com.xudongwu.butteralbum.model.ButterAlbumContract;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FeedListFragment extends Fragment {
    private static final String TAG = "FeedListFragment";
    private ListView mList;
    private static final int LOADER_REMOTE = 0;
    private static final int LOADER_LOCAL = 1;

    private static final String TIME_FORMAT_STRING = "yyyy-MM-dd hh:mm:ss";
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT_STRING);

    private static final String UID = "XXX";
    private static final String ACCESS_TOKEN = "XXX";
    private static final String APP_KEY = "XXX";
    private static final int LIMIT = 100;
    private static final int PAGE = 0;
    private static String URL =
            "http://api.bybutter.com/api/user/user_timeline.php?" +
            "uid=" + UID + "&" +
            "access_token=" + ACCESS_TOKEN + "&" +
            "appkey=" + APP_KEY + "&" +
            "limit=" + LIMIT + "&" +
            "page=" + PAGE;

    private Cursor mCursor;
    private FeedAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_list_fragment, null);
        mList = (ListView) view.findViewById(R.id.feeds_list);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLoaderManager().restartLoader(LOADER_REMOTE, null, new RemoteFeedLoaderCallback()).forceLoad();
            }
        });

        mAdapter = new FeedAdapter(getActivity(), mCursor, false);
        mList.setAdapter(mAdapter);

        getLoaderManager().initLoader(LOADER_LOCAL, null, new LocalFeedLoaderCallback()).forceLoad();
        getLoaderManager().initLoader(LOADER_REMOTE, null, new RemoteFeedLoaderCallback()).forceLoad();
        return view;
    }

    private static class RemoteFeedLoader extends AsyncTaskLoader<Boolean> {
        private RemoteFeedLoader(Context context) {
            super(context);
        }

        @Override
        public Boolean loadInBackground() {
            HttpClient client = new DefaultHttpClient();
            try {
                HttpResponse response = client.execute(new HttpGet(URL));
                StatusLine status = response.getStatusLine();
                if (status.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    String entity = out.toString();
                    Log.d(TAG, entity);
                    JSONArray array = new JSONArray(entity);
                    processFeeds(getContext(), array);
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    private static void processFeeds(Context context, JSONArray s) {
        for (int i = 0; i < s.length(); i++) {
            try {
                JSONObject object = s.getJSONObject(i);
                String rawTime = object.getString("admin_time");
                String imgid = object.getString("imgid");
                String url = null;
                JSONObject picurl = object.getJSONObject("picurl");
                if (picurl != null) {
                    url = picurl.getString("x1000");
                }
                JSONObject user = object.getJSONObject("user");
                String name = null;
                String avatar = null;
                if (user != null) {
                    name = user.getString("screen_name");
                    avatar = user.getJSONObject("profile_image_url").getString("origin");
                }

                Date date = TIME_FORMAT.parse(rawTime);
                long time = date.getTime();
                ContentValues cv = new ContentValues();
                cv.put(ButterAlbumContract.Feed.URL, url);
                cv.put(ButterAlbumContract.Feed.TIMESTAMP, time);
                cv.put(ButterAlbumContract.Feed.IMAGE_ID, imgid);
                cv.put(ButterAlbumContract.Feed.USER_AVATAR, avatar);
                cv.put(ButterAlbumContract.Feed.USER_NAME, name);

                Log.d(TAG, "insert " + url);
                context.getContentResolver().insert(ButterAlbumContract.Feed.CONTENT_URI, cv);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private class RemoteFeedLoaderCallback implements LoaderManager.LoaderCallbacks<Boolean> {
        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            if (id == LOADER_REMOTE) {
                return new RemoteFeedLoader(getActivity());
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader loader, Boolean data) {
            mRefreshLayout.setRefreshing(false);
            if (data) {
                getLoaderManager().restartLoader(LOADER_LOCAL, null, new LocalFeedLoaderCallback());
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    }

    private static class LocalFeedLoader extends AsyncTaskLoader<Cursor> {
        private LocalFeedLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            Log.d(TAG, "load in background");
            return getContext().getContentResolver().query(
                    ButterAlbumContract.Feed.CONTENT_URI,
                    new String[]{
                            ButterAlbumContract.Feed._ID,
                            ButterAlbumContract.Feed.URL,
                            ButterAlbumContract.Feed.USER_AVATAR,
                            ButterAlbumContract.Feed.USER_NAME,
                    },
                    null,
                    null,
                    ButterAlbumContract.Feed.TIMESTAMP + " DESC");
        }
    }

    private class LocalFeedLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.d(TAG, "id:" + id);
            if (id == LOADER_LOCAL) {
                Log.d(TAG, "return local feed loader");
                return new LocalFeedLoader(getActivity());
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.d(TAG, "onloadfinished, data:" + data);
            if (data != null) {
                mAdapter.swapCursor(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }
}
