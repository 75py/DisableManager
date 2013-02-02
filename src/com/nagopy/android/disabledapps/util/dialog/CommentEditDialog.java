package com.nagopy.android.disabledapps.util.dialog;

import java.io.Serializable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nagopy.android.disabledapps.R;

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

	public void setDefaultValue(String value) {
		Bundle bundle = getArguments();
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putString(KEY_DEFAULT_VALUE, value);
		setArguments(bundle);
	}

	private String getDefaultValue() {
		return getArguments().getString(KEY_DEFAULT_VALUE);
	}

	/**
	 * ダイアログのボタンを押したときの動作を指定するリスナー
	 */
	public static abstract class CommentEditDialogListener implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

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
