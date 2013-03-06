package com.nagopy.android.disablemanager.util.sort;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.nagopy.android.disablemanager.util.AppStatus;
import com.nagopy.android.disablemanager.util.AppStatusTest;

public class AppsSorterTest extends AndroidTestCase {

	private AppStatus status4;
	private AppStatus status3;
	private AppStatus status1;
	private AppStatus status0;
	private AppStatus status2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		status2 = AppStatusTest.createMockAppStatus("a", "package.a", false, true, true);
		status0 = AppStatusTest.createMockAppStatus("1", "package.1", false, true, true);
		status1 = AppStatusTest.createMockAppStatus("7", "package.7", false, true, true);
		status3 = AppStatusTest.createMockAppStatus("x", "package.x", false, true, true);
		status4 = AppStatusTest.createMockAppStatus("あ", "package.あ", false, true, true);
	}

	public void test普通のソート() throws Exception {
		List<AppStatus> list = createTestList();
		AppsSorter.sort(list);
		assertEquals(list.get(0), status0);
		assertEquals(list.get(1), status1);
		assertEquals(list.get(2), status2);
		assertEquals(list.get(3), status3);
		assertEquals(list.get(4), status4);
	}

	private ArrayList<AppStatus> createTestList() {
		ArrayList<AppStatus> list = new ArrayList<AppStatus>();
		list.add(status2);
		list.add(status0);
		list.add(status3);
		list.add(status4);
		list.add(status1);
		assertEquals(list.get(2), status3);
		return list;
	}
}
