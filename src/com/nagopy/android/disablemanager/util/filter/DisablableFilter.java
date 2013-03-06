package com.nagopy.android.disablemanager.util.filter;

import java.util.Set;

import com.nagopy.android.disablemanager.util.AppStatus;

/**
 * 無効化可能・まだ無効化していないアプリを抽出するフィルター
 */
class DisablableFilter extends AppsFilterCondition {

	/**
	 * インスタンス
	 */
	private static final AppsFilterCondition instance = new DisablableFilter();

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
		return appStatus.isSystem() && appStatus.canDisable() && appStatus.isEnabled()
				&& !getHideSet().contains(appStatus.getPackageName());
	}
}
