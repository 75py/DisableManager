package com.nagopy.android.disabledapps.util;

import java.lang.reflect.Method;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.nagopy.android.common.app.BaseObject;

/**
 * 「無効化ができるかを判定するためのクラス」の基になるクラス
 */
public abstract class JudgeDisablable extends BaseObject {

	/**
	 * デバイスポリシーマネージャー<br>
	 * 判定に使う
	 */
	private DevicePolicyManager mDevicePolicyManager;

	/**
	 * {@link DevicePolicyManager}のpackageHasActiveAdminsを実行するためのメソッド
	 */
	private Method methodPackageHasActiveAdmins;

	/**
	 * PackageManager
	 */
	private PackageManager mPackageManager;

	/**
	 * システムのPackageInfo
	 */
	private PackageInfo mSystemPackageInfo;

	/**
	 * そのアプリが無効化できるものかどうかを判定する
	 * @param applicationInfo
	 *           ApplicationInfoを渡す
	 * @return 無効化できるならtrue、そうでなければfalseを返す。
	 */
	public abstract boolean isDisablable(ApplicationInfo applicationInfo);

	/**
	 * コンストラクタ
	 * @param context
	 *           アプリケーションのコンストラクタ
	 */
	protected JudgeDisablable(Context context) {
		super(context);
		mPackageManager = context.getPackageManager();
		mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		try {
			methodPackageHasActiveAdmins = mDevicePolicyManager.getClass().getMethod("packageHasActiveAdmins",
					String.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			log("NoSuchMethodException");
		}

		try {
			mSystemPackageInfo = getPackageManager().getPackageInfo("android", PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@link DevicePolicyManager}のpackageHasActiveAdminsメソッドを実行する
	 * @param packageName
	 *           パッケージ名
	 * @return packageHasActiveAdminsの結果を返す。<br>
	 *         エラーがあった場合はfalseを返す。
	 */
	protected boolean packageHasActiveAdmins(String packageName) {
		try {
			return (Boolean) methodPackageHasActiveAdmins.invoke(mDevicePolicyManager, packageName);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * {@link DevicePolicyManager}のisThisASystemPackageメソッドと同じ内容
	 * @param packageInfo
	 *           判定したいpackageinfo
	 * @return isThisASystemPackageの結果をそのまま返す。<br>
	 *         エラーがあった場合はfalseを返す。
	 */
	protected boolean isThisASystemPackage(PackageInfo packageInfo) {
		return (packageInfo != null && packageInfo.signatures != null && mSystemPackageInfo != null && mSystemPackageInfo.signatures[0]
				.equals(packageInfo.signatures[0]));
	}

	/**
	 * 無効化できるかどうかを判定するクラスのオブジェクトを作る
	 * @param context
	 *           アプリケーションのコンテキスト
	 * @return 4.2以上、4.0.以上、それ未満で別々のオブジェクトを返す
	 */
	public static JudgeDisablable getInstance(Context context) {
		int version = Build.VERSION.SDK_INT;
		if (version >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			// 4.2以上
			return new JudgeDisablableJBmr1(context);
		} else if (version >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			// 4.0.4以上
			return new JudgeDisablableICS(context);
		} else {
			// 4.0.4未満の場合は、常にfalseを返すようにする
			return new JudgeDisablable(context) {
				@Override
				public boolean isDisablable(final ApplicationInfo applicationInfo) {
					return false;
				}
			};
		}
	}

	/**
	 * @return パッケージマネージャーを返す
	 */
	public PackageManager getPackageManager() {
		return mPackageManager;
	}
}
