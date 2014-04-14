package com.mid.metp.util;

import java.util.Properties;

public class MailSenderInfo {
	// 发送邮件的服务器的IP和端口
	private String mailServerHost = "mail.renren-inc.com";
	private String mailServerPort = "25";
	// 邮件发送者的地址
	private String fromAddress;
	// 邮件接收者的地址
	private String toAddress;

	private String ccAddress;
	// 登陆邮件发送服务器的用户名和密码
	private String userName;
	private String password;
	// 是否需要身份验证
	private boolean validate = false;
	// 邮件主题
	private String subject;
	// 邮件的文本内容
	private String content;
	// 邮件附件的文件名
	private String[] toAddresses;
	private String[] ccAddresses;
	private String[] attachFileNames;

	/**
	 * 获得邮件会话属性
	 */
	public Properties getProperties() {
		Properties p = new Properties();
		p.put("mail.smtp.host", this.mailServerHost);
		p.put("mail.smtp.port", this.mailServerPort);
		p.put("mail.smtp.auth", this.validate ? "true" : "false");
		return p;
	}

	public MailSenderInfo parseBasicInfo() {
		ParamRetriever xmlParam = ParamRetriever.getParam();
		MailSenderInfo senderInfo = new MailSenderInfo();

		String server = xmlParam.getParam("emailHost");
		if ((server != null) && !server.isEmpty()) {
			senderInfo.setMailServerHost(server);

		}
		senderInfo.setMailServerPort("25");
		senderInfo.setValidate(true);
		senderInfo.setUserName(xmlParam.getParam("emailSender"));
		senderInfo.setPassword(xmlParam.getParam("emailPassword"));
		senderInfo.setFromAddress(xmlParam.getParam("emailSender"));

		// mailInfo.setMailServerHost("mail.renren-inc.com");
		// mailInfo.setMailServerPort("25");
		// mailInfo.setValidate(true);
		// mailInfo.setUserName("defu.li@renren-inc.com");
		// mailInfo.setPassword("7ujm&*()");// 您的邮箱密码
		// mailInfo.setFromAddress("defu.li@renren-inc.com");

		senderInfo.setToAddresses(xmlParam.getParams("emailTos").toArray(
				new String[0]));
		senderInfo.setCcAddresses(xmlParam.getParams("emailCcs").toArray(
				new String[0]));
		return senderInfo;
	}

	public String getMailServerHost() {
		return this.mailServerHost;
	}

	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}

	public String getMailServerPort() {
		return this.mailServerPort;
	}

	public void setMailServerPort(String mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	public boolean isValidate() {
		return this.validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String[] getAttachFileNames() {
		return this.attachFileNames;
	}

	public void setAttachFileNames(String[] fileNames) {
		this.attachFileNames = fileNames;
	}

	public String getFromAddress() {
		return this.fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToAddress() {
		return this.toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String textContent) {
		this.content = textContent;
	}

	public String getCcAddress() {
		return this.ccAddress;
	}

	public void setCcAddress(String ccAddress) {
		this.ccAddress = ccAddress;
	}

	public String[] getToAddresses() {
		return this.toAddresses;
	}

	public void setToAddresses(String[] toAddresses) {
		this.toAddresses = toAddresses;
	}

	public String[] getCcAddresses() {
		return this.ccAddresses;
	}

	public void setCcAddresses(String[] ccAddresses) {
		this.ccAddresses = ccAddresses;
	}

}