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

import com.nagopy.android.disabledapps.filter.AppsFilter;
import com.nagopy.lib.base.BaseActivity;
import com.nagopy.lib.fragment.dialog.AsyncTaskWithProgressDialog;

/**
 * ランチャーから起動するアクティビティ<br>
 * リスト表示など
 */
public class MainActivity extends BaseActivity {

	/**
	 * アプリ一覧を読み込むためのオブジェクト
	 */
	private AppsLoader mAppLoader;

	/**
	 * リストビュー
	 */
	private ListView mListView;

	/**
	 * リストが空の時に表示するテキストビュー
	 */
	private TextView mEmptyView;

	/**
	 * アプリを絞り込むクラス
	 */
	private AppsFilter mAppFilter;

	/**
	 * アプリ一覧を表示するためのアダプタ
	 */
	private AppsListAdapter mAdapter;

	/**
	 * 前回使ったフィルタ条件の値を保持する
	 */
	private int lastAppFilterCondition;

	/**
	 * アイコンをメモリキャッシュするためのハッシュマップ<br>
	 * パッケージ名とアイコン（Drawable）を保存
	 */
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
	 * @param key
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
		// CHECKSTYLE:OFF
		mEmptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
		mListView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
		// CHECKSTYLE:ON

		if (title != null) {
			setTitle(title);

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_filter_disablable_and_enabled_apps:
			updateAppList(AppsFilter.DISABLABLE_AND_ENABLED_SYSTEM, item.getTitle());
			return true;
		case R.id.menu_filter_disabled:
			updateAppList(AppsFilter.DISABLED, item.getTitle());
			return true;
		case R.id.menu_filter_undisablable_system:
			updateAppList(AppsFilter.UNDISABLABLE_SYSTEM, item.getTitle());
			return true;
		case R.id.menu_filter_user_apps:
			updateAppList(AppsFilter.USER_APPS, item.getTitle());
			return true;
		case R.id.menu_share_label:
			sendShareIntent(item.getItemId());
			return true;
		case R.id.menu_share_package:
			sendShareIntent(item.getItemId());
			return true;
		case R.id.menu_share_label_and_package:
			sendShareIntent(item.getItemId());
			return true;

		case R.id.menu_reload:
			createReloadAsyncTask().execute();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * アプリ一覧をメーラーなどに共有<br>
	 * 共有するアプリがない場合はトーストを表示して終了する
	 * @param id
	 *           メニューのID<br>
	 *           これによって共有項目を分ける
	 */
	private void sendShareIntent(int id) {
		ArrayList<AppStatus> appsList = mAdapter.getAppList();
		if (appsList == null || appsList.isEmpty()) {
			showToast(getString(R.string.message_share_no_app));
			return;
		}

		StringBuffer sb = new StringBuffer();
		switch (id) {
		case R.id.menu_share_label:
			for (AppStatus appStatus : appsList) {
				sb.append(appStatus.getLabel());
				sb.append("\n");
			}
			break;
		case R.id.menu_share_package:
			for (AppStatus appStatus : appsList) {
				sb.append(appStatus.getPackageName());
				sb.append("\n");
			}
			break;
		case R.id.menu_share_label_and_package:
			for (AppStatus appStatus : appsList) {
				sb.append(appStatus.getLabel());
				sb.append("\n");
				sb.append(appStatus.getPackageName());
				sb.append("\n\n");
			}
			break;
		default:
			break;
		}

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
		intent.putExtra(Intent.EXTRA_SUBJECT, getTitle());
		startActivity(intent);
	}

	/**
	 * アプリ一覧を表示するためのAdapter
	 */
	private class AppsListAdapter extends BaseAdapter {
		/**
		 * アプリ一覧を保持するリスト
		 */
		private ArrayList<AppStatus> appsList;

		/**
		 * コンストラクタ
		 * @param apps
		 *           アプリ一覧
		 */
		public AppsListAdapter(ArrayList<AppStatus> apps) {
			super();
			appsList = apps;
		}

		/**
		 * @return 現在表示中のアプリ一覧を返す
		 */
		public ArrayList<AppStatus> getAppList() {
			return appsList;
		}

		/**
		 * 表示するアプリの一覧を変更するメソッド
		 * @param newAppsList
		 *           変更するリスト
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

	/**
	 * ListViewのビューを保持するビューホルダー
	 */
	private static class ViewHolder {
		/**
		 * ラベルを表示するテキストビュー
		 */
		TextView labelTextView; // CHECKSTYLE IGNORE THIS LINE

		/**
		 * パッケージ名を表示するテキストビュー
		 */
		TextView pkgNameTextView; // CHECKSTYLE IGNORE THIS LINE
	}

	/**
	 * @return アプリを読み込み直すAsyncTaskWithProgressDialogのインスタンス
	 */
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

}
