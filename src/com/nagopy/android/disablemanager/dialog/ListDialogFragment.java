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

package com.nagopy.android.disablemanager.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * ダイアログでリストを表示するためのフラグメント
 */
public class ListDialogFragment extends DialogFragment {

	/**
	 * 配列のリソースIDを保存するためのキー
	 */
	private static final String KEY_ARRAYSTRING_RES_ID = "KEY_ARRAYSTRING_RES_ID";

	/**
	 * ダイアログのタイトルを保存するためのキー
	 */
	private static final String KEY_DIALOG_TITLE = "KEY_DIALOG_TITLE";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ListView listView = new ListView(getActivity().getApplicationContext());
		String[] array = getActivity().getResources().getStringArray(getArrayResId());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, array);
		listView.setAdapter(adapter);
		if (getActivity() instanceof OnListDialogItemClickListener) {
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					((OnListDialogItemClickListener) getActivity()).onItemClick(getId(), parent, view, position,
							id);
				}
			});
		}

		return new AlertDialog.Builder(getActivity()).setView(listView).setTitle(getDialogTitle()).create();
	}

	/**
	 * 保存した配列のリソースIDを取得する
	 * @return 保存しておいた配列のリソースID
	 */
	private int getArrayResId() {
		return getArguments().getInt(KEY_ARRAYSTRING_RES_ID);
	}

	/**
	 * @return 保存しておいたダイアログのタイトル
	 */
	private String getDialogTitle() {
		return getArguments().getString(KEY_DIALOG_TITLE);
	}

	/**
	 * 配列のリソースIDとリスナーを設定する
	 * @param title
	 *           ダイアログのタイトル
	 * @param arrayResId
	 *           配列のリソースID
	 */
	public void init(String title, int arrayResId) {
		Bundle bundle = new Bundle();
		bundle.putString(KEY_DIALOG_TITLE, title);
		bundle.putInt(KEY_ARRAYSTRING_RES_ID, arrayResId);
		setArguments(bundle);
	}

	/**
	 * ListDialogFragmentで使うリスナー。Activityで実装する
	 */
	public static interface OnListDialogItemClickListener {
		/**
		 * Callback method to be invoked when an item in this AdapterView has been clicked.
		 * 
		 * Implementers can call getItemAtPosition(position) if they need to access the data
		 * associated with the selected item.
		 * 
		 * @param parent
		 *           The AdapterView where the click happened.
		 * @param view
		 *           The view within the AdapterView that was clicked (this will be a view
		 *           provided by the
		 *           adapter)
		 * @param position
		 *           The position of the view in the adapter.
		 * @param id
		 *           The row id of the item that was clicked.
		 * 
		 * @param fragmentId
		 *           フラグメントのid
		 */
		void onItemClick(int fragmentId, AdapterView<?> parent, View view, int position, long id);
	}
}
