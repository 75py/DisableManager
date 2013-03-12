package com.nagopy.android.disablemanager.util.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.test.AndroidTestCase;
import android.util.Xml;

import com.nagopy.android.disablemanager.R;

public class XmlUtilsTest extends AndroidTestCase {

	private XmlUtils mXmlUtils;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mXmlUtils = new XmlUtils(getContext());
	}

	public void testValidXml() throws Exception {
		XmlData xmlData = readTest("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><device>devicename</device><build>buildnumber</build><type>hidden</type><packages><item package_name=\"a.a\" comment=\"com\" /></packages></root>");
		assertNull(xmlData.getErrorMessage());
		assertEquals("devicename", xmlData.getDevice());
		assertEquals("buildnumber", xmlData.getBuild());
		assertEquals("hidden", xmlData.getType());
	}

	private XmlData readTest(String xmlText) {
		XmlPullParser xmlPullParser = Xml.newPullParser();
		XmlData data = new XmlData();
		try {
			xmlPullParser.setInput(new StringReader(xmlText));
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
}
