package com.nagopy.android.disabledapps.test.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

import com.nagopy.android.disabledapps.R;
import com.nagopy.android.disabledapps.util.FormatUtils;

public class FormatUtilsTest extends AndroidTestCase {

	private FormatUtils mFormatUtils;

	private String formatWithComment, formatWithoutComment;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mFormatUtils = new FormatUtils(getContext());
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		formatWithComment = sharedPreferences.getString(
				getContext().getString(R.string.pref_key_share_customformat_with_comment), getContext()
						.getString(R.string.pref_def_share_customformat_with_comment));
		formatWithoutComment = sharedPreferences.getString(
				getContext().getString(R.string.pref_key_share_customformat_without_comment), getContext()
						.getString(R.string.pref_def_share_customformat_without_comment));
	}

	public void testFormat() {
		// これじゃー意味ないような気がする
		String label = getContext().getString(R.string.app_name);
		String packageName = getContext().getPackageName();
		String comment = "your comment";
		assertEquals("コメントがない場合のフォーマットが正しいか", String.format(formatWithoutComment, label, packageName),
				mFormatUtils.format(label, packageName, ""));
		assertEquals("コメントがある場合のフォーマットが正しいか", String.format(formatWithComment, label, packageName, comment),
				mFormatUtils.format(label, packageName, comment));
	}

}
