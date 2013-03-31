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

package com.nagopy.android.disablemanager.util.filter;

import java.util.ArrayList;
import java.util.Set;

import com.nagopy.android.disablemanager.util.AppStatus;
import com.nagopy.android.disablemanager.util.ChangedDateUtils;
import com.nagopy.android.disablemanager.util.sort.AppsSorter;

/**
 * アプリ一覧を、条件を指定してフィルタリングするクラス
 */
public class AppsFilter {

	/**
	 * 編集前のオリジナルを保持するリスト
	 */
	private ArrayList<AppStatus> originalAppList;

	/**
	 * 無効化済み
	 */
	public static final int DISABLED = 0x01;

	/**
	 * 無効化可能だがまだ有効なシステムアプリ
	 */
	public static final int DISABLABLE_AND_ENABLED_SYSTEM = 0x02;

	/**
	 * 無効化できないシステムアプリ
	 */
	public static final int UNDISABLABLE_SYSTEM = 0x04;

	/**
	 * ユーザーアプリ
	 */
	public static final int USER_APPS = 0x08;

	/**
	 * 非表示アプリ
	 */
	public static final int HIDE_APPS = 0x10;

	/**
	 * コンストラクタ<br>
	 * リストの初期化などを行う
	 */
	public AppsFilter() {
		originalAppList = new ArrayList<AppStatus>();
	}

	/**
	 * すべてのアプリを登録しておく<br>
	 * ソートする場合は {@link #sortByLabelAndPackageName(ArrayList)}
	 * @param original
	 *           登録するリスト
	 */
	public void setOriginalAppList(ArrayList<AppStatus> original) {
		this.originalAppList = original;
	}

	/**
	 * オリジナルをソートする
	 */
	public void sortOriginalAppList(ChangedDateUtils changedDateUtils) {
		// ひづけこうりょ
		AppsSorter.sort(changedDateUtils, originalAppList);
	}

	/**
	 * フィルターを実行して、結果を返す
	 * @param key
	 *           実行するフィルタのキー
	 * @param hides
	 *           除外アプリの一覧
	 * @return フィルター結果
	 */
	public ArrayList<AppStatus> execute(int key, final Set<String> hides) {
		AppsFilterCondition condition = createFilterCondition(key, hides);
		ArrayList<AppStatus> filtered = new ArrayList<AppStatus>();
		for (AppStatus appStatus : originalAppList) {
			if (condition.valid(appStatus)) {
				filtered.add(appStatus);
			}
		}
		return filtered;
	}

	/**
	 * フィルターを作成して返す
	 * @param key
	 *           フィルターのキー
	 * @param hides
	 *           非表示アプリ
	 * @return うまいことやったやつ
	 */
	private AppsFilterCondition createFilterCondition(int key, final Set<String> hides) {
		switch (key) {
		case DISABLED:
			return DisabledFilter.getInstance(hides);
		case DISABLABLE_AND_ENABLED_SYSTEM:
			return DisablableFilter.getInstance(hides);
		case UNDISABLABLE_SYSTEM:
			return UndisablableFilter.getInstance(hides);
		case USER_APPS:
			return UserAppsFilter.getInstance(hides);
		case HIDE_APPS:
			return HidedFilter.getInstance(hides);
		default:
			return null;
		}
	}

	/**
	 * 保存しているコレクションのクリア
	 */
	public void deallocate() {
		originalAppList = null;
	}
}
