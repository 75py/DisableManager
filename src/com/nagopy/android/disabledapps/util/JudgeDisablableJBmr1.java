package com.nagopy.android.disabledapps.util;

import java.util.List;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.UserManager;

/**
 * 無効化できるかを判定するクラス（4.2以上用）
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class JudgeDisablableJBmr1 extends JudgeDisablable {
	/**
	 * PackageInfo
	 */
	private PackageInfo mPackageInfo;

	/**
	 * コンストラクタ
	 * @param context
	 *           アプリケーションのコンテキスト
	 */
	public JudgeDisablableJBmr1(Context context) {
		super(context);
	}

	@Override
	public boolean isDisablable(ApplicationInfo applicationInfo) {
		if (!((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0)) {
			// システムアプリじゃない場合
			return false;
		}
		try {
			mPackageInfo = getPackageManager().getPackageInfo(
					applicationInfo.packageName,
					PackageManager.GET_DISABLED_COMPONENTS | PackageManager.GET_UNINSTALLED_PACKAGES
							| PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// パッケージ名からPackageInfoが取得できない場合
			return false;
		}

		return initUninstallButtons(applicationInfo.flags, applicationInfo.packageName);
	}

	/**
	 * 無効化ボタン（アンインストールボタン）が押せるかどうかを判定する
	 * @param flags
	 *           info.flags
	 * @param packageName
	 *           パッケージ名
	 * @return 無効化ボタンが押せるのであればtrue、そうでなければfalse
	 */
	private boolean initUninstallButtons(int flags, String packageName) {
		boolean enabled = true;
		UserManager mUserManager = (UserManager) getContext().getSystemService(Context.USER_SERVICE);
		if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
			enabled = handleDisableable(packageName);
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

	/**
	 * {@link DevicePolicyManager}のhandleDisableableメソッドもどき
	 * @param packageName
	 *           パッケージ名
	 * @return handleDisableableの戻り値と同じ
	 */
	private boolean handleDisableable(String packageName) {
		boolean disableable = true;
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setPackage(packageName);
		List<ResolveInfo> homes = getPackageManager().queryIntentActivities(intent, 0);
		if ((homes != null && homes.size() > 0) || isThisASystemPackage(mPackageInfo)) {
			disableable = false;
			// } else if (enabled) {
			// disableable = true;
			// } else {
			// disableable = true;
		}

		return disableable;
	}
}
