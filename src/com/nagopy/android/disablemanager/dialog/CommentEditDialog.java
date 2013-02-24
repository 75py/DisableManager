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

import java.io.Serializable;

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
	 * 元の値を保存するためのキー
	 */
	private static final String KEY_DEFAULT_VALUE = "KEY_DEFAULT_VALUE";

	/**
	 * リスナーを保存するためのキー
	 */
	private static final String KEY_LISTENER = "KEY_LISTENER";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View rootView = View.inflate(getActivity().getApplicationContext(), R.layout.comment_edit_dialog, null);
		builder.setTitle(R.string.comment_dialog_title).setView(rootView);
		((TextView) rootView.findViewById(R.id.comment_edit_dialog_label_textView)).setText(getLabel());
		final EditText editText = (EditText) rootView.findViewById(R.id.comment_edit_dialog_editText);
		editText.setText(getDefaultValue());

		final CommentEditDialogListener listener = getListener();
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String text = editText.getText().toString();
				listener.onPositiveButtonClicked(dialog, text);
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.onNegativeButtonClicked(dialog);
			}
		});

		return builder.create();
	}

	/**
	 * ラベルを設定する
	 * @param label
	 *           ラベル名
	 */
	public void setLabel(String label) {
		Bundle bundle = getArguments();
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putString(KEY_LABEL, label);
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
	 * リスナーを保存する
	 * @param listener
	 *           保存するリスナー
	 */
	public void setListener(CommentEditDialogListener listener) {
		Bundle bundle = getArguments();
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putSerializable(KEY_LISTENER, listener);
		setArguments(bundle);
	}

	/**
	 * 保存しておいたリスナーを取得する
	 * @return リスナー
	 */
	private CommentEditDialogListener getListener() {
		return (CommentEditDialogListener) getArguments().getSerializable(KEY_LISTENER);
	}

	/**
	 * 入力欄のデフォルト値をセットする
	 * @param value
	 *           デフォルト値
	 */
	public void setDefaultValue(String value) {
		Bundle bundle = getArguments();
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putString(KEY_DEFAULT_VALUE, value);
		setArguments(bundle);
	}

	/**
	 * @return 入力欄のデフォルト値
	 */
	private String getDefaultValue() {
		return getArguments().getString(KEY_DEFAULT_VALUE);
	}

	/**
	 * ダイアログのボタンを押したときの動作を指定するリスナー
	 */
	@SuppressWarnings("serial")
	public abstract static class CommentEditDialogListener implements Serializable {

		/**
		 * OKボタンがおされたとき
		 * @param dialog
		 *           ダイアログ
		 * @param text
		 *           入力されたテキスト
		 */
		public abstract void onPositiveButtonClicked(DialogInterface dialog, String text);

		/**
		 * キャンセルボタンが押されたとき
		 * @param dialog
		 *           ダイアログ
		 */
		public abstract void onNegativeButtonClicked(DialogInterface dialog);
	}
}
