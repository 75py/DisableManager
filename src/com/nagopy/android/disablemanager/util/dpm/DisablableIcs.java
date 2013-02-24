/*
 * Copyright (C) 2013 75py
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * このクラスは、Androidのソースコードの一部を利用しています。
 *  android.app.admin.DevicePolicyManager
 *  com.android.settings.applications.InstalledAppDetails
 */

/*
 * DevicePolicyManagerのライセンスは以下の通りです。
 */

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * InstalledAppDetailsのライセンスは以下の通りです。
 */

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.nagopy.android.disablemanager.util.dpm;

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
class DisablableIcs extends Disablable {
	/**
	 * PackageInfo
	 */
	private PackageInfo mPackageInfo;

	/**
	 * コンストラクタ
	 * @param context
	 *           アプリケーションのコンストラクタ
	 */
	public DisablableIcs(Context context) {
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
