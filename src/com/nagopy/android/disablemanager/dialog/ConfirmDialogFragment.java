package com.nagopy.android.disablemanager.dialog;

import java.io.Serializable;

import com.nagopy.android.disablemanager.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 確認ダイアログ
 */
public class ConfirmDialogFragment extends DialogFragment {

	private static final String KEY_LISTNER = "positiveButtonListner";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new Builder(getActivity());
		TextView textView = new TextView(getActivity().getApplicationContext());
		textView.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
		textView.setPadding(10, 10, 10, 10); // CHECKSTYLE IGNORE THIS LINE
		Bundle args = getArguments();
		textView.setText(args.getCharSequence("message"));
		builder.setView(textView);
		ConfirmDialogListener listener = getListener();
		builder.setPositiveButton(android.R.string.ok, listener);
		builder.setNegativeButton(android.R.string.cancel, listener);
		builder.setTitle(R.string.confirm_dialog_title);
		return builder.create();
	}

	/**
	 * 色々初期設定
	 * @param message
	 *           メッセージ
	 * @param positiveListener
	 *           OKを押されたときのリスナー
	 */
	public void init(CharSequence message, ConfirmDialogListener positiveListener) {
		Bundle args = new Bundle();
		args.putCharSequence("message", message);
		args.putSerializable(KEY_LISTNER, positiveListener);
		setArguments(args);
	}

	/**
	 * 保存しておいたリスナーを取得する
	 * @return リスナー
	 */
	private ConfirmDialogListener getListener() {
		return (ConfirmDialogListener) getArguments().get(KEY_LISTNER);
	}

	/**
	 * シリアライズ可能なDialogInterface.OnClickListener
	 */
	public static interface ConfirmDialogListener extends DialogInterface.OnClickListener, Serializable {

		/**
		 * whichの値によってどのボタンが押されたかを判定するとGood
		 * <ul>
		 * <li>{@link DialogInterface#BUTTON_POSITIVE}</li>
		 * <li>{@link DialogInterface#BUTTON_NEUTRAL}</li>
		 * <li>{@link DialogInterface#BUTTON_NEGATIVE}</li>
		 * </ul>
		 */
		@Override
		public abstract void onClick(DialogInterface dialog, int which);
	}
}
