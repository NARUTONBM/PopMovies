package com.naruto.popmovies.db.model;

import org.litepal.crud.LitePalSupport;

/**
 * 数据库中reviews.db的结构模型
 *
 * @author jelly.
 * @Date 2019-01-30.
 * @Time 13:52.
 */
public class Review extends LitePalSupport {

    private Movie mMovie;
    private int id;
    private String review_id;
    private String author;
    private String content;

    public Movie getMovie() {
        return mMovie;
    }

    public void setMovie(Movie movie) {
        mMovie = movie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReviewId() {
        return review_id;
    }

    public void setReviewId(String reviewId) {
        this.review_id = reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
