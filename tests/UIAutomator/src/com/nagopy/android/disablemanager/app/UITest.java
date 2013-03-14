package com.nagopy.android.disablemanager.app;

import android.widget.ListView;
import android.widget.TextView;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class UITest extends UiAutomatorTestCase {
	public void testPostByTwicca() throws UiObjectNotFoundException {
		// Homeボタンをタップ
		getUiDevice().pressHome();

		// 対象アプリはホームにある前提
		UiObject launcher = new UiObject(new UiSelector().text("無効化マネージャー"));
		launcher.clickAndWaitForNewWindow();

		// 起動を確認
		UiObject validation = new UiObject(new UiSelector().packageName("com.nagopy.android.disablemanager"));
		assertTrue("Unable to detect app", validation.exists());

		// スワイプしながらアイテムを検索できるUiScrollableインスタンスを作成
		UiScrollable appViews = new UiScrollable(new UiSelector().className(ListView.class).scrollable(true));
		// スワイプ時のスクロール方向を水平方向に設定
		appViews.setAsVerticalList();

		// アプリランチャーの子ビューから以下の条件を満たすUIオブジェクトを取得
		UiObject targetApp = appViews.getChildByText(new UiSelector().className(TextView.class.getName()),
				"Earth");
		targetApp.clickAndWaitForNewWindow();

		UiObject buttonClear = new UiObject(new UiSelector().text("無効にする"));
		buttonClear.clickAndWaitForNewWindow();

		UiObject okButton = new UiObject(new UiSelector().text("OK"));
		okButton.clickAndWaitForNewWindow();

		getUiDevice().pressBack();
	}
}
