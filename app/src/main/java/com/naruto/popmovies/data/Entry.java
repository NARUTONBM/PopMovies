package com.naruto.popmovies.data;

/**
 * 全局引用的常量
 *
 * @author jelly.
 * @Date 2019-01-25.
 * @Time 10:11.
 */
public class Entry {

    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    public static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p";
    public static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?";

    /**
     * ContentProvider的主机名
     */
    public static final String CONTENT_AUTHORITY = "com.naruto.popmovies.data";

    /**
     * 表名
     */
    public static final String POPULAR_TABLE_NAME = "popular_details";
    public static final String TOP_RATE_TABLE_NAME = "top_rate_details";
    public static final String FAVORITE_TABLE_NAME = "favorite_details";

    /**
     * 列名
     */
    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_MOVIE_POSTER = "movie_poster";
    public static final String COLUMN_MOVIE_BACKDROP = "movie_backdrop";
    public static final String COLUMN_MOVIE_TITLE = "movie_title";
    public static final String COLUMN_MOVIE_ORIGINAL_TITLE = "movie_original_title";
    public static final String COLUMN_MOVIE_ORIGINAL_LANGUAGE = "movie_original_language";
    public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
    public static final String COLUMN_MOVIE_VOTE_COUNT = "movie_vote_count";
    public static final String COLUMN_MOVIE_VOTE_AVERAGE = "movie_vote_average";
    public static final String COLUMN_MOVIE_GENRES = "movie_genres";
    public static final String COLUMN_MOVIE_OVERVIEW = "movie_overview";
    public static final String COLUMN_MOVIE_RUNNING_TIME = "movie_running_time";
    public static final String COLUMN_MOVIE_VIDEO = "movie_video";
    public static final String COLUMN_MOVIE_REVIEWS = "movie_reviews";

    /**
     * pop的序号
     */
    public static final int POPULAR_MOVIE_DIR = 0;
    public static final int POPULAR_MOVIE_ITEM = 1;
    /**
     * rate的序号
     */
    public static final int TOP_RATE_MOVIE_DIR = 2;
    public static final int TOP_RATE_MOVIE_ITEM = 3;
    /**
     * favor的序号
     */
    public static final int FAVORITE_MOVIE_DIR = 4;
    public static final int FAVORITE_MOVIE_ITEM = 5;
    /**
     * loader序号
     */
    public static final int MOVIE_ORDER_POPULAR_DATABASE_LOADER_ID = 12;
    public static final int MOVIE_ORDER_TOP_RATE_DATABASE_LOADER_ID = 13;
    public static final int MOVIE_ORDER_FAVORITE_DATABASE_LOADER_ID = 14;

    /**
     * SP中的键：order_mode
     */
    public static final String SP_ORDER_MODE = "order_mode";
    /**
     * SP中的键：db_init
     */
    public static final String SP_DB_INIT = "db_init";
    /**
     * SP中的键：movies_status
     */
    public static final String SP_MOVIES_STATUS = "movies_status";
    public static final int WAIT_TIME = 10;

    /**
     * links.db中表示视频的type值
     */
    public static final int LINK_TYPE_VIDEO = 123;
    /**
     * links.db中表示backdrop的type值
     */
    public static final int LINK_TYPE_BACKDROP = 213;
    /**
     * links.db中表示poster的type值
     */
    public static final int LINK_TYPE_POSTER = 321;

    /**
     * 代表下拉刷新
     */
    public static final int ACTION_REFRESH = 11;
    /**
     * 代表上滑加载更多
     */
    public static final int ACTION_LOAD_MORE = 22;
    /**
     * 一次加载的条目数
     */
    public static final int LOAD_PER = 20;

    /**
     * 列表类型：popular
     */
    public static final String REQUEST_POPULAR = "popular";
    /**
     * 列表类型：top_rate
     */
    public static final String REQUEST_TOP_RATE = "top_rate";
    /**
     * 列表类型：favorite
     */
    public static final String REQUEST_FAVORITE = "favorite";
    /**
     * 一页加载的最大数量
     */
    public static final int SINGLE_PAGE_SIZE = 20;
}
