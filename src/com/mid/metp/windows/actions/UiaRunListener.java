package com.mid.metp.windows.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.mid.metp.Config;
import com.mid.metp.model.ApkInfo;
import com.mid.metp.model.Phone;
import com.mid.metp.model.TestCase;
import com.mid.metp.util.AndroidUtility;
import com.mid.metp.util.Helper;
import com.mid.metp.util.Log;
import com.mid.metp.util.Utility;
import com.mid.metp.workflow.UiAutomatorExecuter;

public class UiaRunListener implements ActionListener {

	private JTextField txtApk;
	private JTextField txtJar;
	private JTextField txtXml;
	private DefaultListModel targetsModel;
	private DefaultListModel testcaseModel;
	private JTextField txtTestRound;
	private JCheckBox cbLogcat;
	private JCheckBox cbScreenCap;
	private JCheckBox cbPerfMonitor;
	private JButton makeReportButton;

	private ApkInfo apkInfo;
	private AndroidUtility utility;
	private List<Phone> targets = new ArrayList<Phone>();
	private List<TestCase> cases = new ArrayList<TestCase>();
	private int testRound;
	private String logPath = "";

	public String getLogPath() {
		return this.logPath;
	}

	public ArrayList<UiAutomatorExecuter> getUiaExecuters() {
		return this.uiaExecuters;
	}

	private ArrayList<UiAutomatorExecuter> uiaExecuters = new ArrayList<UiAutomatorExecuter>();

	public UiaRunListener(JTextField txtApk, JTextField txtJar,
			JTextField txtXml, DefaultListModel targetsModel,
			DefaultListModel testcaseModel, JTextField txtTestRound,
			JCheckBox cbLogcat, JCheckBox cbScreenCap, JCheckBox cbPerfMonitor,
			JButton makeReportButton) {
		super();

		this.txtApk = txtApk;
		this.txtJar = txtJar;
		this.txtXml = txtXml;
		this.targetsModel = targetsModel;
		this.testcaseModel = testcaseModel;
		this.txtTestRound = txtTestRound;
		this.cbLogcat = cbLogcat;
		this.cbScreenCap = cbScreenCap;
		this.cbPerfMonitor = cbPerfMonitor;
		this.makeReportButton = makeReportButton;
	}

	public void actionPerformed(ActionEvent e) {
		Log.log("进入UiAutomator执行逻辑...... ");

		this.utility = new AndroidUtility(Utility.getMachineType());
		this.apkInfo = this.utility.getApkInfo(this.txtApk.getText());

		// this.packageName = this.utility.getPackageName(this.apkfile);

		if (this.apkInfo.getPackageName() == null
				|| this.apkInfo.getPackageName() == "") {
			JOptionPane.showMessageDialog(null, "请重新选择apk包", "无法确定包名",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		JButton currentButton = (JButton) e.getSource();
		currentButton.setEnabled(false);
		this.makeReportButton.setEnabled(false);

		Log.log("*****************************************************");
		Log.log("---Apk: " + this.apkInfo.getApkName());
		Log.log("---Package: " + this.apkInfo.getPackageName());
		Log.log("---Testround: " + this.txtTestRound.getText());
		Log.log("---Logcat: " + Boolean.toString(this.cbLogcat.isSelected()));
		Log.log("---Perf: " + Boolean.toString(this.cbPerfMonitor.isSelected()));
		Log.log("---Screenshots: "
				+ Boolean.toString(this.cbScreenCap.isSelected()));
		Log.log("*****************************************************");

		for (int i = 0; i < this.testcaseModel.getSize(); i++) {
			TestCase tc = (TestCase) this.testcaseModel.getElementAt(i);
			// System.out.println(tc.getName());
			this.cases.add(tc);
		}

		for (int i = 0; i < this.targetsModel.getSize(); i++) {
			Phone phone = (Phone) this.targetsModel.getElementAt(i);
			// System.out.println(phone.toString());
			this.targets.add(phone);
		}

		if (this.targets.size() == 0) {
			JOptionPane.showMessageDialog(null, "请您选择手机", "请选择手机",
					JOptionPane.ERROR_MESSAGE);
			currentButton.setEnabled(true);
			this.makeReportButton.setEnabled(true);
			return;
		}

		this.logPath = Helper.combineStrings(Config.ROOTPATH_UIA,
				this.apkInfo.getPackageName(), File.separator,
				Utility.getFormatDate(), File.separator);

		File rootFile = new File(this.logPath);
		if (!rootFile.exists()) {
			rootFile.mkdirs();
		}

		for (Phone targetPhone : this.targets) {
			UiAutomatorExecuter executer = new UiAutomatorExecuter(
					currentButton, this.apkInfo, this.txtJar.getText(),
					this.txtXml.getText(), targetPhone, this.cases,
					Integer.parseInt(this.txtTestRound.getText()),
					this.cbLogcat.isSelected(),
					this.cbPerfMonitor.isSelected(),
					this.cbScreenCap.isSelected(), this.logPath);

			executer.execute();
			this.uiaExecuters.add(executer);
		}
	}
}
