package com.nagopy.android.disablemanager.util;

import com.nagopy.android.disablemanager.util.AppStatus;
import com.nagopy.android.disablemanager.util.CustomSpannableStringBuilder;

import android.app.ActivityManager.RunningAppProcessInfo;
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
		CharSequence actual = mCustomSpannableStringBuilder
				.getLabelText("package", null, AppStatus.NULL_STATUS);
		assertEquals(expected, actual);
	}

	public void testコメントあり・ステータスなし() throws Exception {
		String expected = "comment\npackage";
		String actual = mCustomSpannableStringBuilder.getLabelText("package", "comment", AppStatus.NULL_STATUS)
				.toString();
		assertEquals(expected, actual);
	}

	public void testコメントなし・ステータスあり_background() throws Exception {
		てすと("Background\npackage", null, RunningAppProcessInfo.IMPORTANCE_BACKGROUND);
	}

	public void testコメントなし・ステータスあり_IMPORTANCE_EMPTY() throws Exception {
		てすと("Empty\npackage", null, RunningAppProcessInfo.IMPORTANCE_EMPTY);
	}

	public void testコメントなし・ステータスあり_foreground() throws Exception {
		てすと("Foreground\npackage", null, RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
	}

	public void testコメントなし・ステータスあり_perceptible() throws Exception {
		てすと("Perceptible\npackage", null, RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE);
	}

	public void testコメントなし・ステータスあり_service() throws Exception {
		てすと("Service\npackage", null, RunningAppProcessInfo.IMPORTANCE_SERVICE);
	}

	public void testコメントなし・ステータスあり_visible() throws Exception {
		てすと("Visible\npackage", null, RunningAppProcessInfo.IMPORTANCE_VISIBLE);
	}

	public void testコメントあり・ステータスあり_background() throws Exception {
		てすと("comment\nBackground\npackage", "comment", RunningAppProcessInfo.IMPORTANCE_BACKGROUND);
	}

	public void testコメントあり・ステータスあり_IMPORTANCE_EMPTY() throws Exception {
		てすと("comment\nEmpty\npackage", "comment", RunningAppProcessInfo.IMPORTANCE_EMPTY);
	}

	public void testコメントあり・ステータスあり_foreground() throws Exception {
		てすと("comment\nForeground\npackage", "comment", RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
	}

	public void testコメントあり・ステータスあり_perceptible() throws Exception {
		てすと("comment\nPerceptible\npackage", "comment", RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE);
	}

	public void testコメントあり・ステータスあり_service() throws Exception {
		てすと("comment\nService\npackage", "comment", RunningAppProcessInfo.IMPORTANCE_SERVICE);
	}

	public void testコメントあり・ステータスあり_visible() throws Exception {
		てすと("comment\nVisible\npackage", "comment", RunningAppProcessInfo.IMPORTANCE_VISIBLE);
	}

	public void testタイプが不正() throws Exception {
		CharSequence expected = "package";
		CharSequence actual = mCustomSpannableStringBuilder.getLabelText("package", null, -75);
		assertEquals(expected, actual);
	}

	/**
	 * @param expected
	 */
	private void てすと(String expected, String comment, int type) {
		String actual = mCustomSpannableStringBuilder.getLabelText("package", comment, type).toString();
		assertEquals(expected, actual);
	}
}
