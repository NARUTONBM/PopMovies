package com.naruto.popmovies.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;

import com.naruto.popmovies.data.Entry;
import com.naruto.popmovies.data.MovieDetailsContract;

/**
 * Created by Android Studio. Date: 2016/1/6. Time: 下午4:42.
 *
 * @author Mr.Jude
 */
public class Utils {
	/**
	 * This method converts dp unit to equivalent pixels, depending on device
	 * density.
	 *
	 * @param dp
	 *            A value in dp (density independent pixels) unit. Which we need to
	 *            convert into pixels
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device
	 *         density
	 */
	public static float convertDpToPixel(float dp, Context context) {

		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();

		return dp * (metrics.densityDpi / 160f);
	}

	/**
	 * This method converts device specific pixels to density independent pixels.
	 *
	 * @param px
	 *            A value in px (pixels) unit. Which we need to convert into db
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context) {

		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();

		return px / (metrics.densityDpi / 160f);
	}

	/**
	 * 判断当前是否连接了网络
	 *
	 * @param context
	 *            上下文
	 * @return 有网络连接，则返回true；反之为false
	 */
	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm != null ? cm.getActiveNetworkInfo() : null;

		return networkInfo != null && networkInfo.isConnectedOrConnecting();
	}

	/**
	 * 根据当前的排序模式，返回相应的uri
	 *
	 * @return 相应的uri
	 */
	public static Uri fetchCurrentUri(int orderMode) {

        if (orderMode == Entry.POPULAR_MOVIE_DIR) {

			return MovieDetailsContract.MovieDetailsEntry.CONTENT_POPULAR_URI;
        } else if (orderMode == Entry.TOP_RATE_MOVIE_DIR) {

			return MovieDetailsContract.MovieDetailsEntry.CONTENT_TOP_RATE_URI;
		} else {

			return MovieDetailsContract.MovieDetailsEntry.CONTENT_FAVORITE_URI;
		}
	}
}