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

package com.nagopy.android.disablemanager.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
import android.util.Log;

import com.nagopy.android.common.image.ImageUtils;
import com.nagopy.android.disablemanager.R;
import com.nagopy.android.disablemanager.util.dpm.Disablable;

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
		RunningProcessStatusMap runnings = getRunningProcesses();

		for (ApplicationInfo info : applicationInfo) {
			AppStatus appStatus = new AppStatus(info.loadLabel(packageManager).toString(), info.packageName,
					info.enabled, (info.flags & ApplicationInfo.FLAG_SYSTEM) > 0,
					judgeDisablable.isDisablable(info));

			// 動いている場合はその情報も追加する
			setProcessStrings(appStatus, runnings);

			appsList.add(appStatus);

			// アイコン読み込み
			Drawable icon = null;
			try {
				icon = info.loadIcon(packageManager);
				icon.setBounds(0, 0, iconSize, iconSize);
			} catch (OutOfMemoryError e) {
				Log.d(getContext().getPackageName(), "OutOfMemoryError: loadIcon, " + info.packageName);
			}
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

		int iconSize = ImageUtils.getIconSize(getContext());
		Disablable judgeDisablable = Disablable.getInstance(getContext());
		HashMap<String, Drawable> iconCache = new HashMap<String, Drawable>();
		PackageManager packageManager = getContext().getPackageManager();

		RunningProcessStatusMap runningProcessStatusMap = getRunningProcesses();
		Set<String> runningPackages = runningProcessStatusMap.keySet();
		for (String packageName : runningPackages) {
			ApplicationInfo appInfo;
			try {
				appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				continue;
			}
			// Drawable icon = appInfo.loadIcon(packageManager);
			// icon.setBounds(0, 0, iconSize, iconSize);

			AppStatus appStatus = new AppStatus(appInfo.loadLabel(packageManager).toString(), packageName,
					appInfo.enabled, (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0,
					judgeDisablable.isDisablable(appInfo));
			setProcessStrings(appStatus, runningProcessStatusMap);

			appsList.add(appStatus);

			// アイコン読み込み
			Drawable icon = null;
			try {
				icon = appInfo.loadIcon(packageManager);
				icon.setBounds(0, 0, iconSize, iconSize);
			} catch (OutOfMemoryError e) {
				Log.d(getContext().getPackageName(), "OutOfMemoryError: loadIcon, " + packageName);
			}
			iconCache.put(packageName, icon);
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
	 * パッケージ名を指定してステータスを更新する
	 * @param packageName
	 *           パッケージ名
	 * @return パッケージの有効・無効が切り替わった場合はtrueを返す
	 */
	public boolean updateStatus(String packageName) {
		AppStatus appStatus = null;
		for (AppStatus status : appsList) {
			if (status.getPackageName().equals(packageName)) {
				appStatus = status;
				break;
			}
		}
		if (appStatus == null) {
			return false;
		}

		try {
			PackageManager packageManager = getContext().getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
			ApplicationInfo info = packageInfo.applicationInfo;
			boolean update = appStatus.isEnabled() != info.enabled;
			appStatus.setEnabled(info.enabled);
			return update;
		} catch (PackageManager.NameNotFoundException e) {
			appsList.remove(appStatus);
			return false;
		}
	}

	/**
	 * リストをnullに
	 */
	public void deallocate() {
		appsList = null;
	}

	/**
	 * アプリ一覧をセットする（インポート時に使用）
	 * @param list
	 *           AppStatusの一覧
	 */
	public void setAppsList(ArrayList<AppStatus> list) {
		appsList = list;
	}

	/**
	 * 実行中のアプリのパッケージ名・プロセス名とステータスのマップを返す
	 * @return @see {@link RunningProcessStatusMap}
	 */
	private RunningProcessStatusMap getRunningProcesses() {
		ActivityManager activityManager = (ActivityManager) getContext().getSystemService(
				Context.ACTIVITY_SERVICE);

		List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = activityManager
				.getRunningAppProcesses();

		RunningProcessStatusMap runningProcessStatusMap = new RunningProcessStatusMap();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
			String[] pkgList = runningAppProcessInfo.pkgList;
			for (String pkg : pkgList) {
				runningProcessStatusMap.add(pkg, runningAppProcessInfo.processName,
						runningAppProcessInfo.importance);
			}
		}

		return runningProcessStatusMap;
	}

	/**
	 * ステータスのintをもとに文字列に変換
	 * @param status
	 *           {@link AppStatus#getRunningStatus()}
	 * @return 文字列（Foregroundとか）
	 */
	private String getStatusText(int status) {
		String packageStatusText;
		switch (status) {
		case RunningAppProcessInfo.IMPORTANCE_BACKGROUND:
			packageStatusText = "Background";
			break;
		case RunningAppProcessInfo.IMPORTANCE_FOREGROUND:
			packageStatusText = "Foreground";
			break;
		case RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE:
			packageStatusText = "Perceptible";
			break;
		case RunningAppProcessInfo.IMPORTANCE_SERVICE:
			packageStatusText = "Service";
			break;
		case RunningAppProcessInfo.IMPORTANCE_VISIBLE:
			packageStatusText = "Visible";
			break;
		case RunningAppProcessInfo.IMPORTANCE_EMPTY:
			packageStatusText = "Empty";
			break;
		default:
			packageStatusText = null;
			break;
		}
		return packageStatusText;
	}

	/**
	 * プロセス情報をセットする
	 * @param appStatus
	 *           アプリ
	 * @param runningProcessStatusMap
	 *           {@link RunningProcessStatusMap}
	 * 
	 */
	private void setProcessStrings(AppStatus appStatus, RunningProcessStatusMap runningProcessStatusMap) {
		if (runningProcessStatusMap.containsKey(appStatus.getPackageName())) {
			String processNameValue = null;
			HashMap<String, Integer> statusMap = runningProcessStatusMap.getProcessStatusMap(appStatus
					.getPackageName());
			Set<String> processNamesSet = statusMap.keySet();

			if (processNamesSet.size() == 1) {
				for (String processName : processNamesSet) {
					if (processName.equals(appStatus.getPackageName())) {
						int status = statusMap.get(processName);
						processNameValue = "[" + getStatusText(status) + "]";
					} else {
						StringBuilder sb = new StringBuilder(processName);
						int status = statusMap.get(processName);
						sb.append(" [");
						sb.append(getStatusText(status));
						sb.append("]");
						processNameValue = sb.toString();
					}
				}
			} else {
				StringBuilder sb = new StringBuilder();
				for (String processName : processNamesSet) {
					int status = statusMap.get(processName);
					if (sb.length() > 0) {
						sb.append("\n");
					}
					sb.append(processName);
					sb.append(" [");
					sb.append(getStatusText(status));
					sb.append("]");
				}
				processNameValue = sb.toString();
			}

			appStatus.setProcessStrings(processNameValue);
		}
	}

	/**
	 * HashMap<String, HashMap<String, Integer>><br>
	 * キーは実行中のパッケージ名<br>
	 * 値はプロセス名とステータスのMap
	 */
	private static class RunningProcessStatusMap {
		/**
		 * 実行中のプロセスを持つパッケージ名をキーに、プロセス名とステータスのMapを値として持つマップ
		 */
		private HashMap<String, HashMap<String, Integer>> map;

		/**
		 * コンストラクタ
		 */
		public RunningProcessStatusMap() {
			map = new HashMap<String, HashMap<String, Integer>>();
		}

		/**
		 * @param packageName
		 *           パッケージ名
		 * @return 実行中に含まれていればtrueを返す
		 */
		public boolean containsKey(String packageName) {
			return map.containsKey(packageName);
		}

		/**
		 * 実行中のパッケージ名一覧を取得
		 * @return 実行中アプリのパッケージ名
		 */
		public Set<String> keySet() {
			return map.keySet();
		}

		/**
		 * 追加する
		 * @param packageName
		 *           パッケージ名
		 * @param processName
		 *           プロセス名
		 * @param status
		 *           プロセスのステータス
		 */
		public void add(String packageName, String processName, int status) {
			HashMap<String, Integer> statusMap = map.get(packageName);
			if (statusMap == null) {
				statusMap = new HashMap<String, Integer>();
				map.put(packageName, statusMap);
			}
			statusMap.put(processName, status);
		}

		/**
		 * パッケージ名から実行中のプロセスとステータスのMapを取得する
		 * @param packageName
		 *           パッケージ名
		 * @return プロセス名とステータス
		 */
		public HashMap<String, Integer> getProcessStatusMap(String packageName) {
			return map.get(packageName);
		}

	}

}
