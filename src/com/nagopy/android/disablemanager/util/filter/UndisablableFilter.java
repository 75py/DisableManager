package com.nagopy.android.disablemanager.util.filter;

import java.util.Set;

import com.nagopy.android.disablemanager.util.AppStatus;

/**
 * 無効化不可のシステムアプリを抽出するフィルター
 */
class UndisablableFilter extends AppsFilterCondition {

	private static final AppsFilterCondition instance = new UndisablableFilter();

	public static AppsFilterCondition getInstance(Set<String> hideSet) {
		instance.setHideSet(hideSet);
		return instance;
	}

	@Override
	public boolean valid(AppStatus appStatus) {
		return appStatus.isSystem() && !appStatus.canDisable()
				&& !getHideSet().contains(appStatus.getPackageName());
	}
}
