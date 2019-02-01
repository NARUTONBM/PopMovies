package com.naruto.popmovies.util;

import com.naruto.popmovies.fragment.BaseFragment;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 放置便于使用 RxJava 的一些工具方法
 *
 * @author jelly
 */
public class RxUtils {

    private RxUtils() {
    }

    public static <T> ObservableTransformer<T, T> applySchedulers() {
        //final BaseActivity baseActivity = (BaseActivity) view;
        return observable -> observable.subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) {
                        //显示进度条
                        //baseActivity.showLoadingDialog();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() {
                        //隐藏进度条
                        //baseActivity.closeLoadingDialog();
                    }
                });
    }

    public static <T> ObservableTransformer<T, T> applySchedulers(BaseFragment fragment) {
        return observable -> observable.subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) {
                        //显示进度条
                        fragment.showDialog();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() {
                        //隐藏进度条
                        fragment.dismissDialog();
                    }
                });
    }
}
