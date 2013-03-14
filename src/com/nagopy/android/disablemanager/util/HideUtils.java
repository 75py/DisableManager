package com.nagopy.android.disablemanager.util;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 除外アプリの管理クラス
 */
public class HideUtils {

	/**
	 * コンテキスト
	 */
	@SuppressWarnings("unused")
	private Context mContext;

	/**
	 * デフォルトのSharedPreferences
	 */
	private SharedPreferences sp;

	/**
	 * コンストラクタ
	 * @param context
	 *           アプリケーションのコンテキスト
	 */
	public HideUtils(Context context) {
		mContext = context;
		sp = PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * 非表示アプリ一覧の更新
	 * @param packageName
	 *           パッケージ名
	 * @return 保存が成功すればtrue
	 */
	public boolean updateHideList(String packageName) {
		Set<String> set = getHideAppsList();
		if (set.contains(packageName)) {
			set.remove(packageName);
		} else {
			set.add(packageName);
		}

		return sp.edit().putStringSet("hides", set).commit();
	}

	/**
	 * @return 非表示アプリ一覧
	 */
	public Set<String> getHideAppsList() {
		Set<String> savedHashSet = sp.getStringSet("hides", new HashSet<String>());
		HashSet<String> returnHashSet = new HashSet<String>(savedHashSet.size());
		for (String packageName : savedHashSet) {
			returnHashSet.add(packageName);
		}
		return returnHashSet;
	}

	/**
	 * 除外リストをどかーんと追加する
	 * @param set
	 *           除外するパッケージ
	 * @return 保存が成功すればtrueを返す
	 */
	public boolean addHideAppList(Set<String> set) {
		Set<String> savedSet = getHideAppsList();
		savedSet.addAll(set);
		return sp.edit().putStringSet("hides", savedSet).commit();
	}

}
