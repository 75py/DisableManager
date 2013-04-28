package com.nagopy.android.disablemanager.util.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.nagopy.android.disablemanager.core.R;

public class XmlUtilsTest extends AndroidTestCase {

	private XmlUtils mXmlUtils;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mXmlUtils = new XmlUtils(getContext());
	}

	public void testファイル読み込み_ファイルがない場合() throws Exception {
		XmlData xmlData = new XmlData();
		xmlData.setFilePath("a");
		String text = mXmlUtils.readXmlFile(xmlData);
		assertNull(text);
		assertEquals(getContext().getString(R.string.import_error_not_found, xmlData.getFilePath()),
				xmlData.getErrorMessage());
	}

	public void test読み込み_除外リスト() throws Exception {
		copyAssets();
		XmlData xmlData = mXmlUtils.importFromXml(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/" + "hidden.xml");
		assertNotNull(xmlData);
		assertEquals("Nexus 7", xmlData.getDevice());
		assertEquals("JDQ39", xmlData.getBuild());
		assertNull(xmlData.getErrorMessage());
		assertEquals("hidden", xmlData.getType());
		assertTrue(xmlData.getPackages().contains("android"));
		assertEquals(3, xmlData.getPackages().size());
		assertEquals("こめんと\n改行なう\n\n", xmlData.getComments().get("com.android.bluetooth"));
	}

	public void test読み込み_disable_xml() throws Exception {
		copyAssets();
		XmlData xmlData = mXmlUtils.importFromXml(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/" + "disable.xml");
		assertNotNull(xmlData);
		assertEquals("Nexus 7", xmlData.getDevice());
		assertEquals("JDQ39", xmlData.getBuild());
		assertNull(xmlData.getErrorMessage());
		assertEquals("disabled", xmlData.getType());
		assertTrue(xmlData.getPackages().contains("com.google.android.email"));
		assertEquals(6, xmlData.getPackages().size());
		assertEquals(xmlData.getPackages(), xmlData.getComments().keySet());
	}

	public void test読み込み_over10kb() throws Exception {
		copyAssets();
		XmlData xmlData = mXmlUtils.importFromXml(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/" + "disable_over_10kb.xml");
		assertNotNull(xmlData);
		assertEquals(getContext().getString(R.string.import_error_too_large), xmlData.getErrorMessage());
	}

	public void test読み込み_invalid() throws Exception {
		copyAssets();
		XmlData xmlData = mXmlUtils.importFromXml(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/" + "disable_invalid.xml");
		assertNotNull(xmlData);
		assertEquals(getContext().getString(R.string.xml_error_parser, "XmlPullParserException"),
				xmlData.getErrorMessage());
	}

	@UsesMocks(XmlData.class)
	public void testIsValidBuild() throws Exception {
		XmlData xmlDataMock = AndroidMock.createMock(XmlData.class);
		AndroidMock.expect(xmlDataMock.getDevice()).andStubReturn(Build.MODEL);
		AndroidMock.expect(xmlDataMock.getBuild()).andStubReturn(Build.DISPLAY);
		AndroidMock.replay(xmlDataMock);
		assertTrue(XmlUtils.isValidDevice(xmlDataMock));
		assertTrue(XmlUtils.isValidBuild(xmlDataMock));
	}

	public void testCreateFileName() throws Exception {
		String type = "type";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_hhmmss");
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
				+ getContext().getPackageName() + "/" + type + "_" + Build.MODEL.replace(" ", "_") + "_"
				+ Build.DISPLAY.replace(" ", "_") + "_" + format.format(new Date()) + ".xml";
		assertEquals(path, mXmlUtils.createFileName(type));
	}

	public void test時刻の文字列化() throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_hhmmss");
		assertEquals(format.format(new Date()), mXmlUtils.getTimeText());
		// assertEquals(format.format(new Date()), mXmlUtils.getTimeText(),
		// getContext().getResources()
		// .getConfiguration().locale);
	}

	public void testエクスポート() throws Exception {
		String type = "type";
		ArrayList<String> apps = new ArrayList<String>();
		apps.add("a.a");
		apps.add("b.b");
		String filepath = mXmlUtils.export(apps, type);
		assertNotNull(filepath);
		File file = new File(filepath);
		assertTrue(file.exists());
		try {
			FileReader f = new FileReader(file);
			BufferedReader b = new BufferedReader(f);
			String s;
			StringBuffer sb = new StringBuffer();
			while ((s = b.readLine()) != null) {
				sb.append(s);
			}
			String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><device>"
					+ Build.MODEL
					+ "</device><build>"
					+ Build.DISPLAY
					+ "</build><type>"
					+ type
					+ "</type><packages><item package_name=\"a.a\"/><item package_name=\"b.b\"/></packages></root>";
			assertEquals(expected, sb.toString());
		} catch (Exception e) {
			fail();
		}
	}

	/**
	 * http://stackoverflow.com/questions/4447477/android-how-to-copy-files-in-assets-to-sdcard
	 */
	private void copyAssets() {
		AssetManager assetManager = getTestProjectContext().getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("tag", "Failed to get asset file list.", e);
		}
		for (String filename : files) {
			Log.d("tag", filename);
			InputStream in = null;
			OutputStream out = null;
			try {
				in = assetManager.open(filename);
				out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
						+ filename);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (IOException e) {
				Log.e("tag", "Failed to copy asset file: " + filename, e);
			}
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	/**
	 * http://d.hatena.ne.jp/murakaming/20120727/1343391401
	 * @return テストプロジェクトのコンテキスト
	 */
	public Context getTestProjectContext() {
		try {
			Class<? extends AndroidTestCase> clz = getClass();
			Method method = clz.getMethod("getTestContext");
			return (Context) method.invoke(this);
		} catch (Exception e) {
			return null;
		}
	}
}
