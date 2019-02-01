package com.naruto.popmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Android Studio. Date: 2017/10/30. Time: 下午11:17.
 * Desc:UdaCity_PopularMovies
 *
 * @author: jellybean.
 *
 */
public class AuthenticatorService extends Service {

	/**
	 * 存储authenticator对象的实例
	 */
	private Authenticator mAuthenticator;

	@Override
	public void onCreate() {

		// 创建一个authenticator对象
		mAuthenticator = new Authenticator(this);
	}

	/**
	 * 当系统绑定服务的时候，发出RPC请求，返回IBinder
	 *
	 * @param intent
	 *            intent
	 * @return 返回IBinder
	 */
	@Override
	public IBinder onBind(Intent intent) {

		return mAuthenticator.getIBinder();
	}
}