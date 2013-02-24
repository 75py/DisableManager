package com.nagopy.android.disablemanager.util.share;

import java.util.ArrayList;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.nagopy.android.disablemanager.R;
import com.nagopy.android.disablemanager.util.AppStatus;
import com.nagopy.android.disablemanager.util.AppStatusTest;

public class ShareUtilsTest extends AndroidTestCase {

	private ShareUtils mShareUtils;

	@Override
	@UsesMocks(Activity.class)
	protected void setUp() throws Exception {
		super.setUp();
		Activity activity = AndroidMock.createMock(Activity.class);
		AndroidMock.expect(activity.getApplicationContext()).andStubReturn(getContext());
		AndroidMock.makeThreadSafe(activity, true);
		AndroidMock.replay(activity);
		mShareUtils = new ShareUtils(activity);
	}

	public void test_isEmptyNull() throws Exception {
		assertTrue(mShareUtils.isEmpty(null));
	}

	public void test_isEmptyに空のリストでtrueが返る() throws Exception {
		assertTrue(mShareUtils.isEmpty(new ArrayList<AppStatus>()));
	}

	public void test_isEmptyでアイテムがあればfalseが返る() throws Exception {
		ArrayList<AppStatus> list = new ArrayList<AppStatus>();
		list.add(AppStatusTest.createMockAppStatus("label", "packagename", false, false, false));
		assertFalse(mShareUtils.isEmpty(list));
	}

	public void test_createShareString_アプリ名_改行挿入なし() throws Exception {
		改行なしに変更();
		String actual = mShareUtils.createShareString(R.id.menu_share_label, createTestList());
		String expected = "label_1\nlabel_2\n";
		assertEquals(expected, actual);
	}

	public void test_createShareString_アプリ名_改行挿入あり() throws Exception {
		改行ありに変更();
		String actual = mShareUtils.createShareString(R.id.menu_share_label, createTestList());
		String expected = "label_1\n\nlabel_2\n\n";
		assertEquals(expected, actual);
	}

	public void test_createShareString_パッケージ名_改行挿入なし() throws Exception {
		改行なしに変更();
		String actual = mShareUtils.createShareString(R.id.menu_share_package, createTestList());
		String expected = "packagename_1\npackagename_2\n";
		assertEquals(expected, actual);
	}

	public void test_createShareString_パッケージ名_改行挿入あり() throws Exception {
		改行ありに変更();
		String actual = mShareUtils.createShareString(R.id.menu_share_package, createTestList());
		String expected = "packagename_1\n\npackagename_2\n\n";
		assertEquals(expected, actual);
	}

	public void test_createShareString_アプリ名とパッケージ名_改行挿入なし() throws Exception {
		改行なしに変更();
		String actual = mShareUtils.createShareString(R.id.menu_share_label_and_package, createTestList());
		String expected = "label_1\npackagename_1\nlabel_2\npackagename_2\n";
		assertEquals(expected, actual);
	}

	public void test_createShareString_アプリ名とパッケージ名_改行挿入あり() throws Exception {
		改行ありに変更();
		String actual = mShareUtils.createShareString(R.id.menu_share_label_and_package, createTestList());
		String expected = "label_1\npackagename_1\n\nlabel_2\npackagename_2\n\n";
		assertEquals(expected, actual);
	}

	public void test_createShareString_カスタム_コメントなし_改行挿入なし() throws Exception {
		改行なしに変更();
		コメントなしカスタムフォーマットの変更("%1$s(%2$s)");
		String actual = mShareUtils.createShareString(R.id.menu_share_customformat, createTestList());
		String expected = "label_1(packagename_1)\nlabel_2(packagename_2)\n";
		assertEquals(expected, actual);
	}

	public void test_createShareString_カスタム_コメントなし_改行挿入あり() throws Exception {
		改行ありに変更();
		コメントなしカスタムフォーマットの変更("%1$s(%2$s)");

		String actual = mShareUtils.createShareString(R.id.menu_share_customformat, createTestList());
		String expected = "label_1(packagename_1)\n\nlabel_2(packagename_2)\n\n";
		assertEquals(expected, actual);
	}

	public void test_createShareString_typeが不正の場合() throws Exception {
		改行なしに変更();
		String expected = mShareUtils.createShareString(R.id.menu_share_label, createTestList());
		String actual = mShareUtils.createShareString(-75, createTestList());
		assertEquals(expected, actual);
	}

	/**
	 * @return テスト用のアプリリストを作成して返す
	 */
	private ArrayList<AppStatus> createTestList() {
		ArrayList<AppStatus> list = new ArrayList<AppStatus>();
		list.add(AppStatusTest.createMockAppStatus("label_1", "packagename_1", false, false, false));
		list.add(AppStatusTest.createMockAppStatus("label_2", "packagename_2", false, false, false));
		return list;
	}

	private void 改行ありに変更() {
		assertTrue(PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
				.putBoolean(getContext().getString(R.string.pref_key_share_add_linebreak), true).commit());
	}

	private void 改行なしに変更() {
		assertTrue(PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
				.putBoolean(getContext().getString(R.string.pref_key_share_add_linebreak), false).commit());
	}

	@SuppressWarnings("unused")
	private void コメント有カスタムフォーマットの変更(String format) {
		assertTrue(PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
				.putString(getContext().getString(R.string.pref_key_share_customformat_with_comment), format)
				.commit());
	}

	private void コメントなしカスタムフォーマットの変更(String format) {
		assertTrue(PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
				.putString(getContext().getString(R.string.pref_key_share_customformat_without_comment), format)
				.commit());
	}
}
