package com.nagopy.android.disabledapps.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.nagopy.android.common.app.BaseObject;
import com.nagopy.android.common.image.ImageUtils;

/**
 * アプリ名などを読みこみ、アプリ一覧を取得するクラス。
 */
public class AppsLoader extends BaseObject {
	/**
	 * アプリ一覧を保存しておくためのフィールド。
	 */
	private ArrayList<AppStatus> appsList;

	/**
	 * コンストラクタ
	 * @param context
	 *           　アプリケーションのコンテキスト
	 */
	public AppsLoader(Context context) {
		super(context);
		appsList = new ArrayList<AppStatus>();
	}

	/**
	 * アプリ一覧を読み込む。<br>
	 * ちょっと時間かかるかも
	 * @return アイコンのキャッシュ
	 */
	public HashMap<String, Drawable> load() {
		appsList.clear();

		PackageManager packageManager = getContext().getPackageManager();
		// インストール済みのアプリケーション一覧の取得
		List<ApplicationInfo> applicationInfo = packageManager
				.getInstalledApplications(PackageManager.GET_META_DATA);
		int iconSize = ImageUtils.getIconSize(getContext());

		JudgeDisablable judgeDisablable = JudgeDisablable.getInstance(getContext());
		HashMap<String, Drawable> iconCache = new HashMap<String, Drawable>();

		for (ApplicationInfo info : applicationInfo) {
			Drawable icon = info.loadIcon(packageManager);
			icon.setBounds(0, 0, iconSize, iconSize);

			AppStatus appStatus = new AppStatus(info.loadLabel(packageManager).toString(), info.packageName,
					info.enabled, (info.flags & ApplicationInfo.FLAG_SYSTEM) > 0,
					judgeDisablable.isDisablable(info));

			appsList.add(appStatus);
			iconCache.put(info.packageName, icon);
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
			JudgeDisablable judgeDisablable = JudgeDisablable.getInstance(getContext());

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
