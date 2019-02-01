package com.naruto.popmovies.fragment;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.naruto.popmovies.R;
import com.naruto.popmovies.data.Entry;
import com.naruto.popmovies.data.MovieDetailsContract;
import com.naruto.popmovies.data.MovieDetailsContract.MovieDetailsEntry;
import com.naruto.popmovies.util.ScreenUtils;
import com.naruto.popmovies.util.SpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.provider.BaseColumns._ID;
import static com.naruto.popmovies.data.MovieDetailsContract.MovieDetailsEntry.CONTENT_FAVORITE_URI;
import static com.naruto.popmovies.data.MovieDetailsContract.MovieDetailsEntry.buildDetailsUri;

/**
 * Created by Android Studio. User: jellybean. Date: 2017/10/18. Time: 上午1:08.
 *
 * @author jelly
 */
public class MovieDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    private static final String MOVIE_SHARE_HASH_TAG = " #PopMovies";
    private static int mClickCount = 0;
    public final static String DETAIL_URI = "mUri";
    private static final int IS_FAVORITE = 1;
    private static final int NOT_FAVORITE = 0;
    private ImageView mIvFavorite;
    private ImageView mIvPoster;
    private ShareActionProvider mShareActionProvider;
    private Uri mUri;
    private String mMovieShare;
    private static int mMovieId;

    /**
     * 将CursorLoader获取的数据存在在ContentValues对象中
     */
    ContentValues mValues;
    private TextView mTvTitle;
    private TextView mTvOriginTitle;
    private TextView mTvLanguage;
    private TextView mTvVoteCount;
    private RatingBar mRbRate;
    private TextView mTvVoteAverage;
    private TextView mTvGenres;
    private TextView mTvRuntime;
    private TextView mTvOverview;
    private Button mBtExpShr;
    private ImageView mIvVideo;
    private TextView mTvEmptyVideo;
    private LinearLayout mLlReviewsContainer;
    private LayoutInflater mInflater;
    private int mOrderMode;

    public MovieDetailsFragment() {

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            mUri = bundle.getParcelable(DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // 加载网络图片并设置给imageView
        mIvPoster = rootView.findViewById(R.id.iv_poster);
        // 设置标题
        mTvTitle = rootView.findViewById(R.id.tv_title);
        setViewMaxLineAndEnd(mTvTitle, 1, TextUtils.TruncateAt.END);
        // 设置原名
        mTvOriginTitle = rootView.findViewById(R.id.tv_origin_title);
        setViewMaxLineAndEnd(mTvOriginTitle, 1, TextUtils.TruncateAt.END);
        // 设置语言类别
        mTvLanguage = rootView.findViewById(R.id.tv_language);
        // 设置评分人数
        mTvVoteCount = rootView.findViewById(R.id.tv_vote_count);
        // 设置评分星级
        mRbRate = rootView.findViewById(R.id.rb_rate);
        // 设置评分
        mTvVoteAverage = rootView.findViewById(R.id.tv_vote_average);
        // 设置类型
        mTvGenres = rootView.findViewById(R.id.tv_genres);
        setViewMaxLineAndEnd(mTvGenres, 1, TextUtils.TruncateAt.END);
        // 设置电影时长
        mTvRuntime = rootView.findViewById(R.id.tv_runtime);
        // 设置简介
        mTvOverview = rootView.findViewById(R.id.tv_overview);
        mBtExpShr = rootView.findViewById(R.id.bt_expand_or_shrink);
        mBtExpShr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickCount++;
                if (mClickCount % 2 == 0) {
                    mBtExpShr.setText(R.string.btExpand);
                    setViewMaxLineAndEnd(mTvOverview, 3, TextUtils.TruncateAt.END);
                } else {
                    mBtExpShr.setText(R.string.btShrink);
                    setViewMaxLineAndEnd(mTvOverview, 100, null);
                }
            }
        });
        // 设置收藏
        mIvFavorite = rootView.findViewById(R.id.iv_favorite);
        mIvFavorite.setOnClickListener(this);
        // 设置预告片
        mIvVideo = rootView.findViewById(R.id.iv_video);
        int screenWidth = ScreenUtils.getScreenWidth(getContext());
        ViewGroup.LayoutParams ivLp = mIvVideo.getLayoutParams();
        ivLp.width = screenWidth - 32;
        ivLp.height = (screenWidth - 32) / 16 * 9;
        mIvVideo.setLayoutParams(ivLp);
        mTvEmptyVideo = rootView.findViewById(R.id.tv_empty_video);
        // 设置评论
        mLlReviewsContainer = rootView.findViewById(R.id.ll_reviews_container);
        mInflater = LayoutInflater.from(getContext());

        mOrderMode = SpUtils.getInt(getContext(), Entry.SP_ORDER_MODE, Entry.POPULAR_MOVIE_DIR);

        startCursorLoaderLoadData();

        return rootView;
    }

    /**
     * 设置控件最大行数已经以什么符号结束
     *
     * @param view    需要设置的控件
     * @param maxLine 预设的最大行数
     * @param where   结束的符号
     */
    private void setViewMaxLineAndEnd(TextView view, int maxLine, TextUtils.TruncateAt where) {

        view.setMaxLines(maxLine);
        view.setEllipsize(where);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        startCursorLoaderLoadData();

        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 根据排序模式加载不同的loader加载数据
     */
    private void startCursorLoaderLoadData() {
        if (mOrderMode == Entry.POPULAR_MOVIE_DIR) {
            initCursorLoaderLoadData(Entry.MOVIE_ORDER_POPULAR_DATABASE_LOADER_ID);
        } else if (mOrderMode == Entry.TOP_RATE_MOVIE_DIR) {
            initCursorLoaderLoadData(Entry.MOVIE_ORDER_TOP_RATE_DATABASE_LOADER_ID);
        } else {
            initCursorLoaderLoadData(Entry.MOVIE_ORDER_FAVORITE_DATABASE_LOADER_ID);
        }
    }

    /**
     * 启动loader加载数据库中的数据并展示到控件上
     *
     * @param loaderId loader的id
     */
    private void initCursorLoaderLoadData(int loaderId) {
        getLoaderManager().initLoader(loaderId, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {

                if (null != mUri) {
                    String tableNameDorId;
                    if (mOrderMode == Entry.POPULAR_MOVIE_DIR) {
                        tableNameDorId = Entry.POPULAR_TABLE_NAME + "." + MovieDetailsEntry._ID;
                    } else if (mOrderMode == Entry.TOP_RATE_MOVIE_DIR) {
                        tableNameDorId = Entry.TOP_RATE_TABLE_NAME + "." + MovieDetailsEntry._ID;
                    } else {
                        tableNameDorId = Entry.FAVORITE_TABLE_NAME + "." + MovieDetailsEntry._ID;
                    }

                    String[] detailColumns = new String[]{tableNameDorId, Entry.COLUMN_MOVIE_POSTER, Entry.COLUMN_MOVIE_TITLE,
                            Entry.COLUMN_MOVIE_ORIGINAL_TITLE, Entry.COLUMN_MOVIE_ORIGINAL_LANGUAGE, Entry.COLUMN_MOVIE_VOTE_COUNT,
                            Entry.COLUMN_MOVIE_OVERVIEW, Entry.COLUMN_MOVIE_BACKDROP, Entry.COLUMN_MOVIE_ID, Entry.COLUMN_MOVIE_RELEASE_DATE,
                            Entry.COLUMN_MOVIE_GENRES, Entry.COLUMN_MOVIE_VOTE_AVERAGE, Entry.COLUMN_MOVIE_RUNNING_TIME,
                            Entry.COLUMN_MOVIE_VIDEO, Entry.COLUMN_MOVIE_REVIEWS};

                    // 创建并返回一个负责创建需要展示的数据的cursor的CursorLoader
                    return new CursorLoader(getActivity(), mUri, detailColumns, null, null, null);
                }

                return null;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

                mValues = new ContentValues();
                if (cursor != null && cursor.moveToFirst()) {
                    // 通过cursor获取电影的id，用于存取数据库和收藏
                    mMovieId = cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_MOVIE_ID));
                    mValues.put(Entry.COLUMN_MOVIE_ID, mMovieId);
                    int isCollected = SpUtils.getInt(getContext(), String.valueOf(mMovieId), 0);
                    if (isCollected == 0) {
                        mIvFavorite.setImageResource(R.drawable.ic_favorite_white);
                    } else {
                        mIvFavorite.setImageResource(R.drawable.ic_favorite_red);
                    }

                    // 从cursor中获取poster的地址，并通过Picasso加载到控件上
                    String moviePoster = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_POSTER));
                    mValues.put(Entry.COLUMN_MOVIE_POSTER, moviePoster);
                    RequestOptions options = new RequestOptions()
                            .placeholder(R.mipmap.poster_loading)
                            .error(R.mipmap.poster_fail_load)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .priority(Priority.HIGH);
                    Glide.with(getContext())
                            .load(moviePoster)
                            .into(mIvPoster);

                    // 通过cursor获取剧照的地址，用于存取数据库和收藏
                    String movieBackDrop = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_BACKDROP));
                    mValues.put(Entry.COLUMN_MOVIE_BACKDROP, movieBackDrop);

                    // 通过cursor获取标题，并设置给控件
                    String movieTitle = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_TITLE));
                    mValues.put(Entry.COLUMN_MOVIE_TITLE, movieTitle);
                    mTvTitle.setText(movieTitle);

                    // 通过cursor获取原标题，并设置给控件
                    String movieOriginTitle = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_ORIGINAL_TITLE));
                    mValues.put(Entry.COLUMN_MOVIE_ORIGINAL_TITLE, movieOriginTitle);
                    mTvOriginTitle.setText(String.format("原名：%s", movieOriginTitle));

                    // 通过cursor获取电影语种，并设置给控件
                    String movieOriginLanguage = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_ORIGINAL_LANGUAGE));
                    mValues.put(Entry.COLUMN_MOVIE_ORIGINAL_LANGUAGE, movieOriginLanguage);
                    mTvLanguage.setText(String.format("语言：%s", movieOriginLanguage));

                    // 通过cursor获取上映日期，用于存取数据库和收藏
                    String movieReleaseDateY = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_RELEASE_DATE));
                    mValues.put(Entry.COLUMN_MOVIE_RELEASE_DATE, movieReleaseDateY);

                    // 通过cursor获取投票人数，并设置给控件
                    int movieVoteCount = cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_MOVIE_VOTE_COUNT));
                    mValues.put(Entry.COLUMN_MOVIE_VOTE_COUNT, movieVoteCount);
                    mTvVoteCount.setText(String.format("评分：%s", movieVoteCount));

                    // 通过cursor获取评分，并设置给控件
                    double movieVoteAverage = cursor.getDouble(cursor.getColumnIndex(Entry.COLUMN_MOVIE_VOTE_AVERAGE));
                    mValues.put(Entry.COLUMN_MOVIE_VOTE_AVERAGE, movieVoteAverage);
                    mRbRate.setRating(((float) movieVoteAverage) / 2);
                    mTvVoteAverage.setText(String.valueOf(movieVoteAverage));

                    // 通过cursor获取影片类型，并设置给控件
                    String movieGenres = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_GENRES));
                    mValues.put(Entry.COLUMN_MOVIE_GENRES, movieGenres);
                    mTvGenres.setText(String.format("%s%s", getString(R.string.genres_head), movieGenres));

                    // 通过cursor获取简介，并设置给控件
                    String movieOverview = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_OVERVIEW));
                    mValues.put(Entry.COLUMN_MOVIE_OVERVIEW, movieOverview);
                    mTvOverview.setText(String.format("    %s", movieOverview));

                    // 通过cursor获取电影时长，并设置给控件getDouble
                    String movieRuntime = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_RUNNING_TIME));
                    mValues.put(Entry.COLUMN_MOVIE_RUNNING_TIME, movieRuntime);
                    mTvRuntime.setText(String.format("时长：%s", movieRuntime + "min"));

                    // 通过cursor获取预告片URL地址，并设置给控件
                    String movieVideo = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_VIDEO));
                    mValues.put(Entry.COLUMN_MOVIE_VIDEO, movieVideo);
                    if (!movieVideo.equals(getString(R.string.no_videos))) {
                        mTvEmptyVideo.setVisibility(View.GONE);
                        mIvVideo.setVisibility(View.VISIBLE);
                        mIvVideo.setOnClickListener(MovieDetailsFragment.this);
                    } else {
                        mIvVideo.setVisibility(View.GONE);
                        mTvEmptyVideo.setVisibility(View.VISIBLE);
                    }

                    // 通过cursor获取评论人及评论详细，并设置给控件
                    String movieReviews = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_REVIEWS));
                    mValues.put(Entry.COLUMN_MOVIE_REVIEWS, movieReviews);

                    // 对评论内容进行非空判断
                    if (!movieReviews.equals(getString(R.string.no_reviews))) {
                        // 有评论，按";;"分割，两个一组显示
                        String[] reviews = movieReviews.split(";;");
                        int reviewsCount = reviews.length;
                        for (int i = 0; i < reviewsCount; i = i + 2) {
                            // 新建一个view对象
                            View itemReview = mInflater.inflate(R.layout.item_review, null);
                            // 找到其下的子view
                            TextView tvReviewer = itemReview.findViewById(R.id.tv_reviewer);
                            TextView tvReview = itemReview.findViewById(R.id.tv_review);
                            // 分别设置相应的内容
                            tvReviewer.setText(reviews[i]);
                            tvReview.setText(reviews[i + 1]);
                            // 让reviews的容器添加这个新建的view
                            mLlReviewsContainer.addView(itemReview);
                        }
                    } else {
                        // 没有评论内容，提示用户，暂无评论
                        View itemEmptyReview = mInflater.inflate(R.layout.item_empty_review, null);
                        mLlReviewsContainer.addView(itemEmptyReview);
                    }

                    mMovieShare = String.format("分享影片：%s \n语种：%s \n影片类型：%s \n影片时长：%s \n上映于：%s \n%s \n", movieTitle,
                            movieOriginLanguage, movieGenres, movieRuntime, movieReleaseDateY, movieVideo);

                    if (mShareActionProvider != null) {
                        mShareActionProvider.setShareIntent(createShareMovieIntent());
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

                mValues.clear();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_video:

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mValues.getAsString(Entry.COLUMN_MOVIE_VIDEO)));
                startActivity(intent);

                break;

            case R.id.iv_favorite:

                int isCollected = SpUtils.getInt(getContext(), String.valueOf(mMovieId), 0);

                if (isCollected == 0) {

                    Toast.makeText(getContext(), getString(R.string.add_favorite_successfully), Toast.LENGTH_SHORT).show();
                    SpUtils.putInt(getContext(), String.valueOf(mMovieId), IS_FAVORITE);
                    mIvFavorite.setImageResource(R.drawable.ic_favorite_red);
                    // 在数据库中更新或插入本条数据
                    crudOnThread(1, CONTENT_FAVORITE_URI);
                } else {

                    Toast.makeText(getContext(), getString(R.string.remove_favorite_successfully), Toast.LENGTH_SHORT).show();
                    SpUtils.putInt(getContext(), String.valueOf(mMovieId), NOT_FAVORITE);
                    mIvFavorite.setImageResource(R.drawable.ic_favorite_white);
                    // 在数据库中删除本条数据
                    crudOnThread(4, CONTENT_FAVORITE_URI);
                }

                break;

            default:
                break;
        }
    }

    private void crudOnThread(final int whichOperation, final Uri uri) {

        // 创建队列
        LinkedBlockingQueue queue = new LinkedBlockingQueue<Runnable>();
        // 手动创建线程池
        ThreadFactory crudThread = new ThreadFactoryBuilder().setNameFormat("CRUDThread").build();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 1, TimeUnit.DAYS, queue, crudThread,
                new ThreadPoolExecutor.AbortPolicy());

        executor.execute(new Runnable() {

            @Override
            public void run() {

                ContentResolver contentResolver = getContext().getContentResolver();
                Cursor cursor = contentResolver.query(uri, null, null, null, null);

                if (whichOperation == 1 || whichOperation == 3) {

                    List<Integer> movieIds = new ArrayList<>();
                    int negativeCount = 0;
                    // cursor不为空，且第一个游标不为空
                    if (cursor != null && cursor.moveToFirst()) {

                        do {

                            // 获取当前的电影id值
                            int movieId = cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_MOVIE_ID));
                            movieIds.add(movieId);
                            // 将从游标所指行获得的电影id与本页电影的id对比
                            if (mMovieId == movieId) {

                                // 两者一致，执行update方法
                                int index = cursor.getInt(cursor.getColumnIndex(_ID));
                                int update = contentResolver.update(buildDetailsUri(uri, index), mValues, null, null);
                                Log.i(LOG_TAG, String.valueOf(update));
                            } else {

                                // 两者不一致，计数器+1
                                negativeCount++;
                            }
                        } while (cursor.moveToNext());
                    }
                    // 数据库为空，或这遍历之后发现并无此项，执行插入
                    if (negativeCount == movieIds.size()) {

                        // 执行insert方法
                        Uri insert = contentResolver.insert(uri, mValues);
                        Log.i(LOG_TAG, insert != null ? insert.getPath() : null);
                    }
                } else if (whichOperation == 4) {

                    String selection = MovieDetailsContract.MovieDetailsEntry._ID + "=?";
                    String[] selectionArgs = new String[]{String.valueOf(mMovieId)};
                    int delete = getContext().getContentResolver().delete(uri, selection, selectionArgs);
                    Log.i(LOG_TAG, String.valueOf(delete));
                }

                assert cursor != null;
                cursor.close();
            }
        });
        executor.shutdown();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // 添加分享按钮
        inflater.inflate(R.menu.menu_movie_details_fragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // 获取provider并用它设置或改变分享intent
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // 若onLoadFinished已完成，则可以直接分享
        if (mMovieShare != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        }
    }

    /**
     * 创建分享intent
     *
     * @return 包含分享内容的intent
     */
    private Intent createShareMovieIntent() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovieShare + MOVIE_SHARE_HASH_TAG);

        return shareIntent;
    }
}