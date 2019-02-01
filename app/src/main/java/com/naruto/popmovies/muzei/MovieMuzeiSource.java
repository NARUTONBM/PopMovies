package com.naruto.popmovies.muzei;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;
import com.naruto.popmovies.activity.MainActivity;
import com.naruto.popmovies.data.Entry;
import com.naruto.popmovies.sync.SyncAdapter;
import com.naruto.popmovies.util.SpUtils;
import com.naruto.popmovies.util.Utils;

/**
 * Created by Android Studio. Date: 2017/12/5. Time: 下午3:15.
 *
 * @author jellybean
 */
public class MovieMuzeiSource extends MuzeiArtSource {

	/**
	 * Remember to call this constructor from an empty constructor!
	 */
	public MovieMuzeiSource() {

		super("MovieMuzeiSource");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		super.onHandleIntent(intent);

		boolean dataUpdated = intent != null && SyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction());
		if (dataUpdated && isEnabled()) {

			onUpdate(UPDATE_REASON_OTHER);
		}
	}

	@Override
	protected void onUpdate(int reason) {

        int orderMode = SpUtils.getInt(MovieMuzeiSource.this, Entry.ORDER_MODE, Entry.POPULAR_MOVIE_DIR);
		Cursor cursor = getContentResolver().query(Utils.fetchCurrentUri(orderMode), null, null, null,
                Entry.COLUMN_MOVIE_ID + " ASC");
		if (cursor != null && cursor.moveToFirst()) {

            String movieTitle = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_TITLE));
            String movieOriginalTitle = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_ORIGINAL_TITLE));
            String moviePosterUrl = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_MOVIE_POSTER));

			if (moviePosterUrl != null) {

				publishArtwork(new Artwork.Builder().imageUri(Uri.parse(moviePosterUrl)).title(movieTitle).byline(movieOriginalTitle)
								.viewIntent(new Intent(this, MainActivity.class)).build());
			}
		}

		assert cursor != null;
		cursor.close();
	}
}
