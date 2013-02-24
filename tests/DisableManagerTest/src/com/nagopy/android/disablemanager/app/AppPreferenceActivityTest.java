package com.nagopy.android.disablemanager.app;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

public class AppPreferenceActivityTest extends ActivityInstrumentationTestCase2<AppPreferenceActivity> {

	public AppPreferenceActivityTest() {
		super(AppPreferenceActivity.class);
	}

	public void test子ビューの数() throws Exception {
		ListView listView = getActivity().getListView();
		assertEquals(3, listView.getChildCount());
	}

	/**
	 * 目視ｗ
	 */
	public void testGeneralタップ() throws Exception {
		ListView listView = getActivity().getListView();
		View view = listView.getChildAt(0);
		TouchUtils.clickView(this, view);
		sendKeys(KeyEvent.KEYCODE_BACK);
	}

	public void testShareタップ() throws Exception {
		ListView listView = getActivity().getListView();
		View view = listView.getChildAt(1);
		TouchUtils.clickView(this, view);

		ListView l = getActivity().getListView();
		int child = l.getChildCount();
		assertEquals(3, child);
		sendKeys(KeyEvent.KEYCODE_BACK);
	}

	public void testAboutタップ() throws Exception {
		ListView listView = getActivity().getListView();
		View view = listView.getChildAt(2);
		TouchUtils.clickView(this, view);
		sendKeys(KeyEvent.KEYCODE_BACK);
	}
}
