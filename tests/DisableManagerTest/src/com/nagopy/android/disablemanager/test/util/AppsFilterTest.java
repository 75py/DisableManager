package com.nagopy.android.disablemanager.test.util;

import java.util.ArrayList;
import java.util.HashSet;

import android.test.AndroidTestCase;

import com.nagopy.android.disablemanager.util.AppStatus;
import com.nagopy.android.disablemanager.util.AppsFilter;

public class AppsFilterTest extends AndroidTestCase {

	private AppsFilter mAppsFilter;

	private ArrayList<AppStatus> testList;

	private AppStatus disabledAppStatus;

	private AppStatus disablableAppStatus;

	private AppStatus undisablableAppStatus;

	private AppStatus userAppStatus;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mAppsFilter = new AppsFilter();

		testList = new ArrayList<AppStatus>();
		disabledAppStatus = new AppStatus("0_disabled", "com.disabled", false, true, true);
		disablableAppStatus = new AppStatus("5_disablable", "com.disablable", true, true, true);
		undisablableAppStatus = new AppStatus("a_undisablable", "com.undisablable", true, true, false);
		userAppStatus = new AppStatus("x_user", "com.user", true, false, false);
		testList.add(undisablableAppStatus);
		testList.add(disablableAppStatus);
		testList.add(disabledAppStatus);
		testList.add(userAppStatus);

		mAppsFilter.setOriginalAppList(testList);
	}

	public void test無効化済みアプリの抽出() throws Exception {
		filter(AppsFilter.DISABLED, disabledAppStatus);
	}

	public void test無効化可能の抽出() throws Exception {
		filter(AppsFilter.DISABLABLE_AND_ENABLED_SYSTEM, disablableAppStatus);
	}

	public void test無効化不可アプリの抽出() throws Exception {
		filter(AppsFilter.UNDISABLABLE_SYSTEM, undisablableAppStatus);
	}

	public void testユーザーアプリの抽出() throws Exception {
		filter(AppsFilter.USER_APPS, userAppStatus);
	}

	@SuppressWarnings("unchecked")
	private void filter(int type, AppStatus appStatus) {
		mAppsFilter.setOriginalAppList((ArrayList<AppStatus>) testList.clone());
		ArrayList<AppStatus> result = mAppsFilter.execute(type, new HashSet<String>());
		assertEquals(1, result.size());
		assertEquals(appStatus, result.get(0));
	}

	public void testソート() throws Exception {
		ArrayList<AppStatus> sorted = mAppsFilter.sort(testList);
		assertEquals(disabledAppStatus, sorted.get(0));
		assertEquals(disablableAppStatus, sorted.get(1));
		assertEquals(undisablableAppStatus, sorted.get(2));
		assertEquals(userAppStatus, sorted.get(3));
	}
}
