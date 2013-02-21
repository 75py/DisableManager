/*
 * Copyright 2013 75py
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

package com.nagopy.android.disablemanager.util.share;

import java.util.ArrayList;

import com.nagopy.android.disablemanager.util.AppStatus;

/**
 * ラベル名・パッケージ名を共有するShareTextMaker
 */
class ShareLabelAndPackage implements ShareTextMaker {

	@Override
	public void make(ArrayList<AppStatus> appsList, StringBuffer sb, String lineBreak) {
		String singleLineBreak = System.getProperty("line.separator");
		for (AppStatus appStatus : appsList) {
			sb.append(appStatus.getLabel());
			sb.append(singleLineBreak);
			sb.append(appStatus.getPackageName());
			sb.append(lineBreak);
		}
	}

}
