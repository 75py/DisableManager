package com.nagopy.android.disabledapps.test.util.share;

import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ActivityInstrumentationTestCase2;

import com.nagopy.android.disabledapps.app.MainActivity;
import com.nagopy.android.disabledapps.util.share.ShareUtils;

public class ShareUtilsTest2 extends ActivityInstrumentationTestCase2<MainActivity> {

	private ShareUtils mShareUtils;

	public ShareUtilsTest2() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mShareUtils = new ShareUtils(getActivity());
	}

	public void test_sendIntent() throws Throwable {
		ActivityMonitor monitor = new ActivityMonitor(new IntentFilter(Intent.ACTION_SEND, "text/plain"), null,
				false);
		getInstrumentation().addMonitor(monitor);

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				mShareUtils.sendIntent("text", "title");
			}
		});
		getInstrumentation().waitForIdleSync();

		assertEquals(1, monitor.getHits());
		getInstrumentation().removeMonitor(monitor);

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		getInstrumentation().onDestroy();
	}
}
