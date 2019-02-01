package com.naruto.popmovies.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.naruto.popmovies.R;
import com.naruto.popmovies.adapter.MovieAdapter;
import com.naruto.popmovies.bean.MovieDetail;
import com.naruto.popmovies.data.Entry;
import com.naruto.popmovies.data.MovieDetailsContract;
import com.naruto.popmovies.sync.SyncAdapter;
import com.naruto.popmovies.util.SpUtils;
import com.naruto.popmovies.util.Utils;

import org.litepal.LitePal;

import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.naruto.popmovies.sync.SyncAdapter.MOVIES_STATUS_OK;

/**
 * Created by Android Studio. User: jellybean. Date: 2017/10/18. Time: 上午1:16.
 *
 * @author jellybean
 */
public class MoviesFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String SELECTED_KEY = "selected_key";
    public static int mOrderMode = Entry.POPULAR_MOVIE_DIR;

    private TextView mTvEmptyView;
    private MovieAdapter mAdapter;
    private static int mPosition = RecyclerView.INVALID_TYPE;
    private ArrayList<MovieDetail> mMovieDetails = new ArrayList<>();
    private static final int MOVIE_LIST_MAX = 20;
    private ContentResolver mResolver;
    private SharedPreferences mPreferences;

    public MoviesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        LitePal.getDatabase();
        //数据库联动测试代码
        /*RetrofitHelper.getBaseApi()
                .getGenreList(BuildConfig.MOVIE_DB_KEY)
                .compose(RxUtils.applySchedulers())
                .subscribe(new BaseHandleSubscriber<GenreListBean>() {
                    @Override
                    public void onNext(GenreListBean genreList) {
                        for (Genre genre : genreList.getGenres()) {
                            Genre dbGenre = new Genre();
                            dbGenre.setGenreId(genre.getId());
                            dbGenre.setName(genre.getName());
                            dbGenre.saveOrUpdate("genre_id = ?", String.valueOf(genre.getId()));
                        }
                        RetrofitHelper.getBaseApi()
                                .getPopMovieList(BuildConfig.MOVIE_DB_KEY, 1)
                                .compose(RxUtils.applySchedulers())
                                .subscribe(new BaseHandleSubscriber<MovieListBean>() {
                                    @Override
                                    public void onNext(MovieListBean movieListBean) {
                                        MovieListBean.ResultsBean movieBean = movieListBean.getResults().get(0);
                                        RetrofitHelper.getBaseApi()
                                                .getVideoAndReviewList(movieBean.getId(), BuildConfig.MOVIE_DB_KEY)
                                                .compose(RxUtils.applySchedulers())
                                                .subscribe(new BaseHandleSubscriber<VIRListBean>() {
                                                    @Override
                                                    public void onNext(VIRListBean virListBean) {
                                                        MovieDbUtils.addMovie(movieBean, virListBean);
                                                    }
                                                });
                                    }
                                });
                    }
                });*/
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // 找到空view
        mTvEmptyView = rootView.findViewById(R.id.tv_empty_view);
        // 找到recyclerView
        RecyclerView rvMovie = rootView.findViewById(R.id.rv_movie);
        // 创建适配器
        mAdapter = new MovieAdapter(mMovieDetails);
        mAdapter.setHasStableIds(true);

        // 设置布局管理者
        final StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rvMovie.setLayoutManager(gridLayoutManager);
        // 设置适配器
        rvMovie.setAdapter(mAdapter);
        // 设置边距
        SpaceDecoration itemDecoration = new SpaceDecoration((int) Utils.convertDpToPixel(8, getContext()));
        itemDecoration.setPaddingEdgeSide(true);
        itemDecoration.setPaddingStart(true);
        rvMovie.addItemDecoration(itemDecoration);
        // 给recyclerView添加滚动监听
        rvMovie.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                // 当前RecyclerView显示出来的第一个和最后一个的item的position
                // int firstPosition = -1;
                int lastPosition = -1;

                if (newState == SCROLL_STATE_IDLE) {

                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof GridLayoutManager) {

                        // 通过LayoutManager找到当前的第一个item的position
                        // firstPosition = ((GridLayoutManager)
                        // layoutManager).findFirstVisibleItemPosition();
                        lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                    } else if (layoutManager instanceof LinearLayoutManager) {

                        // firstPosition = ((LinearLayoutManager)
                        // layoutManager).findFirstVisibleItemPosition();
                        lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {

                        // 因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
                        // 得到这个数组后再取到数组中position值最小的那个就是第一个的position值了
                        // int[] firstPositions = new int[((StaggeredGridLayoutManager)
                        // layoutManager).getSpanCount()];
                        // ((StaggeredGridLayoutManager)
                        // layoutManager).findFirstVisibleItemPositions(firstPositions);
                        // firstPosition = findMin(firstPositions);
                        // 得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
                        int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                        ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
                        lastPosition = findMax(lastPositions);
                    }

                    // 把当前的第一个item的position设置给mPosition
                    // mPosition = firstPosition;
                    // 时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
                    // 如果相等则说明已经滑动到最后了
                    if (lastPosition == recyclerView.getLayoutManager().getItemCount() - 1) {

                        Toast.makeText(getContext(), R.string.toast_already_scroll_to_the_bottom, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);
            }
        });

        // 获取当前的排序模式
        mOrderMode = SpUtils.getInt(getContext(), Entry.ORDER_MODE, Entry.POPULAR_MOVIE_DIR);

        // 设置recyclerView的条目点击事件
        mAdapter.setOnItemClickListener((view, position) -> {
            // 根据排序模式传递不同的uri，提供给MovieDetailsFragment来获取数据
            ((CallBack) getActivity()).onItemSelected(MovieDetailsContract.MovieDetailsEntry.buildDetailsUri(Utils.fetchCurrentUri(mOrderMode),
                    position + 1));
            mPosition = position;
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        // 检查是否获得权限，没有这需要申请权限，由用户决定是否给予
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        // 注册内容观察者
        mResolver = getContext().getContentResolver();
        mResolver.registerContentObserver(MovieDetailsContract.BASE_CONTENT_URI, true, new MoviesObserver(new Handler()));
        SyncAdapter.syncImmediately(getActivity());
        // 根据当前用户选择的排序模式，启动相应的loader去加载数据
        getLoaderManager().initLoader(fetchCurrentLoaderId(), null, this);
        if (mOrderMode != Entry.FAVORITE_MOVIE_DIR) {

            showAlertDialog();
        }
        updateEmptyView();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPreferences.registerOnSharedPreferenceChangeListener(this);

        super.onResume();
    }

    /**
     * 找到数组中的最大值
     *
     * @param firstPositions 最上面几个控件的位置index数组
     * @return 返回第一个那个控件的位置index
     */
    private int findMin(int[] firstPositions) {

        int min = firstPositions[0];
        for (int value : firstPositions) {

            if (value < min) {

                min = value;
            }
        }

        return min;
    }

    /**
     * 找到数组中的最大值
     *
     * @param lastPositions 最后几个控件的位置index数组
     * @return 返回最后那个控件的位置index
     */
    private int findMax(int[] lastPositions) {

        int max = lastPositions[0];
        for (int value : lastPositions) {

            if (value > max) {

                max = value;
            }
        }

        return max;
    }

    /**
     * 当排序模式发生变化时，调用相应的方法去执行数据的更新
     */
    public void onOrderModeChanged() {

        updateMovies();
        // 根据当前用户选择的排序模式，启动相应的loader去加载数据
        getLoaderManager().restartLoader(fetchCurrentLoaderId(), null, this);
    }

    /**
     * 根据当前的排序模式，返回相应的loaderId
     *
     * @return 相应的loaderId
     */
    private int fetchCurrentLoaderId() {

        if (mOrderMode == Entry.POPULAR_MOVIE_DIR) {

            return Entry.MOVIE_ORDER_POPULAR_DATABASE_LOADER_ID;
        } else if (mOrderMode == Entry.TOP_RATE_MOVIE_DIR) {

            return Entry.MOVIE_ORDER_TOP_RATE_DATABASE_LOADER_ID;
        } else {

            return Entry.MOVIE_ORDER_FAVORITE_DATABASE_LOADER_ID;
        }
    }

    /**
     * 启动SyncAdapter，后台刷新数据
     */
    private void updateMovies() {

        mMovieDetails.clear();
        mAdapter.notifyDataSetChanged();
        showAlertDialog();
        if (mOrderMode != Entry.FAVORITE_MOVIE_DIR) {

            SyncAdapter.syncImmediately(getActivity());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != RecyclerView.INVALID_TYPE) {

            outState.putInt(SELECTED_KEY, mPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {

        mPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.menu_movies_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_order_by_popularity:
                SpUtils.putInt(getContext(), Entry.ORDER_MODE, Entry.POPULAR_MOVIE_DIR);
                mOrderMode = Entry.POPULAR_MOVIE_DIR;
                onOrderModeChanged();

                break;

            case R.id.action_order_by_top_rate:
                SpUtils.putInt(getContext(), Entry.ORDER_MODE, Entry.TOP_RATE_MOVIE_DIR);
                mOrderMode = Entry.TOP_RATE_MOVIE_DIR;
                onOrderModeChanged();

                break;

            case R.id.action_order_by_favorite:
                SpUtils.putInt(getContext(), Entry.ORDER_MODE, Entry.FAVORITE_MOVIE_DIR);
                mOrderMode = Entry.FAVORITE_MOVIE_DIR;
                onOrderModeChanged();

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getContext(), R.string.denied_permission, Toast.LENGTH_SHORT).show();
                }

            default:
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getContext(), Utils.fetchCurrentUri(mOrderMode), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        fetchDataAndUpdateUI(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mMovieDetails.clear();
        mAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(Entry.MOVIES_STATUS)) {

            updateEmptyView();
        }
    }

    public interface CallBack {

        /**
         * 当条目被选中时，触发此回掉接口，用于向MovieDetailsFragment传递数据
         *
         * @param uri 选中条目对应的数据库uri
         */
        void onItemSelected(Uri uri);
    }

    private class MoviesObserver extends ContentObserver {

        MoviesObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            Cursor cursor = mResolver.query(Utils.fetchCurrentUri(mOrderMode), null, null, null, null);
            fetchDataAndUpdateUI(cursor);
        }
    }

    /**
     * 从cursor获取数据，并更新到UI控件上
     *
     * @param cursor 从制定uri返回的游标
     */
    private void fetchDataAndUpdateUI(Cursor cursor) {

        mMovieDetails.clear();
        if (cursor != null && cursor.moveToNext()) {
            mMovieDetails = MovieDetail.fromCursor(mMovieDetails, cursor);
            mAdapter.notifyDataSetChanged();
            // 加载完成，隐藏进度条
            if (mMovieDetails.size() < MOVIE_LIST_MAX && mOrderMode != Entry.FAVORITE_MOVIE_DIR) {
                showAlertDialog();
            } else {
                dismissDialog();
            }
            updateEmptyView();
        } else {
            // 提示用户没有获取到数据
            updateEmptyView();
            mMovieDetails.clear();
            mAdapter.notifyDataSetChanged();
        }
        mAdapter.swapCursor(cursor);

        assert cursor != null;
        cursor.close();
    }

    /**
     * 用于更新在没有获取到数据的情况下的emptyView的显示设置
     */
    private void updateEmptyView() {

        if (mAdapter.getItemCount() == 0) {

            if (mTvEmptyView != null) {

                int message = R.string.empty_recycler_view;
                @SyncAdapter.MoviesStatus
                int status = SpUtils.getInt(getContext(), Entry.MOVIES_STATUS, MOVIES_STATUS_OK);
                switch (status) {

                    case SyncAdapter.MOVIES_STATUS_OK:

                        mTvEmptyView.setVisibility(View.GONE);
                        mTvEmptyView.setText(null);
                        break;

                    case SyncAdapter.MOVIES_STATUS_SERVER_DOWN:

                        message = R.string.empty_recycler_view_server_down;
                        break;

                    case SyncAdapter.MOVIES_STATUS_SERVER_INVALID:

                        message = R.string.empty_recycler_view_server_invalid;
                        break;

                    default:
                        if (!Utils.isNetworkAvailable(getContext())) {

                            message = R.string.empty_recycler_view_no_connection;
                        }
                }

                mTvEmptyView.setText(message);
                mTvEmptyView.setVisibility(View.VISIBLE);
            }
        } else {

            mTvEmptyView.setVisibility(View.GONE);
        }
    }
}