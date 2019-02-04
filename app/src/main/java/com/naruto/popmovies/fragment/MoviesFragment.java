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

import com.blankj.utilcode.util.ToastUtils;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.naruto.popmovies.BuildConfig;
import com.naruto.popmovies.R;
import com.naruto.popmovies.adapter.MovieAdapter;
import com.naruto.popmovies.bean.MovieDetail;
import com.naruto.popmovies.bean.MovieListBean;
import com.naruto.popmovies.bean.VIRListBean;
import com.naruto.popmovies.data.Entry;
import com.naruto.popmovies.data.MovieDetailsContract;
import com.naruto.popmovies.db.model.Genre;
import com.naruto.popmovies.db.model.Movie;
import com.naruto.popmovies.https.BaseHandleSubscriber;
import com.naruto.popmovies.https.RetrofitHelper;
import com.naruto.popmovies.util.MovieDbUtils;
import com.naruto.popmovies.util.SpUtils;
import com.naruto.popmovies.util.Utils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by Android Studio. User: jellybean. Date: 2017/10/18. Time: 上午1:16.
 *
 * @author jellybean
 */
public class MoviesFragment extends BaseFragment implements MovieDbUtils.OnCurdFinished, LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String SELECTED_KEY = "selected_key";
    public static int mOrderMode = Entry.POPULAR_MOVIE_DIR;

    private TextView mTvEmptyView;
    private MovieAdapter mMovieAdapter;
    private static int mPosition = RecyclerView.INVALID_TYPE;
    private List<MovieDetail> mMovieDetails = new ArrayList<>();
    private List<MovieListBean.ResultsBean> mMovieList = new ArrayList<>();
    private static final int MOVIE_LIST_MAX = 20;
    private ContentResolver mResolver;
    private int mResultSize;
    private List<Boolean> mResultList = new ArrayList<>();
    private static MoviesFragment sFragment;
    private static int sPullAction = Entry.ACTION_REFRESH;
    private List<Boolean> mInitGenreDBResultList = new ArrayList<>();

    public static MoviesFragment newInstance() {

        Bundle args = new Bundle();

        sFragment = new MoviesFragment();
        sFragment.setArguments(args);
        return sFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sFragment = (MoviesFragment) getFragmentManager().findFragmentById(R.id.fragment_favorite);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // 检查是否获得权限，没有这需要申请权限，由用户决定是否给予
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        // 找到空view
        mTvEmptyView = rootView.findViewById(R.id.tv_empty_view);
        // 找到recyclerView
        RecyclerView rvMovie = rootView.findViewById(R.id.rv_movie);
        // 创建适配器
        mMovieAdapter = new MovieAdapter(mMovieList);
        mMovieAdapter.setHasStableIds(true);

        // 设置布局管理者
        final StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rvMovie.setLayoutManager(gridLayoutManager);
        // 设置适配器
        rvMovie.setAdapter(mMovieAdapter);
        // 设置边距
        SpaceDecoration itemDecoration = new SpaceDecoration((int) Utils.convertDpToPixel(8, getContext()));
        itemDecoration.setPaddingEdgeSide(true);
        itemDecoration.setPaddingStart(true);
        rvMovie.addItemDecoration(itemDecoration);
        // 给recyclerView添加滚动监听
        rvMovie.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                // 当前RecyclerView显示出来的最后一个的item的position
                int lastPosition = -1;

                if (newState == SCROLL_STATE_IDLE) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof GridLayoutManager) {
                        // 通过LayoutManager找到当前的最后一个item的position
                        lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                    } else if (layoutManager instanceof LinearLayoutManager) {
                        lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        // 因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
                        // 得到这个数组后再取到数组中position值最小的那个就是第一个的position值了
                        // 得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
                        int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                        ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
                        lastPosition = findMax(lastPositions);
                    }

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
        mOrderMode = mSPUtils.getInt(Entry.SP_ORDER_MODE, Entry.POPULAR_MOVIE_DIR);

        // 设置recyclerView的条目点击事件
        mMovieAdapter.setOnItemClickListener((view, position) -> {
            // 根据排序模式传递不同的uri，提供给MovieDetailsFragment来获取数据
            ((CallBack) getActivity()).onItemSelected(MovieDetailsContract.MovieDetailsEntry.buildDetailsUri(Utils.fetchCurrentUri(mOrderMode),
                    position + 1));
            mPosition = position;
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化影片类型表genre.db
        initGenreDB();
    }

    /**
     * 初始化数据库的方法，首先写入或更新genre.db
     */
    private void initGenreDB() {
        if (!mSPUtils.getBoolean(Entry.SP_DB_INIT, false)) {
            LitePal.getDatabase();
            MovieDbUtils.initGenreDB(sFragment);
        }
    }

    /**
     * 根据当前列表模式加载相应的数据
     */
    private void loadData(int pullAction, int page) {
        if (mOrderMode == Entry.POPULAR_MOVIE_DIR) {
            getMovieList(Entry.REQUEST_POPULAR, pullAction, page);
        } else if (mOrderMode == Entry.TOP_RATE_MOVIE_DIR) {
            getMovieList(Entry.REQUEST_TOP_RATE, pullAction, page);
        } else {
            getTargetMovieList(Entry.FAVORITE_MOVIE_DIR, pullAction, page);
        }
    }

    /**
     * 获取电影列表
     *
     * @param listType   列表类型
     * @param pullAction 滑动动作
     * @param page       页码
     */
    private void getMovieList(String listType, int pullAction, int page) {
        if (!LitePal.findAll(Genre.class).isEmpty()) {
            RetrofitHelper.getBaseApi()
                    .getMovieList(listType, BuildConfig.MOVIE_DB_KEY, page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseHandleSubscriber<MovieListBean>(sFragment) {
                        @Override
                        public void onNext(MovieListBean movieListBean) {
                            List<MovieListBean.ResultsBean> resultList = movieListBean.getResults();
                            mResultSize = resultList.size();
                            for (MovieListBean.ResultsBean movieBean : resultList) {
                                RetrofitHelper.getBaseApi()
                                        .getVideoAndReviewList(movieBean.getId(), BuildConfig.MOVIE_DB_KEY)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new BaseHandleSubscriber<VIRListBean>(sFragment) {
                                            @Override
                                            public void onNext(VIRListBean virListBean) {
                                                MovieDbUtils.addMovie(mOrderMode, movieBean, virListBean, sFragment);
                                            }
                                        });
                            }

                            if (pullAction == Entry.ACTION_REFRESH) {
                                mMovieList.clear();
                            }
                            mMovieList.addAll(resultList);
                            mMovieAdapter.notifyDataSetChanged();
                        /*if (pullAction == Entry.ACTION_REFRESH) {
                            mSrlFriendsContainer.finishRefresh();
                        } else if (pullAction == Entry.ACTION_LOAD_MORE) {
                            mSrlFriendsContainer.finishLoadMore();
                        }*/
                        }
                    });
        } else {
            getMovieList(listType, pullAction, page);
        }
    }

    /**
     * 获取喜爱的电影列表
     *
     * @param pullAction 滑动动作
     * @param page       页码
     */
    private void getTargetMovieList(int orderMode, int pullAction, int page) {
        // TODO: 2019-02-04 待考虑上滑加载更多的逻辑
        //找到目标排序类型的所有电影
        List<Movie> movieList = LitePal.where("type = ?", String.valueOf(orderMode))
                .find(Movie.class);
        if (!movieList.isEmpty()) {
            if (movieList.size() > Entry.SINGLE_PAGE_SIZE) {
                //多余20个，取前20个
                List<Movie> subMovieList = movieList.subList(0, Entry.SINGLE_PAGE_SIZE - 1);
                getMovieList(subMovieList);
            } else {
                getMovieList(movieList);
            }
        /*if (pullAction == Entry.ACTION_REFRESH) {
            mSrlFriendsContainer.finishRefresh();
        } else if (pullAction == Entry.ACTION_LOAD_MORE) {
            mSrlFriendsContainer.finishLoadMore();
        }*/
            mMovieAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 从数据库中获取电影列表转换成MovieListBean.ResultBean类型的列表
     *
     * @param movieList 数据库的电影列表
     */
    private void getMovieList(List<Movie> movieList) {
        for (Movie movie : movieList) {
            MovieListBean.ResultsBean resultsBean = new MovieListBean.ResultsBean();
            resultsBean.setId(movie.getMovieId());
            resultsBean.setAdult(movie.isAdult());
            resultsBean.setBackdropPath(movie.getBackdropPath());
            resultsBean.setPosterPath(movie.getPosterPath());
            resultsBean.setOriginalLanguage(movie.getOriginalLanguage());
            resultsBean.setOriginalTitle(movie.getOriginalTitle());
            resultsBean.setOverview(movie.getOverview());
            resultsBean.setPopularity(movie.getPopularity());
            resultsBean.setReleaseDate(movie.getReleaseDate());
            resultsBean.setTitle(movie.getTitle());
            resultsBean.setVoteAverage(movie.getVoteAverage());
            resultsBean.setVoteCount(movie.getVoteCount());
            resultsBean.setGenreIds(getGenreIdList(movie.getGenreList()));
            mMovieList.add(resultsBean);
        }
    }

    /**
     * 根据类型列表获取类型id列表
     *
     * @param genreList 类型列表
     * @return 类型id列表
     */
    private List<Integer> getGenreIdList(List<Genre> genreList) {
        List<Integer> genreIdList = new ArrayList<>();
        for (Genre genre : genreList) {
            genreIdList.add(genre.getGenreId());
        }
        return genreIdList;
    }

    @Override
    public void onFinished(boolean result) {
        mResultList.add(result);
        if (mResultList.size() == mResultSize) {
            dismissDialog();
            mResultList.clear();
        }
    }

    @Override
    public void onInitGenreDbFinished(boolean result, int genreSize) {
        boolean finalResult = true;
        finalResult = finalResult & result;
        mInitGenreDBResultList.add(result);
        if (mInitGenreDBResultList.size() == genreSize) {
            mSPUtils.put(Entry.SP_DB_INIT, finalResult);
            mInitGenreDBResultList.clear();
            if (!finalResult) {
                MovieDbUtils.initGenreDB(sFragment);
            } else {
                //加载数据库中的本地数据
                getTargetMovieList(mOrderMode, Entry.ACTION_REFRESH, 1);
                //加载数据
                loadData(Entry.ACTION_REFRESH, 1);
            }
        }
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

        mMovieDetails.clear();
        mMovieAdapter.notifyDataSetChanged();
        loadData(sPullAction, 1);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != RecyclerView.INVALID_TYPE) {
            outState.putInt(SELECTED_KEY, mPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.menu_movies_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_order_by_popularity:
                SpUtils.putInt(getContext(), Entry.SP_ORDER_MODE, Entry.POPULAR_MOVIE_DIR);
                mOrderMode = Entry.POPULAR_MOVIE_DIR;
                onOrderModeChanged();

                break;

            case R.id.action_order_by_top_rate:
                SpUtils.putInt(getContext(), Entry.SP_ORDER_MODE, Entry.TOP_RATE_MOVIE_DIR);
                mOrderMode = Entry.TOP_RATE_MOVIE_DIR;
                onOrderModeChanged();

                break;

            case R.id.action_order_by_favorite:
                SpUtils.putInt(getContext(), Entry.SP_ORDER_MODE, Entry.FAVORITE_MOVIE_DIR);
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

    /**
     * 加载下一页
     *
     * @param listType 列表类型
     */
    private void loadMoreMovies(String listType) {
        int itemCount = mMovieAdapter.getItemCount();
        if (itemCount % Entry.LOAD_PER != 0) {
            //没有更多数据了
            ToastUtils.showShort(R.string.value_toast_there_is_no_more);
            //refreshLayout.finishLoadMore();
        } else {
            //可能还有更多数据
            getMovieList(listType, Entry.ACTION_LOAD_MORE, itemCount / Entry.LOAD_PER + 1);
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
        //mMovieAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(Entry.SP_MOVIES_STATUS)) {

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
            //mMovieAdapter.notifyDataSetChanged();
            // 加载完成，隐藏进度条
            if (mMovieDetails.size() < MOVIE_LIST_MAX && mOrderMode != Entry.FAVORITE_MOVIE_DIR) {
                showDialog();
            } else {
                dismissDialog();
            }
            updateEmptyView();
        } else {
            // 提示用户没有获取到数据
            updateEmptyView();
            mMovieDetails.clear();
            //mMovieAdapter.notifyDataSetChanged();
        }
        //mMovieAdapter.swapCursor(cursor);

        assert cursor != null;
        cursor.close();
    }

    /**
     * 用于更新在没有获取到数据的情况下的emptyView的显示设置
     */
    private void updateEmptyView() {

        /*if (mMovieAdapter.getItemCount() == 0) {

            if (mTvEmptyView != null) {

                int message = R.string.empty_recycler_view;
                @SyncAdapter.MoviesStatus
                int status = SpUtils.getInt(getContext(), Entry.SP_MOVIES_STATUS, MOVIES_STATUS_OK);
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
        }*/
    }
}