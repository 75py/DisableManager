package com.nagopy.android.disablemanager2;

import android.os.Build;
import android.util.Log;

import com.android.uiautomator.core.Configurator;
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.fail;

/**
 * uiautomatorのテスト用ユーティリティクラス.
 */
public class TestUtils {

    /**
     * {@link #sleep()}のデフォルト停止時間.
     */
    private static final long WAIT_MS = 251;


    public static final String PACKAGE_NAME = "com.nagopy.android.disablemanager2";
    public static final String TAG = "nagopy_uiautomator";

    /**
     * コンストラクタ.
     */
    private TestUtils() {
    }

    /**
     * 標準出力.
     */
    private static void out(Object obj) {
        System.out.println(obj == null ? "null" : obj.toString());
    }

    /**
     * 標準エラー.
     */
    private static void err(Throwable t) {
        System.err.println(t.getMessage());
    }

    /**
     * デバッグログ出力.
     */
    public static void debugLog(Object obj) {
        Log.d(TAG, obj == null ? "null" : obj.toString());
    }

    /**
     * INFOログ出力＋標準出力.
     */
    public static void infoLog(Object obj) {
        Log.i(TAG, obj == null ? "null" : obj.toString());
        out(obj);
    }

    /**
     * 警告ログ出力＋標準出力.
     */
    public static void warningLog(Object obj) {
        Log.w(TAG, obj == null ? "null" : obj.toString());
        out(obj);
    }

    /**
     * エラーログ出力＋エラー出力.
     */
    public static void errorLog(Throwable t) {
        Log.e(TAG, t.getMessage());
        err(t);
    }


    /**
     * スレッドを一時停止する.
     */
    public static void sleep() {
        sleep(WAIT_MS);
    }

    /**
     * スレッドを一時停止する.
     *
     * @param time 停止時間（ミリ秒）
     */
    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * 対象アプリが表示されているかを確認する.
     *
     * @param packageName パッケージ名
     * @return 指定されたパッケージのなんらかの要素が存在すればtrue
     */
    public static boolean isTargetApp(String packageName) {
        return new UiObject(new UiSelectorBuilder().packageName(packageName).build()).exists();
    }

    /**
     * スクリーンショットを撮る.<br>
     * API17未満の場合は何もしない。
     *
     * @param uiDevice  {@link com.android.uiautomator.testrunner.UiAutomatorTestCase#getUiDevice()}
     * @param outputDir 出力ディレクトリ
     * @param fileName  出力ファイル名
     */
    public static void takeScreenshot(UiDevice uiDevice, String outputDir, String fileName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // API17未満は機能自体がないためここで終了
            return;
        }

        TestUtils.sleep(); // ちょっとWaitしてちゃんとSSに映るようにする

        File ss = new File(outputDir, fileName);
        if (!uiDevice.takeScreenshot(ss)) {
            fail("スクリーンショットの保存に失敗 path=" + ss.getAbsolutePath());
        }
    }

    /**
     * タイムアウト時間の設定等を行う.<br>
     * API18未満の場合は何もしない。
     */
    public static void initConfigure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Configurator configurator = Configurator.getInstance();
            configurator.setWaitForSelectorTimeout(WAIT_MS);
        }
    }


    /**
     * 対象アプリが表示されなくなるまでバックキーを押し続ける.
     *
     * @param uiDevice    {@link com.android.uiautomator.testrunner.UiAutomatorTestCase#getUiDevice()}
     * @param packageName 対象アプリのパッケージ名
     */
    public static void finishApp(UiDevice uiDevice, String packageName) {
        while (TestUtils.isTargetApp(packageName)) {
            uiDevice.pressBack();
        }
    }

    private static final List<String> ignoreAppPrefixList;

    static {
        List<String> tmpIgnoreAppList = new ArrayList<>();
        tmpIgnoreAppList.add("com.google.android.gms"); // 開発者サービス
        tmpIgnoreAppList.add("jp.co.nttdocomo");
        tmpIgnoreAppList.add("com.nttdocomo");
        tmpIgnoreAppList.add("com.sonyericsson");
        tmpIgnoreAppList.add("com.sonymobile");
        ignoreAppPrefixList = Collections.unmodifiableList(tmpIgnoreAppList);
    }

    public static boolean isIgnorePackage(String packageName) {
        for (String ignorePrefix : ignoreAppPrefixList) {
            if (packageName.startsWith(ignorePrefix)) {
                return true;
            }
        }
        return false;
    }
}
