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
	private AppsSorter() {}

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
