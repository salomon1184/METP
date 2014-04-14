package com.mid.metp.model;

import java.util.List;

public class MonkeyResult {

	private String phone;
	private int count;
	private int failures;
	private int successes;
	private List<MonkeyFailInfo> fails;

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getCount() {
		return this.successes + this.failures;
	}

	public int getFailures() {
		return this.failures;
	}

	public void setFailures(int failures) {
		this.failures = failures;
	}

	public int getSuccesses() {
		return this.successes;
	}

	public void setSuccesses(int successes) {
		this.successes = successes;
	}

	public List<MonkeyFailInfo> getFails() {
		return this.fails;
	}

	public void setFails(List<MonkeyFailInfo> fails) {
		this.fails = fails;
	}

}
