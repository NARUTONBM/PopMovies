package com.naruto.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import static android.provider.BaseColumns._ID;

/**
 * @author jellybean
 */
public class MovieDetailsContentProvider extends ContentProvider {

	private static final String LOG_TAG = MovieDetailsContentProvider.class.getSimpleName();
	private static UriMatcher sUriMatcher;
	private ContentResolver mContentResolver;
    private MovieDetailsDbHelper mDbHelper;

	static {

		// 创建uriMatcher
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		// 添加匹配路径格式
        sUriMatcher.addURI(Entry.CONTENT_AUTHORITY, Entry.POPULAR_TABLE_NAME, Entry.POPULAR_MOVIE_DIR);
        sUriMatcher.addURI(Entry.CONTENT_AUTHORITY, Entry.POPULAR_TABLE_NAME + "/#", Entry.POPULAR_MOVIE_ITEM);
        sUriMatcher.addURI(Entry.CONTENT_AUTHORITY, Entry.TOP_RATE_TABLE_NAME, Entry.TOP_RATE_MOVIE_DIR);
        sUriMatcher.addURI(Entry.CONTENT_AUTHORITY, Entry.TOP_RATE_TABLE_NAME + "/#", Entry.TOP_RATE_MOVIE_ITEM);
        sUriMatcher.addURI(Entry.CONTENT_AUTHORITY, Entry.FAVORITE_TABLE_NAME, Entry.FAVORITE_MOVIE_DIR);
        sUriMatcher.addURI(Entry.CONTENT_AUTHORITY, Entry.FAVORITE_TABLE_NAME + "/#", Entry.FAVORITE_MOVIE_ITEM);
	}

	@Override
	public String getType(@NonNull Uri uri) {
		// 判断传入的uri的类型
		switch (sUriMatcher.match(uri)) {
            case Entry.POPULAR_MOVIE_DIR:

                return MovieDetailsContract.MovieDetailsEntry.POPULAR_DIR_TYPE;

            case Entry.POPULAR_MOVIE_ITEM:

                return MovieDetailsContract.MovieDetailsEntry.POPULAR_ITEM_TYPE;

            case Entry.TOP_RATE_MOVIE_DIR:

                return MovieDetailsContract.MovieDetailsEntry.TOP_RATE_DIR_TYPE;

            case Entry.TOP_RATE_MOVIE_ITEM:

                return MovieDetailsContract.MovieDetailsEntry.TOP_RATE_ITEM_TYPE;

            case Entry.FAVORITE_MOVIE_DIR:

                return MovieDetailsContract.MovieDetailsEntry.FAVORITE_DIR_TYPE;

            case Entry.FAVORITE_MOVIE_ITEM:

                return MovieDetailsContract.MovieDetailsEntry.FAVORITE_ITEM_TYPE;

		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		// 创建DbHelper的实例
		mDbHelper = new MovieDetailsDbHelper(getContext());
		mContentResolver = getContext().getContentResolver();

		return true;
	}

	@Override
	public Uri insert(@NonNull Uri uri, ContentValues values) {
		switch (sUriMatcher.match(uri)) {
            case Entry.POPULAR_MOVIE_DIR:

            case Entry.POPULAR_MOVIE_ITEM:

                return insertMovieDetails(Entry.POPULAR_TABLE_NAME, uri, values);

            case Entry.TOP_RATE_MOVIE_DIR:

            case Entry.TOP_RATE_MOVIE_ITEM:

                return insertMovieDetails(Entry.TOP_RATE_TABLE_NAME, uri, values);

            case Entry.FAVORITE_MOVIE_DIR:

            case Entry.FAVORITE_MOVIE_ITEM:

                return insertMovieDetails(Entry.FAVORITE_TABLE_NAME, uri, values);

		default:

			throw new UnsupportedOperationException("Insertion is not supported for unknown URI: " + uri);
		}
	}

	private Uri insertMovieDetails(String tableName, Uri uri, ContentValues values) {
		// 建立数据库连接
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		// 根据传入的uri，插入新的电影信息
		long id = db.insert(tableName, null, values);
		// 如果id＝－1，说明插入失败
		if (id == -1) {
			Log.e(LOG_TAG, "Failed to insert row for " + uri);

			return null;
		}
		// 传入的uri对应的值的内容发生变化时，重新加载
		mContentResolver.notifyChange(uri, null);

		return ContentUris.withAppendedId(uri, id);
	}

	@Override
	public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
		// 创建一个int型的变量，放置删除结果
		int rowDeleted = 0;
		// 建立数据库连接
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		// 根据URI类型不同，返回不同的结果
		switch (sUriMatcher.match(uri)) {
            case Entry.POPULAR_MOVIE_DIR:
                rowDeleted = db.delete(Entry.POPULAR_TABLE_NAME, selection, selectionArgs);

			break;

            case Entry.POPULAR_MOVIE_ITEM:
			selection = _ID + "=?";
			selectionArgs = new String[] {uri.getPathSegments().get(1)};
                rowDeleted = db.delete(Entry.POPULAR_TABLE_NAME, selection, selectionArgs);

			break;

            case Entry.TOP_RATE_MOVIE_DIR:
                rowDeleted = db.delete(Entry.TOP_RATE_TABLE_NAME, selection, selectionArgs);

			break;

            case Entry.TOP_RATE_MOVIE_ITEM:
			selection = _ID + "=?";
			selectionArgs = new String[] {uri.getPathSegments().get(1)};
                rowDeleted = db.delete(Entry.TOP_RATE_TABLE_NAME, selection, selectionArgs);

			break;

            case Entry.FAVORITE_MOVIE_DIR:
                rowDeleted = db.delete(Entry.FAVORITE_TABLE_NAME, selection, selectionArgs);

			break;

            case Entry.FAVORITE_MOVIE_ITEM:
			selection = _ID + "=?";
			selectionArgs = new String[] {uri.getPathSegments().get(1)};
                rowDeleted = db.delete(Entry.FAVORITE_TABLE_NAME, selection, selectionArgs);

			break;

		default:

			throw new UnsupportedOperationException("Cannot query unknown URI :" + uri);
		}
		if (rowDeleted != 0) {

			mContentResolver.notifyChange(uri, null);
		}

		return rowDeleted;
	}

