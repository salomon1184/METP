package com.mid.metp.windows.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.mid.metp.Config;
import com.mid.metp.model.MonkeyRunInfo;
import com.mid.metp.model.Phone;
import com.mid.metp.result.MonkeyResultHandler;
import com.mid.metp.util.AndroidUtility;
import com.mid.metp.util.FileZipper;
import com.mid.metp.util.Helper;
import com.mid.metp.util.Log;
import com.mid.metp.util.MailSenderInfo;
import com.mid.metp.util.ParamRetriever;
import com.mid.metp.util.SimpleMailSender;
import com.mid.metp.util.Utility;
import com.mid.metp.workflow.MonkeyExecuter;

/**
 * 响应按钮Start事件，开始执行测试，并维护相应UI界面状态
 * 
 * @author defu
 * 
 */
public class MonkeyRunListener implements ActionListener {

	private final DefaultListModel<Phone> targetsModel;
	private final JTextField tfApkPath;
	private final JTextField tfRound;
	private final JTextField tfTime;
	private final JButton makeReportButton;

	private AndroidUtility utility;
	// private ArrayList<Phone> targets = new ArrayList<Phone>();

	private final MonkeyRunInfo runInfo = new MonkeyRunInfo();
	// private ApkInfo apkInfo;
	// private int round;
	// private int roundTime;
	// private final ArrayList<MonkeyExecuter> monkeyExecuters = new
	// ArrayList<MonkeyExecuter>();
	private final Hashtable<Phone, MonkeyExecuter> phoneWorkingThreads = new Hashtable<Phone, MonkeyExecuter>();

	private String logPath = "";
	private final Timer timer = new Timer();

	MonkeyResultHandler handler;

	public MonkeyRunListener(JTextField tfApkPath, DefaultListModel phoneList,
			JTextField tfRound, JTextField tfTime, JButton makeReportButton) {
		this.targetsModel = phoneList;
		this.tfApkPath = tfApkPath;
		this.tfRound = tfRound;
		this.tfTime = tfTime;
		this.makeReportButton = makeReportButton;
	}

	public String getLogPath() {
		return this.logPath;
	}

	public Hashtable<Phone, MonkeyExecuter> getPhoneWorkingThreads() {
		return this.phoneWorkingThreads;
	}

	// public List<Phone> getTargets() {
	// return this.targets;
	// }
	//
	// public void setTargets(ArrayList<Phone> targets) {
	// this.targets = targets;
	// }

	// public ArrayList<MonkeyExecuter> getMonkeyExecuters() {
	// return this.monkeyExecuters;
	// }

	public void actionPerformed(ActionEvent e) {

		this.utility = new AndroidUtility(Utility.getMachineType());
		this.runInfo.setApk(this.utility.getApkInfo(this.tfApkPath.getText()));

		this.runInfo.setTestRound(Utility.getInt(this.tfRound.getText()));
		this.runInfo.setTestTime(Utility.getInt(this.tfTime.getText()));

		// this.packageName = this.utility.getPackageName(this.apkfile);

		if ((this.runInfo.getApk().getPackageName() == null)
				|| (this.runInfo.getApk().getPackageName() == "")) {
			JOptionPane.showMessageDialog(null, "请重新选择apk包", "无法确定包名",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		JButton currentButton = (JButton) e.getSource();
		currentButton.setEnabled(false);
		this.makeReportButton.setEnabled(false);

		Log.log("Enter Moneky workflow...... ");
		Log.log("*****************************************************");
		Log.log("---Apk: " + this.runInfo.getApk().getApkName());
		Log.log("---Package: " + this.runInfo.getApk().getPackageName());
		Log.log("---Testround: " + this.runInfo.getTestRound());
		Log.log("---Roundtime: " + this.runInfo.getTestTime());
		Log.log("*****************************************************");

		if (this.targetsModel.getSize() == 0) {
			JOptionPane.showMessageDialog(null, "Please select phone",
					"Please select target phone!", JOptionPane.ERROR_MESSAGE);
			currentButton.setEnabled(true);
			this.makeReportButton.setEnabled(true);
			return;
		}

		this.logPath = Helper.combineStrings(Config.ROOTPATH_MONKEY,
				this.runInfo.getApk().getPackageName(), File.separator,
				Utility.getFormatDate(), File.separator);

		File rootFile = new File(this.logPath);
		if (!rootFile.exists()) {
			rootFile.mkdirs();
		}

		for (int i = 0; i < this.targetsModel.getSize(); i++) {
			Phone phone = this.targetsModel.getElementAt(i);

			// System.out.println(phone.toString());
			MonkeyExecuter me = new MonkeyExecuter(currentButton,
					this.makeReportButton, this.runInfo, phone, this.logPath);
			// this.monkeyExecuters.add(me);
			me.execute();
			this.phoneWorkingThreads.put(phone, me);
		}

		// 在这里增加wait和线程done的判断，一旦所有线程执行完毕，就立刻生成报告
		this.timer.schedule(new MonkeyStopWatcher(), 50000, 10000);
	}

	/**
	 * 监控所有手机monkey是否完成，如果完成则自动生成报告，发送
	 * 
	 * @author defu
	 * 
	 */
	class MonkeyStopWatcher extends TimerTask {

		@Override
		public void run() {
			for (MonkeyExecuter mr : MonkeyRunListener.this.phoneWorkingThreads
					.values()) {
				int count = 0;
				if (mr.isCancelled() || mr.isDone()) {
					// Log.logError(mr.getState().toString());
					count++;
				} else {
					return;
				}

				// Log.log("debug: count= " + count);
				// Log.log("debug: executers= "
				// + MonkeyRunListener.this.phoneWorkingThreads.values()
				// .size());
				if (count < MonkeyRunListener.this.phoneWorkingThreads.values()
						.size()) {
					return;
				}
			}

			MonkeyRunListener.this.zipAndMailReport();
			MonkeyRunListener.this.timer.cancel();
			Log.logFatal(".................Monkey loop stopped");
		}
	}

	public void zipAndMailReport() {
		String logFolder = this.logPath;
		this.handler = new MonkeyResultHandler(this.runInfo,
				Collections.list(this.phoneWorkingThreads.keys()), logFolder);

		String reportContent = this.handler.generateHtmlReport();
		if ((reportContent == "") || (reportContent == null)) {
			return;
		}

		this.handler.generateReportFile(reportContent);
		// 邮件发送报告
		ParamRetriever xmlParam = ParamRetriever.getParam();
		if (xmlParam.getParam("emailToggle").equalsIgnoreCase("on")) {

			MailSenderInfo senderInfo = new MailSenderInfo().parseBasicInfo();
			senderInfo.setSubject("Monkey Report of "
					+ this.runInfo.getApk().getApkLabel() + " v"
					+ this.runInfo.getApk().getVersion());

			senderInfo.setContent(this.handler.generateHtmlReport());

			File logDirectory = new File(logFolder);

			String dateString = logFolder.substring(logFolder.length() - 15,
					logFolder.length() - 1);
			String subFolder = logFolder.substring(0, logFolder.length() - 15);
			String zipFile = subFolder + "log" + dateString + ".zip";

			FileZipper zipper = new FileZipper(zipFile);
			zipper.compress(logFolder);

			senderInfo.setAttachFileNames(new String[] { zipFile });
			SimpleMailSender.sendHtmlMail(senderInfo);
			Log.logFatal("E-mail Sent！");

		} else {
			Log.log("E-mail toggle set off or parameters error, failed to send E-mail!");
		}
	}
}
