package com.mid.metp.model;

public class ApkInfo {
	private String apkName;
	private String apkLabel;
	private String fullPath;
	private String packageName;
	private String version;
	private boolean debuggable;

	public String getFullPath() {
		return this.fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public String getApkName() {
		return this.apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isDebuggable() {
		return this.debuggable;
	}

	public void setDebuggable(boolean debuggable) {
		this.debuggable = debuggable;
	}

	public String getApkLabel() {
		return this.apkLabel;
	}

	public void setApkLabel(String apkLabel) {
		this.apkLabel = apkLabel;
	}

}
