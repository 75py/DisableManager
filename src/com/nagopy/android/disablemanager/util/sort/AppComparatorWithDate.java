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

package com.nagopy.android.disablemanager.util.sort;

import java.util.Comparator;

import com.nagopy.android.disablemanager.util.AppStatus;
import com.nagopy.android.disablemanager.util.ChangedDateUtils;

/**
 * 変更された日付でソート、それ以外はラベル・パッケージ名でソートする
 */
final class AppComparatorWithDate implements Comparator<AppStatus> {

	/**
	 * インスタンス
	 */
	private static final AppComparatorWithDate instance = new AppComparatorWithDate();

	/**
	 * @see ChangedDateUtils
	 */
	private ChangedDateUtils mChangedDateUtils;

	/**
	 * コンストラクタ
	 */
	private AppComparatorWithDate() {} // CHECKSTYLE IGNORE THIS LINE

	/**
	 * インスタンスを取得する
	 * @param dateUtils
	 *           {@link ChangedDateUtils}
	 * @return インスタンス
	 */
	public static Comparator<AppStatus> getInstance(ChangedDateUtils dateUtils) {
		instance.mChangedDateUtils = dateUtils;
		return instance;
	}

	@Override
	public int compare(final AppStatus obj0, final AppStatus obj1) {
		String pkgName0 = obj0.getPackageName();
		String pkgName1 = obj1.getPackageName();

		long date0 = mChangedDateUtils.get(pkgName0);
		long date1 = mChangedDateUtils.get(pkgName1);
		if (date0 != 0 || date1 != 0) {
			return date0 > date1 ? -1 : 1;
		}

		String label0 = obj0.getLabel();
		String label1 = obj1.getLabel();

		int ret = label0.compareToIgnoreCase(label1);
		// ラベルで並び替え、同じラベルがあったらパッケージ名で
		if (ret == 0) {
			ret = pkgName0.compareToIgnoreCase(pkgName1);
		}
		return ret;
	}
}
