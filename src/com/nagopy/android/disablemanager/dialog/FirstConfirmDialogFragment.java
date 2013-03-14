package com.nagopy.android.disablemanager.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class FirstConfirmDialogFragment extends ConfirmDialogFragment {

	private static final String KEY_IS_FIRST = "com.nagopy.android.disablemanager.dialog.FirstConfirmDialogFragment.KEY_IS_FIRST";

	public static boolean isFirst(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getBoolean(KEY_IS_FIRST, true);
	}

	public static boolean setFlagOff(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_IS_FIRST, false)
				.commit();
	}
}
