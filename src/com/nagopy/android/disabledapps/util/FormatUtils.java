/*
 * Copyright 2013 75py
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

package com.nagopy.android.disabledapps.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nagopy.android.disabledapps.R;

/**
 * フォーマットをしたりするクラス
 */
public class FormatUtils {

	/**
	 * コメントありのときに使うフォーマット
	 */
	private String formatWithComment;

	/**
	 * コメントなしの時に使うフォーマット
	 */
	private String formatWithoutComment;

	/**
	 * 改行（一つまたは2つ）
	 */
	private String lineBreak;

	/**
	 * アプリケーションのコンテキスト
	 */
	private Context mContext;

	/**
	 * コンストラクタ。設定を読み込んだりもする
	 * @param context
	 *           アプリケーションのコンテキスト
	 */
	public FormatUtils(Context context) {
		mContext = context;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		formatWithComment = sp.getString(context.getString(R.string.pref_key_share_customformat_with_comment),
				context.getString(R.string.pref_def_share_customformat_with_comment));
		formatWithoutComment = sp.getString(
				context.getString(R.string.pref_key_share_customformat_without_comment),
				context.getString(R.string.pref_def_share_customformat_without_comment));
		if (sp.getBoolean(context.getString(R.string.pref_key_share_add_linebreak), context.getResources()
				.getBoolean(R.bool.pref_def_share_add_linebreak))) {
			lineBreak = System.getProperty("line.separator") + System.getProperty("line.separator");
		} else {
			lineBreak = System.getProperty("line.separator");
		}
	}

	/**
	 * フォーマットがどんな感じになるかをテストするためのメソッド
	 * @param format
	 *           フォーマット前の文字列
	 * @return フォーマット結果。エラーがあった場合はnullを返す
	 */
	public String formatTest(String format) {
		try {
			Context context = mContext;
			String text = String.format(format, context.getString(R.string.app_name), context.getPackageName(),
					"your comment");
			return text + lineBreak + text;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * フォーマットして返す
	 * @param label
	 *           ラベル名
	 * @param packageName
	 *           パッケージ名
	 * @param comment
	 *           コメント<br>
	 *           ない場合はnull
	 * @return フォーマットした文字列
	 */
	public String format(String label, String packageName, String comment) {
		if (comment == null || comment.length() < 1) {
			// コメントがない場合
			return String.format(formatWithoutComment, label, packageName, "");
		} else {
			// コメントがある場合
			return String.format(formatWithComment, label, packageName, comment);
		}
	}
}
