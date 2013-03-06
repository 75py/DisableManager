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

package com.nagopy.android.disablemanager.util.filter;

import java.util.Set;

import com.nagopy.android.disablemanager.util.AppStatus;

/**
 * フィルターかける条件を指定するためのインターフェース
 */
abstract class AppsFilterCondition {

	/**
	 * 除外アプリの一覧
	 */
	private Set<String> hideSet;

	/**
	 * @param appStatus
	 *           判定するアプリのステータス
	 * @return フィルターの条件に適していればtrue
	 */
	public abstract boolean valid(AppStatus appStatus);

	/**
	 * @return 除外アプリの一覧
	 */
	protected Set<String> getHideSet() {
		return hideSet;
	}

	/**
	 * 除外アプリをセットする
	 * @param hideSet
	 *           除外アプリ一覧
	 */
	protected void setHideSet(Set<String> hideSet) {
		this.hideSet = hideSet;
	}
}
