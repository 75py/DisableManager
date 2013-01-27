package com.nagopy.disabledapps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * アプリ一覧を、条件を指定してフィルタリングするクラス
 */
public class AppsFilter {

	private ArrayList<AppStatus> originalAppList;

	public AppsFilter() {
		originalAppList = new ArrayList<AppStatus>();
	}

	/**
	 * ソートする<br>
	 * 大文字小文字は区別しない
	 */
	public ArrayList<AppStatus> sort(ArrayList<AppStatus> list) {
		Comparator<AppStatus> comparator = new Comparator<AppStatus>() {
			@Override
			public int compare(AppStatus obj0, AppStatus obj1) {
				String label0 = ((AppStatus) obj0).getLabel();
				String label1 = ((AppStatus) obj1).getLabel();
				int ret = 0;

				// ラベルで並び替え、同じラベルがあったらパッケージ名で
				if ((ret = label0.compareTo(label1)) == 0) {
					String pkgName0 = ((AppStatus) obj0).getPackageName();
					String pkgName1 = ((AppStatus) obj1).getPackageName();
					ret = pkgName0.compareToIgnoreCase(pkgName1);
				}
				return ret;
			}
		};
		Collections.sort(list, comparator);

		return list;
	}

	/**
	 * すべてのアプリを登録しておく<br>
	 * ソートする場合は {@link #sort(ArrayList)}
	 * @param original
	 */
	public void setOriginalAppList(ArrayList<AppStatus> original) {
		this.originalAppList = original;
	}

	/**
	 * フィルターを実行して、結果を返す
	 * @param condition
	 *           条件。無名クラスでおｋ
	 * @return フィルター結果
	 */
	public ArrayList<AppStatus> execute(AppFilterCondition condition) {
		ArrayList<AppStatus> filtered = new ArrayList<AppStatus>();
		for (AppStatus appStatus : originalAppList) {
			if (condition.valid(appStatus)) {
				filtered.add(appStatus);
			}
		}
		return filtered;
	}

	/**
	 * フィルターかける条件を指定するためのインターフェース<br>
	 * 無名クラスで適宜作ってね
	 */
	public static interface AppFilterCondition {

		/**
		 * @param appStatus
		 *           判定するアプリのステータス
		 * @return フィルターの条件に適していればtrue
		 */
		public boolean valid(AppStatus appStatus);
	}

}
