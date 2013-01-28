package com.nagopy.android.disabledapps;

public class AppStatus {
	private String label, packageName;
	// private Drawable icon;
	private boolean enabled, system, canDisable;

	public AppStatus(String label, String packageName, boolean enabled, boolean system, boolean canDisable) {
		this.label = label;
		this.packageName = packageName;
		this.enabled = enabled;
		this.system = system;
		this.canDisable = canDisable;
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

	@Override
	public String toString() {
		return getLabel() + ":" + getPackageName() + ", enabled:" + isEnabled() + ", system:" + isSystem()
				+ ", canDisable:" + canDisable();
	}
}
