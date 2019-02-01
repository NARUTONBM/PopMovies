package com.naruto.popmovies.https;

import retrofit2.Retrofit;

/**
 * @author jelly.
 * @Date 2018/4/20.
 * @Time 11:02.
 */
public class ApiService {

    public static <T> T getApiService(Class<T> tClass, String baseUrl) {
        Retrofit retrofit = new NetworkHelper().getRetrofitBuilder(baseUrl).build();
        return retrofit.create(tClass);
    }
}