package com.nagopy.android.disabledapps.pref;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.nagopy.android.disabledapps.R;

public class SharePreferenceFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.app_preference_activity_body_share);
	}
}
