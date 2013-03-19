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

package com.nagopy.android.disablemanager.util.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Xml;

import com.nagopy.android.disablemanager.R;
import com.nagopy.android.disablemanager.util.AppStatus;
import com.nagopy.android.disablemanager.util.CommentsUtils;

/**
 * XMLの読み書きに関するクラス
 */
public class XmlUtils {

	/**
	 * 各パッケージの情報
	 */
	private static final String ELEMENT_ITEM = "item";

	/**
	 * パッケージ
	 */
	private static final String ELEMENT_PACKAGES = "packages";

	/**
	 * ビルド番号
	 */
	private static final String ELEMENT_BUILD = "build";

	/**
	 * 端末名
	 */
	private static final String ELEMENT_DEVICE = "device";

	/**
	 * 種類
	 */
	private static final String ELEMENT_TYPE = "type";

	/**
	 * root要素
	 */
	private static final String ELEMENT_ROOT = "root";

	/**
	 * 無効化済みのエクスポート
	 */
	public static final String TYPE_DISABLED = "disabled";

	/**
	 * 除外アプリのエクスポート
	 */
	public static final String TYPE_HIDDEN = "hidden";

	/**
	 * 保存するディレクトリのパス
	 */
	private String path;

	/**
	 * アプリケーションのコンテキスト
	 */
	private Context mContext;

	/**
	 * コンストラクタ
	 * @param context
	 *           アプリケーションのコンテキスト
	 */
	public XmlUtils(Context context) {
		mContext = context;
		path = Environment.getExternalStorageDirectory().toString() + "/" + context.getPackageName() + "/";
	}

	/**
	 * アプリ一覧をXMLに書き出す
	 * @param apps
	 *           アプリ一覧
	 * @param exportType
	 *           タイプ
	 * @return 保存したファイル名。エラーがあった場合はnullを返す
	 */
	public String export(ArrayList<AppStatus> apps, String exportType) {
		HashSet<String> appsString = new HashSet<String>(apps.size());
		for (AppStatus appStatus : apps) {
			appsString.add(appStatus.getPackageName());
		}
		return export(appsString, exportType);
	}

	/**
	 * アプリ一覧をXMLに書き出す
	 * @param apps
	 *           パッケージ名の一覧
	 * @param exportType
	 *           タイプ
	 * @return 保存したファイル名。エラーがあった場合はnullを返す
	 */
	public String export(Collection<String> apps, String exportType) {
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
			Document document = dbuilder.newDocument();
			Element root = document.createElement(ELEMENT_ROOT);

			Element type = document.createElement(ELEMENT_TYPE);
			type.appendChild(document.createTextNode(exportType));

			Element device = document.createElement(ELEMENT_DEVICE);
			device.appendChild(document.createTextNode(Build.MODEL));

			Element build = document.createElement(ELEMENT_BUILD);
			build.appendChild(document.createTextNode(Build.DISPLAY));

			Element packages = document.createElement(ELEMENT_PACKAGES);
			CommentsUtils commentsUtils = new CommentsUtils(mContext);
			for (String pkgName : apps) {
				Element item = document.createElement(ELEMENT_ITEM);
				item.setAttribute("package_name", pkgName);
				String comment = commentsUtils.restoreComment(pkgName);
				if (comment != null) {
					item.setAttribute("comment", comment);
				}
				packages.appendChild(item);
			}

			root.appendChild(device);
			root.appendChild(build);
			root.appendChild(type);
			root.appendChild(packages);
			document.appendChild(root);

			TransformerFactory tffactory = TransformerFactory.newInstance();
			Transformer transformer = tffactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdir();
			}
			File file = new File(createFileName(exportType));
			if (!file.exists()) {
				file.createNewFile();
			}
			transformer.transform(new DOMSource(document), new StreamResult(file));

