package com.nagopy.android.disabledapps.util.filter;

import com.nagopy.android.disabledapps.util.AppStatus;

/**
 * フィルターかける条件を指定するためのインターフェース<br>
 * 無名クラスで適宜作ってね
 */
public interface AppFilterCondition {

	/**
	 * @param appStatus
	 *           判定するアプリのステータス
	 * @return フィルターの条件に適していればtrue
	 */
	boolean valid(AppStatus appStatus);
}
