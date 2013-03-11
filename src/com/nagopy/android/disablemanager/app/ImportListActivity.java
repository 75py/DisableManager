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

package com.nagopy.android.disablemanager.app;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.nagopy.android.common.fragment.dialog.AsyncTaskWithProgressDialog;
import com.nagopy.android.common.image.ImageUtils;
import com.nagopy.android.disablemanager.util.AppStatus;
import com.nagopy.android.disablemanager.util.filter.AppsFilter;

/**
 * インポートしたリストを表示するアクティビティ
 */
public class ImportListActivity extends MainActivity {

	private ArrayList<AppStatus> apps = new ArrayList<AppStatus>();
	private ArrayList<String> importedList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		importedList = getIntent().getStringArrayListExtra("disabledApps");
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	protected ArrayList<Tab> createTabs(ActionBar actionBar, TabListener tabListener) {
		ArrayList<Tab> tabs = new ArrayList<ActionBar.Tab>();
		ArrayList<Tab> superTabs = super.createTabs(actionBar, tabListener);

		for (Tab tab : superTabs) {
			if (tab.getTag().equals(AppsFilter.DISABLABLE_AND_ENABLED_SYSTEM)
					|| tab.getTag().equals(AppsFilter.DISABLED) || tab.getTag().equals(AppsFilter.HIDE_APPS)) {
				tabs.add(tab);
			}
		}

		return tabs;
	}

	@Override
	protected AsyncTaskWithProgressDialog createReloadAsyncTask() {
		AsyncTaskWithProgressDialog tasks = new AsyncTaskWithProgressDialog(getFragmentManager(), this) {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				apps.clear();
			}

			@Override
			protected Void doInBackground(Void... params) {
				ImportListActivity activity = (ImportListActivity) getActivity();
				if (activity != null) {
					PackageManager packageManager = activity.getPackageManager();
					List<ApplicationInfo> applicationInfo = packageManager
							.getInstalledApplications(PackageManager.GET_META_DATA);
					int iconSize = ImageUtils.getIconSize(activity.getApplicationContext());
					for (ApplicationInfo info : applicationInfo) {
						if (activity.importedList.contains(info.packageName)) {
							AppStatus status = new AppStatus(info.loadLabel(packageManager).toString(),
									info.packageName, info.enabled, true, true);
							activity.apps.add(status);
							Drawable icon = info.loadIcon(packageManager);
							icon.setBounds(0, 0, iconSize, iconSize);
							activity.mIconCacheHashMap.put(info.packageName, icon);
						}
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				ImportListActivity activity = (ImportListActivity) getActivity();
				if (activity != null) {
					activity.mAppLoader.setAppsList(activity.apps);
					activity.mAppFilter.setOriginalAppList(activity.mAppLoader.getAppsList());
					activity.mAppFilter.sortOriginalAppList();
					if (activity.mAdapter == null) {
						// 初回なら
						activity.mAdapter = new AppsListAdapter(activity.mAppFilter.execute(
								activity.lastAppFilterCondition, activity.mAppHideUtils.getHideAppsList()),
								activity.mCommentsUtils, activity.getApplicationContext());
						activity.mListView.setAdapter(activity.mAdapter);
						if (activity.mAdapter.getCount() < 1) {
							activity.mEmptyView.setVisibility(View.VISIBLE);
							activity.mListView.setVisibility(View.GONE);
						}
					} else {
						activity.updateAppList(-1);
					}
				}
			}
		};
		return tasks;
	}
}