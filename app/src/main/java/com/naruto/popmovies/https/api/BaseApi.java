package com.naruto.popmovies.https.api;

import com.naruto.popmovies.bean.ConfigurationBean;
import com.naruto.popmovies.bean.GenreListBean;
import com.naruto.popmovies.bean.MovieListBean;
import com.naruto.popmovies.bean.VIRListBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 网络通信的接口
 *
 * @author jelly.
 * @Date 2018/5/7.
 * @Time 15:54.
 */
public interface BaseApi {

    /**
     * 请求配置参数数据
     *
     * @param apiKey api_key
     * @return 返回配置数据信息
     */
    @GET("configuration?language=zh")
    Observable<ConfigurationBean> getConfiguration(@Query("api_key") String apiKey);

    /**
     * 请求电影列表
     *
     * @param listType 列表的类型：popular/top_rate
     * @param apiKey api_key
     * @param page   页码
     * @return 返回电影列表
     */
    @GET("movie/{list_type}?language=zh")
    Observable<MovieListBean> getMovieList(@Path("list_type") String listType, @Query("api_key") String apiKey, @Query("page") int page);

    /**
     * 请求电影视频和评论列表
     *
     * @param movieId 影片ID
     * @param apiKey  api_key
     * @return 返回电影视频、图片和评论列表
     */
    @GET("movie/{movie_id}?append_to_response=videos,images,reviews")
    Observable<VIRListBean> getVideoAndReviewList(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    /**
     * 请求电影类型列表
     *
     * @param apiKey api_key
     * @return 返回电影类型列表
     */
    @GET("genre/movie/list?language=zh")
    Observable<GenreListBean> getGenreList(@Query("api_key") String apiKey);
}