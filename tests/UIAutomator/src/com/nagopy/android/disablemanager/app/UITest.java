package com.nagopy.android.disablemanager.app;

import android.widget.ListView;
import android.widget.TextView;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class UITest extends UiAutomatorTestCase {
	
	
	public void testDisableAndReenable() throws UiObjectNotFoundException {
		// Homeボタンをタップ
		getUiDevice().pressHome();

		// 対象アプリはホームにある前提
		UiObject launcher = new UiObject(new UiSelector().text("無効化マネージャー"));
		launcher.clickAndWaitForNewWindow();

		// 起動を確認
		UiObject validation = new UiObject(new UiSelector().packageName("com.nagopy.android.disablemanager"));
		assertTrue("Unable to detect app", validation.exists());

		pressDisableButton("メッセージ");

		getUiDevice().pressBack();

		UiObject disabledTab = new UiObject(new UiSelector().text("無効化済みのアプリ"));
		disabledTab.clickAndWaitForNewWindow();

		pressEnableButton("メッセージ");
		getUiDevice().pressBack();

		assertTrue(new UiObject(new UiSelector().text("該当するアプリはありません。")).exists());

		UiObject disablableTab = new UiObject(new UiSelector().text("無効化可能、有効なシステムアプリ"));
		assertTrue("無効化可能、有効なアプリタブが存在するか", disablableTab.exists());
		disablableTab.clickAndWaitForNewWindow();

		UiScrollable appViews = new UiScrollable(new UiSelector().className(ListView.class).scrollable(true));
		appViews.setAsVerticalList();
		UiObject targetApp = appViews.getChildByText(new UiSelector().className(TextView.class.getName()),
				"メッセージ");
		assertTrue("無効化可能の一覧に戻っているかどうか", targetApp.exists());
	}

	private void pressDisableButton(String targetLabel) throws UiObjectNotFoundException {
		// スワイプしながらアイテムを検索できるUiScrollableインスタンスを作成
		UiScrollable appViews = new UiScrollable(new UiSelector().className(ListView.class).scrollable(true));
		// スワイプ時のスクロール方向を水平方向に設定
		appViews.setAsVerticalList();

		// アプリランチャーの子ビューから以下の条件を満たすUIオブジェクトを取得
		UiObject targetApp = appViews.getChildByText(new UiSelector().className(TextView.class.getName()),
				targetLabel);
		targetApp.clickAndWaitForNewWindow();

		UiObject disableButton = new UiObject(new UiSelector().text("無効にする"));
		disableButton.clickAndWaitForNewWindow();

		UiObject okButton = new UiObject(new UiSelector().text("OK"));
		okButton.clickAndWaitForNewWindow();
	}

	private void pressEnableButton(String targetLabel) throws UiObjectNotFoundException {
		UiObject targetApp = new UiObject(new UiSelector().text(targetLabel));
		targetApp.clickAndWaitForNewWindow();

		UiObject enableButton = new UiObject(new UiSelector().text("有効にする"));
		enableButton.clickAndWaitForNewWindow();
	}
}
