package com.naruto.popmovies.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author jellybean
 */
public class SpUtils {

	private static SharedPreferences sPreferences;

	/**
	 * 读取SP中的int变量
	 *
	 * @param context
	 *            上下文环境
	 * @param key
	 *            存储节点名称
	 * @param defValue
	 *            默认值
	 * @return 默认值或者相应节点读取到的结果
	 */
	public static int getInt(Context context, String key, int defValue) {

		// 存储节点名称，读写方式
		if (sPreferences == null) {

            sPreferences = context.getSharedPreferences("com.naruto.popmovies_preferences",
							Context.MODE_PRIVATE);
		}

		return sPreferences.getInt(key, defValue);
	}

	/**
	 * 写入int变量到sp中
	 *
	 * @param context
	 *            上下文环境
	 * @param key
	 *            存储节点名称
	 * @param value
	 *            存储节点的值int
	 */
	public static void putInt(Context context, String key, int value) {

		// 存储节点名称，读写方式
		if (sPreferences == null) {

            sPreferences = context.getSharedPreferences("com.naruto._popmovies_preferences",
							Context.MODE_PRIVATE);
		}

		sPreferences.edit().putInt(key, value).apply();
	}
}