package com.nagopy.android.disabledapps.util;

import java.lang.reflect.Method;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.nagopy.lib.base.BaseObject;

public abstract class JudgeDisablable extends BaseObject {

	private DevicePolicyManager mDevicePolicyManager;
	private Method method_packageHasActiveAdmins;

	protected PackageManager mPackageManager;

	protected PackageInfo mPackageInfo;

	/**
	 * そのアプリが無効化できるものかどうかを判定する
	 */
	public abstract boolean isDisablable(ApplicationInfo applicationInfo);

	protected JudgeDisablable(Context context) {
		super(context);
		mPackageManager = context.getPackageManager();
		mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		try {
			method_packageHasActiveAdmins = mDevicePolicyManager.getClass().getMethod("packageHasActiveAdmins",
					String.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			log("NoSuchMethodException");
		}
	}

	protected boolean packageHasActiveAdmins(String packageName) {
		try {
			return (Boolean) method_packageHasActiveAdmins.invoke(mDevicePolicyManager, packageName);
		} catch (Exception e) {
			log("Exception");
			return false;
		}
	}

	protected boolean isThisASystemPackage() {
		try {
			PackageInfo sys = mPackageManager.getPackageInfo("android", PackageManager.GET_SIGNATURES);

			return (mPackageInfo != null && mPackageInfo.signatures != null && sys.signatures[0]
					.equals(mPackageInfo.signatures[0]));
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 無効化できるかどうかを判定するクラスのオブジェクトを作る
	 * @param context
	 * @return 4.2以上、4.0.以上、それ未満で別々のオブジェクトを返す
	 */
	public static JudgeDisablable getInstance(Context context) {
		int version = Build.VERSION.SDK_INT;
		if (version >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			// 4.2以上
			return new JudgeDisablable_JB_MR1(context);
		} else if (version >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			// 4.0.4以上
			return new JudgeDisablable_ICS(context);
		} else {
			// 4.0.4未満の場合は、常にfalseを返すようにする
			return new JudgeDisablable(context) {
				@Override
				public boolean isDisablable(ApplicationInfo applicationInfo) {
					return false;
				}
			};
		}
	}
}
