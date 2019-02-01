package com.naruto.popmovies.db.model;

import org.litepal.crud.LitePalSupport;

/**
 * 数据库中links.db的结构模型
 *
 * @author jelly.
 * @Date 2019-01-30.
 * @Time 13:52.
 */
public class Links extends LitePalSupport {

    private Movie mMovie;
    private int id;
    private String link;
    private int type;

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
