package com.mid.metp.model;

public class MonkeyRunInfo {

	private ApkInfo apk;
	private int testRound;
	private int testTime;

	public ApkInfo getApk() {
		return this.apk;
	}

	public void setApk(ApkInfo apk) {
		this.apk = apk;
	}

	public int getTestRound() {
		return this.testRound;
	}

	public void setTestRound(int testRound) {
		this.testRound = testRound;
	}

	public int getTestTime() {
		return this.testTime;
	}

	public void setTestTime(int testTime) {
		this.testTime = testTime;
	}
}
