package com.mid.metp.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * 日志输出到项目所在的根目录的Log的文件夹下， 并且根据系统日期命名日志文件，在程序退出时需要显示调用 close方法
 */
public class LogHelper {

	/**
	 * 获取日志实例
	 */
	private static Logger logger = Logger.getLogger("UldLog");

	/**
	 * 日志文件Handle
	 */
	private static FileHandler fh = null;

	/**
	 * 存放的文件夹
	 */
	private static String file_name = "Log";

	/**
	 * 日志目录
	 */
	private static String logPathName = getLogPathName();

	/**
	 * 上一日志记录的日期天
	 */
	private static int lastLogDay = 0;

	/**
	 * 关闭文件handler
	 */
	public static void close() {
		System.out.println("LogHelper.close()");
		try {

			Handler[] handlers = logger.getHandlers();
			for (Handler handler : handlers) {
				if (handler != null) {
					System.out.println(handler.toString() + ".close()");
					handler.close();
					logger.removeHandler(handler);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 得到要记录的日志的路径及文件名称
	 * 
	 * @return
	 */
	private static String getLogFileName() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date()) + ".log";
	}

	/**
	 * 获取日志实例
	 * 
	 * @return 日志实例
	 */
	public static Logger getLogger() {
		int currentLogDay = Utility.getDateDay(new Date());
		if (currentLogDay > lastLogDay) {
			try {
				setLogingProperties(logger);
				lastLogDay = currentLogDay;
			} catch (SecurityException e) {
				logger.log(Level.SEVERE, "安全性错误", e);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "读取文件日志错误", e);
			}
		}
		return logger;
	}

	/**
	 * 获取日志路径
	 * 
	 * @return
	 */
	private static String getLogName() {
		StringBuffer logName = new StringBuffer();
		logName.append(logPathName + "\\" + getLogFileName());
		return logName.toString();
	}

	/**
	 * 得到要记录的日志的路径及文件名称
	 * 
	 * @return
	 */
	private static String getLogPathName() {

		StringBuffer logPath = new StringBuffer();
		logPath.append(System.getProperty("user.dir"));
		logPath.append("\\" + file_name);
		File file = new File(logPath.toString());
		if (!file.exists()) {
			file.mkdir();
		}

		return logPath.toString();
	}

	/**
	 * 写入日志
	 * 
	 * @param level
	 *            One of the message level identifiers, e.g. SEVERE
	 * @param msg
	 *            The string message (or a key in the message catalog)
	 */
	public static void log(Level level, String msg) {
		try {
			getLogger().log(level, msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写入日志
	 * 
	 * @param level
	 *            One of the message level identifiers, e.g. SEVERE
	 * @param msg
	 *            The string message (or a key in the message catalog)
	 * @param thrown
	 *            Throwable associated with log message.
	 */
	public static void log(Level level, String msg, Throwable thrown) {
		try {
			getLogger().log(level, msg, thrown);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写入信息日志
	 * 
	 * @param msg
	 *            The string message (or a key in the message catalog)
	 */
	public static void log(String msg) {
		try {
			getLogger().log(Level.INFO, msg);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 配置Logger对象输出日志文件路径
	 * 
	 * @param logger
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static void setLogingProperties(Logger logger)
			throws SecurityException, IOException {
		setLogingProperties(logger, Level.ALL);
	}

	/**
	 * 配置Logger对象输出日志文件路径
	 * 
	 * @param logger
	 * @param level
	 *            在日志文件中输出level级别以上的信息
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static void setLogingProperties(Logger logger, Level level) {
		try {
			if (fh != null) {
				fh.close();
				logger.removeHandler(fh);
			}
			fh = new FileHandler(getLogName(), true);
			fh.setFormatter(new SimpleFormatter());// 输出格式
			// logger.setLevel(level);
			// logger.addHandler(new ConsoleHandler());//输出到控制台
			logger.addHandler(fh);// 日志输出文件

		} catch (SecurityException e) {
			logger.log(Level.SEVERE, "安全性错误", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "读取文件日志错误", e);
		}
	}

	public LogHelper() {
		System.out.println("LogHelper()");
	}
}
