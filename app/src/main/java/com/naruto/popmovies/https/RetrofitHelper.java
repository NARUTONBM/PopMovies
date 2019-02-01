package com.naruto.popmovies.https;

import com.naruto.popmovies.data.Entry;
import com.naruto.popmovies.https.api.BaseApi;

/**
 * @author jelly.
 * @Date 2018/4/20.
 * @Time 11:04.
 */
public class RetrofitHelper {

    private static BaseApi sBaseApi;

    public static BaseApi getBaseApi() {
        return sBaseApi;
    }

    static {
        sBaseApi = ApiService.getApiService(BaseApi.class, Entry.BASE_URL);
    }
}