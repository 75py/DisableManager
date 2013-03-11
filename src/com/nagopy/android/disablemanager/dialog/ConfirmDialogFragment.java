package com.nagopy.android.disablemanager.dialog;

import java.io.Serializable;

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

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new Builder(getActivity());
		TextView textView = new TextView(getActivity().getApplicationContext());
		textView.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
		textView.setPadding(10, 10, 10, 10); // CHECKSTYLE IGNORE THIS LINE
		Bundle args = getArguments();
		textView.setText(args.getCharSequence("message"));
		builder.setView(textView);
		builder.setPositiveButton(android.R.string.ok,
				(android.content.DialogInterface.OnClickListener) args.get("positiveButtonListner"));
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.setTitle("Warning");
		return builder.create();
	}

	/**
	 * 色々初期設定
	 * @param message
	 *           メッセージ
	 * @param positiveListener
	 *           OKを押されたときのリスナー
	 */
	public void init(CharSequence message, AlertDialogListener positiveListener) {
		Bundle args = new Bundle();
		args.putCharSequence("message", message);
		args.putSerializable("positiveButtonListner", positiveListener);
		setArguments(args);
	}

	/**
	 * シリアライズ可能なDialogInterface.OnClickListener
	 */
	public static interface AlertDialogListener extends DialogInterface.OnClickListener, Serializable {
	}
}
