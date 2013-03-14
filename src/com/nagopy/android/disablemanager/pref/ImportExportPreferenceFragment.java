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

package com.nagopy.android.disablemanager.pref;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.nagopy.android.disablemanager.R;
import com.nagopy.android.disablemanager.app.AppPreferenceActivity;
import com.nagopy.android.disablemanager.app.ImportListActivity;
import com.nagopy.android.disablemanager.dialog.ConfirmDialogFragment;
import com.nagopy.android.disablemanager.dialog.ConfirmDialogFragment.ConfirmDialogListener;
import com.nagopy.android.disablemanager.dialog.FileChooserDialogFragment;
import com.nagopy.android.disablemanager.dialog.FileOpenDialog.OnOpenFileSelectedListner;
import com.nagopy.android.disablemanager.util.CommentsUtils;
import com.nagopy.android.disablemanager.util.HideUtils;
import com.nagopy.android.disablemanager.util.xml.XmlData;
import com.nagopy.android.disablemanager.util.xml.XmlUtils;

/**
 * 一般設定を表示するフラグメント
 */
public class ImportExportPreferenceFragment extends PreferenceFragment {

	/**
	 * ヘルプ記事のURL
	 */
	private static final String BLOG_URL = "http://blog.nagopy.com/2013/03/disablemanageraboutimport.html";

	/**
	 * 除外リストを選択する場合
	 */
	private static final int REQUEST_HIDDEN = 1;

	/**
	 * 無効化済みをインポートする場合
	 */
	private static final int REQUEST_DISABLED = 2;

	/**
	 * @see XmlUtils
	 */
	private XmlUtils mXmlUtils;

	/**
	 * @see HideUtils
	 */
	private HideUtils mHideUtils;

	/**
	 * @see CommentsUtils
	 */
	private CommentsUtils mCommentsUtils;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mXmlUtils = new XmlUtils(getActivity().getApplicationContext());
		mHideUtils = new HideUtils(getActivity().getApplicationContext());
		mCommentsUtils = new CommentsUtils(getActivity().getApplicationContext());
		addPreferencesFromResource(R.xml.app_preference_activity_body_import);

