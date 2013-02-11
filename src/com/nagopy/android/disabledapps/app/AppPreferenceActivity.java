package com.nagopy.android.disabledapps.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
		// super.onBuildHeaders(target);
		loadHeadersFromResource(R.xml.app_preference_activity_header, target);
	}

	public static class SharePreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.app_preference_activity_body_share);
		}
	}

	public static class AboutPreferenceFragment extends PreferenceFragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.about_fragment, null);
			TextView versionTextView = (TextView) view.findViewById(R.id.about_fragment_textview_version_name);
			TextView aboutTextView = (TextView) view.findViewById(R.id.about_fragment_textview_about);

			PackageManager packageManager = getActivity().getPackageManager();
			try {
				PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(),
						PackageManager.GET_META_DATA);
				versionTextView.setText("Version: " + packageInfo.versionName);
				versionTextView.setVisibility(View.VISIBLE);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			InputStream inputStream = getActivity().getResources().openRawResource(R.raw.about);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuffer lines = new StringBuffer();
			try {
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					lines.append(line).append("\n");
				}
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			aboutTextView.setText(lines.toString());
			return view;
		}
	}
}
