package com.naruto.popmovies.https;

import android.support.annotation.NonNull;

import com.naruto.popmovies.BuildConfig;
import com.naruto.popmovies.data.Entry;
import com.naruto.popmovies.stetho.MyApplication;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author jellybean.
 * @Date 2018/4/20.
 * @Time 11:03.
 */
public class NetworkHelper {

    private static NetworkHelper sNetworkHelper;
    private static OkHttpClient sOkHttpClient;
    private static Converter.Factory sGsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory sRxJavaCallAdapterFactory = RxJava2CallAdapterFactory.create();
    private static Retrofit.Builder sRetrofitBuilder;

    /**
     * 可重试次数
     */
    private int maxConnectCount = 50;
    /**
     * 当前已重试次数
     */
    private int currentRetryCount = 0;
    /**
     * 重试等待时间
     */
    private int waitRetryTime = 0;

    /**
     * 构造方法，初始化Retrofit Build
     */
    public NetworkHelper() {
        sRetrofitBuilder = new Retrofit.Builder()
                .client(getOkHttpClient());
    }

    /**
     * 获取单例模式实例
     *
     * @return 返回网络工具实例
     */
    public static NetworkHelper getInstance() {
        if (sNetworkHelper == null) {
            synchronized (NetworkHelper.class) {
                if (sNetworkHelper == null) {
                    sNetworkHelper = new NetworkHelper();
                }
            }
        }
        return sNetworkHelper;
    }

    /**
     * 配置OkHttp并获取实例
     *
     * @return OkHttp的实例
     */
    private static OkHttpClient getOkHttpClient() {
        //配置拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String msg) {
                Logger.d("OKHttp----" + msg);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //创建缓存
        File cacheFile = new File(MyApplication.getContext().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100);
        /*PersistentCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(CaiNiaoApplication.getAppContext()));*/

        if (sOkHttpClient == null) {
            synchronized (NetworkHelper.class) {
                if (sOkHttpClient == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    if (BuildConfig.DEBUG) {
                        //拦截okHttp的日志，如果开启了会导致上传回调被调用两次
                        builder.addInterceptor(loggingInterceptor);
                    }
                    //超时时间
                    builder.connectTimeout(Entry.WAIT_TIME, TimeUnit.SECONDS)
                            .readTimeout(Entry.WAIT_TIME, TimeUnit.SECONDS)
                            .writeTimeout(Entry.WAIT_TIME, TimeUnit.SECONDS)
                            //有登录功能的，可以打开cookie保持
                            //.cookieJar(cookieJar)
                            .cache(cache)
                            /*//配置Https认证，不需要可不用
                            .sslSocketFactory(SslContextFactory.getSSLSocketFactoryForTwoWay())
                            .hostnameVerifier(new SafeHostnameVerifier())*/
                            //错误重连
                            .retryOnConnectionFailure(true);
                    sOkHttpClient = builder.build();
                }
            }
        }
        return sOkHttpClient;
    }

    public Retrofit.Builder getRetrofitBuilder(String baseUrl) {
        return sRetrofitBuilder.baseUrl(baseUrl)
                .addConverterFactory(sGsonConverterFactory)
                .addCallAdapterFactory(sRxJavaCallAdapterFactory);
    }

    /**
     * 出现网络异常时重复请求的方法
     *
     * @param throwableObservable 观察者
     * @return 异常处理的观察者
     */
    public Observable<?> getRetryObservable(Observable<Throwable> throwableObservable) {
        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Throwable throwable) {
                Logger.e("网络请求发生异常：" + throwable.toString());
                if (throwable instanceof IOException) {
                    Logger.e("属于IO异常，将重新发出请求！");
                    if (currentRetryCount < maxConnectCount) {
                        currentRetryCount++;
                        Logger.i("重新请求次数：" + currentRetryCount);
                        waitRetryTime = (1 + currentRetryCount) * Entry.WAIT_TIME;
                        Logger.i("等待时间：" + waitRetryTime);

                        return Observable.just(1).debounce(waitRetryTime, TimeUnit.MILLISECONDS);
                    } else {
                        return Observable.error(new Throwable("重试次数已超过设置次数：" + currentRetryCount + "，即 不再重试"));
                    }
                } else {
                    return Observable.error(new Throwable("发生了非网络异常（非I/O异常）"));
                }
            }
        });
    }
}