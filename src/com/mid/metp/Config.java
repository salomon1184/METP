package com.mid.metp;

import java.io.File;

import com.mid.metp.util.Helper;

public class Config {
	/** 命令行传入的参数的key，控制是否全程记录logcat */
	public static final String KEY_LOGCAT = "logcat";

	/** 命令行传入的参数的key，登陆用户名 */
	public static final String KEY_USER = "user";

	/** 命令行传入的参数的key，登陆密码 */
	public static final String KEY_PASSWORD = "password";

	/** 命令行传入的参数的key，控制是否截图 */
	public static final String KEY_SCREENCAP = "screencap";

	/** 命令行传入的参数的key，控制是否开启性能监控 */
	public static final String KEY_PERF = "perf";
	public static final String KEY_PERF_NUM = "perfNum";
	public static final String KEY_PERF_RATE = "perfRate";

	public static final String PATH_COMMON_RES = Helper.combineStrings(
			System.getProperty("user.dir"), File.separator, "res",
			File.separator);
	public static final String PATH_PARAMS_XML = Helper.combineStrings(
			PATH_COMMON_RES, "params.xml");
	public static final String ROOTPATH_LOGCONFIG = Helper.combineStrings(
			PATH_COMMON_RES, "myLog4j.properties");
	public static final String ROOTPATH_LOG = Helper.combineStrings(
			System.getProperty("user.dir"), File.separator, "Log",
			File.separator);
	public static final String ROOTPATH_MONKEY = Helper.combineStrings(
			ROOTPATH_LOG, "monkey", File.separator);
	public static final String ROOTPATH_UIA = Helper.combineStrings(
			ROOTPATH_LOG, "uia", File.separator);
}
