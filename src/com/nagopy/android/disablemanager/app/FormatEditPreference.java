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

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import com.nagopy.android.common.util.CommonUtil;
import com.nagopy.android.disablemanager.core.R;
import com.nagopy.android.disablemanager.dialog.FormatEditDialog;
import com.nagopy.android.disablemanager.dialog.FormatEditDialog.FormatEditDialogListener;

/**
 * フォーマットを編集するPreference
 */
public class FormatEditPreference extends Preference {

	public FormatEditPreference(Context context, AttributeSet attrs) { // CHECKSTYLE IGNORE THIS LINE
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
					.getDefaultSharedPreferences(activity).getString(getKey(), getDefault()),
					new FormatEditDialogListener() {
						@Override
						public void onPositiveButtonClicked(DialogInterface dialog, String text) {
							PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
									.putString(getKey(), text).commit();
						}

						// CHECKSTYLE:OFF
						@Override
						public void onNegativeButtonClicked(DialogInterface dialog) {}

						// CHECKSTYLE:ON

						@Override
						public void onFormatError() {
							CommonUtil commonUtil = new CommonUtil(getContext());
							commonUtil.showToast("format error");
						}
					});
			formatEditDialog.show(fragmentManager, "");
		}
	}

	/**
	 * デフォルト値を返す
	 * @return デフォルト
	 */
	private String getDefault() {
		String key = getKey();
		if (getContext().getString(R.string.pref_key_share_customformat_with_comment).equals(key)) {
			return getContext().getString(R.string.pref_def_share_customformat_with_comment);
		} else {
			return getContext().getString(R.string.pref_def_share_customformat_without_comment);
		}
	}

}
