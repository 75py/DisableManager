package com.nagopy.android.disabledapps.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.nagopy.lib.base.BaseObject;

/**
 * コメントを保存・復元するクラス
 */
public class CommentsUtils extends BaseObject {

	/**
	 * コメントを保存するSharedPreferencesのファイル名
	 */
	private static final String PREFERENCE_FILE_NAME = "comments";

	/**
	 * コメントを保存するSharedPreferences
	 */
	private SharedPreferences commentSharedPreferences;

	/**
	 * コンストラクタ
	 * @param context
	 *           アプリケーションのコンテキスト
	 */
	public CommentsUtils(Context context) {
		super(context);

		commentSharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
	}

	/**
	 * コメントを復元する
	 * @param packageName
	 *           パッケージ名（キー
	 * @return コメントがあれば文字列、なければnullを返す
	 */
	public String restoreComment(String packageName) {
		return commentSharedPreferences.getString(packageName, null);
	}

	/**
	 * コメントを保存する
	 * @param packageName
	 *           パッケージ名（キーに使う）
	 * @param comment
	 *           保存するコメント<br>
	 *           nullまたは文字数0の場合は削除する
	 * @return 保存が完了すればtrueを返す
	 */
	public boolean saveComment(String packageName, String comment) {
		if (comment == null || comment.length() == 0) {
			// コメントが空の場合は削除
			return commentSharedPreferences.edit().remove(packageName).commit();
		}

		return commentSharedPreferences.edit().putString(packageName, comment).commit();
	}

}
