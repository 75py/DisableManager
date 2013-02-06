package com.nagopy.android.disabledapps.app;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import com.nagopy.android.common.util.CommonUtil;
import com.nagopy.android.disabledapps.util.dialog.FormatEditDialog;
import com.nagopy.android.disabledapps.util.dialog.FormatEditDialog.FormatEditDialogListener;

/**
 * フォーマットを編集するPreference
 */
public class FormatEditPreference extends Preference {

	public FormatEditPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressWarnings("serial")
	@Override
	protected void onClick() {
		if (getContext() instanceof Activity) {
			Activity activity = (Activity) getContext();
			FragmentManager fragmentManager = activity.getFragmentManager();

			FormatEditDialog formatEditDialog = new FormatEditDialog();
			formatEditDialog.init(getTitle().toString(), getSummary().toString(), PreferenceManager
					.getDefaultSharedPreferences(activity).getString(getKey(), null),
					new FormatEditDialogListener() {
						@Override
						public void onPositiveButtonClicked(DialogInterface dialog, String text) {
							PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
									.putString(getKey(), text).commit();
						}

						@Override
						public void onNegativeButtonClicked(DialogInterface dialog) {}

						@Override
						public void onFormatError() {
							CommonUtil commonUtil = new CommonUtil(getContext());
							commonUtil.showToast("format error");
						}
					});
			formatEditDialog.show(fragmentManager, "");
		}
	}

}
