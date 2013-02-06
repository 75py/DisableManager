package com.nagopy.android.disabledapps.util.dialog;

import java.io.Serializable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nagopy.android.disabledapps.R;
import com.nagopy.android.disabledapps.util.FormatUtils;

/**
 * コメントのフォーマットを編集するためのダイアログ<br>
 * プレビュー機能も！
 */
public class FormatEditDialog extends DialogFragment {

	/**
	 * タイトルを保存するためのキー
	 */
	private static final String KEY_TITLE = "KEY_TITLE";

	/**
	 * サマリーを保存するためのキー
	 */
	private static final String KEY_SUMMARY = "KEY_SUMMARY";

	/**
	 * 元の値を保存するためのキー
	 */
	private static final String KEY_DEFAULT_VALUE = "KEY_DEFAULT_VALUE";

	/**
	 * リスナーを保存するためのキー
	 */
	private static final String KEY_LISTENER = "KEY_LISTENER";

	private FormatUtils mFormatUtils;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View rootView = View.inflate(getActivity().getApplicationContext(), R.layout.format_edit_dialog, null);
		builder.setTitle(getTitle()).setView(rootView);
		((TextView) rootView.findViewById(R.id.format_edit_dialog_summary_textview)).setText(getSummary());
		final TextView sampleTextView = (TextView) rootView
				.findViewById(R.id.format_edit_dialog_sample_textview);
		sampleTextView.setText(format(getDefaultValue()));
		final EditText editText = (EditText) rootView.findViewById(R.id.format_edit_dialog_editText);
		editText.setText(getDefaultValue());
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String formatString = format(s.toString());
				if (formatString == null) {
					sampleTextView.setText("format error");
				} else {
					// Log.d("debug", formatString + formatString);
					sampleTextView.setText(formatString + formatString);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void afterTextChanged(Editable s) {}
		});

		final FormatEditDialogListener listener = getListener();
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String text = editText.getText().toString();
				if (format(editText.getText().toString()) != null) {
					listener.onPositiveButtonClicked(dialog, text);
				} else {
					listener.onFormatError();
				}
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
	 * 初期設定
	 * @param title
	 *           タイトル
	 * @param summary
	 *           サマリー
	 * @param value
	 *           入力欄に入れとくやつ
	 * @param listener
	 *           リスナー
	 */
	public void init(String title, String summary, String value, FormatEditDialogListener listener) {
		setTitle(title);
		setSummary(summary);
		setListener(listener);
		setDefaultValue(value);
	}

	/**
	 * ラベルを設定する
	 * @param title
	 *           ラベル名
	 */
	public void setTitle(String title) {
		Bundle bundle = getArguments();
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putString(KEY_TITLE, title);
		setArguments(bundle);
	}

	/**
	 * 保存しておいたラベル名を取得する
	 * @return ラベル名
	 */
	private String getTitle() {
		return getArguments().getString(KEY_TITLE);
	}

	/**
	 * サマリーを設定する
	 * @param summary
	 *           サマリー
	 */
	public void setSummary(String summary) {
		Bundle bundle = getArguments();
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putString(KEY_SUMMARY, summary);
		setArguments(bundle);
	}

	/**
	 * 保存しておいたサマリーを取得する
	 * @return サマリー
	 */
	private String getSummary() {
		return getArguments().getString(KEY_SUMMARY);
	}

	/**
	 * リスナーを保存する
	 * @param listener
	 *           保存するリスナー
	 */
	public void setListener(FormatEditDialogListener listener) {
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
	private FormatEditDialogListener getListener() {
		return (FormatEditDialogListener) getArguments().getSerializable(KEY_LISTENER);
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
	public abstract static class FormatEditDialogListener implements Serializable {

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

		/**
		 * フォーマットにエラーがあった場合
		 */
		public abstract void onFormatError();
	}

	private String format(String format) {
		if (mFormatUtils == null) {
			mFormatUtils = new FormatUtils(getActivity().getApplicationContext());
		}
		return mFormatUtils.formatTest(format);
	}
}
