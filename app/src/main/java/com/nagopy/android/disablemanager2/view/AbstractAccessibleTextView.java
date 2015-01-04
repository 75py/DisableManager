package com.nagopy.android.disablemanager2.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;

/**
 * uiautomatorで、クラス名で取得できるカスタムViewのサンプル.<br>
 * 作ってはみたが、descriptionでいいやということで放置。一応、これでnew UiSelector().className("")を使って取得できる。<br>
 */
@SuppressWarnings("unused")
public abstract class AbstractAccessibleTextView extends TextView {

    public AbstractAccessibleTextView(Context context) {
        super(context);
    }

    public AbstractAccessibleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractAccessibleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbstractAccessibleTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setPackageName(getContext().getPackageName());
        info.setClassName(getClass().getName());
    }

}