			return file.getCanonicalPath();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (TransformerException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 保存するファイルのパスを作成する
	 * @param elementLabel
	 *           パッケージ名の親ノード名
	 * @return ファイルパス
	 */
	String createFileName(String elementLabel) {
		StringBuffer sb = new StringBuffer(path);
		sb.append(elementLabel);
		sb.append("_");
		sb.append(Build.MODEL.replace(" ", "_"));
		sb.append("_");
		sb.append(Build.DISPLAY.replace(" ", "_"));
		sb.append("_");
		sb.append(getTimeText());
		sb.append(".xml");
		return sb.toString();
	}

	/**
	 * @return 日時をうまいこと文字列にしたやつ
	 */
	String getTimeText() {
		// CHECKSTYLE:OFF
		Calendar cal = Calendar.getInstance();
		StringBuffer sb = new StringBuffer();

		sb.append(cal.get(Calendar.YEAR));
		int month = cal.get(Calendar.MONTH) + 1;
		if (month < 10) {
			sb.append(0);
		}
		sb.append(month);

		int day = cal.get(Calendar.DAY_OF_MONTH);
		if (day < 10) {
			sb.append(0);
		}
		sb.append(day);

		sb.append("_");

		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (hour < 10) {
			sb.append(0);
		}
		sb.append(hour);

		int min = cal.get(Calendar.MINUTE);
		if (min < 10) {
			sb.append(0);
		}
		sb.append(min);

		int sec = cal.get(Calendar.SECOND);
		if (sec < 10) {
			sb.append(0);
		}
		sb.append(sec);

		return sb.toString();
		// CHECKSTYLE:ON
	}

	/**
	 * XMLファイルを読み込む
	 * @param xmlData
	 *           保存するインスタンス。ファイルパス指定済みのもので
	 * @return 読み込んだテキストをそのまま返す。エラーがあった場合はnullを返す（xmlDataにエラーメッセージをセットする）。
	 */
	String readXmlFile(XmlData xmlData) {
		File file = new File(xmlData.getFilePath());
		if (!file.exists()) {
			xmlData.setErrorMessage(mContext.getString(R.string.import_error_not_found, xmlData.getFilePath()));
			return null;
		} else if (file.length() > 10240) {
			xmlData.setErrorMessage(mContext.getString(R.string.import_error_too_large));
			return null;
		} else {
			try {
				StringBuffer sb = new StringBuffer();
				BufferedReader fReader = new BufferedReader(new FileReader(xmlData.getFilePath()));
				String line;
				while ((line = fReader.readLine()) != null) {
					sb.append(line.trim());
				}

				return sb.toString();
			} catch (IOException e) {
				// ファイルが読み込めなかった場合
				xmlData
						.setErrorMessage(mContext.getString(R.string.xml_error_cannot_open, xmlData.getFilePath()));
				return null;
			}
		}
	}

	/**
	 * XMLからデータを読み込む
	 * @param filePath
	 *           ファイルの絶対パス
	 * @return 読み込んだデータ
	 */
	public XmlData importFromXml(String filePath) {
		XmlData data = new XmlData();
		data.setFilePath(filePath);

		String xmlText = readXmlFile(data);
		if (xmlText == null) {
			return data;
		}

		XmlPullParser xmlPullParser = Xml.newPullParser();
		try {
			Pattern pattern = Pattern.compile(">[ \t\n\f\r]*<");
			Matcher matcher = pattern.matcher(xmlText);
			xmlPullParser.setInput(new StringReader(matcher.replaceAll("><")));
		} catch (XmlPullParserException e) {
			data.setErrorMessage(mContext.getString(R.string.xml_error_invalid_xml));
			return data;
		}

		try {
			int eventType = xmlPullParser.getEventType();
			HashMap<String, String> map = new HashMap<String, String>();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) { // CHECKSTYLE IGNORE THIS LINE
				case XmlPullParser.START_TAG:
					String tag = xmlPullParser.getName();
					if (ELEMENT_ITEM.equals(tag)) {
						map.put(xmlPullParser.getAttributeValue(null, "package_name"),
								xmlPullParser.getAttributeValue(null, "comment"));
					} else if (ELEMENT_DEVICE.equals(tag)) {
						data.setDevice(xmlPullParser.nextText());
					} else if (ELEMENT_BUILD.equals(tag)) {
						data.setBuild(xmlPullParser.nextText());
					} else if (ELEMENT_TYPE.equals(tag)) {
						data.setType(xmlPullParser.nextText());
					}
					break;
				}
				eventType = xmlPullParser.next();
			}
			data.setPackagesAndComments(map);
		} catch (IOException e) {
			e.printStackTrace();
			data.setErrorMessage(mContext.getString(R.string.xml_error_parser, "IOException"));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			data.setErrorMessage(mContext.getString(R.string.xml_error_parser, "XmlPullParserException"));
		}

		return data;
	}

	/**
	 * デバイス名が読み込んだものと同一かどうかを判定する
	 * @param data
	 *           読み込んだXMLデータ
	 * @return デバイス名が等しければtrueを返す
	 */
	public static boolean isValidDevice(XmlData data) {
		return Build.MODEL.equals(data.getDevice());
	}

	/**
	 * ビルド番号が使用端末と同じものかを判定する
	 * @param data
	 *           読み込んだXMLのデータ
	 * @return ビルド番号が等しければtrueを返す
	 */
	public static boolean isValidBuild(XmlData data) {
		return Build.DISPLAY.equals(data.getBuild());
	}

	/**
	 * 無効化したアプリの一覧をエクスポートする
	 * @return 保存したファイルの絶対パス。失敗していたらnullを返す
	 */
	public String exportDisabledApps() {
		PackageManager packageManager = mContext.getPackageManager();
		List<ApplicationInfo> applicationInfo = packageManager
				.getInstalledApplications(PackageManager.GET_META_DATA);

		ArrayList<String> disabledApps = new ArrayList<String>();
		for (ApplicationInfo info : applicationInfo) {
			if (!info.enabled) {
				disabledApps.add(info.packageName);
			}
		}

		return export(disabledApps, TYPE_DISABLED);
	}
}
