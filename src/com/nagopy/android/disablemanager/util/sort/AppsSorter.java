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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.nagopy.android.disablemanager.util.AppStatus;
import com.nagopy.android.disablemanager.util.ChangedDateUtils;

/**
 * アプリのリストをソートするクラス
 */
public final class AppsSorter {

	/**
	 * コンストラクタ
	 */
	private AppsSorter() {} // CHECKSTYLE IGNORE THIS LINE

	/**
	 * ラベルとパッケージ名でソートする
	 * @param list
	 *           ソートしたいリスト
	 */
	public static void sort(List<AppStatus> list) {
		Comparator<AppStatus> comparator = AppComparator.getInstance();
		Collections.sort(list, comparator);
	}

	/**
	 * 変更日時があればそれでソートし、それ以外はラベル・パッケージ名でソートする
	 * @param dateUtils
	 *           {@link ChangedDateUtils}
	 * @param list
	 *           ソートしたいリスト
	 */
	public static void sort(ChangedDateUtils dateUtils, List<AppStatus> list) {
		Comparator<AppStatus> comparator = AppComparatorWithDate.getInstance(dateUtils);
		Collections.sort(list, comparator);
	}

}
