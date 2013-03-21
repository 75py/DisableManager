/*
 * Copyright (C) 2013 75py
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nagopy.android.disablemanager.app;

import java.util.HashSet;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;

import com.nagopy.android.common.app.BasePreferenceActivity;
import com.nagopy.android.disablemanager.R;

/**
 * 設定画面を表示するアクティビティ
 */
public class AppPreferenceActivity extends BasePreferenceActivity {

	/**
	 * 読みこみ直すかどうかを保存しておくキー
	 */
	public static final String KEY_RELOAD_FLAG = "com.nagopy.android.disablemanager.app.AppPreferenceActivity.KEY_RELOAD_FLAG";

	/**
	 * 読み込み直すべきキーのセット
	 */
	private HashSet<String> keySet;

	/**
	 * デフォのSP
	 */
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSP();
		keySet = new HashSet<String>();
		add(R.string.pref_key_general_show_changed_date, R.string.pref_key_general_show_only_running_packages,
				R.string.pref_key_general_sort_by_changed_date);
	}

	/**
	 * @param resIds
	 *           追加するresId
	 */
	private void add(int... resIds) {
		for (int resId : resIds) {
			keySet.add(getString(resId));
		}
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.app_preference_activity_header, target);
	}

	/**
	 * リスナー
	 */
	private OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (keySet.contains(key)) {
				sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
				sharedPreferences.edit().putBoolean(KEY_RELOAD_FLAG, true).apply();
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		if (!sp.getBoolean(KEY_RELOAD_FLAG, false)) {
			getSP().registerOnSharedPreferenceChangeListener(listener);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!sp.getBoolean(KEY_RELOAD_FLAG, false)) {
			getSP().unregisterOnSharedPreferenceChangeListener(listener);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		sp = null;
		keySet = null;
	}
}
