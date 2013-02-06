package com.nagopy.android.disabledapps.app;

import android.os.Bundle;

import com.nagopy.android.common.app.BasePreferenceActivity;
import com.nagopy.android.disabledapps.R;

/**
 * 設定画面を表示するアクティビティ
 */
public class AppPreferenceActivity extends BasePreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.app_preference_activity);
	}
}
