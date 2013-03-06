package com.nagopy.android.disablemanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

public class ChangedDateUtilsTest extends AndroidTestCase {

	private ChangedDateUtils mChangedDateUtils;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mChangedDateUtils = new ChangedDateUtils(getContext());
	}

	public void testPut() throws Exception {
		String packageName = "a.a";
		long time = 1000L;
		mChangedDateUtils.put(packageName, time);
		SharedPreferences sp = getContext().getSharedPreferences("date", Context.MODE_PRIVATE);
		assertEquals(1000L, sp.getLong(packageName, 0));
	}

	public void testGet() throws Exception {
		SharedPreferences sp = getContext().getSharedPreferences("date", Context.MODE_PRIVATE);
		String packageName = "a.a";
		long time = 1000L;
		assertTrue(sp.edit().putLong(packageName, time).commit());
		assertEquals(time, mChangedDateUtils.get(packageName));
	}
}
