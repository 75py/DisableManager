package com.nagopy.android.disablemanager.util.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
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
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.nagopy.android.disablemanager.util.AppStatus;

public class XmlUtils {

	private static final String ELEMENT_ITEM = "item";

	private static final String ELEMENT_PACKAGES = "packages";

	private static final String ELEMENT_BUILD = "build";

	private static final String ELEMENT_DEVICE = "device";

	private static final String ELEMENT_TYPE = "type";

	private static final String ELEMENT_ROOT = "root";

	public static final int TYPE_DISABLED = 0x01;

	public static final int TYPE_HIDDEN = 0x02;

	private String path;

	/**
	 * コンストラクタ
	 * @param context
	 *           アプリケーションのコンテキスト
	 */
	public XmlUtils(Context context) {
		path = Environment.getExternalStorageDirectory().toString() + "/" + context.getPackageName() + "/";
	}

	/**
	 * アプリ一覧をXMLに書き出す
	 * @param apps
	 *           アプリ一覧
	 * @param elementLabel
	 *           パッケージ名の親ノード名
	 * @return 保存したファイル名。エラーがあった場合はnullを返す
	 */
	public String export(ArrayList<AppStatus> apps, int flags) {
		HashSet<String> appsString = new HashSet<String>(apps.size());
		for (AppStatus appStatus : apps) {
			appsString.add(appStatus.getPackageName());
		}
		return export(appsString, flags);
	}

	/**
	 * アプリ一覧をXMLに書き出す
	 * @param apps
	 *           パッケージ名の一覧
	 * @param elementLabel
	 *           パッケージ名の親ノード名
	 * @return 保存したファイル名。エラーがあった場合はnullを返す
	 */
	public String export(Collection<String> apps, int flags) {
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
			Document document = dbuilder.newDocument();
			Element root = document.createElement(ELEMENT_ROOT);

			Element type = document.createElement(ELEMENT_TYPE);
			type.appendChild(document.createTextNode(getTypeString(flags)));

			Element device = document.createElement(ELEMENT_DEVICE);
			device.appendChild(document.createTextNode(Build.MODEL));

			Element build = document.createElement(ELEMENT_BUILD);
			build.appendChild(document.createTextNode(Build.DISPLAY));

			Element packages = document.createElement(ELEMENT_PACKAGES);
			for (String pkgName : apps) {
				Element item = document.createElement(ELEMENT_ITEM);
				item.appendChild(document.createTextNode(pkgName));
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
			File file = new File(createFileName(getTypeString(flags)));
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

	private String getTypeString(int flags) {
		switch (flags) {
		case TYPE_DISABLED:
			return "disabled";
		case TYPE_HIDDEN:
			return "hidden";
		default:
			return String.valueOf(flags);
		}
	}

	/**
	 * 保存するファイルのパスを作成する
	 * @param elementLabel
	 *           パッケージ名の親ノード名
	 * @return ファイルパス
	 */
	private String createFileName(String elementLabel) {
		StringBuffer sb = new StringBuffer(path);
		sb.append(elementLabel);
		sb.append("_");
		sb.append(Build.MODEL.replace(" ", "_"));
		sb.append("_");
		sb.append(Build.DISPLAY.replace(" ", "_"));
		sb.append("_");
		sb.append(getTimeText());
		sb.append(".xml");

		Log.d("XmlPullParserSample", sb.toString());
		return sb.toString();
	}

	private String getTimeText() {
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

		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (hour < 10) {
			sb.append(0);
		}
		sb.append(hour);

		int min = cal.get(Calendar.MINUTE);
		if (min < 0) {
			sb.append(0);
		}
		sb.append(min);

		int sec = cal.get(Calendar.SECOND);
		if (sec < 10) {
			sb.append(0);
		}
		sb.append(sec);

		return sb.toString();
	}

	public XmlData importFromXml(String filePath) {
		XmlData data = new XmlData();

		StringBuffer xmlBuffer = new StringBuffer();
		try {
			BufferedReader fReader = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = fReader.readLine()) != null) {
				xmlBuffer.append(line.trim());
			}
		} catch (IOException e) {
			data.setErrorMessage("IOException: ファイルの読み込みでエラーが発生しました。\nファイル名：" + filePath);
			return data;
		}

		XmlPullParser xmlPullParser = Xml.newPullParser();
		try {
			Pattern pattern = Pattern.compile(">[ \t\n\f\r]*<");
			Matcher matcher = pattern.matcher(xmlBuffer);
			xmlPullParser.setInput(new StringReader(matcher.replaceAll("><")));
		} catch (XmlPullParserException e) {
			data.setErrorMessage("XmlPullParserException: XMLの書式が正しくありません");
			return data;
		}

		try {
			int eventType = xmlPullParser.getEventType();
			HashSet<String> packages = new HashSet<String>();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String tag = xmlPullParser.getName();
					if (ELEMENT_ITEM.equals(tag)) {
						packages.add(xmlPullParser.nextText());
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
			data.setPackages(packages);
		} catch (IOException e) {
			e.printStackTrace();
			data.setErrorMessage("IOException: XMLの読み込み中にエラーが発生しました。");
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			data.setErrorMessage("XmlPullParserException: XMLの読み込み中にエラーが発生しました。");
		}

		return data;
	}

	public static boolean isValidDevice(XmlData data) {
		return Build.MODEL.equals(data.getDevice());
	}

	public static boolean isValidBuild(XmlData data) {
		return Build.DISPLAY.equals(data.getBuild());
	}
}
