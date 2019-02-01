package com.naruto.popmovies.bean;

import com.naruto.popmovies.db.model.Genre;

import java.util.List;

/**
 * genre接口返回的数据类型
 *
 * @author jelly.
 * @Date 2019-01-31.
 * @Time 23:31.
 */
public class GenreListBean {

    private List<Genre> genres;

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
