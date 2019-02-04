package com.naruto.popmovies.util;

import com.naruto.popmovies.BuildConfig;
import com.naruto.popmovies.bean.GenreListBean;
import com.naruto.popmovies.bean.MovieListBean;
import com.naruto.popmovies.bean.VIRListBean;
import com.naruto.popmovies.data.Entry;
import com.naruto.popmovies.db.model.Genre;
import com.naruto.popmovies.db.model.Links;
import com.naruto.popmovies.db.model.Movie;
import com.naruto.popmovies.db.model.Review;
import com.naruto.popmovies.fragment.MoviesFragment;
import com.naruto.popmovies.https.BaseHandleSubscriber;
import com.naruto.popmovies.https.RetrofitHelper;
import com.orhanobut.logger.Logger;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * movies_info.db数据库的增删查工具类
 *
 * @author jelly.
 * @Date 2019-01-30.
 * @Time 17:21.
 */
public class MovieDbUtils {

    /**
     * 添加单条movie数据
     *
     * @param movie       movie原始数据
     * @param virListBean movie对应的video,image,review数据
     */
    public static void addMovie(int type, MovieListBean.ResultsBean movie, VIRListBean virListBean, OnCurdFinished onCurdFinished) {
        Movie movieInfo = new Movie();
        int movieId = movie.getId();
        movieInfo.setMovieId(movieId);
        movieInfo.setAdult(movie.isAdult());
        movieInfo.setBackdropPath(movie.getBackdropPath());
        movieInfo.setOriginalLanguage(movie.getOriginalLanguage());
        movieInfo.setOriginalTitle(movie.getOriginalTitle());
        movieInfo.setOverview(movie.getOverview());
        movieInfo.setPopularity(movie.getPopularity());
        movieInfo.setPosterPath(movie.getPosterPath());
        movieInfo.setReleaseDate(movie.getReleaseDate());
        movieInfo.setTitle(movie.getTitle());
        movieInfo.setVoteAverage(movie.getVoteAverage());
        movieInfo.setVoteCount(movie.getVoteCount());
        movieInfo.setType(type);
        movieInfo.setLinkList(addLinks(virListBean));
        movieInfo.setReviewList(addReview(virListBean));
        movieInfo.saveOrUpdate("movie_id = ?", String.valueOf(movieId));
        movieInfo.setGenreList(getGenreList(movie.getGenres(), movieInfo));
        boolean saveOrUpdate = movieInfo.saveOrUpdate("movie_id = ?", String.valueOf(movieId));
        Logger.d("更新movie_id: " + movieId + (saveOrUpdate ? "成功" : "失败"));
        onCurdFinished.onFinished(saveOrUpdate);
    }

    /*public static void addMovieList(MovieListBean movieListBean, List<VIRListBean> virListBeanList) {
        List<MovieListBean.ResultsBean> resultsBeanList = movieListBean.getResults();
        for (int i = 0; i < resultsBeanList.size(); i++) {
            addMovie(resultsBeanList.get(i), virListBeanList.get(0));
        }
    }*/

    public static void initGenreDB(MoviesFragment fragment) {
        RetrofitHelper.getBaseApi()
                .getGenreList(BuildConfig.MOVIE_DB_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseHandleSubscriber<GenreListBean>(fragment) {
                    @Override
                    public void onNext(GenreListBean genreList) {
                        for (Genre genre : genreList.getGenres()) {
                            Genre dbGenre = new Genre();
                            dbGenre.setGenreId(genre.getId());
                            dbGenre.setName(genre.getName());
                            boolean suResult = dbGenre.saveOrUpdate("genre_id = ?", String.valueOf(genre.getId()));
                            fragment.onInitGenreDbFinished(suResult, genreList.getGenres().size());
                        }
                    }
                });
    }

    /**
     * 为movie匹配对应的链接
     *
     * @param virListBean movie对应的video,image,review数据
     * @return 链接列表
     */
    private static List<Links> addLinks(VIRListBean virListBean) {
        List<Links> linkList = new ArrayList<>();
        List<VIRListBean.VideosBean.ResultsBean> videoList = virListBean.getVideos().getResults();
        for (VIRListBean.VideosBean.ResultsBean video : videoList) {
            Links links = new Links();
            links.setLink(video.getUrl());
            links.setType(Entry.LINK_TYPE_VIDEO);
            links.saveOrUpdate("link = ?", video.getUrl());
            linkList.add(links);
        }
        List<VIRListBean.ImagesBean.BackdropsBean> backdropList = virListBean.getImages().getBackdrops();
        for (VIRListBean.ImagesBean.BackdropsBean backdrop : backdropList) {
            Links links = new Links();
            links.setLink(backdrop.getUrl());
            links.setType(Entry.LINK_TYPE_BACKDROP);
            links.saveOrUpdate("link = ?", backdrop.getUrl());
            linkList.add(links);
        }
        List<VIRListBean.ImagesBean.PostersBean> posterList = virListBean.getImages().getPosters();
        for (VIRListBean.ImagesBean.PostersBean poster : posterList) {
            Links links = new Links();
            links.setLink(poster.getUrl());
            links.setType(Entry.LINK_TYPE_POSTER);
            links.saveOrUpdate("link = ?", poster.getUrl());
            linkList.add(links);
        }

        return linkList;
    }

    /**
     * 为movie匹配对应的评论
     *
     * @param virListBean movie对应的video,image,review数据
     * @return 评论列表
     */
    private static List<Review> addReview(VIRListBean virListBean) {
        List<Review> reviewList = new ArrayList<>();
        List<VIRListBean.ReviewsBean.ResultsBeanX> reviews = virListBean.getReviews().getResults();
        for (VIRListBean.ReviewsBean.ResultsBeanX review : reviews) {
            Review newReview = new Review();
            newReview.setReviewId(review.getId());
            newReview.setAuthor(review.getAuthor());
            newReview.setContent(review.getContent());
            newReview.saveOrUpdate("review_id = ?", String.valueOf(review.getId()));
            reviewList.add(newReview);
        }

        return reviewList;
    }

    /**
     * 为movie匹配对应的类型
     *
     * @param genreIds movie的类型id列表
     * @param movie    对应的movie对象
     * @return 类型列表
     */
    private static List<Genre> getGenreList(List<Integer> genreIds, Movie movie) {
        List<Genre> genreList = new ArrayList<>();
        for (Integer genreId : genreIds) {
            Genre genre = LitePal.where("genre_id = ?", String.valueOf(genreId))
                    .find(Genre.class)
                    .get(0);
            List<Movie> movieList = genre.getMovieList();
            movieList.add(movie);
            genre.setMovieList(movieList);
            boolean saveOrUpdate = genre.saveOrUpdate("genre_id = ?", String.valueOf(genreId));
            Logger.d("更新genre_id: " + genreId + (saveOrUpdate ? "成功" : "失败"));
            genreList.add(genre);
        }

        return genreList;
    }

    /**
     * 数据库操作结果的回调接口
     */
    public interface OnCurdFinished {

        /**
         * 当CURD操作完成时触发该回调方法
         *
         * @param result CURD的结果
         */
        void onFinished(boolean result);

        /**
         * 当genre.db写入完成时触发该回调方法
         *
         * @param result    单条genre写入结果
         * @param genreSize genreList的数量
         */
        void onInitGenreDbFinished(boolean result, int genreSize);
    }
}
