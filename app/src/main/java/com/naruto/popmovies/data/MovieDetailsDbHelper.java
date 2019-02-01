package com.naruto.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.naruto.popmovies.data.MovieDetailsContract.MovieDetailsEntry._ID;

/**
 * Created with Android Studio.
 * User: narutonbm@gmail.com
 * Date: 17/3/9
 * Time: 下午2:31
 * Desc: UdaCity_PopularMovies
 *
 * @author naruto
 */
public class MovieDetailsDbHelper extends SQLiteOpenHelper {

    /**
     * 数据库名
     */
    private static final String DATABASE_NAME = "movie_details.db";
    /**
     * 数据库版本号
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * 构造方法
     *
     * @param context 上下文环境
     */
    MovieDetailsDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String sqlCreateMovieDetailOrderPopularTable = "CREATE TABLE " + Entry.POPULAR_TABLE_NAME + "(" + _ID
                + " INTEGER PRIMARY KEY, " + Entry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " + Entry.COLUMN_MOVIE_POSTER
                + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_BACKDROP + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_TITLE
                + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_ORIGINAL_TITLE + " TEXT NOT NULL, "
                + Entry.COLUMN_MOVIE_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_RELEASE_DATE
                + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_VOTE_COUNT + " INTEGER NOT NULL, " + Entry.COLUMN_MOVIE_VOTE_AVERAGE
                + " REAL NOT NULL, " + Entry.COLUMN_MOVIE_GENRES + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_OVERVIEW
                + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_RUNNING_TIME + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_VIDEO
                + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_REVIEWS + " TEXT NOT NULL " + ");";

        final String sqlCreateMovieDetailOrderTopStarTable = "CREATE TABLE " + Entry.TOP_RATE_TABLE_NAME + "(" + _ID
                + " INTEGER PRIMARY KEY, " + Entry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " + Entry.COLUMN_MOVIE_POSTER
                + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_BACKDROP + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_TITLE
                + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_ORIGINAL_TITLE + " TEXT NOT NULL, "
                + Entry.COLUMN_MOVIE_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_RELEASE_DATE
                + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_VOTE_COUNT + " INTEGER NOT NULL, " + Entry.COLUMN_MOVIE_VOTE_AVERAGE
                + " REAL NOT NULL, " + Entry.COLUMN_MOVIE_GENRES + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_OVERVIEW
                + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_RUNNING_TIME + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_VIDEO
                + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_REVIEWS + " TEXT NOT NULL " + ");";

        final String sqlCreateMovieDetailOrderFavorite = "CREATE TABLE " + Entry.FAVORITE_TABLE_NAME + "(" + _ID
                + " INTEGER PRIMARY KEY, " + Entry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " + Entry.COLUMN_MOVIE_POSTER
                + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_BACKDROP + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_TITLE
                + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_ORIGINAL_TITLE + " TEXT NOT NULL, "
                + Entry.COLUMN_MOVIE_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_RELEASE_DATE
                + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_VOTE_COUNT + " INTEGER NOT NULL, " + Entry.COLUMN_MOVIE_VOTE_AVERAGE
                + " REAL NOT NULL, " + Entry.COLUMN_MOVIE_GENRES + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_OVERVIEW
                + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_RUNNING_TIME + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_VIDEO
                + " TEXT NOT NULL, " + Entry.COLUMN_MOVIE_REVIEWS + " TEXT NOT NULL " + ");";
        // 执行创建表的操作
        db.execSQL(sqlCreateMovieDetailOrderPopularTable);
        db.execSQL(sqlCreateMovieDetailOrderTopStarTable);
        db.execSQL(sqlCreateMovieDetailOrderFavorite);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Entry.POPULAR_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Entry.TOP_RATE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Entry.FAVORITE_TABLE_NAME);
        onCreate(db);
    }
}