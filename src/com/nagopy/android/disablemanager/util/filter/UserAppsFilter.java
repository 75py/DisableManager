package com.nagopy.android.disablemanager.util.filter;

import java.util.Set;

import com.nagopy.android.disablemanager.util.AppStatus;

/**
 * ユーザーアプリを抽出するフィルター
 */
class UserAppsFilter extends AppsFilterCondition {

	private static final AppsFilterCondition instance = new UserAppsFilter();

	public static AppsFilterCondition getInstance(Set<String> hideSet) {
		instance.setHideSet(hideSet);
		return instance;
	}

	@Override
	public boolean valid(AppStatus appStatus) {
		return !appStatus.isSystem() && !getHideSet().contains(appStatus.getPackageName());
	}
}
