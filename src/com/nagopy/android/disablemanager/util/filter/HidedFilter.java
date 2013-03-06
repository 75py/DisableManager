package com.nagopy.android.disablemanager.util.filter;

import java.util.Set;

import com.nagopy.android.disablemanager.util.AppStatus;

/**
 * 除外アプリを抽出するフィルター
 */
class HidedFilter extends AppsFilterCondition {

	/**
	 * インスタンス
	 */
	private static final AppsFilterCondition instance = new HidedFilter();

	/**
	 * インスタンスの取得
	 * @param hideSet
	 *           除外アプリの一覧
	 * @return インスタンス
	 */
	public static AppsFilterCondition getInstance(Set<String> hideSet) {
		instance.setHideSet(hideSet);
		return instance;
	}

	@Override
	public boolean valid(AppStatus appStatus) {
		return getHideSet().contains(appStatus.getPackageName());
	}
}
