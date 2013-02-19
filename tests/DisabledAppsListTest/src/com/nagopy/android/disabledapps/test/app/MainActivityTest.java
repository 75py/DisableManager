package com.nagopy.android.disabledapps.test.app;

import java.util.HashMap;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.nagopy.android.disabledapps.R;
import com.nagopy.android.disabledapps.app.MainActivity;
import com.nagopy.android.disabledapps.util.filter.AppsFilter;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private Activity mActivity;
	private ActionBar mActionBar;

	public MainActivityTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
		mActionBar = mActivity.getActionBar();
	}

	public void testActionBar() {
		assertEquals("タブ表示になっているか", ActionBar.NAVIGATION_MODE_TABS, mActionBar.getNavigationMode());

		// テキストがちゃんと表示できているか
		HashMap<CharSequence, Integer> tabTextsAndTags = new HashMap<CharSequence, Integer>();
		tabTextsAndTags.put(_(R.string.menu_filter_disablable_and_enabled_apps),
				AppsFilter.DISABLABLE_AND_ENABLED_SYSTEM);
		tabTextsAndTags.put(_(R.string.menu_filter_disabled), AppsFilter.DISABLED);
		tabTextsAndTags.put(_(R.string.menu_filter_undisablable_system), AppsFilter.UNDISABLABLE_SYSTEM);
		tabTextsAndTags.put(_(R.string.menu_filter_user_apps), AppsFilter.USER_APPS);
		int tabcount = mActionBar.getTabCount();
		for (int i = 0; i < tabcount; i++) {
			Tab tab = mActionBar.getTabAt(i);
			CharSequence text = tab.getText();
			Object tag = tab.getTag();
			assertTrue("タブのテキストが予定のものの一覧に含まれているか", tabTextsAndTags.containsKey(text));
			assertTrue("タブのタグが予定のものの一覧に含まれているか", tabTextsAndTags.containsValue(tag));
			assertTrue("文字とタグの組み合わせが一致しているか", tabTextsAndTags.get(text).equals(tag));
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		getInstrumentation().onDestroy();
	}


	private CharSequence _(int resId) {
		return mActivity.getText(resId);
	}

}
