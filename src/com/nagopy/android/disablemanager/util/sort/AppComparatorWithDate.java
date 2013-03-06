package com.nagopy.android.disablemanager.util.sort;

import java.util.Comparator;

import com.nagopy.android.disablemanager.util.AppStatus;
import com.nagopy.android.disablemanager.util.ChangedDateUtils;

class AppComparatorWithDate implements Comparator<AppStatus> {

	private static final AppComparatorWithDate instance = new AppComparatorWithDate();

	private ChangedDateUtils mDateUtils;

	private AppComparatorWithDate() {}

	public static final Comparator<AppStatus> getInstance(ChangedDateUtils dateUtils) {
		instance.mDateUtils = dateUtils;
		return instance;
	}

	@Override
	public int compare(final AppStatus obj0, final AppStatus obj1) {
		String pkgName0 = obj0.getPackageName();
		String pkgName1 = obj1.getPackageName();

		long date0 = mDateUtils.get(pkgName0);
		long date1 = mDateUtils.get(pkgName1);
		if (date0 != date1) {
			return (int) (date1 - date0);
		}

		String label0 = obj0.getLabel();
		String label1 = obj1.getLabel();

		int ret = label0.compareToIgnoreCase(label1);
		// ラベルで並び替え、同じラベルがあったらパッケージ名で
		if (ret == 0) {
			ret = pkgName0.compareToIgnoreCase(pkgName1);
		}
		return ret;
	}
}
