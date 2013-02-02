package com.nagopy.android.disabledapps.filter;

import com.nagopy.android.disabledapps.AppStatus;

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
