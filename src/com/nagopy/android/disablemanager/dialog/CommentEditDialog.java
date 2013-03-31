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
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nagopy.android.disablemanager.R;

/**
 * コメントを編集するためのダイアログ
 */
public class CommentEditDialog extends DialogFragment {

	/**
	 * ラベル名を保存するためのキー
	 */
	private static final String KEY_LABEL = "KEY_LABEL";
	/**
	 * ラベル名を保存するためのキー
	 */
	private static final String KEY_PACKAGE_NAME = "KEY_PACKAGE_NAME";

	/**
	 * 元の値を保存するためのキー
	 */
	private static final String KEY_DEFAULT_VALUE = "KEY_DEFAULT_VALUE";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View rootView = View.inflate(getActivity().getApplicationContext(), R.layout.comment_edit_dialog, null);
		builder.setTitle(R.string.comment_dialog_title).setView(rootView);
		((TextView) rootView.findViewById(R.id.comment_edit_dialog_label_textView)).setText(getLabel());
		final EditText editText = (EditText) rootView.findViewById(R.id.comment_edit_dialog_editText);
		editText.setText(getDefaultValue());

		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String text = editText.getText().toString();
				((CommentEditDialogListener) getActivity()).onCommentEditDialogPositiveButtonClicked(getId(),
						dialog, getPackageName(), text);
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((CommentEditDialogListener) getActivity()).onCommentEditDialogNegativeButtonClicked(getId(),
						dialog, getPackageName());
			}
		});

		return builder.create();
	}

	/**
	 * 初期設定
	 * @param label
	 *           ラベル名
	 * @param packageName
	 *           パッケージ名
	 * @param defaultValue
	 *           デフォルトの値
	 */
	public void init(String label, String packageName, String defaultValue) {
		Bundle bundle = new Bundle();
		bundle.putString(KEY_LABEL, label);
		bundle.putString(KEY_PACKAGE_NAME, packageName);
		bundle.putString(KEY_DEFAULT_VALUE, defaultValue);
		setArguments(bundle);
	}

	/**
	 * 保存しておいたラベル名を取得する
	 * @return ラベル名
	 */
	private String getLabel() {
		return getArguments().getString(KEY_LABEL);
	}

	/**
	 * @return パッケージ名
	 */
	private String getPackageName() {
		return getArguments().getString(KEY_PACKAGE_NAME);
	}

	/**
	 * @return 入力欄のデフォルト値
	 */
	private String getDefaultValue() {
		return getArguments().getString(KEY_DEFAULT_VALUE);
	}

	/**
	 * コメント編集ダイアログのボタンを押したときの動作を指定するリスナー
	 */
	public interface CommentEditDialogListener {

		/**
		 * コメント編集ダイアログでOKボタンがおされたとき
		 * @param fragmentId
		 *           フラグメントのid
		 * @param dialog
		 *           ダイアログ
		 * @param packageName
		 *           パッケージ名
		 * @param text
		 *           入力されたテキスト
		 */
		void onCommentEditDialogPositiveButtonClicked(int fragmentId, DialogInterface dialog,
				String packageName, String text);

		/**
		 * コメント編集ダイアログでキャンセルボタンが押されたとき
		 * @param fragmentId
		 *           フラグメントのid
		 * @param dialog
		 *           ダイアログ
		 * @param packageName
		 *           パッケージ名
		 */
		void onCommentEditDialogNegativeButtonClicked(int fragmentId, DialogInterface dialog, String packageName);
	}
}
