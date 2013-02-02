package com.nagopy.android.disabledapps.util;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.UserManager;
import android.widget.Button;

@TargetApi(17)
public class JudgeDisablable_JB_MR1 extends JudgeDisablable {

	public JudgeDisablable_JB_MR1(Context context) {
		super(context);
	}

	public boolean isDisablable(ApplicationInfo applicationInfo) {
		if (!((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0)) {
			// システムアプリじゃない場合
			return false;
		}
		try {
			mPackageInfo = mPackageManager.getPackageInfo(applicationInfo.packageName,
					PackageManager.GET_DISABLED_COMPONENTS | PackageManager.GET_UNINSTALLED_PACKAGES
							| PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// パッケージ名からPackageInfoが取得できない場合
			return false;
		}

		return initUninstallButtons(applicationInfo.flags, applicationInfo.packageName, applicationInfo.enabled);
	}

	private boolean initUninstallButtons(int flags, String packageName, boolean pkgENABLED) {
		boolean enabled = true;
		UserManager mUserManager = (UserManager) getContext().getSystemService(Context.USER_SERVICE);
		if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
			enabled = handleDisableable(null, packageName, pkgENABLED);
		} else if ((mPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_INSTALLED) == 0
				&& mUserManager.getUserCount() >= 2) {
			enabled = false;
		}

		// If this is a device admin, it can't be uninstall or disabled.
		// We do this here so the text of the button is still set correctly.
		if (packageHasActiveAdmins(mPackageInfo.packageName)) {
			enabled = false;
		}

		return enabled;
	}

	private boolean handleDisableable(Button button, String packageName, boolean enabled) {
		boolean disableable = true;
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setPackage(packageName);
		List<ResolveInfo> homes = mPackageManager.queryIntentActivities(intent, 0);
		if ((homes != null && homes.size() > 0) || isThisASystemPackage()) {
			disableable = false;
			// } else if (enabled) {
			// disableable = true;
			// } else {
			// disableable = true;
		}

		return disableable;
	}
}
