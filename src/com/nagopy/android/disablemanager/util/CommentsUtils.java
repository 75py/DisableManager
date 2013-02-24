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

import android.content.Context;
import android.content.SharedPreferences;

/**
 * コメントを保存・復元するクラス
 */
public class CommentsUtils {

	/**
	 * アプリケーションのコンテキスト
	 */
	@SuppressWarnings("unused")
	private Context mContext;

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
		mContext = context;
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
