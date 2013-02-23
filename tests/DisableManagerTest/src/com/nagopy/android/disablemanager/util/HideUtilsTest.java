package com.nagopy.android.disablemanager.util;

import java.util.HashSet;
import java.util.Set;

import com.nagopy.android.disablemanager.util.HideUtils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

public class HideUtilsTest extends AndroidTestCase {

	private HideUtils mHideUtils;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mHideUtils = new HideUtils(getContext());
	}

	public void test読み込みテスト_リストに何もない時空のSetが返ってくるか() throws Exception {
		SharedPreferences sp = getSharedPreferences();
		assertTrue(sp.edit().clear().commit());
		assertNotNull(mHideUtils.getHideAppsList());
		assertTrue(mHideUtils.getHideAppsList().isEmpty());
	}

	public void test読み込みテスト() throws Exception {
		SharedPreferences sp = getSharedPreferences();
		Set<String> arg1 = new HashSet<String>();
		arg1.add("hide.package");
		assertTrue(sp.edit().putStringSet("hides", arg1).commit());
		// 保存したものが読み込めるか
		assertNotNull(mHideUtils.getHideAppsList().contains("hide.package"));
	}

	public void test更新テスト() throws Exception {
		SharedPreferences sp = getSharedPreferences();
		assertTrue(sp.edit().clear().commit());

		assertTrue(mHideUtils.updateHideList("test.hide"));
		assertTrue(getSharedPreferences().getStringSet("hides", null).contains("test.hide"));

		// もう一度同じことをすると削除
		assertTrue(mHideUtils.updateHideList("test.hide"));
		assertFalse(getSharedPreferences().getStringSet("hides", null).contains("test.hide"));
	}

	/**
	 * @return
	 */
	private SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(getContext());
	}

}
