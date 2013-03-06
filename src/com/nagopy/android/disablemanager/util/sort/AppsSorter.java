package com.nagopy.android.disablemanager.util.sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.nagopy.android.disablemanager.util.AppStatus;
import com.nagopy.android.disablemanager.util.ChangedDateUtils;

public class AppsSorter {

	public static void sort(List<AppStatus> list) {
		Comparator<AppStatus> comparator = AppComparator.getInstance();
		Collections.sort(list, comparator);
	}

	public static void sort(ChangedDateUtils dateUtils, List<AppStatus> list) {
		Comparator<AppStatus> comparator = AppComparatorWithDate.getInstance(dateUtils);
		Collections.sort(list, comparator);
	}

}
