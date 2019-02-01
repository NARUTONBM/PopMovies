package com.naruto.popmovies.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.naruto.popmovies.R;
import com.naruto.popmovies.fragment.MoviesPreferenceFragment;

/**
 * Created by Android Studio. Date: 2017/10/31. Time: 下午10:17. Desc:
 * UdaCity_PopularMovies
 *
 * @author: jellybean.
 */
public class SettingActivity extends AppCompatActivity {

	private ListPreference mFrequencyListPreference;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preference);
		Toolbar toolbar = findViewById(R.id.toolbar_preference);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				onBackPressed();
			}
		});
		getFragmentManager().beginTransaction().replace(R.id.fragment_preference, new MoviesPreferenceFragment()).commit();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public Intent getParentActivityIntent() {

		return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	}
}
