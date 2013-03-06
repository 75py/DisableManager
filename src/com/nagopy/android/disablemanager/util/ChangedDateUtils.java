package com.nagopy.android.disablemanager.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 無効化した・有効に戻したときの日時を管理するクラス
 */
public class ChangedDateUtils {

	private Context mContext;

	private SharedPreferences mSharedPreferences;

	public ChangedDateUtils(Context context) {
		mContext = context;
		mSharedPreferences = context.getSharedPreferences("date", Context.MODE_PRIVATE);
	}

	/**
	 * パッケージ名をキーに、変更時間を保存する
	 * @param packageName
	 *           パッケージ名
	 * @param time
	 *           時刻（ms）
	 * @return 保存が成功すればtrueを返す
	 */
	public boolean put(String packageName, long time) {
		return mSharedPreferences.edit().putLong(packageName, time).commit();
	}

	/**
	 * 変更時間を取得
	 * @param packageName
	 *           パッケージ名
	 * @return 最後に変更された時刻をミリ秒で返す。保存されていない場合は0を返す。
	 */
	public long get(String packageName) {
		return mSharedPreferences.getLong(packageName, 0);
	}

	/**
	 * 変更時刻を文字列で取得
	 * @param packageName
	 *           パッケージ名
	 * @return 変更時刻の文字列（年月日と時刻）。保存されていない場合はnullを返す
	 */
	public String getString(String packageName) {
		long ms = get(packageName);
		if (ms > 0) {
			return android.text.format.DateUtils.formatDateTime(mContext, ms,
					android.text.format.DateUtils.FORMAT_SHOW_YEAR
							| android.text.format.DateUtils.FORMAT_SHOW_DATE
							| android.text.format.DateUtils.FORMAT_SHOW_TIME);
		} else {
			return null;
		}
	}
}
