package com.naruto.popmovies.db.model;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * 数据库中movies.db的结构模型
 *
 * @author jelly.
 * @Date 2019-01-30.
 * @Time 13:49.
 */
public class Movie extends LitePalSupport {

    private int id;
    private int movie_id;
    private int vote_count;
    private boolean video;
    private double vote_average;
    private String title;
    private double popularity;
    private String poster_path;
    private String original_language;
    private String original_title;
    private String backdrop_path;
    private boolean adult;
    private String overview;
    private String release_date;
    private List<Links> mLinkList;
    private List<Genre> mGenreList;
    private List<Review> mReviewList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movie_id;
    }

    public void setMovieId(int movieId) {
        this.movie_id = movieId;
    }

    public int getVoteCount() {
        return vote_count;
    }

    public void setVoteCount(int voteCount) {
        this.vote_count = voteCount;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVoteAverage() {
        return vote_average;
    }

    public void setVoteAverage(double voteAverage) {
        this.vote_average = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public void setPosterPath(String posterPath) {
        this.poster_path = posterPath;
    }

    public String getOriginalLanguage() {
        return original_language;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.original_language = originalLanguage;
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public void setOriginalTitle(String originalTitle) {
        this.original_title = originalTitle;
    }

    public String getBackdropPath() {
        return backdrop_path;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdrop_path = backdropPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(String releaseDate) {
        this.release_date = releaseDate;
    }

    public List<Links> getLinkList() {
        return mLinkList;
    }

    public void setLinkList(List<Links> linkList) {
        mLinkList = linkList;
    }

    public List<Genre> getGenreList() {
        return mGenreList;
    }

    public void setGenreList(List<Genre> genreList) {
        mGenreList = genreList;
    }

    public List<Review> getReviewList() {
        return mReviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        mReviewList = reviewList;
    }
}
