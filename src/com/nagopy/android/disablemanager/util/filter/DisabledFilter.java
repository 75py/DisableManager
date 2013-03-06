package com.nagopy.android.disablemanager.util.filter;

import java.util.Set;

import com.nagopy.android.disablemanager.util.AppStatus;

/**
 * 無効化済みを抽出するフィルター
 */
class DisabledFilter extends AppsFilterCondition {

	private static final AppsFilterCondition instance = new DisabledFilter();

	public static AppsFilterCondition getInstance(Set<String> hideSet) {
		instance.setHideSet(hideSet);
		return instance;
	}

	@Override
	public boolean valid(AppStatus appStatus) {
		return !appStatus.isEnabled() && !getHideSet().contains(appStatus.getPackageName());
	}
}
