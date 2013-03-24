package com.nagopy.android.disablemanager.util;

import android.test.AndroidTestCase;

public class CustomSpannableStringBuilderTest extends AndroidTestCase {

	private CustomSpannableStringBuilder mCustomSpannableStringBuilder;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mCustomSpannableStringBuilder = new CustomSpannableStringBuilder(getContext());
	}

	public void testコメントなし・ステータスなし() throws Exception {
		CharSequence expected = "package";
		CharSequence actual = mCustomSpannableStringBuilder.getLabelText("package", null, null);
		assertEquals(expected, actual);
	}

	public void testコメントあり・ステータスなし() throws Exception {
		String expected = "comment\npackage";
		String actual = mCustomSpannableStringBuilder.getLabelText("package", "comment", null).toString();
		assertEquals(expected, actual);
	}

	public void testコメントあり・ステータスあり() throws Exception {
		String expected = "comment\nprocess\npackage";
		String actual = mCustomSpannableStringBuilder.getLabelText("package", "comment", "process").toString();
		assertEquals(expected, actual);
	}

	// public void testコメントなし・ステータスあり_background() throws Exception {
	// doTest("Background\npackage", null, RunningAppProcessInfo.IMPORTANCE_BACKGROUND);
	// }
	//
	// public void testコメントなし・ステータスあり_IMPORTANCE_EMPTY() throws Exception {
	// doTest("Empty\npackage", null, RunningAppProcessInfo.IMPORTANCE_EMPTY);
	// }
	//
	// public void testコメントなし・ステータスあり_foreground() throws Exception {
	// doTest("Foreground\npackage", null, RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
	// }
	//
	// public void testコメントなし・ステータスあり_perceptible() throws Exception {
	// doTest("Perceptible\npackage", null, RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE);
	// }
	//
	// public void testコメントなし・ステータスあり_service() throws Exception {
	// doTest("Service\npackage", null, RunningAppProcessInfo.IMPORTANCE_SERVICE);
	// }
	//
	// public void testコメントなし・ステータスあり_visible() throws Exception {
	// doTest("Visible\npackage", null, RunningAppProcessInfo.IMPORTANCE_VISIBLE);
	// }
	//
	// public void testコメントあり・ステータスあり_background() throws Exception {
	// doTest("comment\nBackground\npackage", "comment",
	// RunningAppProcessInfo.IMPORTANCE_BACKGROUND);
	// }
	//
	// public void testコメントあり・ステータスあり_IMPORTANCE_EMPTY() throws Exception {
	// doTest("comment\nEmpty\npackage", "comment", RunningAppProcessInfo.IMPORTANCE_EMPTY);
	// }
	//
	// public void testコメントあり・ステータスあり_foreground() throws Exception {
	// doTest("comment\nForeground\npackage", "comment",
	// RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
	// }
	//
	// public void testコメントあり・ステータスあり_perceptible() throws Exception {
	// doTest("comment\nPerceptible\npackage", "comment",
	// RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE);
	// }
	//
	// public void testコメントあり・ステータスあり_service() throws Exception {
	// doTest("comment\nService\npackage", "comment", RunningAppProcessInfo.IMPORTANCE_SERVICE);
	// }
	//
	// public void testコメントあり・ステータスあり_visible() throws Exception {
	// doTest("comment\nVisible\npackage", "comment", RunningAppProcessInfo.IMPORTANCE_VISIBLE);
	// }

	// /**
	// * @param expected
	// */
	// private void doTest(String expected, String comment, int type) {
	// String actual = mCustomSpannableStringBuilder.getLabelText("package", comment, type)
	// .toString();
	// assertEquals(expected, actual);
	// }
}
