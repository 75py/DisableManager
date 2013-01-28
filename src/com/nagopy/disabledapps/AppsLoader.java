package com.nagopy.disabledapps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.nagopy.disabledapps.util.JudgeDisablable;
import com.nagopy.lib.base.BaseObject;
import com.nagopy.lib.image.ImageUtils;

public class AppsLoader extends BaseObject {
	private ArrayList<AppStatus> appsList;

	public AppsLoader(Context context) {
		super(context);
		appsList = new ArrayList<AppStatus>();
	}

	/**
	 * アプリ一覧を読み込む<br>
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
	 * アプリ一覧（未編集）を取得する
	 * @return
	 */
	public ArrayList<AppStatus> getAppsList() {
		return appsList;
	}

	public void deallocate() {
		appsList = null;
	}
}