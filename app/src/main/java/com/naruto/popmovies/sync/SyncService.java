package com.naruto.popmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author jellybean
 */
public class SyncService extends Service {

	public SyncService() {
	}

	/**
	 * 存储SyncAdapter的一个实例
	 */
	private static SyncAdapter sSyncAdapter = null;

	/**
	 * 作为线程安全锁使用的对象
	 */
	private static final Object SYNC_ADAPTER_LOCK = new Object();

	/**
	 * 实例化SyncAdapter
	 */
	@Override
	public void onCreate() {

		/*
		 * Create the sync adapter as a singleton. Set the sync adapter as syncable
		 * Disallow parallel syncs
		 */
		synchronized (SYNC_ADAPTER_LOCK) {

			if (sSyncAdapter == null) {

				sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
			}
		}
	}

	/**
	 * Return an object that allows the system to invoke the sync adapter.
	 *
	 * @param intent
	 *            intent
	 * @return an object that allows the system to invoke the sync adapter.
	 */
	@Override
	public IBinder onBind(Intent intent) {

		/*
		 * Get the object that allows external processes to call onPerformSync(). The
		 * object is created in the base class code when the SyncAdapter constructors
		 * call super()
		 */
		return sSyncAdapter.getSyncAdapterBinder();
	}
}
