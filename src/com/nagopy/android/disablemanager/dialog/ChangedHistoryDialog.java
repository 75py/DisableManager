package com.nagopy.android.disablemanager.dialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.nagopy.android.disablemanager.R;

/**
 * 変更履歴を表示するダイアログ
 */
public class ChangedHistoryDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.history_title);

		InputStream inputStream = getActivity().getResources().openRawResource(R.raw.history);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder lines = new StringBuilder();
		try {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				lines.append(line).append("\n");
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		builder.setMessage(lines);
		builder.setPositiveButton(android.R.string.ok, null);

		return builder.create();
	}

	/**
	 * 更新されたかどうかをチェックする
	 * @param activity
	 *           アクティビティ
	 * @return 前回起動時よりversionCodeが上がっていればtrueを返す
	 */
	public static boolean isUpdated(Activity activity) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity
				.getApplicationContext());
		PackageInfo packageInfo = null;
		try {
			packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(),
					PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		int savedVer = sharedPreferences.getInt("ver", 0);
		if (savedVer < packageInfo.versionCode) {
			sharedPreferences.edit().putInt("ver", packageInfo.versionCode).apply();
			return true;
		} else {
			return false;
		}
	}
}
