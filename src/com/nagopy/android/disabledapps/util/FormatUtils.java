package com.nagopy.android.disabledapps.util;

import com.nagopy.android.common.app.BaseObject;
import com.nagopy.android.disabledapps.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FormatUtils extends BaseObject {

	private String formatWithComment;
	private String formatWithoutComment;

	public FormatUtils(Context context) {
		super(context);

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		formatWithComment = sp.getString(context.getString(R.string.pref_key_share_customformat_with_comment),
				context.getString(R.string.pref_def_share_customformat_with_comment));
		formatWithoutComment = sp.getString(
				context.getString(R.string.pref_key_share_customformat_without_comment),
				context.getString(R.string.pref_def_share_customformat_without_comment));
	}

	/**
	 * フォーマットがどんな感じになるかをテストするためのメソッド
	 * @param format
	 *           フォーマット前の文字列
	 * @return フォーマット結果。エラーがあった場合はnullを返す
	 */
	public String formatTest(String format) {
		try {
			Context context = getContext();
			return String.format(format, context.getString(R.string.app_name), context.getPackageName(),
					"your comment");
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
