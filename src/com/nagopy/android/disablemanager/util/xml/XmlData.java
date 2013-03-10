package com.nagopy.android.disablemanager.util.xml;

import java.util.HashSet;

public class XmlData {
	private String device;
	private String build;
	private String type;
	private HashSet<String> packages = new HashSet<String>();
	private String errorMessage;

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getBuild() {
		return build;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public HashSet<String> getPackages() {
		return packages;
	}

	public void setPackages(HashSet<String> packages) {
		this.packages = packages;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Device:");
		sb.append(getDevice());
		sb.append("\nBuild:");
		sb.append(getBuild());
		sb.append("\nType:");
		sb.append(getType());
		sb.append("\nPackages:");
		sb.append(getPackages());
		sb.append("\nErrorMessage:");
		sb.append(getErrorMessage());
		return sb.toString();
	}
}