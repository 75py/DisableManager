package com.nagopy.disabledapps.util;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.UserManager;
import android.util.Log;
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
		return initUninstallButtons(applicationInfo.flags, applicationInfo.packageName, applicationInfo.enabled);
	}

	@Override
	public void log(Object object) {
		Log.d("debug", object.toString());
	}

	private boolean initUninstallButtons(int flags, String packageName, boolean pkgENABLED) {
		// boolean mUpdatedSysApp = (flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
		boolean enabled = true;
		// Object mUninstallButton;
		PackageInfo mPackageInfo = null;
		try {
			mPackageInfo = getContext().getPackageManager().getPackageInfo(
					packageName,
					PackageManager.GET_DISABLED_COMPONENTS | PackageManager.GET_UNINSTALLED_PACKAGES
							| PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		UserManager mUserManager = (UserManager) getContext().getSystemService(Context.USER_SERVICE);
		// if (mUpdatedSysApp) {
		// mUninstallButton.setText(R.string.app_factory_reset);
		// boolean specialDisable = false;
		// if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
		// specialDisable = handleDisableable(mSpecialDisableButton);
		// mSpecialDisableButton.setOnClickListener(this);
		// }
		// mMoreControlButtons.setVisibility(specialDisable ? View.VISIBLE : View.GONE);
		// } else {
		// mMoreControlButtons.setVisibility(View.GONE);
		if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
			enabled = handleDisableable(null, packageName, pkgENABLED);
		} else if ((mPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_INSTALLED) == 0
				&& mUserManager.getUserCount() >= 2) {
			// When we have multiple users, there is a separate menu
			// to uninstall for all users.
			// mUninstallButton.setText(R.string.uninstall_text);
			enabled = false;
		} else {
			// mUninstallButton.setText(R.string.uninstall_text);
		}
		// }
		// If this is a device admin, it can't be uninstall or disabled.
		// We do this here so the text of the button is still set correctly.
		if (packageHasActiveAdmins(mPackageInfo.packageName)) {
			enabled = false;
		}
		// mUninstallButton.setEnabled(enabled);
		if (enabled) {
			// Register listener
			// mUninstallButton.setOnClickListener(this);
		}
		return enabled;
	}

	private boolean handleDisableable(Button button, String packageName, boolean enabled) {
		boolean disableable = false;
//		try {
			// Try to prevent the user from bricking their phone
			// by not allowing disabling of apps signed with the
			// system cert and any launcher app in the system.
			// PackageInfo sys = getContext().getPackageManager().getPackageInfo("android",
			// PackageManager.GET_SIGNATURES);
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setPackage(packageName);
			List<ResolveInfo> homes = getContext().getPackageManager().queryIntentActivities(intent, 0);
			if ((homes != null && homes.size() > 0) || isThisASystemPackage(packageName)) {
				// Disable button for core system applications.
				// button.setText(R.string.disable_text);
			} else if (enabled) {
				// button.setText(R.string.disable_text);
				disableable = true;
			} else {
				// button.setText(R.string.enable_text);
				disableable = true;
			}
		// } catch (PackageManager.NameNotFoundException e) {
		// log("Unable to get package info");
		// }
		return disableable;
	}

	private boolean isThisASystemPackage(String packageName) {
		try {
			PackageInfo sys = getContext().getPackageManager().getPackageInfo("android",
					PackageManager.GET_SIGNATURES);
			PackageInfo mPackageInfo = null;
			try {
				mPackageInfo = getContext().getPackageManager().getPackageInfo(
						packageName,
						PackageManager.GET_DISABLED_COMPONENTS | PackageManager.GET_UNINSTALLED_PACKAGES
								| PackageManager.GET_SIGNATURES);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return (mPackageInfo != null && mPackageInfo.signatures != null && sys.signatures[0]
					.equals(mPackageInfo.signatures[0]));
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}
}
