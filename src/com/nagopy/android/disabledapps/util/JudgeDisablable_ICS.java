package com.nagopy.android.disabledapps.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

public class JudgeDisablable_ICS extends JudgeDisablable {

	public JudgeDisablable_ICS(Context context) {
		super(context);
	}

	@Override
	public boolean isDisablable(ApplicationInfo applicationInfo) {
		try {
			mPackageInfo = mPackageManager.getPackageInfo(applicationInfo.packageName,
					PackageManager.GET_DISABLED_COMPONENTS | PackageManager.GET_UNINSTALLED_PACKAGES
							| PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return initUninstallButtons(applicationInfo.flags, applicationInfo.packageName, applicationInfo.enabled);
	}

	private boolean initUninstallButtons(int flags, String packageName, boolean pkgEnabled) {
		boolean enabled = true;

		if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setPackage(packageName);
			List<ResolveInfo> homes = mPackageManager.queryIntentActivities(intent, 0);
			if ((homes != null && homes.size() > 0) || isThisASystemPackage()) {
				enabled = false;
			}
		}

		// If this is a device admin, it can't be uninstall or disabled.
		// We do this here so the text of the button is still set correctly.
		if (packageHasActiveAdmins(packageName)) {
			enabled = false;
		}

		return enabled;
	}
}
