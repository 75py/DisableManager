package com.nagopy.disabledapps.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

public class JudgeDisablable_ICS_MR1 extends JudgeDisablable {

	private static final boolean SUPPORT_DISABLE_APPS = true;
	private PackageManager mPm;
	private PackageInfo mPackageInfo;

	public JudgeDisablable_ICS_MR1(Context context) {
		super(context);
		mPm = context.getPackageManager();
	}

	@Override
	public boolean isDisablable(ApplicationInfo applicationInfo) {
		try {
			mPackageInfo = getContext().getPackageManager().getPackageInfo(
					applicationInfo.packageName,
					PackageManager.GET_DISABLED_COMPONENTS | PackageManager.GET_UNINSTALLED_PACKAGES
							| PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return initUninstallButtons(applicationInfo.flags, applicationInfo.packageName, applicationInfo.enabled);
	}

	private boolean initUninstallButtons(int flags, String packageName, boolean pkgEnabled) {
		// boolean mUpdatedSysApp = (flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
		boolean enabled = false;
		// if (mUpdatedSysApp) {
		// mUninstallButton.setText(R.string.app_factory_reset);
		// } else {
		if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
			enabled = false;
			if (SUPPORT_DISABLE_APPS) {
				try {
					// Try to prevent the user from bricking their phone
					// by not allowing disabling of apps signed with the
					// system cert and any launcher app in the system.
					PackageInfo sys = mPm.getPackageInfo("android", PackageManager.GET_SIGNATURES);
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					intent.setPackage(packageName);
					List<ResolveInfo> homes = mPm.queryIntentActivities(intent, 0);
					if ((homes != null && homes.size() > 0)
							|| (mPackageInfo != null && mPackageInfo.signatures != null && sys.signatures[0]
									.equals(mPackageInfo.signatures[0]))) {
						// Disable button for core system applications.
						// mUninstallButton.setText(R.string.disable_text);
						enabled = false;//
					} else if (pkgEnabled) {
						// mUninstallButton.setText(R.string.disable_text);
						enabled = true;
					} else {
						// mUninstallButton.setText(R.string.enable_text);
						enabled = true;
					}
				} catch (PackageManager.NameNotFoundException e) {
					// Log.w(TAG, "Unable to get package info", e);
					enabled = false;
				}
			}
		} else {
			// mUninstallButton.setText(R.string.uninstall_text);
			enabled = true;//
		}
		// }
		// If this is a device admin, it can't be uninstall or disabled.
		// We do this here so the text of the button is still set correctly.
		if (packageHasActiveAdmins(packageName)) {
			enabled = false;
		}
		// mUninstallButton.setEnabled(enabled);
		// if (enabled) {
		// Register listener
		// mUninstallButton.setOnClickListener(this);
		// }

		return enabled;
	}
}
