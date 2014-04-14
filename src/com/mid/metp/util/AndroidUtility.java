package com.mid.metp.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.mid.metp.Config;
import com.mid.metp.Constants;
import com.mid.metp.model.ApkInfo;
import com.mid.metp.model.Phone;
import com.mid.metp.model.TestCase;

public class AndroidUtility {

	private String aapt;
	private String adb;
	private String commonResPath;

	private static final String BIN_SH = "/bin/sh";

	public AndroidUtility(String machineType) {
		this.initTools(machineType);
	}

	/**
	 * 确定adb文件本地路径，执行adb命令的时候需要
	 */
	private void initTools(String machineType) {
		if (machineType.equals(Constants.OSX_OSNAME)) {
			this.commonResPath = Helper.combineStrings(Config.PATH_COMMON_RES,
					"macosx");
			this.adb = Helper.combineStrings(this.commonResPath,
					File.separator, "adb");
			this.aapt = Helper.combineStrings(this.commonResPath,
					File.separator, "aapt");
		} else if (machineType.equals(Constants.WIN_OSNAME)) {
			this.commonResPath = Helper.combineStrings(Config.PATH_COMMON_RES,
					"windows");
			this.adb = Helper.combineStrings(this.commonResPath,
					File.separator, "adb.exe");
			// System.out.println(new File(this.adb).getAbsolutePath());
			// System.out.println(Config.ROOTPATH_LOGCONFIG);
			this.aapt = Helper.combineStrings(this.commonResPath,
					File.separator, "aapt.exe");
		} else {
			this.commonResPath = Helper.combineStrings(Config.PATH_COMMON_RES,
					"linux");
			this.adb = Helper.combineStrings(this.commonResPath,
					File.separator, "adb");
			this.aapt = Helper.combineStrings(this.commonResPath,
					File.separator, "aapt");
		}
	}

	/**
	 * 获取本机插入的所有手机的serial number (adb devices).
	 * 
	 * @return
	 */
	private ArrayList<String> getPhoneSerialNum() {
		String cmdGetPhones = this.adb + " devices";
		ArrayList<String> serialNumList = new ArrayList<String>();
		try {
			Process p = Runtime.getRuntime().exec(cmdGetPhones);
			final InputStream inStream = p.getInputStream();

			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					inStream));

			String line2 = null;
			while ((line2 = bReader.readLine()) != null) {
				if ((line2 != null) && line2.contains("device")
						&& !line2.contains("List of devices")) {
					String templine = line2.trim();
					serialNumList.add(templine.substring(0,
							templine.length() - 6));
					// System.out.println("thread" + line2);
				}
			}

