package com.naruto.popmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.naruto.popmovies.BuildConfig;
import com.naruto.popmovies.R;
import com.naruto.popmovies.activity.MainActivity;
import com.naruto.popmovies.bean.MovieListBean;
import com.naruto.popmovies.data.Entry;
import com.naruto.popmovies.util.ImageUtils;
import com.naruto.popmovies.util.SpUtils;
import com.naruto.popmovies.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.BaseColumns._ID;
import static com.naruto.popmovies.data.MovieDetailsContract.MovieDetailsEntry.buildDetailsUri;

/**
 * Created by Android Studio. Date: 2017/10/30. Time: 下午11:26. Desc:
 * UdaCity_PopularMovies
 *
 * @author jelly
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private final static String LOG_TAG = SyncAdapter.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED = "com.naruto.popmovies.ACTION_DATA_UPDATED";
    /**
     * Interval at which to sync with the data, in seconds. default: 60 seconds (1
     * minute) *180 = 3 hours
     */
    private static int sSyncInterval = 60 * 180;
    private static final int SYNC_FLEXTIME = 3600;
    private static final long BASE_SYNC_FREQUENCY_IN_MILLIS = 1000 * 60 * 60;
    private static long sSyncFrequencyInMillis = BASE_SYNC_FREQUENCY_IN_MILLIS * 24;
    private static final int MOVIE_NOTIFICATION_ID = 2963;

    /**
     * Global variables Define a variable to contain a content resolver instance
     */
    private ContentResolver mContentResolver;

    private static final String[] NOTIFY_MOVIE_PROJECTION = new String[]{Entry.COLUMN_MOVIE_TITLE, Entry.COLUMN_MOVIE_VOTE_AVERAGE,
            Entry.COLUMN_MOVIE_POSTER};
    private static final String POSTER_HTTP_PREFIX = Entry.BASE_IMAGE_URL + "/w342";
    private static final String BACKDROP_HTTP_PREFIX = Entry.BASE_IMAGE_URL + "/w300";
    private SharedPreferences mPrefs;
    private List<String> mOtherInfo = new ArrayList<>();
    private StringBuilder mGenresStr = new StringBuilder();
    private String mGenreStrs;

    /**
     * 来源保留政策告诉工具链我们不需要在类中或运行时保留该注释，确保注释不会对我们的运行时或分发产生影响
     */
    @Retention(RetentionPolicy.SOURCE)

    // 用IntDef标注新注释，包括受支持的不同整数常量
    @IntDef({MOVIES_STATUS_OK, MOVIES_STATUS_SERVER_DOWN, MOVIES_STATUS_SERVER_INVALID})
    // 定义整数变量时，也会创建接口注释
    public @interface MoviesStatus {
    }

    public static final int MOVIES_STATUS_OK = 0;
    public static final int MOVIES_STATUS_SERVER_DOWN = 1;
    public static final int MOVIES_STATUS_SERVER_INVALID = 2;

    /**
     * 设置 sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {

        super(context, autoInitialize, true);
        // 如果你的app使用content resolver，从传入的Context获取一个实例
        mContentResolver = context.getContentResolver();
        // 获取preference实例对象
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        checkOutSyncFrequency(context);
    }

    /**
     * 设置 sync adapter. 这个构造器具有对AS3.0及之后平台的兼容性
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {

        super(context, autoInitialize, allowParallelSyncs);
        // 如果你的app使用content resolver，从传入的Context获取一个实例
        mContentResolver = context.getContentResolver();
        // 获取preference实例对象
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        checkOutSyncFrequency(context);
    }

    private void checkOutSyncFrequency(Context context) {

        sSyncFrequencyInMillis = Integer.valueOf(mPrefs.getString(context.getString(R.string.key_sync_frequency), String.valueOf(24)))
                * BASE_SYNC_FREQUENCY_IN_MILLIS;
        sSyncInterval = (int) (sSyncFrequencyInMillis / 1000);
    }

    /**
     * 具体化要在sync adapter下运行的代码。整个sync adapter在后台运行，所以不需要设置自己的后台线程
     *
     * @param account    账户
     * @param extras     bundle
     * @param authority  与provider匹配的authority
     * @param provider   ContentProvider
     * @param syncResult sync结果
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.i(LOG_TAG, "开始同步");
        // 创建url
        String popularURL = Entry.BASE_URL + "movie/popular?language=zh&api_key=" + BuildConfig.MOVIE_DB_KEY;
        String topRateURL = Entry.BASE_URL + "movie/top_rated?language=zh&api_key=" + BuildConfig.MOVIE_DB_KEY;
        // 创建url对象
        URL url;
        try {

            url = new URL(popularURL);
            String popularJson = getJsonResponse(url);
            if (popularJson != null) {

                extractFeatureFromJson(popularJson, Entry.POPULAR_MOVIE_DIR);
            } else {

                toastConnectionProblem(null);
            }
            url = new URL(topRateURL);
            String topRateJson = getJsonResponse(url);
            if (topRateJson != null) {

                extractFeatureFromJson(topRateJson, Entry.TOP_RATE_MOVIE_DIR);
            } else {

                toastConnectionProblem(null);
            }

        } catch (Exception e) {

            toastConnectionProblem(e);
        }
    }

    private void toastConnectionProblem(Exception e) {

        if (e != null) {
            Log.e(LOG_TAG, "这里有异常！", e);
        }
        Looper.prepare();
        Toast.makeText(getContext(), R.string.toast_unstable_connection, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    /**
     * 根据url地址获取jsonResponse
     *
     * @param url 目标url地址
     * @return 获取到的jsonResponse
     */
    private String getJsonResponse(URL url) {

        HttpURLConnection urlConnection = null;
        InputStream is = null;

        // 发出url请求，并返回jsonResponse
        String jsonResponse = null;
        try {
            // url为空，返回null
            if (url == null) {

                return null;
            }

            try {

                urlConnection = (HttpURLConnection) url.openConnection();
                assert urlConnection != null;
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // 响应码==200，读取输入流。得到jsonResponse
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    is = urlConnection.getInputStream();
                    // 创建StringBuilder对象
                    StringBuilder output = new StringBuilder();
                    // 输入流不为空，读取流中的信息
                    if (is != null) {

                        InputStreamReader inputStreamReader = new InputStreamReader(is, Charset.forName("UTF-8"));
                        BufferedReader reader = new BufferedReader(inputStreamReader);
                        String line = reader.readLine();
                        while (line != null) {

                            output.append(line);
                            line = reader.readLine();
                        }
                    } else {

                        SpUtils.putInt(getContext(), Entry.MOVIES_STATUS, MOVIES_STATUS_SERVER_DOWN);
                    }
                    jsonResponse = output.toString();
                } else {

                    Log.e(LOG_TAG, "错误响应码：" + urlConnection.getResponseCode());
                    SpUtils.putInt(getContext(), Entry.MOVIES_STATUS, MOVIES_STATUS_SERVER_INVALID);
                }
            } catch (Exception e) {

                SpUtils.putInt(getContext(), Entry.MOVIES_STATUS, MOVIES_STATUS_SERVER_DOWN);
                toastConnectionProblem(e);
            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                // 关闭流
                if (is != null) {
                    is.close();
                }
            }
        } catch (Exception e) {

            toastConnectionProblem(e);
        }
        return jsonResponse;
    }

    /**
     * 从返回的jsonResponse中解析，获得MovieDetail数组
     *
     * @param movieDetailJSON 返回的jsonResponse
     * @param orderMode       当前的排序模式
     */
    private void extractFeatureFromJson(String movieDetailJSON, int orderMode) {

        try {

            // 获取JsonArray格式的results
            JSONObject baseJsonResponse = new JSONObject(movieDetailJSON);
            JSONArray results = baseJsonResponse.getJSONArray("results");
            Log.i(LOG_TAG, "开始解析results");
            // 创建一个Gson对象
            Gson gson = new Gson();
            List<MovieListBean.ResultsBean> movieList = gson.fromJson(results.toString(), new TypeToken<List<MovieListBean.ResultsBean>>() {
            }.getType());
            // 遍历循环movieList
            for (int i = 0; i < movieList.size(); i++) {
                // 得到单条movieDetails
                MovieListBean.ResultsBean details = movieList.get(i);
                // 得到movieId
                int id = details.getId();
                // 分别拼接出电影时长，电影评论，电影预告片各自对应的url地址
                String apiKey = BuildConfig.MOVIE_DB_KEY;
                String combineUrl = Entry.BASE_URL + "movie/" + String.valueOf(id) + "?api_key=" + apiKey + "&append_to_response=videos,reviews";
                String jsonResponse = getJsonResponse(new URL(combineUrl));
                List<String> otherInfo = extractOtherFeatureFromJson(jsonResponse);
                // 得到poster_path
                String posterPath = details.getPosterPath();
                // 经测试，picasso会自动缓存在内存中，退出应用，即使你杀了后台，也就是完全退出，断开网络，重新打开应用，加载过的图片就不用再加载了
                // ImageUtils ImageUtils = new ImageUtils(mContext);
                // String posterStoragePath =
                // ImageUtils.imageFromUrlToByteArray(posterHttpPath,
                // posterPath);
                // 得到overview
                String overview = details.getOverview();
                if (overview.isEmpty()) {
                    overview = getContext().getString(R.string.no_overview);
                }
                // 得到release_date
                String releaseDate = details.getReleaseDate();
                String[] split = releaseDate.split("-");
                String releaseDateY = split[0];
                // 得到genre_ids
                List<Integer> genreIds = details.getGenres();
                String genreStrs = ensureGenres(genreIds);
                // 得到original_title
                String originalTitle = details.getOriginalTitle();
                // 得到original_language
                String originalLanguage = details.getOriginalLanguage();
                // 得到title
                String title = details.getTitle();
                // 得到backdrop_path
                String backdropPath = details.getBackdropPath();
                // String backDropStoragePath =
                // ImageUtils.imageFromUrlToByteArray(backdropHttpPath,
                // backdropPath);
                // 得到vote_count
                int voteCount = details.getVoteCount();
                // 得到vote_average
                double voteAverage = details.getVoteAverage();
                // 创建用于插入或更新的value
                ContentValues values = new ContentValues();
                values.put("movie_id", id);
                values.put("movie_poster", posterPath);
                values.put("movie_backdrop", backdropPath);
                values.put("movie_title", title);
                values.put("movie_original_title", originalTitle);
                values.put("movie_original_language", originalLanguage);
                values.put("movie_release_date", releaseDateY);
                values.put("movie_vote_count", voteCount);
                values.put("movie_vote_average", voteAverage);
                values.put("movie_genres", genreStrs);
                values.put("movie_overview", overview);
                values.put("movie_running_time", otherInfo.get(0));
                values.put("movie_video", otherInfo.get(1));
                if (otherInfo.get(2).isEmpty()) {
                    values.put("movie_reviews", getContext().getString(R.string.no_reviews));
                } else {
                    values.put("movie_reviews", otherInfo.get(2));
                }

                // 准备将要去做插入或更新的URI
                updateOrInsertToDb(i, values, orderMode);
                SpUtils.putInt(getContext(), Entry.MOVIES_STATUS, MOVIES_STATUS_OK);
                notifyMovies(orderMode);
            }
        } catch (Exception e) {
            toastConnectionProblem(e);
        }
    }

    /**
     * 从jsonResponse解析出一些其他较复杂的信息
     *
     * @param jsonResponse 待解析的jsonResponse
     * @return 解析之后组合的String类型数组
     */
    private List<String> extractOtherFeatureFromJson(String jsonResponse) {

        Context context = getContext();

        List<String> otherInfo = new ArrayList<>();
        // 如果json内容为空，返回null
        if (jsonResponse.isEmpty()) {
            otherInfo.add(context.getString(R.string.no_runtime));
            otherInfo.add(context.getString(R.string.no_videos));
            otherInfo.add(context.getString(R.string.no_reviews));

            return otherInfo;
        }

        try {

            JSONObject jsonObject = new JSONObject(jsonResponse);
            // 获取电影时长
            int runtime = jsonObject.getInt("runtime");
            String runtimeStr = String.valueOf(runtime);
            otherInfo.add(runtimeStr);

            // 获取电影预告片的url地址
            JSONObject videos = jsonObject.getJSONObject("videos");
            JSONArray results = videos.getJSONArray("results");
            StringBuilder builder = new StringBuilder();
            String videoUrl;
            int count = results.length();
            if (count > 0) {

                // 得到results下第0个的内容
                JSONObject result = results.getJSONObject(0);
                // 得到key的内容
                String key = result.getString("key");
                if (key.contains(context.getString(R.string.value_name_youtube_id))) {

                    // 如果key中自带"v＝"，直接拼接
                    videoUrl = "https://www.youtube.com/watch?" + key;
                } else {

                    // 如果key中不带"v＝"，加上"v＝"再拼接
                    videoUrl = "https://www.youtube.com/watch?" + "v=" + key;
                }
                // 将拼接好的url地址放入准备好的数组中
                builder.append(videoUrl);
            } else {

                builder.append(context.getString(R.string.no_videos));
            }
            otherInfo.add(builder.toString());

            // 获取评论详细内容
            JSONObject reviews = jsonObject.getJSONObject("reviews");
            StringBuilder buffer = new StringBuilder();
            int totalResult = reviews.getInt("total_results");
            if (totalResult != 0) {

                // 得到results节点的数据
                JSONArray resultsArray = reviews.getJSONArray("results");
                for (int i = 0; i < resultsArray.length(); i++) {

                    // 得到results节点下第i个的内容
                    JSONObject result = resultsArray.getJSONObject(i);
                    // 得到author
                    String author = result.getString("author");
                    // 得到content
                    String content = result.getString("content");
                    // 将author和content拼接起来以string类型存储
                    buffer.append(author).append(";;").append(content).append(";;");
                }
            } else {

                buffer.append(context.getString(R.string.no_reviews));
            }
            otherInfo.add(buffer.toString());
        } catch (Exception e) {

            toastConnectionProblem(e);
        }

        return otherInfo;
    }

    /**
     * 根据传入的genreId，拼接出完整的genre的String类型值
     *
     * @param movieGenreIds 该电影所有的genreId
     */
    private String ensureGenres(List<Integer> movieGenreIds) throws MalformedURLException, JSONException {

        StringBuilder genresStr = new StringBuilder();

        String genreListUrl = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + BuildConfig.MOVIE_DB_KEY + "&language=zh";
        String jsonResponse = getJsonResponse(new URL(genreListUrl));
        if (jsonResponse != null) {

            JSONArray jsonGenres = new JSONObject(jsonResponse).getJSONArray("genres");
            // 创建一个Gson对象
            Gson gson = new Gson();
            List<MovieGenres> movieGenres = gson.fromJson(jsonGenres.toString(), new TypeToken<List<MovieGenres>>() {
            }.getType());
            @SuppressLint("UseSparseArrays")
            Map<Integer, String> genreMap = new HashMap<Integer, String>();
            for (MovieGenres genres : movieGenres) {

                genreMap.put(genres.getId(), genres.getName());
            }

            for (int genreId : movieGenreIds) {

                String genre = genreMap.get(genreId);
                genresStr.append(genre).append(" ");
            }

            return genresStr.toString();
        } else {

            return genresStr.append(getContext().getString(R.string.no_genre)).toString();
        }
    }

    /**
     * 用于网络加载时，对结果的处理：更新或插入数据库
     *
     * @param i         cursor的索引值
     * @param values    用于更新或插入的值
     * @param orderMode 排序模式
     */
    private void updateOrInsertToDb(int i, ContentValues values, int orderMode) {

        // TODO: 2017/10/29 需注意，下面这段方法，仅适用于始终只有20条数据的情况，一旦加入下拉加载更多，需要重新考虑这段方法是否仍然适用
        Uri uri;
        uri = Utils.fetchCurrentUri(orderMode);
        // 使用ContentResolver查询当前行的数据
        Log.i(LOG_TAG, uri.toString());
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToPosition(i)) {

            int index = cursor.getInt(cursor.getColumnIndex(_ID));
            // 执行更新方法
            uri = buildDetailsUri(uri, index);
            getContext().getContentResolver().update(uri, values, null, null);
            Log.d(LOG_TAG, "No." + index + "just be updated");
        } else {

            // 执行insert方法
            Uri insert = getContext().getContentResolver().insert(uri, values);
            Log.d(LOG_TAG, (insert != null ? insert.getPath() : null) + "just be updated");
        }

        // 数据更新了，通知以更新widget上的数据
        updateWidget();

        assert cursor != null;
        cursor.close();
    }

    private void updateWidget() {

        Context context = getContext();
        // 设置包名，确保app中的仅有的组件准确地收到广播
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED).setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    /**
     * 发出一条通知，提示用户排名第一的电影
     *
     * @param orderMode 当前的排序模式
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void notifyMovies(int orderMode) {

        Context context = getContext();
        // 判断用户是否选择打开通知
        String displayNotificationsKey = context.getString(R.string.key_enable_notifications);
        boolean displayNotifications = mPrefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.default_enable_notifications)));
        checkOutSyncFrequency(context);

        if (displayNotifications) {

            // 用户打开了通知，为用户推送通知
            String lastNotificationKey = context.getString(R.string.pref_last_notification);
            long lastSync = mPrefs.getLong(lastNotificationKey, 0);

            if (System.currentTimeMillis() - lastSync >= sSyncFrequencyInMillis) {
                // 根据排序模式，拼接出当前
                Uri movieUri = buildDetailsUri(Utils.fetchCurrentUri(orderMode), 1);

                // we'll query our contentProvider, as always
                Cursor cursor = context.getContentResolver().query(movieUri, NOTIFY_MOVIE_PROJECTION, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {

                    String movieTitle = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_TITLE));
                    double voteAverage = cursor.getDouble(cursor.getColumnIndex(Entry.COLUMN_MOVIE_VOTE_AVERAGE));
                    String posterUrl = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_POSTER));
                    // 准备通知图标的bitmap
                    Bitmap bitmap = new ImageUtils().imageFromUrlToBitmap(posterUrl);
                    // 拼接出标题
                    StringBuilder builder = new StringBuilder();
                    builder.append("当前");
                    if (orderMode == Entry.POPULAR_MOVIE_DIR) {

                        builder.append("最热门");
                    } else if (orderMode == Entry.TOP_RATE_MOVIE_DIR) {

                        builder.append("评分最高");
                    } else {

                        builder.append("最喜爱");
                    }
                    String title = builder.append("电影").toString();
                    // 定义排名第一的电影文本内容.
                    String contentText = String.format(context.getString(R.string.format_notification), movieTitle, String.valueOf(voteAverage));

                    // NotificationCompatBuilder is a very convenient way to build
                    // backward-compatible
                    // notifications. Just throw in some data.
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext(), null)
                            .setColor(context.getResources().getColor(R.color.colorPrimary, null)).setSmallIcon(R.mipmap.ic_movie)
                            .setLargeIcon(bitmap).setContentTitle(title).setContentText(contentText);

                    // Make something interesting happen when the user clicks on the notification.
                    // In this case, opening the app is sufficient.
                    Intent resultIntent = new Intent(context, MainActivity.class);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
                    assert mNotificationManager != null;
                    mNotificationManager.notify(MOVIE_NOTIFICATION_ID, mBuilder.build());

                    // refreshing last sync
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putLong(lastNotificationKey, System.currentTimeMillis());
                    editor.apply();
                }

                assert cursor != null;
                cursor.close();
            }
        }
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a
     * new one if the fake account doesn't exist yet. If we make a new account, we
     * call the onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    private static Account getSyncAccount(Context context) {

        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

            /*
             * Add the account and account type, no password or user data If successful,
             * return the Account object, otherwise report an error.
             */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {

                return null;
            }
            /*
             * If you don't set android:syncable="true" in in your <provider> element in the
             * manifest, then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        // 已创建账户
        SyncAdapter.configurePeriodicSync(context, sSyncInterval, SYNC_FLEXTIME);

        // 不调用这个方法，将无法启用syncadapter
        ContentResolver.setSyncAutomatically(newAccount, Entry.CONTENT_AUTHORITY, true);

        // 立即同步
        syncImmediately(context);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), Entry.CONTENT_AUTHORITY, bundle);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     *
     * @param context      上下文环境
     * @param syncInterval 同步间隔
     * @param flexTime     弹性工作时间
     */
    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {

        Account account = getSyncAccount(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().syncPeriodic(syncInterval, flexTime).setSyncAdapter(account, Entry.CONTENT_AUTHORITY)
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, Entry.CONTENT_AUTHORITY, new Bundle(), syncInterval);
        }
    }

    public static void initializeSyncAdapter(Context context) {

        getSyncAccount(context);
    }

    public ContentResolver getContentResolver() {

        return mContentResolver;
    }

    public void setContentResolver(ContentResolver contentResolver) {

        mContentResolver = contentResolver;
    }

    private class MovieGenres {

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}