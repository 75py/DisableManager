package com.nagopy.disabledapps;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nagopy.disabledapps.AppsFilter.AppFilterCondition;
import com.nagopy.lib.base.BaseActivity;
import com.nagopy.lib.fragment.dialog.AsyncTaskWithProgressDialog;

public class MainActivity extends BaseActivity {

	private AppsLoader mAppLoader;

	private ListView mListView;

	private AppsFilter mAppFilter;

	private AppsListAdapter mAdapter;

	private AppFilterCondition lastAppFilterCondition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAppFilter = new AppsFilter();
		mAppLoader = new AppsLoader(getApplicationContext());
		mListView = (ListView) findViewById(R.id.listView_enabled_apps);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
				String packageName = ((TextView) view.findViewById(R.id.list_textview_package_name)).getText()
						.toString();

				Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri
						.parse("package:" + packageName));
				startActivity(intent);
			}
		});

		createReloadAsyncTask().execute();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mListView.setAdapter(null);
		mListView = null;
		mAdapter = null;
		mAppFilter.setOriginalAppList(null);
		mAppFilter = null;
		mAppLoader = null;
		mCommonUtil = null;
		lastAppFilterCondition = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * 表示するアプリを更新する
	 * @param condition
	 *           表示するアプリを選ぶ条件<br>
	 *           nullを渡すと前回と同じフィルタを使う
	 */
	private void updateAppList(AppFilterCondition condition) {
		if (condition == null) {
			condition = lastAppFilterCondition;
		} else {
			lastAppFilterCondition = condition;
		}
		mAdapter.updateAppList(mAppFilter.execute(condition));
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_filter_disablable_and_enabled_apps:
			updateAppList(new AppFilterCondition() {
				@Override
				public boolean valid(AppStatus appStatus) {
					// システムで、無効化可能で、まだ有効なアプリ
					return appStatus.isSystem() && appStatus.canDisable() && appStatus.isEnabled();
				}
			});
			setTitle(item.getTitle());
			break;
		case R.id.menu_filter_disabled:
			updateAppList(new AppFilterCondition() {
				@Override
				public boolean valid(AppStatus appStatus) {
					// 無効化済み
					return !appStatus.isEnabled();
				}
			});
			setTitle(item.getTitle());
			break;
		case R.id.menu_filter_undisablable_system:
			updateAppList(new AppFilterCondition() {
				@Override
				public boolean valid(AppStatus appStatus) {
					// 無効化できないシステムアプリ
					return appStatus.isSystem() && !appStatus.canDisable();
				}
			});
			setTitle(item.getTitle());
			break;
		case R.id.menu_filter_user_apps:
			updateAppList(new AppFilterCondition() {
				@Override
				public boolean valid(AppStatus appStatus) {
					// 通常のアプリ
					return !appStatus.isSystem();
				}
			});
			setTitle(item.getTitle());
			break;
		case R.id.menu_share_label:
			StringBuffer sb = new StringBuffer();
			for (AppStatus appStatus : mAdapter.getAppList()) {
				sb.append(appStatus.getLabel());
				sb.append("\n");
			}

			sendShareIntent(sb.toString());
			break;
		case R.id.menu_share_package:
			StringBuffer sb1 = new StringBuffer();
			for (AppStatus appStatus : mAdapter.getAppList()) {
				sb1.append(appStatus.getPackageName());
				sb1.append("\n");
			}

			sendShareIntent(sb1.toString());
			break;
		case R.id.menu_share_label_and_package:
			StringBuffer sb11 = new StringBuffer();
			for (AppStatus appStatus : mAdapter.getAppList()) {
				sb11.append(appStatus.getLabel());
				sb11.append("\n");
				sb11.append(appStatus.getPackageName());
				sb11.append("\n\n");
			}

			sendShareIntent(sb11.toString());
			break;

		case R.id.menu_reload:
			createReloadAsyncTask().execute();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class AppsListAdapter extends BaseAdapter {
		private ArrayList<AppStatus> appsList;

		public AppsListAdapter(ArrayList<AppStatus> apps) {
			super();
			appsList = apps;
		}

		/**
		 * 現在表示中のアプリ一覧を取得する
		 */
		public ArrayList<AppStatus> getAppList() {
			return appsList;
		}

		/**
		 * 表示するアプリの一覧を変更するメソッド
		 */
		public void updateAppList(ArrayList<AppStatus> newAppsList) {
			appsList = newAppsList;
		}

		@Override
		public int getCount() {
			return appsList.size();
		}

		@Override
		public Object getItem(int position) {
			return appsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.app_list_row, null);
				holder = new ViewHolder();
				holder.labelTextView = (TextView) convertView.findViewById(R.id.list_textview_label);
				holder.pkgNameTextView = (TextView) convertView.findViewById(R.id.list_textview_package_name);
				convertView.setTag(R.string.app_name, holder);
			} else {
				holder = (ViewHolder) convertView.getTag(R.string.app_name);
			}

			AppStatus appStatus = (AppStatus) getItem(position);
			if (appStatus != null) {
				holder.labelTextView.setText(appStatus.getLabel());
				holder.pkgNameTextView.setText(appStatus.getPackageName());
				holder.labelTextView.setCompoundDrawables(appStatus.getIcon(), null, null, null);
			}
			return convertView;
		}

	}

	private static class ViewHolder {
		TextView labelTextView;
		TextView pkgNameTextView;
	}

	private AsyncTaskWithProgressDialog createReloadAsyncTask() {
		return new AsyncTaskWithProgressDialog(getSupportFragmentManager(), this) {

			@Override
			protected void onPreExecute() {
				PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
				if (!powerManager.isScreenOn()) {
					this.cancel(true);
					return;
				}
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... params) {
				MainActivity activity = (MainActivity) getActivity();
				if (activity != null) {
					activity.mAppLoader.load();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				MainActivity activity = (MainActivity) getActivity();
				if (activity != null) {
					activity.mAppFilter.setOriginalAppList(activity.mAppFilter.sort(activity.mAppLoader
							.getAppsList()));
					if (activity.mAdapter == null) {
						// 初回なら
						activity.mAdapter = new AppsListAdapter(
								activity.mAppFilter.execute(new AppFilterCondition() {
									@Override
									public boolean valid(AppStatus appStatus) {
										return !appStatus.isEnabled();
									}
								}));
					} else {
					}
					// activity.mListView.setAdapter(activity.mAdapter);
					if (activity.mListView.getAdapter() == null) {
						activity.mListView.setAdapter(activity.mAdapter);
						activity.setTitle(R.string.menu_filter_disabled);
					} else {
						activity.updateAppList(null);
					}
				}
			}
		};
	}

	/**
	 * テキストをメーラーなどに共有
	 * @param text
	 *           共有したい文字列
	 */
	private void sendShareIntent(String text) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, text);
		startActivity(intent);
	}
}
