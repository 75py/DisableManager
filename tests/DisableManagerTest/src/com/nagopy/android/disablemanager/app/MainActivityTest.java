package com.nagopy.android.disablemanager.app;

import java.lang.reflect.Field;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.nagopy.android.disablemanager.R;
import com.nagopy.android.disablemanager.app.MainActivity;
import com.nagopy.android.disablemanager.util.filter.AppsFilter;

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
		tabTextsAndTags.put(_(R.string.menu_filter_hide), AppsFilter.HIDE_APPS);
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

	public void test表示アプリがないときのテキストビュー() throws Throwable {
		Activity activity = getActivity();
		try {
			Field mEmptyView = activity.getClass().getDeclaredField("mEmptyView");
			mEmptyView.setAccessible(true);
			assertNotNull(mEmptyView.get(activity));
			assertEquals(TextView.class, mEmptyView.get(activity).getClass());
			TextView textView = (TextView) mEmptyView.get(activity);
			assertEquals(activity.getText(R.string.message_empty_list), textView.getText());
		} catch (NoSuchFieldException e) {
			fail();
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
