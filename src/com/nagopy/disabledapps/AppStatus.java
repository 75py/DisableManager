package com.nagopy.disabledapps;

import android.graphics.drawable.Drawable;

public class AppStatus {
	private String label, packageName;
	private Drawable icon;
	private boolean enabled, system, canDisable;

	public AppStatus(String label, String packageName, boolean enabled, boolean system, boolean canDisable,
			Drawable icon) {
		this.label = label;
		this.packageName = packageName;
		this.enabled = enabled;
		this.system = system;
		this.canDisable = canDisable;
		this.icon = icon;
	}

	public String getLabel() {
		return label;
	}

	public String getPackageName() {
		return packageName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isSystem() {
		return system;
	}

	public boolean canDisable() {
		return canDisable;
	}

	public Drawable getIcon() {
		return icon;
	}

	@Override
	public String toString() {
		return getLabel() + ":" + getPackageName() + ", enabled:" + isEnabled() + ", system:" + isSystem()
				+ ", canDisable:" + canDisable();
	}
}
