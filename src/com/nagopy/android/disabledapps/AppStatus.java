package com.nagopy.android.disabledapps;

/**
 * アプリの情報を保持するクラス
 */
public class AppStatus {

	/**
	 * ラベル名
	 */
	private String label;

	/**
	 * パッケージ名
	 */
	private String packageName;

	/**
	 * 有効かどうか
	 */
	private boolean enabled;

	/**
	 * システムアプリかどうか
	 */
	private boolean system;

	/**
	 * 無効化が可能かどうか
	 */
	private boolean canDisable;

	/**
	 * コメント
	 */
	private String comment;

	/**
	 * コンストラクタ
	 * @param label
	 *           ラベル名
	 * @param packageName
	 *           パッケージ名
	 * @param enabled
	 *           有効かどうか
	 * @param system
	 *           システムアプリかどうか
	 * @param canDisable
	 *           無効化できるかどうか
	 */
	public AppStatus(String label, String packageName, boolean enabled, boolean system, boolean canDisable) {
		this.label = label;
		this.packageName = packageName;
		this.enabled = enabled;
		this.system = system;
		this.canDisable = canDisable;
	}

	/**
	 * @return ラベル名
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return パッケージ名
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @return 有効ならtrue、そうでなければfalse
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @return システムアプリならtrue、そうでなければfalse
	 */
	public boolean isSystem() {
		return system;
	}

	/**
	 * @return 無効化できるアプリならtrue、そうでなければfalse
	 */
	public boolean canDisable() {
		return canDisable;
	}

	@Override
	public String toString() {
		return getLabel() + ":" + getPackageName() + ", enabled:" + isEnabled() + ", system:" + isSystem()
				+ ", canDisable:" + canDisable();
	}

	/**
	 * @return コメントを取得する<br>
	 *         コメントがない場合はnull
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * コメントを設定
	 * @param comment
	 *           追加するコメント
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
}
