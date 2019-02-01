package com.naruto.popmovies.fragment;

import com.naruto.popmovies.R;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by Android Studio. Date: 2017/11/15. Time: 上午1:12. Desc:
 * UdaCity_PopularMovies
 *
 * @author: jellybean.
 */
public class MoviesPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

	private ListPreference mFrequencyListPreference;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// 加载preference资源文件
		addPreferencesFromResource(R.xml.pref_general);
		initUI();
	}

	private void initUI() {

		mFrequencyListPreference = (ListPreference) findPreference(getString(R.string.key_sync_frequency));
		mFrequencyListPreference.setOnPreferenceChangeListener(this);
		// 设置summary为所选中的值列表值
		if (mFrequencyListPreference.getEntry() != null) {

			// 初始化时设置summary
			mFrequencyListPreference.setSummary(mFrequencyListPreference.getEntry());
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {

		String stringValue = newValue.toString();

		if (preference instanceof ListPreference) {

			// 把这个preference强制转换成ListPreference类型
			ListPreference listPreference = (ListPreference) preference;
			// 获取ListPreference中的实体内容
			CharSequence[] entries = listPreference.getEntries();
			// 获取ListPreference中的实体内容的下标值
			int prefIndex = listPreference.findIndexOfValue(stringValue);
			// 设置summary为当前ListPreference的实体内容中选择的
			if (prefIndex >= 0) {

				preference.setSummary(entries[prefIndex]);
				Toast.makeText(getActivity(), R.string.toast_sync_frequency_already_updated, Toast.LENGTH_SHORT).show();
			}
		} else {

			preference.setSummary(stringValue);
		}

		return true;
	}
}