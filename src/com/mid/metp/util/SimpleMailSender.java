package com.mid.metp.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class SimpleMailSender {
	/**
	 * 以文本格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件的信息
	 */
	public static boolean sendTextMail(MailSenderInfo mailInfo) {
		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			// 如果需要身份认证，则创建一个密码验证器
			authenticator = new MyAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			new InternetAddress();
			// 创建邮件的接收者地址，并设置到邮件消息中
			// InternetAddress[] iaToList = new InternetAddress().parse(toList);
			InternetAddress[] to = InternetAddress.parse(mailInfo
					.getToAddress());
			mailMessage.setRecipients(Message.RecipientType.TO, to);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// 设置邮件消息的主要内容
			String mailContent = mailInfo.getContent();
			mailMessage.setText(mailContent);
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * 以HTML格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件信息
	 */
	public static boolean sendHtmlMail(MailSenderInfo mailInfo) {
		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		// 如果需要身份认证，则创建一个密码验证器
		if (mailInfo.isValidate()) {
			authenticator = new MyAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			List<InternetAddress> toList = new ArrayList<InternetAddress>();
			for (String to : mailInfo.getToAddresses()) {
				InternetAddress toAddress = new InternetAddress(to);
				toList.add(toAddress);
			}
			// 创建邮件的接收者地址，并设置到邮件消息中

			mailMessage.setRecipients(Message.RecipientType.TO,
					toList.toArray(new InternetAddress[0]));

			List<InternetAddress> ccList = new ArrayList<InternetAddress>();
			for (String cc : mailInfo.getCcAddresses()) {
				InternetAddress ccAddress = new InternetAddress(cc);
				ccList.add(ccAddress);
			}
			// 创建邮件的接收者地址，并设置到邮件消息中

			mailMessage.setRecipients(Message.RecipientType.CC,
					ccList.toArray(new InternetAddress[0]));

			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
			Multipart mainPart = new MimeMultipart();
			// 创建一个包含HTML内容的MimeBodyPart
			BodyPart html = new MimeBodyPart();
			// 设置HTML内容
			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
			mainPart.addBodyPart(html);
			// 添加附件
			Vector<String> file = new Vector<String>();
			for (String attachFile : mailInfo.getAttachFileNames()) {
				file.add(attachFile);
			}

			sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
			Enumeration<String> efile = file.elements();// 向Multipart添加附件
			while (efile.hasMoreElements()) {
				MimeBodyPart mbpFile = new MimeBodyPart();
				String filename = efile.nextElement().toString();
				FileDataSource fds = new FileDataSource(filename);
				mbpFile.setDataHandler(new DataHandler(fds));
				// mbpFile.setFileName(" =?GBK?B? "
				// + enc.encode(fds.getName().getBytes()) + " ?= ");
				mbpFile.setFileName(fds.getName());
				// 向MimeMessage添加（Multipart代表附件）
				mainPart.addBodyPart(mbpFile);
			}
			// 将MiniMultipart对象设置为邮件内容
			mailMessage.setContent(mainPart);
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static String transferChinese(String strText) {
		try {
			strText = MimeUtility.encodeText(new String(strText.getBytes(),
					"ISO-8859-1"), "GB2312", "B");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strText;
	}

	public static void main(String[] args) {
		// 这个类主要是设置邮件
		MailSenderInfo mailInfo = new MailSenderInfo().parseBasicInfo();
		// mailInfo.setMailServerHost("mail.renren-inc.com");
		// mailInfo.setMailServerPort("25");
		// mailInfo.setValidate(true);
		// mailInfo.setUserName("defu.li@renren-inc.com");
		// mailInfo.setPassword("7ujm^&*()");// 您的邮箱密码
		// mailInfo.setFromAddress("defu.li@renren-inc.com");
		Log.log(mailInfo.getMailServerHost());
		Log.log(mailInfo.getMailServerPort());
		Log.log(mailInfo.getUserName());
		Log.log(mailInfo.getPassword());
		Log.log(mailInfo.getFromAddress());
		mailInfo.setSubject("MonkeyReport");
		mailInfo.setContent("MonkeyReport");

		String[] tos = { "defu.li@renren-inc.com", "didi.mao@renren-inc.com" };
		String[] ccs = { "defu.li@renren-inc.com", "didi.mao@renren-inc.com" };
		String[] files = { "C:\\Users\\defus_000\\Desktop\\QUAD 3.3需求文档20140112.docx" };
		mailInfo.setToAddresses(tos);
		mailInfo.setCcAddresses(ccs);
		mailInfo.setAttachFileNames(files);
		// 这个类主要来发送邮件
		SimpleMailSender sms = new SimpleMailSender();
		// sms.sendTextMail(mailInfo);// 发送文体格式
		sms.sendHtmlMail(mailInfo);// 发送html格式
	}
}