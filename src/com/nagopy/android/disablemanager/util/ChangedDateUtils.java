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

package com.nagopy.android.disablemanager.util;

import com.nagopy.android.disablemanager.core.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 無効化した・有効に戻したときの日時を管理するクラス
 */
public class ChangedDateUtils {

	/**
	 * アプリケーションのコンテキスト
	 */
	private Context mContext;

	/**
	 * 変更日時を保存しておくSP
	 */
	private SharedPreferences mSharedPreferences;

	/**
	 * コンストラクタ
	 * @param context
	 *           アプリケーションのコンテキスト
	 */
	public ChangedDateUtils(Context context) {
		mContext = context;
		mSharedPreferences = context.getSharedPreferences("date",
				Context.MODE_PRIVATE);
	}

	public Boolean isEnabled() {
		boolean boolean1 = PreferenceManager.getDefaultSharedPreferences(mContext)
				.getBoolean(
						mContext.getString(R.string.pref_key_general_sort_by_changed_date),
						mContext.getResources().getBoolean(
								R.bool.pref_def_general_sort_by_changed_date));
		return boolean1;
	}

	/**
	 * パッケージ名をキーに、変更時間を保存する
	 * @param packageName
	 *            パッケージ名
	 * @param time
	 *            時刻（ms）
	 * @return 保存が成功すればtrueを返す
	 */
	public boolean put(String packageName, long time) {
		return mSharedPreferences.edit().putLong(packageName, time).commit();
	}

	/**
	 * 変更時間を取得
	 * @param packageName
	 *            パッケージ名
	 * @return 最後に変更された時刻をミリ秒で返す。保存されていない場合は0を返す。
	 */
	public long get(String packageName) {
		return mSharedPreferences.getLong(packageName, 0);
	}

	/**
	 * 変更時刻を文字列で取得
	 * @param packageName
	 *            パッケージ名
	 * @return 変更時刻の文字列（年月日と時刻）。保存されていない場合はnullを返す
	 */
	public String getString(String packageName) {
		long ms = get(packageName);
		if (ms > 0) {
			return android.text.format.DateUtils.formatDateTime(mContext, ms,
					android.text.format.DateUtils.FORMAT_SHOW_YEAR
							| android.text.format.DateUtils.FORMAT_SHOW_DATE
							| android.text.format.DateUtils.FORMAT_SHOW_TIME);
		} else {
			return null;
		}
	}
}
