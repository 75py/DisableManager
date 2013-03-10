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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nagopy.android.common.util.CommonUtil;
import com.nagopy.android.disablemanager.R;
import com.nagopy.android.disablemanager.dialog.FileChooserDialogFragment;
import com.nagopy.android.disablemanager.dialog.FileOpenDialog.OnOpenFileSelectedListner;
import com.nagopy.android.disablemanager.util.HideUtils;
import com.nagopy.android.disablemanager.util.xml.XmlData;
import com.nagopy.android.disablemanager.util.xml.XmlUtils;

/**
 * 一般設定を表示するフラグメント
 */
public class ImportExportPreferenceFragment extends PreferenceFragment {

	private XmlUtils mXmlUtils;
	private HideUtils mHideUtils;

	@Override	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mXmlUtils = new XmlUtils(getActivity().getApplicationContext());
		mHideUtils = new HideUtils(getActivity().getApplicationContext());
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
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("text/xml");
					try {
						startActivityForResult(intent, 1);
					} catch (ActivityNotFoundException e) {
						FileChooserDialogFragment fragment = new FileChooserDialogFragment();
						fragment.init(new OnOpenFileSelectedListner() {
							@Override
							public void onOpenFileSelected(File file) {
								importFromXml(file.getAbsolutePath());
							}

							@Override
							public void onOpenFileCanceled() {
								showToast("canceled");
							}
						}, "xml");
						fragment.show(getFragmentManager(), "fod");
					}

					break;
				case R.id.export_hidden_button:
					new CommonUtil(getActivity()).log(mHideUtils.getHideAppsList());
					String filename = mXmlUtils.export(mHideUtils.getHideAppsList(), XmlUtils.TYPE_HIDDEN);
					if (filename == null) {
						showToast("Error(´・ω・`)");
					} else {
						showToast("保存が成功しました：" + filename);
					}
					break;
				}
			}
		};
		view.findViewById(R.id.import_hidden_button).setOnClickListener(listener);
		view.findViewById(R.id.export_hidden_button).setOnClickListener(listener);
		return view;
	}

	private void showToast(Object object) {
		Toast.makeText(getActivity().getApplicationContext(), object.toString(), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			String path = data.getData().getPath();
			importFromXml(path);
		}
	}

	/**
	 * @param path
	 */
	private void importFromXml(String path) {
		XmlData xmlData = mXmlUtils.importFromXml(path);
		if (xmlData.getErrorMessage() != null) {
			showToast(xmlData.getErrorMessage());
		} else if (XmlUtils.isValidDevice(xmlData) && XmlUtils.isValidBuild(xmlData)) {
			if (mHideUtils.setHideAppList(xmlData.getPackages())) {
				showToast("インポート成功：" + xmlData.getPackages());
			} else {
				showToast("インポート失敗(´・ω・`)");
			}
		} else {
			showToast("???");
		}
	}
}
