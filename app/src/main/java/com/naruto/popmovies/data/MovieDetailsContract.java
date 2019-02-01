package com.naruto.popmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created with Android Studio.
 * User: narutonbm@gmail.com
 * Date: 17/3/8
 * Time: 下午8:13
 * Desc: UdaCity_PopularMovies
 * @author jellybean
 */
public class MovieDetailsContract {

	/**
	 * 所有URI共用的基础URI
	 */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + Entry.CONTENT_AUTHORITY);

	public static final class MovieDetailsEntry implements BaseColumns {

        public static final Uri CONTENT_POPULAR_URI = BASE_CONTENT_URI.buildUpon().appendPath(Entry.POPULAR_TABLE_NAME).build();
        public static final Uri CONTENT_TOP_RATE_URI = BASE_CONTENT_URI.buildUpon().appendPath(Entry.TOP_RATE_TABLE_NAME).build();
        public static final Uri CONTENT_FAVORITE_URI = BASE_CONTENT_URI.buildUpon().appendPath(Entry.FAVORITE_TABLE_NAME).build();

        static final String POPULAR_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + Entry.CONTENT_AUTHORITY + "/"
                + Entry.POPULAR_TABLE_NAME;
        static final String POPULAR_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + Entry.CONTENT_AUTHORITY + "/"
                + Entry.POPULAR_TABLE_NAME;
        static final String TOP_RATE_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + Entry.CONTENT_AUTHORITY + "/"
                + Entry.TOP_RATE_TABLE_NAME;
        static final String TOP_RATE_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + Entry.CONTENT_AUTHORITY + "/"
                + Entry.TOP_RATE_TABLE_NAME;
        static final String FAVORITE_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + Entry.CONTENT_AUTHORITY + "/"
                + Entry.FAVORITE_TABLE_NAME;
        static final String FAVORITE_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + Entry.CONTENT_AUTHORITY + "/"
                + Entry.FAVORITE_TABLE_NAME;

		public static Uri buildDetailsUri(Uri uri, long id) {

			return ContentUris.withAppendedId(uri, id);
		}
	}
}