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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.nagopy.android.disablemanager.util.CommentsUtils;
import com.nagopy.android.disablemanager.util.dpm.Disablable;
import com.nagopy.android.disablemanager.util.filter.AppsFilter;

/**
 * インポートしたリストを表示するアクティビティ<br>
 * MainActivityの流用。メニュー非表示、タブ削減、タイトルをファイル名にとかしたアクティビティ
 */
public class ImportListActivity extends MainActivity {

	/**
	 * intentでファイル名を送る際のキー
	 */
	public static final String EXTRA_FILE_NAME = "com.nagopy.android.disablemanager.app.ImportListActivity.EXTRA_FILE_NAME";

	/**
	 * パッケージ名・コメントのマップをintentで送る際のキー
	 */
	public static final String EXTRA_PACKAGES_AND_COMMENTS = "com.nagopy.android.disablemanager.app.ImportListActivity.EXTRA_PACKAGES_AND_COMMENTS";

	/**
	 * アプリ一覧
	 */
	private ArrayList<AppStatus> apps = new ArrayList<AppStatus>();

	private HashMap<String, String> importedMap;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		importedMap = (HashMap<String, String>) getIntent().getSerializableExtra(EXTRA_PACKAGES_AND_COMMENTS);
		super.onCreate(savedInstanceState);
		setTitle(getIntent().getStringExtra(EXTRA_FILE_NAME));
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
					Disablable judgeDisablable = Disablable.getInstance(getActivity().getApplicationContext());
					PackageManager packageManager = activity.getPackageManager();
					List<ApplicationInfo> applicationInfo = packageManager
							.getInstalledApplications(PackageManager.GET_META_DATA);
					int iconSize = ImageUtils.getIconSize(activity.getApplicationContext());
					for (ApplicationInfo info : applicationInfo) {
						if (activity.importedMap.containsKey(info.packageName)) {
							AppStatus status = new AppStatus(info.loadLabel(packageManager).toString(),
									info.packageName, info.enabled, (info.flags & ApplicationInfo.FLAG_SYSTEM) > 0,
									judgeDisablable.isDisablable(info));
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
								activity.lastAppFilterCondition, activity.mHideUtils.getHideAppsList()),
								activity.getCommentsUtils(), activity.getApplicationContext());
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

	@Override
	protected CommentsUtils getCommentsUtils() {
		if (mCommentsUtils == null) {
			class ImportedCommentsUtils extends CommentsUtils {
				private WeakReference<Map<String, String>> importedMap;

				public ImportedCommentsUtils(ImportListActivity activity) {
					super(activity.getApplicationContext());
					importedMap = new WeakReference<Map<String, String>>(activity.importedMap);
				}

				@Override
				public String restoreComment(String packageName) {
					Map<String, String> map = importedMap.get();
					if (map != null) {
						return map.get(packageName);
					} else {
						return null;
					}
				}

				@Override
				public boolean saveComment(String packageName, String comment) {
					Map<String, String> map = importedMap.get();
					if (map != null) {
						map.put(packageName, comment);
					}
					return true;
				}

			}
			mCommentsUtils = new ImportedCommentsUtils(this);
		}
		return mCommentsUtils;
	}
}
