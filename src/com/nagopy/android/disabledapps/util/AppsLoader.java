/*
 * Copyright 2013 75py
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

package com.nagopy.android.disabledapps.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import com.nagopy.android.common.image.ImageUtils;
import com.nagopy.android.disabledapps.R;
import com.nagopy.android.disabledapps.util.dpm.Disablable;

/**
 * アプリ名などを読みこみ、アプリ一覧を取得するクラス。
 */
public class AppsLoader {
	/**
	 * アプリ一覧を保存しておくためのフィールド。
	 */
	private ArrayList<AppStatus> appsList;

	/**
	 * コンテキスト
	 */
	private Context mContext;

	/**
	 * コンストラクタ
	 * @param context
	 *           　アプリケーションのコンテキスト
	 */
	public AppsLoader(Context context) {
		mContext = context;
		appsList = new ArrayList<AppStatus>();
	}

	/**
	 * @return コンテキスト
	 */
	private Context getContext() {
		return mContext;
	}

	/**
	 * アプリを読みこむ
	 * @return アイコンのキャッシュ
	 */
	public HashMap<String, Drawable> load() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		if (sharedPreferences.getBoolean(
				getContext().getString(R.string.pref_key_general_show_only_running_packages), getContext()
						.getResources().getBoolean(R.bool.pref_def_general_show_only_running_packages))) {
			return loadRunningApps();
		} else {
			return loadAll();
		}
	}

	/**
	 * アプリ一覧を読み込む。<br>
	 * ちょっと時間かかるかも
	 * @return アイコンのキャッシュ
	 */
	private HashMap<String, Drawable> loadAll() {
		appsList.clear();

		PackageManager packageManager = getContext().getPackageManager();
		// インストール済みのアプリケーション一覧の取得
		List<ApplicationInfo> applicationInfo = packageManager
				.getInstalledApplications(PackageManager.GET_META_DATA);
		int iconSize = ImageUtils.getIconSize(getContext());

		Disablable judgeDisablable = Disablable.getInstance(getContext());
		HashMap<String, Drawable> iconCache = new HashMap<String, Drawable>();

		// 現在動いてるプロセスを読みこんで、パッケージ名とフラグをHashmapに入れる
		ActivityManager activityManager = (ActivityManager) getContext().getSystemService(
				Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = activityManager
				.getRunningAppProcesses();
		HashMap<String, Integer> runningPackages = new HashMap<String, Integer>();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
			String[] pkgList = runningAppProcessInfo.pkgList;
			for (String pkg : pkgList) {
				runningPackages.put(pkg, runningAppProcessInfo.importance);
			}
		}

		for (ApplicationInfo info : applicationInfo) {
			Drawable icon = info.loadIcon(packageManager);
			icon.setBounds(0, 0, iconSize, iconSize);

			AppStatus appStatus = new AppStatus(info.loadLabel(packageManager).toString(), info.packageName,
					info.enabled, (info.flags & ApplicationInfo.FLAG_SYSTEM) > 0,
					judgeDisablable.isDisablable(info));

			// 動いている場合はその情報も追加する
			if (runningPackages.containsKey(info.packageName)) {
				appStatus.setRunningStatus(runningPackages.get(info.packageName));
			}

			appsList.add(appStatus);
			iconCache.put(info.packageName, icon);
		}

		return iconCache;
	}

	/**
	 * アプリ一覧を読み込む。<br>
	 * ちょっと時間かかるかも
	 * @return アイコンのキャッシュ
	 */
	private HashMap<String, Drawable> loadRunningApps() {
		appsList.clear();

		ActivityManager activityManager = (ActivityManager) getContext().getSystemService(
				Context.ACTIVITY_SERVICE);
		PackageManager packageManager = getContext().getPackageManager();
		List<ActivityManager.RunningAppProcessInfo> applicationInfo = activityManager.getRunningAppProcesses();
		int iconSize = ImageUtils.getIconSize(getContext());

		Disablable judgeDisablable = Disablable.getInstance(getContext());
		HashMap<String, Drawable> iconCache = new HashMap<String, Drawable>();

		for (RunningAppProcessInfo info : applicationInfo) {
			String[] pkgList = info.pkgList;
			for (String pkg : pkgList) {
				ApplicationInfo appInfo;
				try {
					appInfo = packageManager.getApplicationInfo(pkg, PackageManager.GET_META_DATA);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
					continue;
				}
				Drawable icon = appInfo.loadIcon(packageManager);
				icon.setBounds(0, 0, iconSize, iconSize);

				AppStatus appStatus = new AppStatus(appInfo.loadLabel(packageManager).toString(),
						info.processName, appInfo.enabled, (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0,
						judgeDisablable.isDisablable(appInfo));
				appStatus.setRunningStatus(info.importance);

				appsList.add(appStatus);
				iconCache.put(info.processName, icon);
			}

		}
		return iconCache;
	}

	/**
	 * アプリ一覧（未編集）を取得する。
	 * @return アプリ情報一覧のリストを返す
	 */
	public ArrayList<AppStatus> getAppsList() {
		return appsList;
	}

	/**
	 * ペッケージ名を指定してステータスを更新する
	 * @param packageName
	 *           パッケージ名
	 */
	public void updateStatus(String packageName) {
		AppStatus appStatus = null;
		for (AppStatus status : appsList) {
			if (status.getPackageName().equals(packageName)) {
				appStatus = status;
				break;
			}
		}
		if (appStatus == null) {
			return;
		}

		try {
			appsList.remove(appStatus);
			PackageManager packageManager = getContext().getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
			ApplicationInfo info = packageInfo.applicationInfo;
			Disablable judgeDisablable = Disablable.getInstance(getContext());

			AppStatus newStatus = new AppStatus(info.loadLabel(packageManager).toString(), info.packageName,
					info.enabled, (info.flags & ApplicationInfo.FLAG_SYSTEM) > 0,
					judgeDisablable.isDisablable(info));
			appsList.add(newStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * リストをnullに
	 */
	public void deallocate() {
		appsList = null;
	}
}
