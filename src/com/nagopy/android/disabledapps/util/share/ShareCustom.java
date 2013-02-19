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

package com.nagopy.android.disabledapps.util.share;

import java.util.ArrayList;

import android.content.Context;

import com.nagopy.android.disabledapps.util.AppStatus;
import com.nagopy.android.disabledapps.util.CommentsUtils;
import com.nagopy.android.disabledapps.util.FormatUtils;

/**
 * カスタムフォーマットで共有するShareTextMaker
 */
class ShareCustom implements ShareTextMaker {

	private CommentsUtils mCommentsUtils;
	private FormatUtils mFormatUtils;

	public ShareCustom(Context context) {
		mCommentsUtils = new CommentsUtils(context);
		mFormatUtils = new FormatUtils(context);
	}

	@Override
	public void make(ArrayList<AppStatus> appsList, StringBuffer sb, String lineBreak) {
		for (AppStatus appStatus : appsList) {
			String comment = mCommentsUtils.restoreComment(appStatus.getPackageName());
			sb.append(mFormatUtils.format(appStatus.getLabel(), appStatus.getPackageName(), comment));
			sb.append(lineBreak);
		}
	}

}
