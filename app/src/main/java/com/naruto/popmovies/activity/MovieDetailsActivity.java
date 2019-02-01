package com.naruto.popmovies.activity;

/**
 * @author jellybean
 * Created with Android Studio.
 * User: narutonbm@gmail.com
 * Date: 2017-02-09
 * Time: 16:49
 * Desc: UdaCity_PopularMovies
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.naruto.popmovies.R;
import com.naruto.popmovies.fragment.MovieDetailsFragment;

public class MovieDetailsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_details);
		Toolbar toolbar = findViewById(R.id.toolbar_details);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				onBackPressed();
			}
		});

		if (savedInstanceState == null) {

			Bundle bundle = new Bundle();
			bundle.putParcelable(MovieDetailsFragment.DETAIL_URI, getIntent().getData());

			MovieDetailsFragment fragment = new MovieDetailsFragment();
			fragment.setArguments(bundle);
			getSupportFragmentManager().beginTransaction().add(R.id.movie_details_container, fragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// 添加选项条目
		getMenuInflater().inflate(R.menu.menu_movie_details, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		// noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {

			startActivity(new Intent(this, SettingActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}