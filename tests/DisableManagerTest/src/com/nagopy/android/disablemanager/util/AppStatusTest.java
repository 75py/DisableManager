package com.nagopy.android.disablemanager.util;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.nagopy.android.disablemanager.util.AppStatus;

import android.test.AndroidTestCase;

public class AppStatusTest extends AndroidTestCase {

	private AppStatus mAppStatus;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mAppStatus = new AppStatus("label", "packageName", false, false, false);
	}

	public void testラベル名のget() throws Exception {
		assertEquals("label", mAppStatus.getLabel());
	}

	public void testパッケージ名のget() throws Exception {
		assertEquals("packageName", mAppStatus.getPackageName());
	}

	public void testIsSystemのget() throws Exception {
		assertEquals(false, mAppStatus.isSystem());
	}

	public void testIsEnabledのget() throws Exception {
		assertEquals(false, mAppStatus.isEnabled());
	}

	public void testCanDisableのget() throws Exception {
		assertEquals(false, mAppStatus.canDisable());
	}

	public void testステータスのget_set() throws Exception {
		mAppStatus.setRunningStatus(1);
		assertEquals(1, mAppStatus.getRunningStatus());
	}

	public void testToString() throws Exception {
		String expected = "label:packageName, enabled:false, system:false, canDisable:false";
		String actual = mAppStatus.toString();
		assertEquals(expected, actual);
	}

	@UsesMocks(AppStatus.class)
	public static AppStatus createMockAppStatus(String label, String packageName, boolean isEnabled,
			boolean isSystem, boolean canDisable) {
		AppStatus mock = AndroidMock.createMock(AppStatus.class, label, packageName, isEnabled, isSystem,
				canDisable);
		AndroidMock.expect(mock.getLabel()).andStubReturn(label);
		AndroidMock.expect(mock.getPackageName()).andStubReturn(packageName);
		AndroidMock.expect(mock.isEnabled()).andStubReturn(isEnabled);
		AndroidMock.expect(mock.isSystem()).andStubReturn(isSystem);
		AndroidMock.expect(mock.canDisable()).andStubReturn(canDisable);
		AndroidMock.replay(mock);
		return mock;
	}
}
