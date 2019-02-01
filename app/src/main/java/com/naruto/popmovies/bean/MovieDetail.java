package com.naruto.popmovies.bean;


import android.database.Cursor;

import com.naruto.popmovies.data.Entry;

import java.util.ArrayList;

/**
 * Created with Android Studio.
 * User: narutonbm@gmail.com
 * Date: 2017-02-08
 * Time: 14:39
 * Desc: UdaCity_PopularMovies
 *
 * @author jellybean
 */
public class MovieDetail {

    // 创建参数
    private final String mPosterHttpPath;
    private final String mOverview;
    private final String mReleaseDateY;
    private final String mGenreStrs;
    private final String mOriginalTitle;
    private final String mOriginalLanguage;
    private final String mTitle;
    private final String mBackdropHttpPath;
    private final int mVoteCount;
    private final double mVoteAverage;
    private final String mRunTimeData;
    private final String mReviewsData;
    private final String mVideosData;
    private final int mId;

    /**
     * 构造函数
     *
     * @param id               电影id
     * @param posterHttpPath   海报URL地址
     * @param overview         简介内容
     * @param releaseDateY     上映日期（年）
     * @param genreStrs        影片类别
     * @param originalTitle    原始标题
     * @param originalLanguage 原始语言
     * @param title            标题
     * @param backdropHttpPath 特辑图片地址
     * @param voteCount        投票数
     * @param voteAverage      平均分数
     * @param runTimeData      电影时长
     * @param reviewsData      评论内容
     * @param videosData       预告片的url地址
     */
    private MovieDetail(int id, String posterHttpPath, String overview, String releaseDateY, String genreStrs, String originalTitle,
                        String originalLanguage, String title, String backdropHttpPath, int voteCount, double voteAverage, String runTimeData,
                        String reviewsData, String videosData) {
        mId = id;
        mPosterHttpPath = posterHttpPath;
        mOverview = overview;
        mReleaseDateY = releaseDateY;
        mGenreStrs = genreStrs;
        mOriginalTitle = originalTitle;
        mOriginalLanguage = originalLanguage;
        mTitle = title;
        mBackdropHttpPath = backdropHttpPath;
        mVoteCount = voteCount;
        mVoteAverage = voteAverage;
        mRunTimeData = runTimeData;
        mReviewsData = reviewsData;
        mVideosData = videosData;
    }

    public static ArrayList<MovieDetail> fromCursor(ArrayList<MovieDetail> movieDetails, Cursor cursor) {
        MovieDetail details = null;
        if (cursor != null && cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_MOVIE_ID));
                String poster = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_POSTER));
                String backDrop = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_BACKDROP));
                String title = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_TITLE));
                String originTitle = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_ORIGINAL_TITLE));
                String originLanguage = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_ORIGINAL_LANGUAGE));
                String releaseDate = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_RELEASE_DATE));
                int voteCount = cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_MOVIE_VOTE_COUNT));
                double voteAverage = cursor.getDouble(cursor.getColumnIndex(Entry.COLUMN_MOVIE_VOTE_AVERAGE));
                String genres = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_GENRES));
                String overview = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_OVERVIEW));
                String runTime = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_RUNNING_TIME));
                String videos = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_VIDEO));
                String reviews = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_REVIEWS));

                details = new MovieDetail(id, poster, overview, releaseDate, genres, originTitle, originLanguage, title,
                        backDrop, voteCount, voteAverage, runTime, reviews, videos);
                movieDetails.add(details);
            } while (cursor.moveToNext());
        }

        return movieDetails;
    }

    public int getId() {
        return mId;
    }

    public String getPosterHttpPath() {
        return mPosterHttpPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getReleaseDateY() {
        return mReleaseDateY;
    }

    public String getGenreStrs() {
        return mGenreStrs;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBackdropHttpPath() {
        return mBackdropHttpPath;
    }

    public int getVoteCount() {
        return mVoteCount;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public String getRunTimeData() {
        return mRunTimeData;
    }

    public String getReviewsData() {
        return mReviewsData;
    }

    public String getVideosData() {
        return mVideosData;
    }
}