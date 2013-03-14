package com.nagopy.android.disablemanager.util.xml;

import java.util.HashMap;

import android.test.AndroidTestCase;

public class XmlDataTest extends AndroidTestCase {

	private XmlData mXmlData;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mXmlData = new XmlData();
	}

	public void testDevice() throws Exception {
		String text = "aaa";
		mXmlData.setDevice(text);
		assertEquals(text, mXmlData.getDevice());
	}
	public void testBuild() throws Exception {
		String text = "aaa";
		mXmlData.setBuild(text);
		assertEquals(text, mXmlData.getBuild());
	}
	public void testType() throws Exception {
		String text = "aaa";
		mXmlData.setType(text);
		assertEquals(text, mXmlData.getType());
	}
	public void testErrorMessage() throws Exception {
		String text = "aaa";
		mXmlData.setErrorMessage(text);
		assertEquals(text, mXmlData.getErrorMessage());
	}
	public void testFilePath() throws Exception {
		String text = "aaa";
		mXmlData.setFilePath(text);
		assertEquals(text, mXmlData.getFilePath());
	}
	public void testMap() throws Exception {
		HashMap<String, String> map = new HashMap<String, String>();
		mXmlData.setPackagesAndComments(map);
		assertEquals(map, mXmlData.getComments());
		assertEquals(map.keySet(), mXmlData.getPackages());
	}
}