	@Override
	public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		switch (sUriMatcher.match(uri)) {
            case Entry.POPULAR_MOVIE_DIR:

                return updateMovieDetails(Entry.POPULAR_TABLE_NAME, uri, values, selection, selectionArgs);

            case Entry.POPULAR_MOVIE_ITEM:

			selection = _ID + " = ?";
			selectionArgs = new String[] {uri.getPathSegments().get(1)};

                return updateMovieDetails(Entry.POPULAR_TABLE_NAME, uri, values, selection, selectionArgs);

            case Entry.TOP_RATE_MOVIE_DIR:

                return updateMovieDetails(Entry.TOP_RATE_TABLE_NAME, uri, values, selection, selectionArgs);

            case Entry.TOP_RATE_MOVIE_ITEM:

			selection = _ID + " = ?";
			selectionArgs = new String[] {uri.getPathSegments().get(1)};

                return updateMovieDetails(Entry.TOP_RATE_TABLE_NAME, uri, values, selection, selectionArgs);

            case Entry.FAVORITE_MOVIE_DIR:

                return updateMovieDetails(Entry.FAVORITE_TABLE_NAME, uri, values, selection, selectionArgs);

            case Entry.FAVORITE_MOVIE_ITEM:

			selection = _ID + " = ?";
			selectionArgs = new String[] {uri.getPathSegments().get(1)};

                return updateMovieDetails(Entry.FAVORITE_TABLE_NAME, uri, values, selection, selectionArgs);

		default:

			throw new UnsupportedOperationException("Update is not supported for unknown URI: " + uri);
		}
	}

	/**
	 * 根据uri更新数据库中电影信息的数据
	 *
	 * @param tableName
	 *            执行更新数据的表名
	 * @param uri
	 *            传入的uri
	 * @param values
	 *            新的值
	 * @param selection
	 *            选择条件
	 * @param selectionArgs
	 *            选择条件的匹配内容
	 * @return 返回更新的行号
	 */
	private int updateMovieDetails(String tableName, Uri uri, ContentValues values, String selection,
					String[] selectionArgs) {
		// 对更新的值做非空判断
		if (values.size() == 0) {

			return 0;
		}
		// 建立数据库连接
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		// 执行更新方法
		int rowUpdated = db.update(tableName, values, selection, selectionArgs);
		// 只要更新的行号！＝0，就说明更新成功，反之失败
		if (rowUpdated != 0) {

			mContentResolver.notifyChange(uri, null);
		}

		return rowUpdated;
	}

	@Override
	public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// 创建一个cursor，放置查询结果
		Cursor rowQueried;
		// 建立数据库连接
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		// 根据URI类型不同，返回不同的结果
		switch (sUriMatcher.match(uri)) {
            case Entry.POPULAR_MOVIE_DIR:
                rowQueried = db.query(Entry.POPULAR_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			rowQueried.setNotificationUri(mContentResolver, uri);

			break;

            case Entry.POPULAR_MOVIE_ITEM:
			selection = _ID + "=?";
			selectionArgs = new String[] {uri.getPathSegments().get(1)};
                rowQueried = db.query(Entry.POPULAR_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

			break;

            case Entry.TOP_RATE_MOVIE_DIR:
                rowQueried = db.query(Entry.TOP_RATE_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			rowQueried.setNotificationUri(mContentResolver, uri);

			break;

            case Entry.TOP_RATE_MOVIE_ITEM:
			selection = _ID + "=?";
			selectionArgs = new String[] {uri.getPathSegments().get(1)};
                rowQueried = db.query(Entry.TOP_RATE_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

			break;

            case Entry.FAVORITE_MOVIE_DIR:
                rowQueried = db.query(Entry.FAVORITE_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			rowQueried.setNotificationUri(mContentResolver, uri);

			break;

            case Entry.FAVORITE_MOVIE_ITEM:
			selection = _ID + "=?";
			selectionArgs = new String[] {uri.getPathSegments().get(1)};
                rowQueried = db.query(Entry.FAVORITE_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

			break;

		default:
			throw new UnsupportedOperationException("Cannot query unknown URI :" + uri);
		}

		return rowQueried;
	}

	@Override
	public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
		final SQLiteDatabase db = mDbHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int returnCount = 0;
		switch (match) {
            case Entry.POPULAR_MOVIE_DIR:
			db.beginTransaction();
			try {

				for (ContentValues value : values) {

                    long _id = db.insert(Entry.POPULAR_TABLE_NAME, null, value);
					if (_id != -1) {

						returnCount ++;
					}
				}
				db.setTransactionSuccessful();
			} finally {

				db.endTransaction();
			}
			mContentResolver.notifyChange(uri, null);

			return returnCount;

            case Entry.TOP_RATE_MOVIE_DIR:
			db.beginTransaction();
			try {

				for (ContentValues value : values) {

                    long _id = db.insert(Entry.TOP_RATE_TABLE_NAME, null, value);
					if (_id != -1) {

						returnCount ++;
					}
				}
				db.setTransactionSuccessful();
			} finally {

				db.endTransaction();
			}
			mContentResolver.notifyChange(uri, null);

			return returnCount;

		default:

			return super.bulkInsert(uri, values);
		}
	}
}