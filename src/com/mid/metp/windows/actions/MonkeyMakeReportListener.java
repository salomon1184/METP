package com.mid.metp.windows.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MonkeyMakeReportListener implements ActionListener {

	private final MonkeyRunListener runListener;

	public MonkeyMakeReportListener(MonkeyRunListener listener) {
		this.runListener = listener;
	}

	public void actionPerformed(ActionEvent e) {

		this.runListener.zipAndMailReport();
		// String logFolder = this.runListener.getLogPath();
		// this.runListener.handler = new MonkeyResultHandler(
		// this.runListener.getApkInfo(),
		// Collections.list(this.runListener.getPhoneWorkingThreads()
		// .keys()), logFolder);
		//
		// this.runListener.handler.generateReportFile(this.runListener.handler
		// .generateHtmlReport());
		// // 邮件发送报告
		// Param xmlParam = Param.getParam();
		// if (xmlParam.getParam("emailToggle").equalsIgnoreCase("on")) {
		//
		// MailSenderInfo senderInfo = new MailSenderInfo().parseBasicInfo();
		// senderInfo.setSubject("Monkey Report of "
		// + this.runListener.getApkInfo().getPackageName()
		// + this.runListener.getApkInfo().getVersion());
		//
		// senderInfo
		// .setContent(this.runListener.handler.generateHtmlReport());
		//
		// File logDirectory = new File(logFolder);
		//
		// String dateString = logFolder.substring(logFolder.length() - 15,
		// logFolder.length() - 1);
		// String subFolder = logFolder.substring(0, logFolder.length() - 15);
		// String zipFile = subFolder + "log" + dateString + ".zip";
		//
		// FileZipper zipper = new FileZipper();
		// try {
		// zipper.zip(zipFile, logDirectory);
		// } catch (Exception e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		//
		// senderInfo.setAttachFileNames(new String[] { zipFile });
		// SimpleMailSender.sendHtmlMail(senderInfo);
		// Log.logFatal("邮件发送成功！");
		//
		// } else {
		// Log.log("参数为off或者错误参数，不发送邮件");
		// }

	}
}
