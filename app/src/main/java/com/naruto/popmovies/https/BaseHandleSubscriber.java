package com.naruto.popmovies.https;

import com.blankj.utilcode.util.ToastUtils;
import com.naruto.popmovies.fragment.BaseFragment;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * @author jelly
 */
public abstract class BaseHandleSubscriber<T> implements Observer<T> {

    private BaseFragment mFragment;

    public BaseHandleSubscriber(BaseFragment fragment) {
        mFragment = fragment;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        mFragment.showDialog();
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(@NonNull Throwable t) {
        mFragment.dismissDialog();
        t.printStackTrace();
        ToastUtils.showLong("服务器请求报错：" + t.getMessage());
        //如果你某个地方不想使用全局错误处理,则重写 onError(Throwable) 并将 super.onError(e); 删掉
        //如果你不仅想使用全局错误处理,还想加入自己的逻辑,则重写 onError(Throwable) 并在 super.onError(e); 后面加入自己的逻辑
        //mHandlerFactory.handleError(t);
    }
}