			// for (String d : serialNumList) {
			// System.out.println(d);
			// }
			return serialNumList;
		} catch (Exception e) {
			Log.logError("shell-" + cmdGetPhones + "-execution failed. Error:"
					+ e.getMessage());
			return null;
		}
	}

	private boolean getAvailability(String serialNum) {

		String cmdgetManufacturer = this.adb + " -s " + serialNum
				+ " get-state";
		boolean availability = false;
		try {
			Process p = Runtime.getRuntime().exec(cmdgetManufacturer);

			final InputStream inStream = p.getInputStream();

			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					inStream));

			// String line2 = null;
			String line2 = null;
			while ((line2 = bReader.readLine()) != null) {
				if ((line2 != null) && line2.contains("device")) {

					return true;
				}
			}

			// InputStream stdoutStream = new BufferedInputStream(
			// p.getInputStream());
			// StringBuffer buffer = new StringBuffer();
			// for (;;) {
			// int c = stdoutStream.read();
			// if (c == -1) {
			// break;
			// }
			// buffer.append((char) c);
			// }
			//
			// String outputText = buffer.toString();
			// stdoutStream.close();
			// LogHelper.log(cmdgetManufacturer);
			// String flagAvailability = outputText;
			// if (flagAvailability.contains("device")) {
			// availability = true;
			// } else {
			// availability = false;
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}

		return availability;
	}

	private String getManufacturer(String serialNum) {
		String cmdgetManufacturer = this.adb + " -s " + serialNum
				+ " shell getprop ro.product.manufacturer";
		String manufacturer = null;

		try {
			Process p = Runtime.getRuntime().exec(cmdgetManufacturer);

			final InputStream inStream = p.getInputStream();

			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					inStream));

			manufacturer = bReader.readLine().toString();
			manufacturer.replaceAll("\\||[\\s]+", "");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return manufacturer;
	}

	private String getModel(String serialNum) {
		String cmdgetManufacturer = this.adb + " -s " + serialNum
				+ " shell getprop ro.product.model";
		String model = null;

		try {
			Process p = Runtime.getRuntime().exec(cmdgetManufacturer);

			final InputStream inStream = p.getInputStream();

			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					inStream));

			model = bReader.readLine().toString();
			model = model.replaceAll("\\||[\\s]+", "");

			// InputStream stdoutStream = new BufferedInputStream(
			// p.getInputStream());
			// StringBuffer buffer = new StringBuffer();
			// for (;;) {
			// int c = stdoutStream.read();
			// if (c == -1) {
			// break;
			// }
			// buffer.append((char) c);
			// }
			// String outputText = buffer.toString();
			// stdoutStream.close();
			// LogHelper.log(Level.SEVERE, outputText);
			// model = outputText;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}

	private String getRelease(String serialNum) {
		String cmdgetManufacturer = this.adb + " -s " + serialNum
				+ " shell getprop ro.build.version.release";
		String release = null;

		try {
			Process p = Runtime.getRuntime().exec(cmdgetManufacturer);

			final InputStream inStream = p.getInputStream();

			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					inStream));

			release = bReader.readLine().toString();

			// InputStream stdoutStream = new BufferedInputStream(
			// p.getInputStream());
			// StringBuffer buffer = new StringBuffer();
			// for (;;) {
			// int c = stdoutStream.read();
			// if (c == -1) {
			// break;
			// }
			// buffer.append((char) c);
			// }
			// String outputText = buffer.toString();
			// stdoutStream.close();
			// LogHelper.log(Level.SEVERE, outputText);
			// release = outputText;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return release;
	}

	/***
	 * 获取手机信息
	 * 
	 * @return
	 */
	public ArrayList<Phone> getPhones() {
		ArrayList<Phone> phoneList = new ArrayList<Phone>();
		String cmdStartServer = this.adb + " start-server";
		if (!executeCommand(cmdStartServer, "start adb server")) {
			return null;
		}

		ArrayList<String> serialNumList = this.getPhoneSerialNum();
		for (String serialNum : serialNumList) {
			Phone phone = new Phone();
			phone.setId(serialNum);
			phone.setAvailability(this.getAvailability(serialNum));
			phone.setManufacturer(this.getManufacturer(serialNum));
			phone.setModel(this.getModel(serialNum));
			phone.setOsVersion(this.getRelease(serialNum));
			phoneList.add(phone);
			Log.log("检测到手机： " + phone.toString());
		}

		return phoneList;
	}

	/**
	 * 获取Apk安装包的label，packagename， version， debuggable等信息
	 * 
	 * @param apkFilePath
	 * @return
	 */
	public ApkInfo getApkInfo(String apkFilePath) {

		ApkInfo apkInfo = new ApkInfo();
		String[] s = apkFilePath.split(File.separator + File.separator);
		apkInfo.setApkName(s[s.length - 1]);
		apkInfo.setFullPath(apkFilePath);

		apkInfo.setPackageName(this.getPackageName(apkFilePath));
		String cmd = Helper.combineStrings(this.aapt, " dump badging ",
				apkFilePath);
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			InputStream inStream = p.getInputStream();
			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					inStream));
			String line2 = null;
			while ((line2 = bReader.readLine()) != null) {
				if (line2.startsWith("package:")) {
					String[] splits = line2.split(" ");
					String packageNameSplits = splits[1];
					String versionSplits = splits[3];
					apkInfo.setPackageName(packageNameSplits.substring(6,
							packageNameSplits.length() - 1));
					apkInfo.setVersion(versionSplits.substring(13,
							versionSplits.length() - 1));
				} else if (line2.startsWith("application-label:")) {
					apkInfo.setApkLabel(line2.substring(19, line2.length() - 1));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Log.log(apkInfo.getApkName());
		// Log.log(apkInfo.getPackageName());
		// Log.log(apkInfo.getVersion());
		return apkInfo;
	}

	public static void main(String[] args) {

		AndroidUtility ultility = new AndroidUtility(Utility.getMachineType());
		// ultility.getApkInfo("C:\\Users\\defus_000\\Desktop\\Quad_V2.4_release_20130924_1843.apk");
		// try {
		String file = "C:\\Users\\defus_000\\Desktop\\monkey__4.txt";
		Log.logError(ultility.searchMonkeyError(file, 0));

		// File testFile = new File(System.getProperty("user.dir")
		// + File.separator + "res" + File.separator
		// + "myLog4j.properties");
		//
		// System.out.println(testFile.getAbsolutePath());
		// ultility.searchMonkeyError("D:\\Workspaces_Eclipse\\METP\\Log\\monkey\\com.quad\\20131015--1102\\samsung-GT-N7000-4.1.2-364790339AB2819F\\monkey_0.log");
	}

	/**
	 * 根据apk安装包获取卸载时所需得包名。
	 * 
	 * @param apkPath
	 *            apk安装包所在路径。
	 * @return
	 */
	public String getPackageName(String apkPath) {
		String packageName = "";
		String apptCommand = Helper.combineStrings(this.aapt, " dump xmltree ",
				apkPath, " AndroidManifest.xml");
		try {
			Process p = Runtime.getRuntime().exec(apptCommand);
			InputStream stdoutStream = new BufferedInputStream(
					p.getInputStream());
			StringBuffer buffer = new StringBuffer();
			for (;;) {
				int c = stdoutStream.read();
				if (c == -1) {
					break;
				}
				buffer.append((char) c);
			}
			String outputText = buffer.toString();
			stdoutStream.close();

			StringTokenizer tokenizer = new StringTokenizer(outputText, "\n");
			while (tokenizer.hasMoreTokens()) {
				String line = tokenizer.nextToken().trim();
				int packageNamePosition = line.indexOf("package=");
				int packageNameEndPostion = line.indexOf(" (Raw",
						packageNamePosition);
				if ((packageNamePosition > 0) && (packageNameEndPostion > 0)) {
					packageName = line.substring(packageNamePosition + 9,
							packageNameEndPostion - 1);
					break;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return packageName;
	}

	/**
	 * 删除设备中的/data/local/tmp/logcat和/data/local/tmp/screenshots/
	 * 
	 * @param targetPhone
	 * @return
	 */
	public void cleanDevice(Phone targetPhone) {
		String cmd = Helper.combineStrings(this.adb, " -s ",
				targetPhone.getId(), " shell rm -r ");
		executeCommand(cmd + Constants.DEVICE_PATH_LOGCAT, "clean logcat");
		executeCommand(cmd + Constants.DEVICE_PATH_SCREENSHOTS,
				"clean screenshots");
	}

	/**
	 * 清楚手机logcat缓存信息
	 * 
	 * @param targetPhone
	 * @return
	 */
	public boolean logcatClear(Phone targetPhone) {
		String cmd = Helper.combineStrings(this.adb, " -s ",
				targetPhone.getId(), " logcat -c");
		return executeCommand(cmd, "Phone--" + targetPhone.toString()
				+ "--clear logcat");
	}

	/**
	 * 将logcat输出重定向到文件中（PC文件）
	 * 
	 * @param targetPhone
	 * @param logfile
	 * @return
	 */
	public boolean logcatDump(Phone targetPhone, String logfile) {
		String cmd = Helper.combineStrings(this.adb, " -s ",
				targetPhone.getId(), " logcat -d -v time");
		return executeCommandWithLog(cmd, "Phone--" + targetPhone.toString()
				+ "dump logcat info to file" + logfile, new File(logfile));
	}

	/**
	 * 拉取手机中logcat文件到PC文件夹
	 * 
	 * @param targetPhone
	 * @param targetFolder
	 * @return
	 */
	public boolean pullLogcat(Phone targetPhone, String targetFolder) {
		String cmd = Helper.combineStrings(this.adb, " -s ",
				targetPhone.getId(), " pull ", Constants.DEVICE_PATH_LOGCAT,
				" ", targetFolder);
		return executeCommand(cmd, "Phone--" + targetPhone.toString()
				+ " Pull logcat to folder: " + targetFolder);
	}

	/**
	 * 拉取手机中截图文件到PC文件夹
	 * 
	 * @param targetPhone
	 * @param targetFolder
	 * @return
	 */
	public boolean pullScreenShot(Phone targetPhone, String targetFolder) {
		String cmd = Helper.combineStrings(this.adb, " -s ",
				targetPhone.getId(), " pull ",
				Constants.DEVICE_PATH_SCREENSHOTS, " ", targetFolder);
		return executeCommand(cmd, "Phone--" + targetPhone.toString()
				+ " Pull screenshots to folder: " + targetFolder);
	}

	/**
	 * 安装apk
	 * 
	 * @param targetPhone
	 * @param apkFilePath
	 * @return
	 */
	public boolean installApk(Phone targetPhone, String apkFilePath) {
		// String packageName = getPackageName(apkFilePath);

		String installCommand = Helper.combineStrings(this.adb, " -s ",
				targetPhone.getId(), " install -r ", apkFilePath);

		return executeCommand(installCommand,
				"Phone--" + targetPhone.toString() + "--Install apk ");
	}

	/**
	 * push jar包到/data/local/tmp中
	 * 
	 * @param targetPhone
	 * @param testJarFile
	 * @return
	 */
	public boolean installTestJar(Phone targetPhone, String testJarFile) {
		String installCommand = Helper.combineStrings(this.adb, " -s ",
				targetPhone.getId(), " push ", testJarFile, " ",
				Constants.DEVICE_PATH_TMP);

		return executeCommand(installCommand,
				"Phone--" + targetPhone.toString() + "--Push testjar to tmp ");
	}

	/**
	 * push xml包到/data/local/tmp中
	 * 
	 * @param targetPhone
	 * @param testJarFile
	 * @return
	 */
	public boolean pushParamXml(Phone targetPhone, String xmlFile) {
		String installCommand = Helper.combineStrings(this.adb, " -s ",
				targetPhone.getId(), " push ", xmlFile, " ",
				Constants.DEVICE_PATH_TMP);

		return executeCommand(installCommand,
				"Phone--" + targetPhone.toString() + "--Push param xml to tmp ");
	}

	/**
	 * 由包名获取进程id
	 * 
	 * @param targetPhone
	 * @param processName
	 * @return
	 */
	public int getPid(Phone targetPhone, String processName) {
		final String strProcessName = processName;
		String cmd = Helper.combineStrings(this.adb, " -s ",
				targetPhone.getId(), " shell ps c");
		int pid = 0;

		try {
			Process process = Runtime.getRuntime().exec(cmd);

			final InputStream inStream = process.getInputStream();

			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					inStream));

			String line2 = null;
			// Log.log("process details:");
			while ((line2 = bReader.readLine()) != null) {

				if (line2.contains(strProcessName)) {
					String[] splits = line2.split("\\||[\\s]+");
					// Log.log("--" + line2);
					pid = Integer.parseInt(splits[1]);
				}
			}

		} catch (Exception e) {
			Log.log("shell-" + cmd + "-FAIL. ERROR MESSAGE:" + e.getMessage());
		}

		return pid;
	}

	/**
	 * 根据进程id，杀掉手机进程
	 * 
	 * @param targetPhone
	 * @param pid
	 * @return
	 */
	public boolean killProcessById(Phone targetPhone, int pid) {
		String cmd = Helper.combineStrings(this.adb, " -s ",
				targetPhone.getId(), " shell kill -9 ", Integer.toString(pid));

		return executeCommand(cmd, "kill process: " + pid);
	}

	/**
	 * 
	 * @param targetPhone
	 * @param packageName
	 * @return
	 */
	public boolean killProcessByPackageName(Phone targetPhone,
			String packageName) {
		Log.log("Kill process: " + packageName);
		return this.killProcessById(targetPhone,
				this.getPid(targetPhone, packageName));
	}

	/**
	 * 执行monkey
	 * 
	 * @param targetPhone
	 * @param packageName
	 * @param logfile
	 * @return
	 */
	public boolean runMonkey(Phone targetPhone, String packageName,
			String logfile) {
		// "adb -s jdfkljsfklj wait-for-device  shell monkey -p comn.quad  --bugreport  --ignore-timeouts  --ignore-security-exceptions "
		// +
		// "  --monitor-native-crashes  --kill-process-after-error --pct-syskeys 0   --pct-touch 80 --throttle 500  -v -v 400000 > monkeypath"
		String cmd = Helper
				.combineStrings(
						this.adb,
						" -s ",
						targetPhone.getId(),
						" wait-for-device  shell monkey -p ",
						packageName,
						"  --bugreport  --ignore-timeouts  --ignore-security-exceptions  --monitor-native-crashes  --kill-process-after-error ",
						"--pct-syskeys 0   --pct-touch 80 --throttle 500  -v -v 400000 ");
		// Log.log("monkey command: " + cmd);
		Log.log("手机--" + targetPhone.toString() + "--Moneky 开始执行");
		return executeCommandWithLog(cmd, "手机" + targetPhone.toString()
				+ "--Run monkey", new File(logfile));
	}

	public boolean runUiaTestCase(Phone targetPhone, String jarFileName,
			String logfile, TestCase tc, boolean isLogcatOn, boolean isPerfOn,
			boolean isScreenCap) {
		String cmd = Helper.combineStrings(this.adb, " -s ",
				targetPhone.getId(), " shell uiautomator runtest ",
				jarFileName, " -e class ", tc.getName(), " -e ",
				Config.KEY_LOGCAT, " ", Boolean.toString(isLogcatOn), " -e ",
				Config.KEY_PERF, " ", Boolean.toString(isPerfOn), " -e ",
				Config.KEY_SCREENCAP, " ", Boolean.toString(isScreenCap));

		Log.log("Uia command: " + cmd);

		return executeCommandWithLog(cmd, "手机" + targetPhone.toString()
				+ "--Run uia Test", new File(logfile));

	}

	/**
	 * kill monkey process of the target phone
	 * 
	 * @param targetPhone
	 * @return
	 */
	public boolean killMonkey(Phone targetPhone) {
		return this.killProcessByPackageName(targetPhone,
				"com.android.commands.monkey");
	}

	/**
	 * 倒叙搜索logcat文件，查找崩溃信息
	 * 
	 * @param monkeyLogFile
	 * @return
	 */
	public String searchMonkeyError(String monkeyLogFile) {
		// FIXME 这里的搜索性能和搜索关键字都需要调整
		String strError = "";
		try {
			RandomAccessFile raf = new RandomAccessFile(monkeyLogFile, "r");
			long length = raf.length();
			long start = raf.getFilePointer();
			long nextEnd = (start + length) - 1;

			raf.seek(nextEnd);
			while (nextEnd > start) {
				String line = raf.readLine();

				if ((line == null) || (line.length() == 0)) {
					nextEnd = nextEnd - 50;
					if (nextEnd > 0) {
						raf.seek(nextEnd);
					}
					continue;
				} else {
					nextEnd = nextEnd - line.length();
					if (nextEnd > 0) {
						raf.seek(nextEnd);

						nextEnd = nextEnd + line.length();
						if (line.contains("CRASH:")) {
							// monkeyLogFile.seek(nextIndex);
							// String packageLine =
							// monkeyLogFile.readLine();
							// monkeyLogFile.seek(nextIndex +
							// packageLine.length());
							raf.seek(nextEnd - line.length());
							String ErrorLine = raf.readLine();
							// || line.contains("new native crash detected")
							// || line.contains("native_crash_")
							// || line.contains("unexpected power cycle")
							String[] packageSplit = line.split(":");
							strError = "	CRASH	"
									+ packageSplit[1].split("\\(")[0] + "	"
									+ ErrorLine.split(":")[1];
							break;
						} else if (line.contains("not responding:")) {
							strError = "	ANR	" + "	" + line;
							break;
						} else if (line.contains("new native crash detected")
								|| line.contains("native_crash_")) {
							strError = "	NATIVE_CRASH	" + "	" + line;
							break;
						} else if (line.contains("unexpected power cycle")) {
							strError = "	POWER_CYCLE	" + "	" + line;
							break;
						}

					}
				}

			}

			raf.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
		if (!strError.isEmpty()) {
			Log.log(strError);
		}
		return strError;
	}

	/**
	 * 搜索logcat文件，查找崩溃信息
	 * 
	 * @param monkeyLogFile
	 * @return
	 */
	public String searchMonkeyError(String monkeyFile, long startIndex) {
		String strError = "";
		try {
			RandomAccessFile monkeyLogFile = new RandomAccessFile(monkeyFile,
					"r");
			long start = monkeyLogFile.getFilePointer();
			long length = start + monkeyLogFile.length();
			// Log.logError("length" + length);
			long nextIndex = start + startIndex;
			monkeyLogFile.seek(nextIndex);
			while (nextIndex < length) {
				String line = monkeyLogFile.readLine();

				if (line == null) {
					monkeyLogFile.close();
					return strError;
				}

				nextIndex = nextIndex + line.length();
				if (nextIndex < length) {
					if (line.contains("CRASH:")) {
						// monkeyLogFile.seek(nextIndex);
						// String packageLine = monkeyLogFile.readLine();
						// monkeyLogFile.seek(nextIndex + packageLine.length());
						String ErrorLine = monkeyLogFile.readLine();
						// || line.contains("new native crash detected")
						// || line.contains("native_crash_")
						// || line.contains("unexpected power cycle")
						String[] packageSplit = line.split(":");
						strError = "	CRASH	" + packageSplit[1].split("\\(")[0]
								+ "	" + ErrorLine.split(":")[1];
						break;
					} else if (line.contains("not responding:")) {
						strError = "	ANR	" + "	" + line;
						break;
					} else if (line.contains("new native crash detected")
							|| line.contains("native_crash_")) {
						strError = "	NATIVE_CRASH	" + "	" + line;
						break;
					} else if (line.contains("unexpected power cycle")) {
						strError = "	POWER_CYCLE	" + "	" + line;
						break;
					}
				}
			}

			monkeyLogFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strError;
	}

	public static boolean executeCommand(String strCommand, String strComment) {
		int result = 0;

		try {
			Process process = Runtime.getRuntime().exec(strCommand);

			final InputStream inStream = process.getInputStream();
			final InputStream errorStream = process.getErrorStream();
			new Thread() {
				@Override
				public void run() {
					BufferedReader bReader = new BufferedReader(
							new InputStreamReader(inStream));
					try {
						String line2 = null;
						while ((line2 = bReader.readLine()) != null) {
							if (line2 != null) {
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							inStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();

			new Thread() {
				@Override
				public void run() {

					BufferedReader bReader = new BufferedReader(
							new InputStreamReader(errorStream));
					try {
						String line2 = null;
						while ((line2 = bReader.readLine()) != null) {
							if (line2 != null) {
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							errorStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();

			result = process.waitFor();
			process.destroy();

			if (result == 0) {
				Log.log(strComment + " success!");
				return true;
			} else {
				Log.logError(strComment + " fail!");
				return false;
			}
		} catch (Exception e) {
			Log.logError(strComment + "-fail or be cancelled! Error message："
					+ e.getMessage());

			return false;
		}
	}

	/**
	 * 执行shell 命令，将执行log保存在文件中
	 * 
	 * @param strCommand
	 *            shell 命令
	 * @param logfile
	 *            log文件（必须指定路径）。
	 * @return
	 */
	public static boolean executeCommandWithLog(String strCommand,
			String strComment, File logfile) {
		int result = 0;
		try {
			Process process = Runtime.getRuntime().exec(strCommand);

			final InputStream inStream = process.getInputStream();
			final InputStream errorStream = process.getErrorStream();
			final File logFile = logfile;
			new Thread() {
				@Override
				public void run() {

					try {
						Helper.writeInputStreamToFile(inStream, logFile);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							inStream.close();
						} catch (Exception e2) {
							e2.printStackTrace();
						}

					}
				}
			}.start();

			new Thread() {
				@Override
				public void run() {

					BufferedReader bReader = new BufferedReader(
							new InputStreamReader(errorStream));
					Log.log(Helper.inputStreamToString(errorStream));
					try {
						errorStream.close();
						bReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();

			process.waitFor();
			process.destroy();

			if (result == 0) {
				Log.log(strComment + " success!");
				return true;

			} else {
				Log.logError(strComment + " fail!");
				return false;
			}

		} catch (Exception e) {
			Log.logError(strComment + "-FAIL OR BE CANCELLED. ERROR MESSAGE："
					+ e.getMessage());

			return false;
		}
	}
}
