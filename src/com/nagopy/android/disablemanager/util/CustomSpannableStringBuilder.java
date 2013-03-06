package com.nagopy.android.disablemanager.util;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;

import com.nagopy.android.disablemanager.R;

/**
 * 一覧画面のリストで、コメント・ステータス・パッケージ名を一つのTextViewで表示するために見た目を弄ってうまいことやるためのクラス
 */
public class CustomSpannableStringBuilder {

	/**
	 * プロセス表示で使うやつ
	 */
	private TextAppearanceSpan mTextAppearanceSpanProcess;

	/**
	 * コメント表示で使うやつ
	 */
	private TextAppearanceSpan mTextAppearanceSpanComment;

	/**
	 * 変更日時表示で使うやつ
	 */
	private TextAppearanceSpan mTextAppearanceSpanDate;

	/**
	 * @see ChangedDateUtils
	 */
	private ChangedDateUtils mDateUtils;

	/**
	 * アプリケーションのコンテキスト
	 */
	private Context mContext;

	/**
	 * コンストラクタ
	 * @param context
	 *           アプリケーションのコンテキスト
	 */
	public CustomSpannableStringBuilder(Context context) {
		mContext = context;
		mDateUtils = new ChangedDateUtils(context);
		mTextAppearanceSpanProcess = new TextAppearanceSpan(context, R.style.TextAppearance_ProcessStatus);
		mTextAppearanceSpanComment = new TextAppearanceSpan(context, R.style.TextAppearance_Comment);
		mTextAppearanceSpanDate = new TextAppearanceSpan(context, R.style.TextAppearance_Date);
	}

	/**
	 * ラベルを取得する
	 * @param packageName
	 *           パッケージ名
	 * @param comment
	 *           コメント<br>
	 *           ない場合はnull
	 * @param status
	 *           ステータス
	 * @return うまいことやった結果。setTextでおｋ
	 */
	public CharSequence getLabelText(String packageName, String comment, int status) {
		String statusText = getStatusText(status);
		String changedDate = null;
		if (PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(
				mContext.getString(R.string.pref_key_general_show_changed_date),
				mContext.getResources().getBoolean(R.bool.pref_def_general_show_changed_date))) {
			changedDate = mDateUtils.getString(packageName);
		}
		if (statusText == null && comment == null && changedDate == null) {
			return packageName;
		}

		SpannableStringBuilder mBuilder = new SpannableStringBuilder();
		if (comment != null) {
			int start = mBuilder.length();
			mBuilder.append(comment);
			int end = mBuilder.length();
			mBuilder.setSpan(mTextAppearanceSpanComment, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if (statusText != null) {
			if (mBuilder.length() > 0) {
				mBuilder.append("\n");
			}
			int start = mBuilder.length();
			mBuilder.append(statusText);
			int end = mBuilder.length();
			mBuilder.setSpan(mTextAppearanceSpanProcess, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if (changedDate != null) {
			if (mBuilder.length() > 0) {
				mBuilder.append("\n");
			}
			int start = mBuilder.length();
			mBuilder.append(changedDate);
			int end = mBuilder.length();
			mBuilder.setSpan(mTextAppearanceSpanDate, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		mBuilder.append("\n");
		mBuilder.append(packageName);

		return mBuilder;
	}

	/**
	 * ステータスのintをもとに文字列に変換
	 * @param status
	 *           {@link AppStatus#getRunningStatus()}
	 * @return 文字列（Foregroundとか）
	 */
	private String getStatusText(int status) {
		String packageStatusText;
		switch (status) {
		case AppStatus.NULL_STATUS:
			packageStatusText = null;
			break;
		case RunningAppProcessInfo.IMPORTANCE_BACKGROUND:
			packageStatusText = "Background";
			break;
		case RunningAppProcessInfo.IMPORTANCE_FOREGROUND:
			packageStatusText = "Foreground";
			break;
		case RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE:
			packageStatusText = "Perceptible";
			break;
		case RunningAppProcessInfo.IMPORTANCE_SERVICE:
			packageStatusText = "Service";
			break;
		case RunningAppProcessInfo.IMPORTANCE_VISIBLE:
			packageStatusText = "Visible";
			break;
		case RunningAppProcessInfo.IMPORTANCE_EMPTY:
			packageStatusText = "Empty";
			break;
		default:
			packageStatusText = null;
			break;
		}
		return packageStatusText;
	}
}
