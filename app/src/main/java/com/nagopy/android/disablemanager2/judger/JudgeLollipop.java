/*
 * Copyright (C) 2014 75py
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * このクラスは、Androidのソースコードの一部を利用しています。
 *  /frameworks/base/services/core/java/com/android/server/pm/PackageManagerService.java
 *  /packages/apps/Settings/src/com/android/settings/applications/InstalledAppDetails.java
 */

/*
 * InstalledAppDetailsのライセンスは以下の通りです。
 */

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/*
 * PackageManagerServiceのライセンスは以下の通りです。
 */

/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nagopy.android.disablemanager2.judger;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 無効化できるかを判定するクラス（5.0以上用）.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class JudgeLollipop extends Judge {
    /**
     * InstalledAppDetailsのフィールド.
     */
    private PackageInfo mPackageInfo;

    /**
     * InstalledAppDetailsのフィールド.
     */
    private final HashSet<String> mHomePackages = new HashSet<>();

    /**
     * コンストラクタ
     *
     * @param context アプリケーションのコンテキスト
     */
    public JudgeLollipop(Context context) {
        super(context);

        // mHomePackagesの初期化が含まれるメソッドを実行しておく
        refreshUi();
    }

    @Override
    public boolean isDisablable(ApplicationInfo applicationInfo) {
        if (!((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0)) {
            // システムアプリじゃない場合
            return false;
        }
        try {
            mPackageInfo = getPackageManager().getPackageInfo(
                    applicationInfo.packageName,
                    PackageManager.GET_DISABLED_COMPONENTS | PackageManager.GET_UNINSTALLED_PACKAGES
                            | PackageManager.GET_SIGNATURES);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            // パッケージ名からPackageInfoが取得できない場合
            return false;
        }

        return initUninstallButtons(applicationInfo);
    }

    /**
     * InstalledAppDetailsのメソッド
     */
    private boolean initUninstallButtons(ApplicationInfo applicationInfo) {
        boolean mUpdatedSysApp = (applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
        final boolean isBundled = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        UserManager mUserManager = (UserManager) getContext().getSystemService(Context.USER_SERVICE);
        boolean mAppControlRestricted = mUserManager.hasUserRestriction(UserManager.DISALLOW_APPS_CONTROL);
        boolean enabled = true;
        if (mUpdatedSysApp || isBundled) {
            enabled = handleDisableable();
        } else if ((mPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_INSTALLED) == 0 && mUserManager.getUserCount() >= 2) {
            enabled = false;
        }

        if (packageHasActiveAdmins(mPackageInfo.packageName)) {
            enabled = false;
        }

        if (enabled && mHomePackages.contains(mPackageInfo.packageName)) {
            if (isBundled) {
                enabled = false;
            } else {
                ComponentName currentDefaultHome = PackageManagerService.getCurrentDefaultHome(getPackageManager());
                if (currentDefaultHome == null) {
                    // No preferred default, so permit uninstall only when
                    // there is more than one candidate
                    enabled = (mHomePackages.size() > 1);
                } else {
                    // There is an explicit default home app -- forbid uninstall of
                    // that one, but permit it for installed-but-inactive ones.
                    enabled = !mPackageInfo.packageName.equals(currentDefaultHome.getPackageName());
                }
            }
        }

        if (mAppControlRestricted) {
            enabled = false;
        }

        return enabled;
    }

    /**
     * InstalledAppDetailsのメソッド
     */
    private boolean handleDisableable() {
//        boolean disableable = false;
//        if (mHomePackages.contains(mPackageInfo.applicationInfo.packageName)
//                || Utils.isSystemPackage(getPackageManager(), mPackageInfo)) {
//        } else if (mPackageInfo.applicationInfo.enabled) {
//            disableable = true;
//        } else {
//            disableable = true;
//        }
//
//        return disableable;
        // 要するにこういうこと
        return !(mHomePackages.contains(mPackageInfo.applicationInfo.packageName)
                || Utils.isSystemPackage(getPackageManager(), mPackageInfo));
    }

    /**
     * InstalledAppDetailsのメソッド
     * mHomePackagesの初期化だけ抜き出し。毎回呼ぶ必要はないのでコンストラクタ実行でOK
     */
    private void refreshUi() {
        List<ResolveInfo> homeActivities = PackageManagerService.getHomeActivities(getPackageManager());
        mHomePackages.clear();
        for (int i = 0; i < homeActivities.size(); i++) {
            ResolveInfo ri = homeActivities.get(i);
            final String activityPkg = ri.activityInfo.packageName;
            mHomePackages.add(activityPkg);

            // Also make sure to include anything proxying for the home app
            final Bundle metadata = ri.activityInfo.metaData;
            if (metadata != null) {
                final String metaPkg = metadata.getString(ActivityManager.META_HOME_ALTERNATE);
                if (signaturesMatch(metaPkg, activityPkg)) {
                    mHomePackages.add(metaPkg);
                }
            }
        }
    }

    /**
     * InstalledAppDetailsのメソッド
     */
    private boolean signaturesMatch(String pkg1, String pkg2) {
        if (pkg1 != null && pkg2 != null) {
            try {
                final int match = getPackageManager().checkSignatures(pkg1, pkg2);
                if (match >= PackageManager.SIGNATURE_MATCH) {
                    return true;
                }
            } catch (Exception e) {
                // e.g. named alternate package not found during lookup;
                // this is an expected case sometimes
            }
        }
        return false;
    }

    /**
     * /frameworks/base/services/core/java/com/android/server/pm/PackageManagerService.java<br>
     * 上記クラスを再現（クラス名・メソッド名のみ。引数や戻り値は異なる）。
     */
    private static class PackageManagerService {

        public static List<ResolveInfo> getHomeActivities(PackageManager packageManager) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            return packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA);
        }

        public static ComponentName getCurrentDefaultHome(PackageManager packageManger) {
            // 色々やってるけど要はデフォルトのホームアプリが分かればいいだけ
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);

            List<ResolveInfo> list = packageManger.queryIntentActivities(intent, 0);
            for (ResolveInfo info : list) {
                List<IntentFilter> filters = new ArrayList<>();
                List<ComponentName> activities = new ArrayList<>();
                packageManger.getPreferredActivities(filters, activities, info.activityInfo.packageName);
                if (activities.size() > 0) {
                    return activities.get(0);
                }
            }

            return null;
        }
    }

    /**
     * /packages/apps/Settings/src/com/android/settings/Utils.java
     * 上記クラスを再現。
     */
    private static class Utils {
        /**
         * Determine whether a package is a "system package", in which case certain things (like
         * disabling notifications or disabling the package altogether) should be disallowed.
         */
        public static boolean isSystemPackage(PackageManager pm, PackageInfo pkg) {
            if (sSystemSignature == null) {
                sSystemSignature = new Signature[]{getSystemSignature(pm)};
            }
            return sSystemSignature[0] != null && sSystemSignature[0].equals(getFirstSignature(pkg));
        }

        private static Signature[] sSystemSignature;

        private static Signature getFirstSignature(PackageInfo pkg) {
            if (pkg != null && pkg.signatures != null && pkg.signatures.length > 0) {
                return pkg.signatures[0];
            }
            return null;
        }

        private static Signature getSystemSignature(PackageManager pm) {
            try {
                final PackageInfo sys = pm.getPackageInfo("android", PackageManager.GET_SIGNATURES);
                return getFirstSignature(sys);
            } catch (NameNotFoundException e) {
                // ignore
            }
            return null;
        }
    }
}
