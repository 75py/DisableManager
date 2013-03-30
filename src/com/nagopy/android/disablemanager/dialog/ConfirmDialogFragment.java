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
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nagopy.android.disablemanager.R;

/**
 * 確認ダイアログ
 */
public class ConfirmDialogFragment extends DialogFragment {

	/**
	 * リスナーを保存するキー
	 * @deprecated
	 */
	private static final String KEY_LISTNER = "positiveButtonListner";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new Builder(getActivity());
		TextView textView = new TextView(getActivity().getApplicationContext());
		textView.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
		Bundle args = getArguments();
		textView.setText(args.getCharSequence("message"));
		ScrollView scrollView = new ScrollView(getActivity().getApplicationContext());
		scrollView.addView(textView);
		scrollView.setPadding(16, 16, 16, 16); // CHECKSTYLE IGNORE THIS LINE
		builder.setView(scrollView);

		OnClickListener listener;
		if (getActivity() instanceof ConfirmDialogListener) {
			listener = new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					((ConfirmDialogListener) getActivity()).onClick(getId(), dialog, which);
				}
			};
		} else {
			listener = getListener();
		}
		builder.setPositiveButton(android.R.string.ok, listener);
		builder.setNegativeButton(android.R.string.cancel, listener);

		builder.setTitle(R.string.confirm_dialog_title);
		return builder.create();
	}

	/**
	 * 色々初期設定
	 * @param message
	 *           メッセージ
	 */
	public void init(CharSequence message) {
		Bundle args = new Bundle();
		args.putCharSequence("message", message);
		setArguments(args);
	}

	/**
	 * 色々初期設定
	 * @param message
	 *           メッセージ
	 * @param positiveListener
	 *           OKを押されたときのリスナー
	 * @deprecated
	 */
	public void init(CharSequence message, ConfirmDialogListenerCompat positiveListener) {
		Bundle args = new Bundle();
		args.putCharSequence("message", message);
		args.putSerializable(KEY_LISTNER, positiveListener);
		setArguments(args);
	}

	/**
	 * 保存しておいたリスナーを取得する
	 * @return リスナー
	 */
	private ConfirmDialogListenerCompat getListener() {
		return (ConfirmDialogListenerCompat) getArguments().get(KEY_LISTNER);
	}

	/**
	 * シリアライズ可能なDialogInterface.OnClickListener
	 * @deprecated
	 */
	public static interface ConfirmDialogListenerCompat extends DialogInterface.OnClickListener, Serializable {

		/**
		 * whichの値によってどのボタンが押されたかを判定するとGood
		 * 
		 * @param dialog
		 *           ダイアログ
		 * @param which
		 *           <ul>
		 *           <li>{@link DialogInterface#BUTTON_POSITIVE}</li>
		 *           <li>
		 *           {@link DialogInterface#BUTTON_NEUTRAL}</li>
		 *           <li>
		 *           {@link DialogInterface#BUTTON_NEGATIVE}</li>
		 *           </ul>
		 */
		@Override
		void onClick(DialogInterface dialog, int which);
	}

	/**
	 * Activityに設定する場合のリスナー
	 */
	public static interface ConfirmDialogListener {

		/**
		 * whichの値によってどのボタンが押されたかを判定するとGood
		 * @param fragmentId
		 *           フラグメントのid
		 * @param dialog
		 *           ダイアログ
		 * @param which
		 *           <ul>
		 *           <li>{@link DialogInterface#BUTTON_POSITIVE}</li>
		 *           <li>
		 *           {@link DialogInterface#BUTTON_NEUTRAL}</li>
		 *           <li>
		 *           {@link DialogInterface#BUTTON_NEGATIVE}</li>
		 *           </ul>
		 */
		void onClick(int fragmentId, DialogInterface dialog, int which);
	}

}
