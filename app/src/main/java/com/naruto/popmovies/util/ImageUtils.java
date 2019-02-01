package com.naruto.popmovies.util;

/*
 * Created with Android Studio.
 * User: narutonbm@gmail.com
 * Date: 17/3/12
 * Time: 下午3:04
 * Desc: UdaCity_PopularMovies
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class ImageUtils {

	private static final String PIC_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/movies";
	private static final String LOG_TAG = ImageUtils.class.getSimpleName();
	private String mImageUrl;

	public ImageUtils() {
	}

	public Bitmap imageFromUrlToBitmap(String imageUrl) {
		mImageUrl = imageUrl;
		URL url = null;
		Bitmap bitmap = null;
		try {
			url = new URL(imageUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(8 * 1000);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, "IO异常", e);
		}

		return bitmap;
	}

	public String imageFromUrlToByteArray(String imageUrl, String fileName) {
		mImageUrl = imageUrl;
		URL url = null;
		Bitmap bitmap;
		try {
			url = new URL(imageUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		String storagePath = null;
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(8 * 1000);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
				storagePath = saveFile(bitmap, fileName);
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, "IO异常", e);
		}

		return storagePath;
	}

	private String saveFile(Bitmap bitmap, String fileName) {
		boolean compress = false;
		File file = null;
		try {
			file = new File(PIC_PATH, fileName);
			File parentFile = file.getParentFile();
			// 如果文件夹不存在创建文件夹
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					Log.e(LOG_TAG, "文件创建失败");
				}
			}
			compress = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
			if (compress) {

				return file.getAbsolutePath();
			} else {

				return mImageUrl;
			}
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "文件未找到");

			return mImageUrl;
		}
	}
}