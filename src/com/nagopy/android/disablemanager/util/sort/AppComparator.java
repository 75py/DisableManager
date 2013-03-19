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

/**
 * ラベル・パッケージ名でソートする
 */
final class AppComparator implements Comparator<AppStatus> {

	/**
	 * インスタンス
	 */
	private static final AppComparator instance = new AppComparator();

	/**
	 * コンストラクタ
	 */
	private AppComparator() {} // CHECKSTYLE IGNORE THIS LINE

	/**
	 * インスタンスを取得する
	 * @return インスタンス
	 */
	public static Comparator<AppStatus> getInstance() {
		return instance;
	}

	@Override
	public int compare(final AppStatus obj0, final AppStatus obj1) {
		String label0 = ((AppStatus) obj0).getLabel();
		String label1 = ((AppStatus) obj1).getLabel();

		int ret = label0.compareToIgnoreCase(label1);
		// ラベルで並び替え、同じラベルがあったらパッケージ名で
		if (ret == 0) {
			String pkgName0 = ((AppStatus) obj0).getPackageName();
			String pkgName1 = ((AppStatus) obj1).getPackageName();
			ret = pkgName0.compareToIgnoreCase(pkgName1);
		}
		return ret;
	}
}
