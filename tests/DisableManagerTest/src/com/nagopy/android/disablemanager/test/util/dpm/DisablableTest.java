package com.nagopy.android.disablemanager.test.util.dpm;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.AndroidTestCase;

import com.nagopy.android.disablemanager.util.dpm.Disablable;

public class DisablableTest extends AndroidTestCase {

	private Disablable mJudgeDisablable;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mJudgeDisablable = Disablable.getInstance(getContext());
	}

	public void testUserApp() {
		ApplicationInfo applicationInfo = getAppInfo(getContext().getPackageName());
		assertFalse("ユーザーアプリの判定", mJudgeDisablable.isDisablable(applicationInfo));
	}

	public void testDisablableApp() {
		ApplicationInfo applicationInfo = getAppInfo("com.google.android.email");
		if (applicationInfo == null) {
			applicationInfo = getAppInfo("com.android.email");
			if (applicationInfo == null) {
				fail("パッケージが見つからないお");
			}
		}
		assertTrue("無効化可能アプリの判定", mJudgeDisablable.isDisablable(applicationInfo));
	}

	public void testUndisablableApp() {
		ApplicationInfo applicationInfo = getAppInfo("android");
		assertFalse("無効化不可能なアプリの判定", mJudgeDisablable.isDisablable(applicationInfo));
	}

	/**
	 * @return
	 * @throws NameNotFoundException
	 */
	private ApplicationInfo getAppInfo(String packageName) {
		try {
			return getContext().getPackageManager()
					.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
