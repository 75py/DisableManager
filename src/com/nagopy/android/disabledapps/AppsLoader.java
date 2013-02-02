package com.nagopy.android.disabledapps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.nagopy.android.disabledapps.util.CommentsUtils;
import com.nagopy.android.disabledapps.util.JudgeDisablable;
import com.nagopy.lib.base.BaseObject;
import com.nagopy.lib.image.ImageUtils;

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
		CommentsUtils commentsUtils = new CommentsUtils(getContext());

		for (ApplicationInfo info : applicationInfo) {
			Drawable icon = info.loadIcon(packageManager);
			icon.setBounds(0, 0, iconSize, iconSize);

			AppStatus appStatus = new AppStatus(info.loadLabel(packageManager).toString(), info.packageName,
					info.enabled, (info.flags & ApplicationInfo.FLAG_SYSTEM) > 0,
					judgeDisablable.isDisablable(info));
			appStatus.setComment(commentsUtils.restoreComment(info.packageName));
			log(appStatus.getComment());

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
	 * リストをnullに
	 */
	public void deallocate() {
		appsList = null;
	}
}
