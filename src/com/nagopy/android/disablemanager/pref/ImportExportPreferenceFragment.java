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
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nagopy.android.disablemanager.R;
import com.nagopy.android.disablemanager.app.ImportListActivity;
import com.nagopy.android.disablemanager.dialog.ConfirmDialogFragment;
import com.nagopy.android.disablemanager.dialog.ConfirmDialogFragment.AlertDialogListener;
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.import_export_fragment, null);
		OnClickListener listener = new OnClickListener() {
			@SuppressWarnings("serial")
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.import_hidden_button:
					chooser(REQUEST_HIDDEN, new OnOpenFileSelectedListner() {
						@Override
						public void onOpenFileSelected(File file) {
							checkAndImportHiddenFromXml(file.getAbsolutePath());
						}

						@Override
						public void onOpenFileCanceled() {}
					});

					break;
				case R.id.export_hidden_button:
					String filename = mXmlUtils.export(mHideUtils.getHideAppsList(), XmlUtils.TYPE_HIDDEN);
					if (filename == null) {
						showToast(R.string.export_error_cannot_save);
					} else {
						showToast(getString(R.string.export_success, filename));
					}
					break;
				case R.id.import_disabled_button:
					chooser(REQUEST_DISABLED, new OnOpenFileSelectedListner() {
						@Override
						public void onOpenFileSelected(File file) {
							checkAndImportDisabledApps(file.getAbsolutePath());
						}

						@Override
						public void onOpenFileCanceled() {}
					});
					break;
				case R.id.export_disabled_button:
					String f = mXmlUtils.exportDisabledApps();
					if (f == null) {
						showToast(R.string.export_error_cannot_save);
					} else {
						showToast(getString(R.string.export_success, f));
					}
					break;
				}
			}
		};
		view.findViewById(R.id.import_hidden_button).setOnClickListener(listener);
		view.findViewById(R.id.export_hidden_button).setOnClickListener(listener);
		view.findViewById(R.id.import_disabled_button).setOnClickListener(listener);
		view.findViewById(R.id.export_disabled_button).setOnClickListener(listener);
		return view;
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
		if (xmlData.getErrorMessage() != null) {
			showToast(xmlData.getErrorMessage());
		} else if (!XmlUtils.isValidDevice(xmlData)) {
			showAlertDialog(getString(R.string.import_different_device), new AlertDialogListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					importHiddenApps(xmlData);
				}
			});
		} else if (!XmlUtils.isValidBuild(xmlData)) {
			showAlertDialog(getString(R.string.import_different_build), new AlertDialogListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					importHiddenApps(xmlData);
				}
			});
		} else {
			importHiddenApps(xmlData);
		}
	}

	/**
	 * XMLデータから除外リストを読み込んで保存する
	 * @param xmlData
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
	private void showAlertDialog(String message, ConfirmDialogFragment.AlertDialogListener positiveListener) {
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
		intent.setType("text/xml");
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
		if (xmlData.getErrorMessage() != null) {
			showToast(xmlData.getErrorMessage());
		} else if (!XmlUtils.isValidDevice(xmlData)) {
			showAlertDialog(getString(R.string.import_different_device), new AlertDialogListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					importDisabledAppsAndLaunchActivity(xmlData);
				}
			});
		} else if (!XmlUtils.isValidBuild(xmlData)) {
			showAlertDialog(getString(R.string.import_different_build), new AlertDialogListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					importDisabledAppsAndLaunchActivity(xmlData);
				}
			});
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
		Intent intent = new Intent(getActivity().getApplicationContext(), ImportListActivity.class);
		intent.putStringArrayListExtra("disabledApps", new ArrayList<String>(packages));
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
