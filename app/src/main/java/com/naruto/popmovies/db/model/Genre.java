package com.naruto.popmovies.db.model;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库中genres.db的结构模型
 *
 * @author jelly.
 * @Date 2019-01-30.
 * @Time 13:50.
 */
public class Genre extends LitePalSupport {

    private List<Movie> mMovieList;
    private int id;
    private int genre_id;
    private String name;

    public List<Movie> getMovieList() {
        if (mMovieList == null) {
            mMovieList = new ArrayList<>();
        }
        return mMovieList;
    }

    public void setMovieList(List<Movie> movieList) {
        mMovieList = movieList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGenreId() {
        return genre_id;
    }

    public void setGenreId(int genreId) {
        this.genre_id = genreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
