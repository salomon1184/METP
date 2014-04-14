package com.mid.metp.workflow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;

import com.mid.metp.model.MonkeyRunInfo;
import com.mid.metp.model.Phone;
import com.mid.metp.model.Result;
import com.mid.metp.util.AndroidUtility;
import com.mid.metp.util.Helper;
import com.mid.metp.util.Log;
import com.mid.metp.util.Utility;

public class MonkeyExecuter extends BaseExecuter {
	private AndroidUtility utility;
	private final MonkeyRunInfo runInfo;
	private String logPath;
	private final String rootPath;
	private String monkeyLogPath;
	private String logcatPath;
	private Timer timer;
	private final JButton startButton;
	private final JButton makeReportButton;
	private File resultFile;

	public MonkeyExecuter(JButton startButton, JButton makeReportButton,
			MonkeyRunInfo runInfo, Phone phone, String logPath) {
		super(runInfo.getApk().getFullPath(),
				runInfo.getApk().getPackageName(), phone, null);

		this.runInfo = runInfo;
		this.startButton = startButton;
		this.makeReportButton = makeReportButton;

		this.rootPath = logPath;

		try {
			this.utility = new AndroidUtility(Utility.getMachineType());
			// /文件格式：log/monkey/20130826-211631/samsung__google__GalaxyNexus__4.2.2__014691470D01000A/
			// / monkey_1.log
			// / logcat_1.log
			// / log/monkey/20130826-211631/result.html
			this.logPath = Helper.combineStrings(logPath,
					this.target.toString(), File.separator);
			File rootFile = new File(this.logPath);
			if (!rootFile.exists()) {
				rootFile.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected List<Result> doInBackground() throws Exception {
		Log.logFatal("Phone-" + this.target.toString()
				+ ": Start Monkey workflow.");
		String rfp = Helper.combineStrings(this.rootPath,
				this.target.toString(), ".log");

		this.resultFile = new File(rfp);

		if (this.resultFile.exists()) {
			this.resultFile.delete();
		}

		this.resultFile.createNewFile();

		for (int i = 0; i < this.runInfo.getTestRound(); i++) {
			// 如果监测到有stop消息，则停止整个循环
			if (this.isCancelled()) {
				Log.logError("用户点击Stop按钮，停止测试");
				break;
			}

			Log.log("Phone-" + this.target.toString() + ": Round "
					+ Integer.toString(i + 1));
			this.utility.killMonkey(this.target);
			this.utility.logcatClear(this.target);
			this.utility.installApk(this.target, this.apkfile);

			this.monkeyLogPath = Helper.combineStrings(this.logPath, "monkey_",
					Integer.toString(i), ".log");
			this.logcatPath = Helper.combineStrings(this.logPath, "logcat_",
					Integer.toString(i), ".log");

			File monkeyLogFile = new File(this.monkeyLogPath);
			if (monkeyLogFile.exists()) {
				monkeyLogFile.delete();
			}
			monkeyLogFile.createNewFile();

			// 每隔一秒搜索一次monkey文件，如果找到error信息，停止搜索
			// Log.log("启动搜索线程");
			long startDate = System.currentTimeMillis();
			this.timer = new Timer();
			this.timer.schedule(new MonkeyErrorSearchTask(i,
					this.monkeyLogPath, this.logcatPath, startDate,
					this.runInfo.getTestTime()), 10000, 1000);

			// 主执行逻辑单元
			this.utility.runMonkey(this.target, this.packageName,
					this.monkeyLogPath);
			Thread.sleep(15000);
		}

		Log.log("生成最终报告文件: " + rfp);
		return null;
	}

	@Override
	protected void done() {
		this.startButton.setEnabled(true);
		this.makeReportButton.setEnabled(true);

		Log.logFatal("Phone-" + this.target.toString()
				+ ": Finished Monkey Workflow.");
		super.done();
	}

	class MonkeyErrorSearchTask extends TimerTask {

		private final long startDate;
		private final long interval;
		private final int testRound;
		private final String monkeyLogPath;
		private final String logcatPath;

		private long startIndex = 0;

		public MonkeyErrorSearchTask(int testRound, String monkeyLogPath,
				String logcatPath, long startTime, int interval)
				throws FileNotFoundException {
			this.testRound = testRound;
			this.startDate = startTime;
			this.interval = interval * 1000;
			this.monkeyLogPath = monkeyLogPath;
			this.logcatPath = logcatPath;
		}

		@Override
		public void run() {
			try {
				String errorString = MonkeyExecuter.this.utility
						.searchMonkeyError(this.monkeyLogPath, this.startIndex);
				// 如果找到error信息，或者搜索时间超出了预定间隔，或者监控到stop消息，就取消本次搜索任务
				// 注意： 就单个循环来讲最终的执行流程还是要走完
				if (MonkeyExecuter.this.isCancelled()
						|| !errorString.equals("")
						|| ((System.currentTimeMillis() - this.startDate) > this.interval)) {
					MonkeyExecuter.this.utility
							.killMonkey(MonkeyExecuter.this.target);
					MonkeyExecuter.this.utility.logcatDump(
							MonkeyExecuter.this.target, this.logcatPath);

					// Log.log("从文件" + this.monkeyLogPath +
					// "搜索error信息，并写入文件...");

					this.startIndex += 100;
					String errorString2 = MonkeyExecuter.this.utility
							.searchMonkeyError(this.monkeyLogPath,
									this.startIndex);
					// this.startIndex = this.startIndex
					// + this.monkeyLogPath.length();
					// Log.logFatal("start Index : " + this.startIndex);
					FileWriter fw = new FileWriter(
							MonkeyExecuter.this.resultFile, true);
					;
					if (errorString2 != "") {
						Log.logError("搜索到error信息： " + errorString2);
						// <th>phone</th>
						// <th>round</th>
						// <th>type</th>
						// <th>app</th>
						// <th>short message</th>
						fw.append(Integer.toString(this.testRound) + "	FAIL	"
								+ errorString2 + "\r\n");

					} else {
						fw.append(Integer.toString(this.testRound) + "	PASS	"
								+ "\r\n");
						Log.log("没有找到Error信息");
					}
					fw.flush();
					fw.close();

					MonkeyExecuter.this.timer.cancel();
					Log.log(".................本轮测试停止");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
