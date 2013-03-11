package com.nagopy.android.disablemanager.util.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * XMLファイルから読みこんだ内容を保持するためのクラス
 */
public class XmlData {

	/**
	 * 端末名
	 */
	private String device;

	/**
	 * ビルド番号
	 */
	private String build;

	/**
	 * 出力の種類
	 */
	private String type;

	/**
	 * パッケージ名・コメントを保持するHashMap
	 */
	private HashMap<String, String> packageAndComment = new HashMap<String, String>();

	/**
	 * エラーメッセージ
	 */
	private String errorMessage;

	/**
	 * @return 端末名
	 */
	public String getDevice() {
		return device;
	}

	/**
	 * @param device
	 *           端末名
	 */
	public void setDevice(String device) {
		this.device = device;
	}

	/**
	 * @return ビルド番号
	 */
	public String getBuild() {
		return build;
	}

	/**
	 * @param build
	 *           ビルド番号
	 */
	public void setBuild(String build) {
		this.build = build;
	}

	/**
	 * @return ファイルの種類
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *           ファイルの種類
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return パッケージ名の一覧
	 */
	public Set<String> getPackages() {
		return packageAndComment.keySet();
	}

	/**
	 * @return パッケージ名とコメントのHashMap
	 */
	public Map<String, String> getComments() {
		return packageAndComment;
	}

	/**
	 * パッケージ名とコメントのHashMapをセットする
	 * @param map
	 *           パッケージ名とコメントのHashMap
	 */
	public void setPackagesAndComments(HashMap<String, String> map) {
		this.packageAndComment = map;
	}

	/**
	 * @return エラーメッセージ
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage
	 *           エラーメッセージ
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Device:");
		sb.append(getDevice());
		sb.append("\nBuild:");
		sb.append(getBuild());
		sb.append("\nType:");
		sb.append(getType());
		sb.append("\nPackages:");
		sb.append(getPackages());
		sb.append("\nErrorMessage:");
		sb.append(getErrorMessage());
		return sb.toString();
	}
}
