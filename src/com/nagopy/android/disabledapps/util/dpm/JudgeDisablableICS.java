package com.nagopy.android.disabledapps.util.dpm;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

/**
 * 無効化できるかを判定するクラス（ICS～JB用）
 */
class JudgeDisablableICS extends JudgeDisablable {
	/**
	 * PackageInfo
	 */
	private PackageInfo mPackageInfo;

	/**
	 * コンストラクタ
	 * @param context
	 *           アプリケーションのコンストラクタ
	 */
	public JudgeDisablableICS(Context context) {
		super(context);
	}

	@Override
	public boolean isDisablable(ApplicationInfo applicationInfo) {
		try {
			mPackageInfo = getPackageManager().getPackageInfo(
					applicationInfo.packageName,
					PackageManager.GET_DISABLED_COMPONENTS | PackageManager.GET_UNINSTALLED_PACKAGES
							| PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return initUninstallButtons(applicationInfo.flags, applicationInfo.packageName);
	}

	/**
	 * {@link DevicePolicyManager}のinitUninstallButtonsメソッドもどき
	 * @param flags
	 *           info.flags
	 * @param packageName
	 *           パッケージ名
	 * @return 無効化ボタンが押せる状態ならtrueを返す
	 */
	private boolean initUninstallButtons(int flags, String packageName) {
		boolean enabled = true;

		if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setPackage(packageName);
			List<ResolveInfo> homes = getPackageManager().queryIntentActivities(intent, 0);
			if ((homes != null && homes.size() > 0) || isThisASystemPackage(mPackageInfo)) {
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
