package com.nagopy.android.disablemanager.test.util;

import com.nagopy.android.disablemanager.util.CommentsUtils;

import android.test.AndroidTestCase;

public class CommentsUtilsTest extends AndroidTestCase {

	private CommentsUtils mCommentsUtils;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mCommentsUtils = new CommentsUtils(getContext());
	}

	public void test保存・再読み込みできるか() throws Exception {
		String comment = "comment";
		String packageName = getContext().getPackageName();
		assertTrue("保存が成功したか", mCommentsUtils.saveComment(packageName, comment));

		assertEquals("先ほど保存したものと復元したものが同じ文字列かどうか", comment, mCommentsUtils.restoreComment(packageName));
	}

	public void testヌルを渡した場合() throws Exception {
		String comment = null;
		String packageName = getContext().getPackageName();
		assertTrue("保存が成功したか", mCommentsUtils.saveComment(packageName, comment));

		assertNull("データが削除されたか", mCommentsUtils.restoreComment(packageName));
	}

	public void test空文字を場合() throws Exception {
		String comment = "";
		String packageName = getContext().getPackageName();
		assertTrue("保存が成功したか", mCommentsUtils.saveComment(packageName, comment));

		assertNull("データが削除されたか", mCommentsUtils.restoreComment(packageName));
	}
}
