package com.mid.metp.workflow;

import java.io.File;
import java.util.List;

import javax.swing.JButton;

import com.mid.metp.model.ApkInfo;
import com.mid.metp.model.Phone;
import com.mid.metp.model.Result;
import com.mid.metp.model.TestCase;
import com.mid.metp.util.AndroidUtility;
import com.mid.metp.util.Helper;
import com.mid.metp.util.Log;
import com.mid.metp.util.Utility;

public class UiAutomatorExecuter extends BaseExecuter {

	private AndroidUtility utility;
	private ApkInfo apkInfo;
	private int testRound;
	private String rootPath;
	private String logPath;
	private String jarFilePath;
	private boolean isLogcat;
	private boolean isPerf;
	private boolean isScreenCap;
	private JButton startButton;
	private String xmlFilePath;

	public UiAutomatorExecuter(JButton startButton, ApkInfo apkInfo,
			String jarFile, String xmlFile, Phone phone,
			List<TestCase> testCases, int testRound, boolean isLogcatOn,
			boolean isPerfOn, boolean isScreenCap, String logPath) {
		super(apkInfo.getFullPath(), apkInfo.getPackageName(), phone, testCases);

		this.apkInfo = apkInfo;
		this.startButton = startButton;
		this.jarFilePath = jarFile;
		this.xmlFilePath = xmlFile;
		this.testRound = testRound;
		this.isLogcat = isLogcatOn;
		this.isPerf = isPerfOn;
		this.isScreenCap = isScreenCap;
		this.rootPath = logPath;
	}

	@Override
	protected List<Result> doInBackground() throws Exception {

		/**
		 * 单个手机执行过程： 1. 安装apk 2. 安装jar 3. 将xml放入手机中 4. 按照testround， case设置循环
		 * 依次执行
		 * 
		 */
		try {
			this.utility = new AndroidUtility(Utility.getMachineType());
			this.logPath = Helper.combineStrings(this.rootPath,
					this.target.toString(), File.separator);
			File rootFile = new File(this.logPath);

			if (!rootFile.exists()) {
				rootFile.mkdirs();
			}
			// System.out.println(this.rootLogPath);
		} catch (Exception e) {

			e.printStackTrace();
		}

		this.utility.cleanDevice(this.target);
		this.utility.installApk(this.target, this.apkfile);
		this.utility.installTestJar(this.target, this.jarFilePath);
		if (this.xmlFilePath != null && this.xmlFilePath != "") {
			this.utility.pushParamXml(this.target, this.xmlFilePath);
		}

		for (int i = 0; i < this.testRound; i++) {
			Log.log("手机-" + this.target.toString() + "进入第  " + this.testRound
					+ " 轮执行");

			// 如果监测到有stop消息，则停止整个循环
			if (this.isCancelled()) {
				Log.log("用户点击Stop按钮，停止测试");
				break;
			}

			// System.out.println(this.cases.size());
			for (TestCase tc : this.cases) {
				if (this.isCancelled()) {
					Log.log("用户点击Stop按钮，停止测试");
					break;
				}

				Log.log("手机-" + this.target.toString() + "--执行用例"
						+ tc.getName());

				String uiaLogPath = Helper.combineStrings(this.logPath, "uia_",
						tc.getMethodName(), "_", Integer.toString(i), ".log");
				// System.out.println(uiaLogPath);

				File uiaLogFile = new File(uiaLogPath);
				if (uiaLogFile.exists()) {
					uiaLogFile.delete();
				}

				uiaLogFile.createNewFile();

				// System.out.println(this.jarFilePath);
				String[] spilts = this.jarFilePath.split(File.separator
						+ File.separator);
				String jarFileName = spilts[spilts.length - 1];
				// System.out.println(jarFileName);
				this.utility.runUiaTestCase(this.target, jarFileName,
						uiaLogPath, tc, this.isLogcat, this.isPerf,
						this.isScreenCap);
			}

			this.startButton.setEnabled(true);
			Log.log("手机-" + this.target.toString() + "用例执行完毕");

			String logcatPath = Helper.combineStrings(this.logPath, "logcat",
					File.separator);
			String screenshotPath = Helper.combineStrings(this.logPath,
					"screenshots", File.separator);

			Utility.makeDir(logcatPath);
			Utility.makeDir(screenshotPath);

			this.utility.pullLogcat(this.target, logcatPath);
			this.utility.pullScreenShot(this.target, screenshotPath);

			// 从手机拉出logcat，截图，性能监控数据等
			// 分析数据生成报告
		}

		return null;
	}
}
