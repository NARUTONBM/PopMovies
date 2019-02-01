package com.naruto.popmovies.activity;

/**
 * @author jellybean
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.naruto.popmovies.BuildConfig;
import com.naruto.popmovies.R;
import com.naruto.popmovies.bean.GenreListBean;
import com.naruto.popmovies.data.Entry;
import com.naruto.popmovies.db.model.Genre;
import com.naruto.popmovies.fragment.MovieDetailsFragment;
import com.naruto.popmovies.fragment.MoviesFragment;
import com.naruto.popmovies.https.BaseHandleSubscriber;
import com.naruto.popmovies.https.RetrofitHelper;
import com.naruto.popmovies.sync.SyncAdapter;
import com.naruto.popmovies.util.RxUtils;

import org.litepal.LitePal;

public class MainActivity extends AppCompatActivity implements MoviesFragment.CallBack {

    private static final String DETAILS_FRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane = false;
    private int mOrderMode;
    private SPUtils mSpUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_movies);
        setSupportActionBar(toolbar);
        mSpUtils = SPUtils.getInstance(AppUtils.getAppName());
        mOrderMode = mSpUtils.getInt(Entry.SP_ORDER_MODE, Entry.POPULAR_MOVIE_DIR);
        //初始化影片类型表genre.db
        initGenreDB();

        if (findViewById(R.id.movie_details_container) != null) {

            mTwoPane = true;
            if (savedInstanceState == null) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new MovieDetailsFragment(), DETAILS_FRAGMENT_TAG)
                        .commit();
            }
        } else {

            mTwoPane = false;
        }

        SyncAdapter.initializeSyncAdapter(this);
    }

    /**
     * 初始化数据库的方法，首先写入或更新genre.db
     */
    private void initGenreDB() {
        if (!mSpUtils.getBoolean(Entry.SP_DB_INIT, false)) {
            LitePal.getDatabase();
        }
        RetrofitHelper.getBaseApi()
                .getGenreList(BuildConfig.MOVIE_DB_KEY)
                .compose(RxUtils.applySchedulers())
                .subscribe(new BaseHandleSubscriber<GenreListBean>() {
                    @Override
                    public void onNext(GenreListBean genreList) {
                        boolean finalResult = true;
                        for (Genre genre : genreList.getGenres()) {
                            Genre dbGenre = new Genre();
                            dbGenre.setGenreId(genre.getId());
                            dbGenre.setName(genre.getName());
                            boolean suResult = dbGenre.saveOrUpdate("genre_id = ?", String.valueOf(genre.getId()));
                            finalResult = finalResult & suResult;
                        }
                        mSpUtils.put(Entry.SP_DB_INIT, finalResult);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // 添加选项条目
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        // noinspection SimplifiableIfStatement
        if (id == R.id.action_setting) {

            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        super.onResume();
        // 当排序模式发生变化时，调用方法刷新数据
        int orderMode = mSpUtils.getInt(Entry.SP_ORDER_MODE, Entry.POPULAR_MOVIE_DIR);
        if (orderMode != mOrderMode) {

            MoviesFragment moviesFragment = (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
            if (moviesFragment != null) {

                moviesFragment.onOrderModeChanged();
            }
            mOrderMode = orderMode;
        }
    }

    @Override
    public void onItemSelected(Uri uri) {

        if (mTwoPane) {

            // 对于two-pane模式，通过MovieDetailsFragment展示详细信息
            Bundle bundle = new Bundle();
            bundle.putParcelable(MovieDetailsFragment.DETAIL_URI, uri);

            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.movie_details_container, fragment, DETAILS_FRAGMENT_TAG).commit();
        } else {

            Intent intent = new Intent(this, MovieDetailsActivity.class).setData(uri);
            startActivity(intent);
        }
    }
}