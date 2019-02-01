package com.naruto.popmovies.activity;

/**
 * @author jellybean
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.naruto.popmovies.R;
import com.naruto.popmovies.data.Entry;
import com.naruto.popmovies.fragment.MovieDetailsFragment;
import com.naruto.popmovies.fragment.MoviesFragment;
import com.naruto.popmovies.sync.SyncAdapter;
import com.naruto.popmovies.util.SpUtils;

public class MainActivity extends AppCompatActivity implements MoviesFragment.CallBack {

    private static final String DETAILS_FRAGMENT_TAG = "DFTAG";
	private boolean mTwoPane = false;
	private int mOrderMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar_movies);
		setSupportActionBar(toolbar);
        mOrderMode = SpUtils.getInt(this, Entry.ORDER_MODE, Entry.POPULAR_MOVIE_DIR);

		if (findViewById(R.id.movie_details_container) != null) {

			mTwoPane = true;
			if (savedInstanceState == null) {

				getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new MovieDetailsFragment(), DETAILS_FRAGMENT_TAG)
								.commit();
			}
		} else {

			mTwoPane = false;
		}

		SyncAdapter.initializeSyncAdapter(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// 添加选项条目
		getMenuInflater().inflate(R.menu.menu_main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		// noinspection SimplifiableIfStatement
		if (id == R.id.action_setting) {

			startActivity(new Intent(this, SettingActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {

		super.onResume();
		// 当排序模式发生变化时，调用方法刷新数据
        int orderMode = SpUtils.getInt(this, Entry.ORDER_MODE, Entry.POPULAR_MOVIE_DIR);
		if (orderMode != mOrderMode) {

			MoviesFragment moviesFragment = (MoviesFragment) getSupportFragmentManager()
							.findFragmentById(R.id.fragment_movies);
			if (moviesFragment != null) {

				moviesFragment.onOrderModeChanged();
			}
			mOrderMode = orderMode;
		}
	}

	@Override
	public void onItemSelected(Uri uri) {

		if (mTwoPane) {

			// 对于two-pane模式，通过MovieDetailsFragment展示详细信息
			Bundle bundle = new Bundle();
			bundle.putParcelable(MovieDetailsFragment.DETAIL_URI, uri);

			MovieDetailsFragment fragment = new MovieDetailsFragment();
			fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.movie_details_container, fragment, DETAILS_FRAGMENT_TAG).commit();
		} else {

			Intent intent = new Intent(this, MovieDetailsActivity.class).setData(uri);
			startActivity(intent);
		}
	}
}