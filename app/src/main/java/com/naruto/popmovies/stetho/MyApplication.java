package com.naruto.popmovies.stetho;

import android.content.Context;

import com.blankj.utilcode.util.Utils;
import com.facebook.stetho.Stetho;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

/**
 * @author jelly
 * Created with Android Studio. User: narutonbm@gmail.com
 * Date: 17/3/21 Time: 下午6:41 Desc: UdaCity_PopularMovies
 */
public class MyApplication extends LitePalApplication {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        Stetho.initialize(Stetho.newInitializerBuilder(mContext).enableDumpapp(Stetho.defaultDumperPluginsProvider(mContext))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(mContext)).build());
        Utils.init(mContext);
        LitePal.initialize(mContext);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    public static Context getContext() {
        return mContext;
    }
}