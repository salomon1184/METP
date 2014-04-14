package com.mid.metp.model;

public class Result {

	private String caseName;
	private String outputLog;
	private String logcatLog;
	private ResultStatus status;

	public String getCaseName() {
		return this.caseName;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public String getOutputLog() {
		return this.outputLog;
	}

	public void setOutputLog(String outputLog) {
		this.outputLog = outputLog;
	}

	public String getLogcatLog() {
		return this.logcatLog;
	}

	public void setLogcatLog(String logcatLog) {
		this.logcatLog = logcatLog;
	}

	public ResultStatus getStatus() {
		return this.status;
	}

	public void setStatus(ResultStatus status) {
		this.status = status;
	}

	/**
	 * 执行状态：0、NOTSTART, 1 RUNNING；2 SUCCESS， 3 FAIL， 4 ERROR, 5 CANCEL
	 */
	public enum ResultStatus {
		NOTSTART(0), RUNNING(1), SUCCESS(2), FAIL(3), ERROR(4), CANCEL(5);

		public static ResultStatus parse(int value) {
			ResultStatus retValue = ResultStatus.NOTSTART;
			for (ResultStatus item : ResultStatus.values()) {
				if (value == item.getValue()) {
					retValue = item;
					break;
				}
			}

			return retValue;
		}

		private final int value;

		ResultStatus(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

	}
}
