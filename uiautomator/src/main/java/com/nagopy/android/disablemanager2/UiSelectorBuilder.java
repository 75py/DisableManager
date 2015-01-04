package com.nagopy.android.disablemanager2;

import android.os.Build;

import com.android.uiautomator.core.UiSelector;

@SuppressWarnings("unused")
public class UiSelectorBuilder {

    private UiSelector uiSelector;

    public UiSelector build() {
        return uiSelector;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder() {
        uiSelector = new UiSelector();
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder text(String text) {
        uiSelector = uiSelector.text(text);
        return this;
    }

    /**
     * @since API Level 17
     */
    public UiSelectorBuilder textMatches(String regex) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            uiSelector = uiSelector.textMatches(regex);
        }
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder textStartsWith(String text) {
        uiSelector = uiSelector.textStartsWith(text);
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder textContains(String text) {
        uiSelector = uiSelector.textContains(text);
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder className(String className) {
        uiSelector = uiSelector.className(className);
        return this;
    }

    /**
     * @since API Level 17
     */
    public UiSelectorBuilder classNameMatches(String regex) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            uiSelector = uiSelector.classNameMatches(regex);
        }
        return this;
    }

    /**
     * @since API Level 17
     */
    public UiSelectorBuilder className(Class<?> type) {
        uiSelector = uiSelector.className(type.getName());
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder description(String desc) {
        uiSelector = uiSelector.description(desc);
        return this;
    }

    /**
     * @since API Level 17
     */
    public UiSelectorBuilder descriptionMatches(String regex) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            uiSelector = uiSelector.descriptionMatches(regex);
        }
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder descriptionStartsWith(String desc) {
        uiSelector = uiSelector.descriptionStartsWith(desc);
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder descriptionContains(String desc) {
        uiSelector = uiSelector.descriptionContains(desc);
        return this;
    }

    /**
     * @since API Level 18
     */
    public UiSelectorBuilder resourceId(String id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            uiSelector = uiSelector.resourceId(id);
        }
        return this;
    }

    /**
     * @since API Level 18
     */
    public UiSelectorBuilder resourceIdMatches(String regex) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            uiSelector = uiSelector.resourceIdMatches(regex);
        }
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder index(final int index) {
        uiSelector = uiSelector.index(index);
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder instance(final int instance) {
        uiSelector = uiSelector.instance(instance);
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder enabled(boolean val) {
        uiSelector = uiSelector.enabled(val);
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder focused(boolean val) {
        uiSelector = uiSelector.focused(val);
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder focusable(boolean val) {
        uiSelector = uiSelector.focusable(val);
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder scrollable(boolean val) {
        uiSelector = uiSelector.scrollable(val);
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder selected(boolean val) {
        uiSelector = uiSelector.selected(val);
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder checked(boolean val) {
        uiSelector = uiSelector.checked(val);
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder clickable(boolean val) {
        uiSelector = uiSelector.clickable(val);
        return this;
    }

    /**
     * @since API Level 18
     */
    public UiSelectorBuilder checkable(boolean val) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            uiSelector = uiSelector.checkable(val);
        }
        return this;
    }

    /**
     * @since API Level 17
     */
    public UiSelectorBuilder longClickable(boolean val) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            uiSelector = uiSelector.longClickable(val);
        }
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder childSelector(UiSelector selector) {
        uiSelector = uiSelector.childSelector(selector);
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder fromParent(UiSelector selector) {
        uiSelector = uiSelector.fromParent(selector);
        return this;
    }

    /**
     * @since API Level 16
     */
    public UiSelectorBuilder packageName(String name) {
        uiSelector = uiSelector.packageName(name);
        return this;
    }

    /**
     * @since API Level 17
     */
    public UiSelectorBuilder packageNameMatches(String regex) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            uiSelector = uiSelector.packageNameMatches(regex);
        }
        return this;
    }

}
