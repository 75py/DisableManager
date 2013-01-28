package com.nagopy.android.disabledapps;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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

import com.nagopy.android.disabledapps.R;
import com.nagopy.android.disabledapps.filter.AppsFilter;
import com.nagopy.lib.base.BaseActivity;
import com.nagopy.lib.fragment.dialog.AsyncTaskWithProgressDialog;

public class MainActivity extends BaseActivity {

	private AppsLoader mAppLoader;

	private ListView mListView;

	private TextView mEmptyView;

	private AppsFilter mAppFilter;

	private AppsListAdapter mAdapter;

	private int lastAppFilterCondition;

	private HashMap<String, Drawable> mIconCacheHashMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAppFilter = new AppsFilter();
		mIconCacheHashMap = new HashMap<String, Drawable>();
		lastAppFilterCondition = AppsFilter.DISABLED;
		mAppLoader = new AppsLoader(getApplicationContext());
		mListView = (ListView) findViewById(R.id.listView_enabled_apps);
		mEmptyView = (TextView) findViewById(R.id.listView_empty);

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

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mListView.setAdapter(null);
		mListView.setOnItemClickListener(null);
		mListView = null;

		mEmptyView = null;

		mAdapter.updateAppList(null);
		mAdapter = null;

		mAppFilter.deallocate();
		mAppFilter = null;

		mAppLoader.deallocate();
		mAppLoader = null;

		mCommonUtil = null;

		// Set<String> keyset = mIconCacheHashMap.keySet();
		// for (Iterator<String> iterator = keyset.iterator(); iterator.hasNext();) {
		// String packageName = (String) iterator.next();
		// Drawable icon = mIconCacheHashMap.get(packageName);
		// }
		mIconCacheHashMap.clear();
		mIconCacheHashMap = null;
		mIconCacheHashMap = new HashMap<String, Drawable>();
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
	 *           負の数を渡すと前回と同じフィルタを使う
	 * @param title
	 *           ヘッダーのテキストを指定。nullなら変更しない
	 */
	private void updateAppList(int key, CharSequence title) {
		if (key < 0) {
			key = lastAppFilterCondition;
		} else {
			lastAppFilterCondition = key;
		}
		mAdapter.updateAppList(mAppFilter.execute(key));
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(0);

		boolean isEmpty = mAdapter.getCount() < 1;
		mEmptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
		mListView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

		if (title != null) {
			setTitle(title);

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_filter_disablable_and_enabled_apps:
			updateAppList(AppsFilter.DISABLABLE_AND_ENABLED_SYSTEM, item.getTitle());
			break;
		case R.id.menu_filter_disabled:
			updateAppList(AppsFilter.DISABLED, item.getTitle());
			break;
		case R.id.menu_filter_undisablable_system:
			updateAppList(AppsFilter.UNDISABLABLE_SYSTEM, item.getTitle());
			break;
		case R.id.menu_filter_user_apps:
			updateAppList(AppsFilter.USER_APPS, item.getTitle());
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
				// holder.labelTextView.setCompoundDrawables(appStatus.getIcon(), null, null, null);
				Drawable icon = mIconCacheHashMap.get(appStatus.getPackageName());
				holder.labelTextView.setCompoundDrawables(icon, null, null, null);
				icon.setCallback(null);
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
					activity.mIconCacheHashMap = activity.mAppLoader.load();
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
						activity.mAdapter = new AppsListAdapter(activity.mAppFilter.execute(AppsFilter.DISABLED));
						activity.mListView.setAdapter(activity.mAdapter);
						if (activity.mAdapter.getCount() < 1) {
							activity.mEmptyView.setVisibility(View.VISIBLE);
							activity.mListView.setVisibility(View.GONE);
						}
					} else {
						activity.updateAppList(-1, null);
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
