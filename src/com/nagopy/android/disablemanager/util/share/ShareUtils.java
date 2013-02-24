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

package com.nagopy.android.disablemanager.util.share;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.nagopy.android.disablemanager.R;
import com.nagopy.android.disablemanager.util.AppStatus;

/**
 * 共有のテキストを作成したり、intentを発行するアクティビティ
 */
public class ShareUtils {

	/**
	 * アクティビティを保持
	 */
	private WeakReference<Activity> mWeakReference;

	/**
	 * コンストラクタ
	 * @param activity
	 *           アクティビティ
	 */
	public ShareUtils(Activity activity) {
		mWeakReference = new WeakReference<Activity>(activity);
	}

	/**
	 * @param appsList
	 *           アプリ一覧
	 * @return 一覧がnullまたは1つもない場合はtrueを返す
	 */
	public boolean isEmpty(ArrayList<AppStatus> appsList) {
		return appsList == null || appsList.isEmpty();
	}

	/**
	 * 共有するテキストを作成する
	 * @param type
	 *           R.id.menu_share_～
	 * @param appsList
	 *           アプリ一覧
	 * @return 共有する文字列
	 */
	public String createShareString(int type, ArrayList<AppStatus> appsList) {
		StringBuffer sb = new StringBuffer();
		String lineBreak;

		Activity activity = getActivity();
		if (activity == null) {
			return null;
		}
		Context context = activity.getApplicationContext();

		if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				context.getString(R.string.pref_key_share_add_linebreak),
				context.getResources().getBoolean(R.bool.pref_def_share_add_linebreak))) {
			lineBreak = System.getProperty("line.separator") + System.getProperty("line.separator");
		} else {
			lineBreak = System.getProperty("line.separator");
		}

		ShareTextMaker maker = getShareTextMaker(type);
		maker.make(appsList, sb, lineBreak);

		return sb.toString();
	}

	/**
	 * 文字列を共有する
	 * @param text
	 *           文字列
	 * @param title
	 *           タイトル
	 */
	public void sendIntent(String text, String title) {
		Activity activity = getActivity();
		if (activity == null) {
			return;
		}

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, text);
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		activity.startActivity(intent);
	}

	/**
	 * @param type
	 *           R.id.menu_share_～
	 * @return 適切なShareTextMakerクラス
	 */
	private ShareTextMaker getShareTextMaker(int type) {
		switch (type) {
		case R.id.menu_share_label:
			return new ShareLabel();
		case R.id.menu_share_package:
			return new SharePackage();
		case R.id.menu_share_label_and_package:
			return new ShareLabelAndPackage();
		case R.id.menu_share_customformat:
			Activity activity = getActivity();
			return new ShareCustom(activity.getApplicationContext());
		default:
			return new ShareLabel();
		}
	}

	/**
	 * @return activity
	 */
	private Activity getActivity() {
		return mWeakReference.get();
	}
}
