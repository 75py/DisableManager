package com.nagopy.android.disablemanager2;

import android.os.Build;

import com.android.uiautomator.core.UiSelector;

/**
 * {@link UiSelector}のラッパークラス.<br>
 * APIレベルが足りない場合は例外とせず、無視する。
 */
@SuppressWarnings("unused")
@Deprecated
public class UiSelectorCompat extends UiSelector {

    public UiSelectorCompat() {
        super();
    }

    /**
     * @since API Level 17
     */
    @Override
    public UiSelector textMatches(String regex) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            super.textMatches(regex);
        }
        return this;
    }

    /**
     * @since API Level 17
     */
    @Override
    public UiSelector classNameMatches(String regex) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            super.classNameMatches(regex);
        }
        return this;
    }

    public <T> UiSelector className(Class<T> type) {
        return super.className(type.getName());
    }

    /**
     * @since API Level 17
     */
    @Override
    public UiSelector descriptionMatches(String regex) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            super.descriptionMatches(regex);
        }
        return this;
    }

    /**
     * @since API Level 18
     */
    @Override
    public UiSelector resourceId(String id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            super.resourceId(id);
        }
        return this;
    }

    /**
     * @since API Level 18
     */
    @Override
    public UiSelector resourceIdMatches(String regex) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            super.resourceIdMatches(regex);
        }
        return this;
    }

    /**
     * @since API Level 18
     */
    @Override
    public UiSelector checkable(boolean val) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            super.checkable(val);
        }
        return this;
    }

    /**
     * @since API Level 17
     */
    @Override
    public UiSelector longClickable(boolean val) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            super.longClickable(val);
        }
        return this;
    }

    /**
     * @since API Level 17
     */
    @Override
    public UiSelector packageNameMatches(String regex) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            super.packageNameMatches(regex);
        }
        return this;
    }
}
