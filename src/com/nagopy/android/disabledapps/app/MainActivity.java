package com.nagopy.android.disabledapps.app;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nagopy.android.common.app.BaseActivity;
import com.nagopy.android.common.fragment.dialog.AsyncTaskWithProgressDialog;
import com.nagopy.android.disabledapps.R;
import com.nagopy.android.disabledapps.util.AppStatus;
import com.nagopy.android.disabledapps.util.AppsLoader;
import com.nagopy.android.disabledapps.util.CommentsUtils;
import com.nagopy.android.disabledapps.util.dialog.CommentEditDialog;
import com.nagopy.android.disabledapps.util.dialog.CommentEditDialog.CommentEditDialogListener;
import com.nagopy.android.disabledapps.util.filter.AppsFilter;

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

	/**
	 * コメントを編集するダイアログ
	 */
	private CommentEditDialog mCommentEditDialog;

	/**
	 * コメントを編集するためのクラス
	 */
	private CommentsUtils mCommentsUtils;

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

		mCommentsUtils = new CommentsUtils(getApplicationContext());
		mCommentEditDialog = new CommentEditDialog();
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long arg3) {
				final String packageName = ((TextView) view.findViewById(R.id.list_textview_package_name))
						.getText().toString();
				String label = ((TextView) view.findViewById(R.id.list_textview_label)).getText().toString();
				mCommentEditDialog.setLabel(label);
				mCommentEditDialog.setDefaultValue(mCommentsUtils.restoreComment(packageName));
				mCommentEditDialog.setListener(new CommentEditDialogListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void onPositiveButtonClicked(DialogInterface dialog, String text) {
						mCommentsUtils.saveComment(packageName, text);

						updateAppList(-1, null);
					}

					@Override
					// CHECKSTYLE:OFF
					public void onNegativeButtonClicked(DialogInterface dialog) {}
					// CHECKSTYLE:ON
				});
				mCommentEditDialog.show(getFragmentManager(), "CommentEditDialog");

				return false;
			}
		});

		createReloadAsyncTask().execute();

		initActionBarTabs();
	}

	/**
	 * タブを設定する
	 */
	private void initActionBarTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// CHECKSTYLE:OFF
		// 何となくローカルインナークラスを使ってみたかったｗ
		class TabListener implements ActionBar.TabListener {
			private WeakReference<MainActivity> weakReference;

			public TabListener(MainActivity activity) {
				weakReference = new WeakReference<MainActivity>(activity);
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				MainActivity activity = weakReference.get();
				if (activity != null) {
					int id = (Integer) tab.getTag();
					if (activity.mAdapter != null) {
						updateAppList(id, tab.getText());
					} else {
						activity.lastAppFilterCondition = id;
					}
				}
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {}
		}
		// CHECKSTYLE:ON

		TabListener tabListener = new TabListener(this);

		actionBar.addTab(actionBar.newTab().setText(R.string.menu_filter_disabled).setTag(AppsFilter.DISABLED)
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(R.string.menu_filter_disablable_and_enabled_apps)
				.setTag(AppsFilter.DISABLABLE_AND_ENABLED_SYSTEM).setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(R.string.menu_filter_undisablable_system)
				.setTag(AppsFilter.UNDISABLABLE_SYSTEM).setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(R.string.menu_filter_user_apps)
				.setTag(AppsFilter.USER_APPS).setTabListener(tabListener));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mListView.setAdapter(null);
		mListView.setOnItemClickListener(null);
		mListView.setOnItemLongClickListener(null);
		mListView = null;

		mEmptyView = null;

		mAdapter.updateAppList(null);
		mAdapter = null;

		mAppFilter.deallocate();
		mAppFilter = null;

		mAppLoader.deallocate();
		mAppLoader = null;

		mCommentEditDialog.setListener(null);
		mCommentEditDialog = null;
		mCommentsUtils = null;

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
		case R.id.menu_share_label:
		case R.id.menu_share_package:
		case R.id.menu_share_label_and_package:
		case R.id.menu_share_customformat:
			sendShareIntent(item.getItemId());
			return true;

		case R.id.menu_reload:
			createReloadAsyncTask().execute();
			return true;

		case R.id.menu_preference:
			Intent intent = new Intent(getApplicationContext(), AppPreferenceActivity.class);
			startActivity(intent);
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
		String lineBreak = System.getProperty("line.separator");
		switch (id) {
		case R.id.menu_share_label:
			for (AppStatus appStatus : appsList) {
				sb.append(appStatus.getLabel());
				// String comment = appStatus.getComment();
				// if (comment != null) {
				// sb.append("（");
				// sb.append(comment);
				// sb.append("）");
				// }
				sb.append(lineBreak);
				// sb.append(String.format("%1$s（%2$s）%3$s", appStatus.getLabel(),
				// appStatus.getComment(), lineBreak));
			}
			break;
		case R.id.menu_share_package:
			for (AppStatus appStatus : appsList) {
				sb.append(appStatus.getPackageName());
				sb.append(lineBreak);
			}
			break;
		case R.id.menu_share_label_and_package:
			for (AppStatus appStatus : appsList) {
				sb.append(appStatus.getLabel());
				sb.append(lineBreak);
				sb.append(appStatus.getPackageName());
				sb.append(lineBreak);
				sb.append(lineBreak);
			}
			break;
		case R.id.menu_share_customformat:
			for (AppStatus appStatus : appsList) {
				String comment = mCommentsUtils.restoreComment(appStatus.getPackageName());
				SharedPreferences sp = getSP();
				String formatWithComment = sp.getString(
						getString(R.string.pref_key_share_customformat_with_comment),
						getString(R.string.pref_def_share_customformat_with_comment));
				String formatWithoutComment = sp.getString(
						getString(R.string.pref_key_share_customformat_without_comment),
						getString(R.string.pref_def_share_customformat_without_comment));
				if (comment == null) {
					// コメントがない場合
					sb.append(String.format(formatWithoutComment, appStatus.getLabel(),
							appStatus.getPackageName(), lineBreak));
				} else {
					// コメントがある場合
					sb.append(String.format(formatWithComment, appStatus.getLabel(), appStatus.getPackageName(),
							lineBreak, comment));
				}
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
		 * コメントを読みこむためのクラス
		 */
		private CommentsUtils mCommentsUtils;

		/**
		 * コンストラクタ
		 * @param apps
		 *           アプリ一覧
		 * @param commentsUtils
		 *           CommentsUtilsを渡す
		 */
		public AppsListAdapter(ArrayList<AppStatus> apps, CommentsUtils commentsUtils) {
			super();
			appsList = apps;
			this.mCommentsUtils = commentsUtils;
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
				holder.commentTextView = (TextView) convertView.findViewById(R.id.list_textview_comment);
				holder.pkgNameTextView = (TextView) convertView.findViewById(R.id.list_textview_package_name);
				convertView.setTag(R.string.app_name, holder);
			} else {
				holder = (ViewHolder) convertView.getTag(R.string.app_name);
			}

			AppStatus appStatus = (AppStatus) getItem(position);
			if (appStatus != null) {
				holder.labelTextView.setText(appStatus.getLabel());
				holder.pkgNameTextView.setText(appStatus.getPackageName());
				Drawable icon = mIconCacheHashMap.get(appStatus.getPackageName());
				holder.labelTextView.setCompoundDrawables(icon, null, null, null);
				icon.setCallback(null);

				String comment = this.mCommentsUtils.restoreComment(appStatus.getPackageName());
				if (comment != null) {
					holder.commentTextView.setText(comment);
					holder.commentTextView.setVisibility(View.VISIBLE);
				} else {
					holder.commentTextView.setVisibility(View.GONE);
				}
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
		 * コメントを表示するテキストビュー
		 */
		TextView commentTextView; // CHECKSTYLE IGNORE THIS LINE

		/**
		 * パッケージ名を表示するテキストビュー
		 */
		TextView pkgNameTextView; // CHECKSTYLE IGNORE THIS LINE
	}

	/**
	 * @return アプリを読み込み直すAsyncTaskWithProgressDialogのインスタンス
	 */
	private AsyncTaskWithProgressDialog createReloadAsyncTask() {
		return new AsyncTaskWithProgressDialog(getFragmentManager(), this) {

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
						activity.mAdapter = new AppsListAdapter(
								activity.mAppFilter.execute(activity.lastAppFilterCondition), activity.mCommentsUtils);
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
