package com.nagopy.android.disablemanager2;

import android.os.Build;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivityTestCase extends UiAutomatorTestCase {

    public static final String COM_ANDROID_CALCULATOR2 = "com.android.calculator2";
    private String screenshotOutputPath;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.debugLog("start " + getName());
        TestUtils.initConfigure();

        screenshotOutputPath = getParams().getString("baseDir") + getName() + "/";
        TestUtils.debugLog("mkdir " + screenshotOutputPath + " : " + new File(screenshotOutputPath).mkdirs());
    }

    @Override
    protected void runTest() throws Throwable {
        try {
            super.runTest();
        } catch (Throwable t) {
            TestUtils.errorLog(t);
            throw t;
        }
    }

    @Override
    protected void tearDown() throws Exception {
        // 無効化マネージャーの場合、一度別アプリ（設定画面）へ繊維する場合がある。
        // この時に異常終了した場合、無効化マネージャーが表示されていない状態になり、finishAppメソッドで対応できなくなる。
        // これを回避するため、とりあえず一度はバックキーを押すことにする。
        getUiDevice().pressBack();
        TestUtils.finishApp(getUiDevice(), TestUtils.PACKAGE_NAME);

        TestUtils.debugLog("finish " + getName());
        super.tearDown();
    }

    public void testDisableButtonClick() throws Exception {
        startApp();
        validateCurrentPageTitle("無効化可能");
        UiScrollable listView = new UiScrollable(new UiSelectorBuilder().className(ListView.class).scrollable(true).build());
        UiObject calculator = listView.getChildByText(
                new UiSelectorBuilder()
                        .resourceId("com.nagopy.android.disablemanager2:id/list_package_name")
                        .className(TextView.class)
                        .description("package name").build(),
                COM_ANDROID_CALCULATOR2);
        calculator.clickAndWaitForNewWindow();

        UiObject disableButton = new UiObject(
                new UiSelectorBuilder()
                        .className(Button.class)
                        .resourceId("com.android.settings:id/right_button")
                        .text("無効にする").build()
        );
        disableButton.clickAndWaitForNewWindow();

        UiObject okButton = new UiObject(
                new UiSelectorBuilder()
                        .text("OK").build()
        );
        okButton.clickAndWaitForNewWindow();

        getUiDevice().pressBack();
        boolean found = false;
        try {
            listView.getChildByText(
                    new UiSelectorBuilder()
                            .resourceId("com.nagopy.android.disablemanager2:id/list_package_name")
                            .className(TextView.class)
                            .description("package name").build(),
                    COM_ANDROID_CALCULATOR2);
            found = true;
        } catch (UiObjectNotFoundException e) {
            // OK
        }
        assertFalse("無効化可能一覧から消えていることを確認", found);

        // 無効化済みへ移動
        swipeLeft();
        swipeLeft();
        validateCurrentPageTitle("無効化済み");
        calculator = listView.getChildByText(
                new UiSelectorBuilder()
                        .resourceId("com.nagopy.android.disablemanager2:id/list_package_name")
                        .className(TextView.class)
                        .description("package name").build(),
                COM_ANDROID_CALCULATOR2);
        assertTrue("電卓が無効化済みで表示されている", calculator.exists());
        calculator.clickAndWaitForNewWindow();
        UiObject enableButton = new UiObject(
                new UiSelectorBuilder()
                        .className(Button.class)
                        .resourceId("com.android.settings:id/right_button")
                        .text("有効にする").build()
        );
        assertTrue("有効にするボタンが表示されている", enableButton.exists());
        enableButton.clickAndWaitForNewWindow();
        getUiDevice().pressBack();

        found = false;
        try {
            listView.getChildByText(
                    new UiSelectorBuilder()
                            .resourceId("com.nagopy.android.disablemanager2:id/list_package_name")
                            .className(TextView.class)
                            .description("package name").build(),
                    COM_ANDROID_CALCULATOR2);
            found = true;
        } catch (UiObjectNotFoundException e) {
            // OK
        }
        assertFalse("無効化済み一覧から消えていることを確認", found);

        swipeRight();
        swipeRight();
        validateCurrentPageTitle("無効化可能");
        calculator = listView.getChildByText(
                new UiSelectorBuilder()
                        .resourceId("com.nagopy.android.disablemanager2:id/list_package_name")
                        .className(TextView.class)
                        .description("package name").build(),
                COM_ANDROID_CALCULATOR2);
        assertTrue("電卓が無効化可能で表示されている", calculator.exists());
    }

    public void testDisableable() throws Exception {
        startApp();
        validateCurrentPageTitle("無効化可能");

        UiScrollable listView = new UiScrollable(new UiSelectorBuilder().className(ListView.class).scrollable(true).build());
        listView.scrollToBeginning(10);

        // リストの一つ一つをチェックする前に、現状のスクリーンショットを撮っておく
        // （目的のタブが表示されているかを目視確認するため）
        // （ViewPagerIndicatorはTextViewを使っていないためuiautomatorでは文字列を取得できない）
        TestUtils.takeScreenshot(getUiDevice(), screenshotOutputPath, "LIST.jpg");

        List<String> errorAppList = validateAllItems(listView, new InstalledAppDetailValidator() {
                    @Override
                    public boolean validate(String packageName) throws UiObjectNotFoundException {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            // 4.2以上
                            UiObject disableButton = new UiObject(
                                    new UiSelectorBuilder()
                                            .className(Button.class)
                                            .resourceId("com.android.settings:id/right_button")
                                            .text("無効にする").build()
                            );
                            return disableButton.exists() && disableButton.isEnabled();
                        } else {
                            // 4.2未満は「アップデートのアンインストール」と「無効にする」のボタンが分離していないため、
                            // 「無効にする」がない場合もエラーにはしない
                            UiObject disableButton = new UiObject(
                                    new UiSelectorBuilder()
                                            .className(Button.class)
                                            .resourceId("com.android.settings:id/right_button")
                                            .text("無効にする").build()
                            );
                            if (disableButton.exists() && disableButton.isEnabled()) {
                                return true;
                            } else {
                                UiObject updateUninstallButton = new UiObject(
                                        new UiSelectorBuilder()
                                                .resourceId("com.android.settings:id/right_button")
                                                .className(Button.class)
                                                .textContains("アップデートのアンインストール").build()
                                );
                                boolean result = updateUninstallButton.exists() && updateUninstallButton.isEnabled();
                                if (result) {
                                    // 「アップデートのアンインストール」が存在する場合は検証は問題なしとするが、
                                    // 本当に無効化できるかはこれ以上判定できないため、警告として出力する
                                    TestUtils.warningLog("[Warning] Updated System App:" + packageName);
                                }
                                return result;
                            }
                        }
                    }
                }
        );

        TestUtils.debugLog(errorAppList);
        assertTrue("「無効化可能」に表示されている無効化不可アプリ " + errorAppList.toString(), errorAppList.isEmpty());
    }

    public void testDisableableRunning() throws Exception {
        startApp();
        swipeLeft();
        validateCurrentPageTitle("無効化可能（実行中）");

        UiScrollable listView = new UiScrollable(new UiSelectorBuilder().className(ListView.class).scrollable(true).build());
        listView.scrollToBeginning(10);

        // リストの一つ一つをチェックする前に、現状のスクリーンショットを撮っておく
        // （目的のタブが表示されているかを目視確認するため）
        // （ViewPagerIndicatorはTextViewを使っていないためuiautomatorでは文字列を取得できない）
        TestUtils.takeScreenshot(getUiDevice(), screenshotOutputPath, "LIST.jpg");

        List<String> errorAppList = validateAllItems(listView, new InstalledAppDetailValidator() {
                    @Override
                    public boolean validate(String packageName) throws UiObjectNotFoundException {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            // 4.2以上
                            UiObject disableButton = new UiObject(
                                    new UiSelectorBuilder()
                                            .className(Button.class)
                                            .resourceId("com.android.settings:id/right_button")
                                            .text("無効にする").build()
                            );
                            return disableButton.exists() && disableButton.isEnabled();
                        } else {
                            // 4.2未満は「アップデートのアンインストール」と「無効にする」のボタンが分離していないため、
                            // 「無効にする」がない場合もエラーにはしない
                            UiObject disableButton = new UiObject(
                                    new UiSelectorBuilder()
                                            .className(Button.class)
                                            .resourceId("com.android.settings:id/right_button")
                                            .text("無効にする").build()
                            );
                            if (disableButton.exists() && disableButton.isEnabled()) {
                                return true;
                            } else {
                                UiObject updateUninstallButton = new UiObject(
                                        new UiSelectorBuilder()
                                                .resourceId("com.android.settings:id/right_button")
                                                .className(Button.class)
                                                .textContains("アップデートのアンインストール").build()
                                );
                                boolean result = updateUninstallButton.exists() && updateUninstallButton.isEnabled();
                                if (result) {
                                    // 「アップデートのアンインストール」が存在する場合は検証は問題なしとするが、
                                    // 本当に無効化できるかはこれ以上判定できないため、警告として出力する
                                    TestUtils.warningLog("[Warning] Updated System App:" + packageName);
                                }
                                return result;
                            }
                        }
                    }
                }
        );

        TestUtils.debugLog(errorAppList);
        assertTrue("「無効化可能（実行中）」に表示されている無効化不可アプリ " + errorAppList.toString(), errorAppList.isEmpty());
    }

    public void testDisabled() throws Exception {
        startApp();

        swipeLeft();
        swipeLeft();
        validateCurrentPageTitle("無効化済み");

        UiScrollable listView = new UiScrollable(new UiSelectorBuilder().className(ListView.class).scrollable(true).build());
        listView.scrollToBeginning(10);

        // リストの一つ一つをチェックする前に、現状のスクリーンショットを撮っておく
        // （目的のタブが表示されているかを目視確認するため）
        // （ViewPagerIndicatorはTextViewを使っていないためuiautomatorでは文字列を取得できない）
        TestUtils.takeScreenshot(getUiDevice(), screenshotOutputPath, "LIST.jpg");

        List<String> errorAppList = validateAllItems(listView, new InstalledAppDetailValidator() {
            @Override
            public boolean validate(String packageName) throws UiObjectNotFoundException {
                UiObject uninstallButton = new UiObject(
                        new UiSelectorBuilder()
                                .className(Button.class)
                                .resourceId("com.android.settings:id/right_button")
                                .text("有効にする").build()
                );
                return uninstallButton.exists() && uninstallButton.isEnabled();
            }
        });

        TestUtils.debugLog(errorAppList);
        assertTrue("「無効化済み」に表示されている無効化されていないアプリ " + errorAppList.toString(), errorAppList.isEmpty());
    }

    public void testUndisableable() throws Exception {
        startApp();

        swipeLeft();
        swipeLeft();
        swipeLeft();
        validateCurrentPageTitle("無効化不可");

        UiScrollable listView = new UiScrollable(new UiSelectorBuilder().className(ListView.class).scrollable(true).build());
        listView.scrollToBeginning(10);

        // リストの一つ一つをチェックする前に、現状のスクリーンショットを撮っておく
        // （目的のタブが表示されているかを目視確認するため）
        // （ViewPagerIndicatorはTextViewを使っていないためuiautomatorでは文字列を取得できない）
        TestUtils.takeScreenshot(getUiDevice(), screenshotOutputPath, "LIST.jpg");

        List<String> errorAppList = validateAllItems(listView, new InstalledAppDetailValidator() {
            @Override
            public boolean validate(String packageName) throws UiObjectNotFoundException {
                UiObject disableButton = new UiObject(
                        new UiSelectorBuilder()
                                .className(Button.class)
                                .resourceId("com.android.settings:id/right_button")
                                .text("無効にする").build()
                );
                // 「無効にする」ボタンが存在しないか、無効になっている
                return !disableButton.exists() || !disableButton.isEnabled();
            }
        });

        TestUtils.debugLog(errorAppList);
        assertTrue("「無効化不可」に表示されている無効化可能アプリ " + errorAppList.toString(), errorAppList.isEmpty());
    }

    public void testUser() throws Exception {
        startApp();
        swipeLeft();
        swipeLeft();
        swipeLeft();
        swipeLeft();
        validateCurrentPageTitle("ユーザー");

        UiScrollable listView = new UiScrollable(new UiSelectorBuilder().className(ListView.class).scrollable(true).build());
        listView.scrollToBeginning(10);

        // リストの一つ一つをチェックする前に、現状のスクリーンショットを撮っておく
        // （目的のタブが表示されているかを目視確認するため）
        // （ViewPagerIndicatorはTextViewを使っていないためuiautomatorでは文字列を取得できない）
        TestUtils.takeScreenshot(getUiDevice(), screenshotOutputPath, "LIST.jpg");

        List<String> errorAppList = validateAllItems(listView, new InstalledAppDetailValidator() {
            @Override
            public boolean validate(String packageName) throws UiObjectNotFoundException {
                UiObject uninstallButton = new UiObject(
                        new UiSelectorBuilder()
                                .resourceId("com.android.settings:id/right_button")
                                .className(Button.class)
                                .textContains("アンインストール").build()
                );
                // 「アンインストール」ボタンが存在する
                // （かつ有効である、とすると、Lollipopではホームアプリのアンインストールボタンは無効になっているため適切でない）
                // return uninstallButton.exists() && uninstallButton.isEnabled();
                return uninstallButton.exists();
            }
        });

        TestUtils.debugLog(errorAppList);
        assertTrue("「ユーザー」に表示されているアンインストールできないアプリ " + errorAppList.toString(), errorAppList.isEmpty());
    }

    /**
     * ViewPagerを左スワイプ（＝右から左へスワイプ＝右の画面へ移動）する.
     *
     * @return {@link com.android.uiautomator.core.UiScrollable#swipeLeft(int)}の戻り値
     * @throws UiObjectNotFoundException
     */
    private boolean swipeLeft() throws UiObjectNotFoundException {
        return new UiScrollable((new UiSelectorBuilder().className("android.support.v4.view.ViewPager").build())).swipeLeft(10);
    }

    /**
     * ViewPagerを右スワイプ（＝左から右へスワイプ＝右の画面へ移動）する.
     *
     * @return {@link com.android.uiautomator.core.UiScrollable#swipeLeft(int)}の戻り値
     * @throws UiObjectNotFoundException
     */
    private boolean swipeRight() throws UiObjectNotFoundException {
        return new UiScrollable((new UiSelectorBuilder().className("android.support.v4.view.ViewPager").build())).swipeRight(10);
    }

    /**
     * 無効化マネージャーを起動する
     */
    private void startApp() throws Exception {
        getUiDevice().pressHome();
        getUiDevice().pressBack();

        Runtime.getRuntime().exec("am start -n com.nagopy.android.disablemanager2/.MainActivity");
        if (true) return;

        UiObject allAppsButton = new UiObject(
                new UiSelectorBuilder()
                        .description("アプリ").build()
        );
        allAppsButton.clickAndWaitForNewWindow();

        UiScrollable appViews = new UiScrollable(
                new UiSelectorBuilder().
                        scrollable(true).build()
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            appViews.setAsHorizontalList();
        }
        UiObject settingsApp = appViews.getChildByText(
                new UiSelectorBuilder()
                        .className(TextView.class.getName()).build(),
                "無効化マネージャー");
        settingsApp.clickAndWaitForNewWindow();

        assertTrue("アプリ起動に失敗", TestUtils.isTargetApp(TestUtils.PACKAGE_NAME));
    }

    private interface InstalledAppDetailValidator {
        /**
         * 検証を行う.
         *
         * @param packageName 対象パッケージ名（警告がある場合のメッセージ出力に使用）
         * @return 検証エラーがなければtrue、エラーがあればfalse.<br>
         * 警告がある場合はtrueを返し、標準出力にメッセージを出力する。
         * @throws UiObjectNotFoundException
         */
        boolean validate(String packageName) throws UiObjectNotFoundException;
    }

    /**
     * ListViewの各要素を検証する.
     *
     * @param listView  ListViewを取得した{@link com.android.uiautomator.core.UiScrollable}
     * @param validator {@link com.nagopy.android.disablemanager2.MainActivityTestCase.InstalledAppDetailValidator}を実装したクラス
     * @return エラーがあったアプリのパッケージ名のリスト
     * @throws UiObjectNotFoundException
     */
    private List<String> validateAllItems(UiScrollable listView, InstalledAppDetailValidator validator) throws UiObjectNotFoundException {
        try {
            listView.setSwipeDeadZonePercentage(0.2); // デッドゾーンを若干大きめにして、スクロール幅を縮小する
        } catch (NoSuchMethodError e) {
            // API16以上のはずだが、SH-02Eでエラーになる。テストに大きく支障があるわけではないため無視する。
            TestUtils.errorLog(e);
        }

        List<String> errorAppList = new ArrayList<>();
        Set<String> testedPackages = new HashSet<>();
        boolean hasNext;
        do { // ListViewのスクロールをループするdo-whileループ
            hasNext = false;
            for (int i = 0; ; i++) { // ListViewに今表示されている要素を一つ一つ見ていくforループ
                try {
                    UiObject clickable = new UiObject(
                            new UiSelectorBuilder()
                                    .resourceId("com.nagopy.android.disablemanager2:id/list_parent")
                                    .description("listItem")
                                    .index(i)
                                    .build()
                    );
                    if (!clickable.exists()) {
                        // i番目の要素が見つからない場合
                        break;
                    }

                    UiObject titleTextView;
                    String label;
                    try {
                        titleTextView =
                                clickable.getChild(
                                        new UiSelectorBuilder()
                                                .resourceId("com.nagopy.android.disablemanager2:id/list_title")
                                                .className(TextView.class)
                                                .description("application name")
                                                .build()
                                );
                        label = titleTextView.getText();
                    } catch (UiObjectNotFoundException e) {
                        // タイトルが取得できない場合
                        TestUtils.debugLog("continue (title not found)");
                        continue;
                    }
                    UiObject packageNameTextView =
                            clickable.getChild(
                                    new UiSelectorBuilder()
                                            .resourceId("com.nagopy.android.disablemanager2:id/list_package_name")
                                            .className(TextView.class)
                                            .description("package name")
                                            .build()
                            );
                    String packageName = packageNameTextView.getText();
                    if (TextUtils.isEmpty(packageName) || testedPackages.contains(packageName)) {
                        TestUtils.debugLog("continue " + packageName);
                        continue;
                    }
                    TestUtils.infoLog(label + " [" + packageName + "]");
                    hasNext = true;
                    testedPackages.add(packageName);
                    titleTextView.clickAndWaitForNewWindow();

                    TestUtils.takeScreenshot(getUiDevice(), screenshotOutputPath, packageName + ".png");

                    if (!validator.validate(packageName)) {
                        // バリデータでエラーになった場合
                        if (TestUtils.isIgnorePackage(packageName)) {
                            // 無視リストに入っている場合はエラーにせず、ログ出力のみで次へ進む
                            TestUtils.infoLog("[Info] Ignore application:" + packageName);
                        } else {
                            // バリデータでエラーになり、かつ無視リストに入っていない場合はエラーとする
                            errorAppList.add(packageName);
                        }
                    }

                    getUiDevice().pressBack();
                } catch (UiObjectNotFoundException e) {
                    // 画面に表示されているListViewの要素のi番目が見つからない OR パッケージ名が見つからない
                    // ＝ 今表示されている分は全部見た
                    // ＝ forループは抜け、次へ進む
//                    TestUtils.debugLog("continue? " + i);
//                    if(i > 5) {
                    TestUtils.debugLog(i + ", continue(e) " + e.getMessage());
                    break;
//                    }
                }
            }
            listView.scrollForward();
        } while (hasNext);
        return errorAppList;
    }

    private void validateCurrentPageTitle(String title) throws UiObjectNotFoundException {
        UiObject titlePageIndicator = new UiObject(
                new UiSelectorBuilder()
                        .className("com.viewpagerindicator.TabPageIndicator").build()
        );
        assertEquals("TabPageIndicatorが【" + title + "】であることを確認", title, titlePageIndicator.getText());
    }

}