		PreferenceScreen preferenceScreen = getPreferenceScreen();
		int count = preferenceScreen.getPreferenceCount();
		OnPreferenceClickListener onPreferenceClickListener = new OnPreferenceClickListener() {
			@SuppressWarnings("serial")
			@Override
			public boolean onPreferenceClick(Preference preference) {
				switch (preference.getTitleRes()) {
				case R.string.pref_title_import_hidden_import:
					chooser(REQUEST_HIDDEN, new OnOpenFileSelectedListner() {
						@Override
						public void onOpenFileSelected(File file) {
							checkAndImportHiddenFromXml(file.getAbsolutePath());
						}

						@Override
						public void onOpenFileCanceled() {} // CHECKSTYLE IGNORE THIS LINE
					});
					return true;
				case R.string.pref_title_import_hidden_export:
					String filename = mXmlUtils.export(mHideUtils.getHideAppsList(), XmlUtils.TYPE_HIDDEN);
					if (filename == null) {
						showToast(R.string.export_error_cannot_save);
					} else {
						showToast(getString(R.string.export_success, filename));
					}
					return true;
				case R.string.pref_title_import_disabled_import:
					chooser(REQUEST_DISABLED, new OnOpenFileSelectedListner() {
						@Override
						public void onOpenFileSelected(File file) {
							checkAndImportDisabledApps(file.getAbsolutePath());
						}

						@Override
						public void onOpenFileCanceled() {} // CHECKSTYLE IGNORE THIS LINE
					});
					return true;
				case R.string.pref_title_import_disabled_export:
					String f = mXmlUtils.exportDisabledApps();
					if (f == null) {
						showToast(R.string.export_error_cannot_save);
					} else {
						showToast(getString(R.string.export_success, f));
					}
					return true;
				case R.string.pref_title_import_help:
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(BLOG_URL));
					startActivity(intent);
					return true;
				default:
					return false;
				}
			}
		};

		for (int index = 0; index < count; index++) {
			final Preference preference = preferenceScreen.getPreference(index);
			preference.setOnPreferenceClickListener(onPreferenceClickListener);
			if (preference.getTitleRes() == R.string.pref_title_import_help) {
				preference.setSummary(BLOG_URL);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_HIDDEN:
				String path = data.getData().getPath();
				checkAndImportHiddenFromXml(path);
				break;
			case REQUEST_DISABLED:
				String path1 = data.getData().getPath();
				checkAndImportDisabledApps(path1);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 読み込んだデータの端末名・ビルド番号をチェックして、問題なければ
	 * {@link ImportExportPreferenceFragment#importHiddenApps(XmlData)}を呼び出す
	 * @param path
	 *           ファイルの絶対パス
	 */
	@SuppressWarnings("serial")
	private void checkAndImportHiddenFromXml(String path) {
		final XmlData xmlData = mXmlUtils.importFromXml(path);
		final ConfirmDialogListener listener = new ConfirmDialogListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					importHiddenApps(xmlData);
					break;
				default:
					break;
				}
			}
		};
		if (xmlData.getErrorMessage() != null) {
			showToast(xmlData.getErrorMessage());
		} else if (!XmlUtils.isValidDevice(xmlData)) {
			showAlertDialog(getString(R.string.import_different_device), listener);
		} else if (!XmlUtils.isValidBuild(xmlData)) {

			showAlertDialog(getString(R.string.import_different_build), listener);
		} else if (!XmlUtils.TYPE_HIDDEN.equals(xmlData.getType())) {
			showAlertDialog(getString(R.string.import_different_type, xmlData.getType()), listener);
		} else {
			importHiddenApps(xmlData);
		}
	}

	/**
	 * XMLデータから除外リストを読み込んで保存する
	 * @param xmlData
	 *           読み込んだデータ
	 */
	private void importHiddenApps(XmlData xmlData) {
		Map<String, String> map = xmlData.getComments();
		Set<String> packages = xmlData.getPackages();
		if (!map.isEmpty()) {
			for (String pkg : packages) {
				String comment = map.get(pkg);
				if (comment != null) {
					mCommentsUtils.saveComment(pkg, comment);
				}
			}
		}
		if (mHideUtils.setHideAppList(xmlData.getPackages())) {
			showToast(R.string.import_success);
			PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit()
					.putBoolean(AppPreferenceActivity.KEY_RELOAD_FLAG, true).apply();
		} else {
			showToast(R.string.import_error_cannot_write_sp);
		}
	}

	/**
	 * 確認ダイアログを表示する
	 * @param message
	 *           メッセージ
	 * @param positiveListener
	 *           Okボタンが押されたときのリスナー
	 */
	private void showAlertDialog(String message, ConfirmDialogFragment.ConfirmDialogListener positiveListener) {
		ConfirmDialogFragment alertDialogFragment = new ConfirmDialogFragment();
		alertDialogFragment.init(message, positiveListener);
		alertDialogFragment.show(getFragmentManager(), "alert");
	}

	/**
	 * ファイル選択を開始する。ファイラーがあればそっち、なければダイアログ表示
	 * @param requestCode
	 *           リクエストコード。ファイラー使用でonActivityResultで使う
	 * @param listner
	 *           FOD使用時のリスナー
	 */
	private void chooser(int requestCode, OnOpenFileSelectedListner listner) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/*");
		try {
			startActivityForResult(intent, requestCode);
		} catch (ActivityNotFoundException e) {
			FileChooserDialogFragment fragment = new FileChooserDialogFragment();
			fragment.init(listner, "xml");
			fragment.show(getFragmentManager(), "fod");
		}
	}

	/**
	 * 端末名、ビルド名をチェックし、問題なければ
	 * {@link ImportExportPreferenceFragment#importDisabledAppsAndLaunchActivity(XmlData)} を呼び出す
	 * @param path
	 *           ファイルの絶対パス
	 */
	@SuppressWarnings("serial")
	private void checkAndImportDisabledApps(String path) {
		final XmlData xmlData = mXmlUtils.importFromXml(path);
		final ConfirmDialogListener listener = new ConfirmDialogListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				importDisabledAppsAndLaunchActivity(xmlData);
			}
		};
		if (xmlData.getErrorMessage() != null) {
			showToast(xmlData.getErrorMessage());
		} else if (!XmlUtils.isValidDevice(xmlData)) {
			showAlertDialog(getString(R.string.import_different_device), listener);
		} else if (!XmlUtils.isValidBuild(xmlData)) {
			showAlertDialog(getString(R.string.import_different_build), listener);
		} else if (!XmlUtils.TYPE_DISABLED.equals(xmlData.getType())) {
			showAlertDialog(getString(R.string.import_different_type, xmlData.getType()), listener);
		} else {
			importDisabledAppsAndLaunchActivity(xmlData);
		}
	}

	/**
	 * 無効化済みをインポートしてリストを起動する
	 * @param xmlData
	 *           読み込んだデータ
	 */
	private void importDisabledAppsAndLaunchActivity(XmlData xmlData) {
		HashMap<String, String> map = (HashMap<String, String>) xmlData.getComments();
		Intent intent = new Intent(getActivity().getApplicationContext(), ImportListActivity.class);
		intent.putExtra(ImportListActivity.EXTRA_PACKAGES_AND_COMMENTS, map);
		intent.putExtra(ImportListActivity.EXTRA_FILE_NAME, xmlData.getFilePath());
		startActivity(intent);
	}

	/**
	 * トーストを表示する
	 * @param resId
	 *           リソースID
	 */
	private void showToast(int resId) {
		showToast(getString(resId));
	}

	/**
	 * トーストを表示する
	 * @param object
	 *           表示したいオブジェクト
	 */
	private void showToast(Object object) {
		Toast.makeText(getActivity().getApplicationContext(), object.toString(), Toast.LENGTH_LONG).show();
	}
}
