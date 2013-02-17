package com.nagopy.android.disabledapps.app;

import java.util.List;

import android.os.Bundle;

import com.nagopy.android.common.app.BasePreferenceActivity;
import com.nagopy.android.disabledapps.R;

/**
 * 設定画面を表示するアクティビティ
 */
public class AppPreferenceActivity extends BasePreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.app_preference_activity_header, target);
	}

}
