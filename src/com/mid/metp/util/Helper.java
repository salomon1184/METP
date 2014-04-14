/**
 * 
 */
package com.mid.metp.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provide some static methods for convenience.
 * 
 * @author <a href="mailto:jiebo.wang@renren-inc.com">Jiebo Wang</a> Sep 21,
 *         2012
 */
public class Helper {

	/**
	 * Write data to a file. Make the directories and file if they don't exist.
	 * Delete the file first if exists.
	 * 
	 * @param dirName
	 *            the directory of the file
	 * @param data
	 *            data to be written into the file
	 * @param fileName
	 *            the file name
	 */
	public static void writeDataToFile(String dirName, String data,
			String fileName) {
		// dir exists or not
		File dir = new File(dirName);
		if (!dir.exists()) {
			dir.mkdir();
		}
		// file exists or not
		File file = new File(dirName + File.separator + fileName);
		System.out.println("writeDataToFile() file path: "
				+ file.getAbsolutePath());
		if (file.exists()) {
			file.delete();
		}
		// write data to file
		try {
			file = new File(dirName + File.separator + fileName);
			file.createNewFile();
			FileWriter writer = new FileWriter(file, true);
			writer.write(data);
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 把InputStream的内容写入到文件
	 * 
	 * @param inputStream
	 * @param filePath
	 *            文件路径
	 */
	public static void writeInputStreamToFile(InputStream inputStream,
			String filePath) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(new File(
					filePath));
			byte buf[] = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0) {
				fileOutputStream.write(buf, 0, len);
			}
			fileOutputStream.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("写入文件失败");
		}
	}

	/**
	 * 把InputStream的内容写入到文件
	 * 
	 * @param inputStream
	 * @param targetFile
	 *            目标文件
	 */
	public static void writeInputStreamToFile(InputStream inputStream,
			File targetFile) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
			byte buf[] = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0) {
				fileOutputStream.write(buf, 0, len);
			}
			fileOutputStream.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("写入文件失败");
		}
	}

	/**
	 * Combines strings into a string using {@link StringBuilder}. The default
	 * capacity of the StringBuilder is 100. <br>
	 * <br>
	 * Use this method when make shell script, test case list and etc..
	 * 
	 * @param strings
	 *            the strings to be combined into one
	 * @return the combined string
	 */
	public static String combineStrings(String... strings) {
		StringBuilder stringBuilder = new StringBuilder(100);
		for (String string : strings) {
			stringBuilder.append(string);
		}
		return stringBuilder.toString();
	}

	/**
	 * 获取当前时间，格式为：yyyyMMddHHmm，如：201210130109
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		SimpleDateFormat timingFormat = new SimpleDateFormat("yyyyMMddHHmm");
		return timingFormat.format(new Date());
	}

	/**
	 * 获取当前时间
	 * 
	 * @param pattern
	 *            日期格式，如:yyyyMMddhhmm
	 * @return 给定格式的当前时间，如:yyyyMMddhhmm，返回的就是:201210130109
	 */
	public static String getCurrentTime(String pattern) {
		SimpleDateFormat timingFormat = null;
		try {
			timingFormat = new SimpleDateFormat(pattern);
		} catch (NullPointerException e) {
			System.out.println("时间格式不能为空");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("时间格式不正确");
			e.printStackTrace();
		}
		return timingFormat.format(new Date());
	}

	/**
	 * 读取文件中的内容
	 * 
	 * @param filename
	 *            文件名
	 * @return 文件中的内容
	 * @throws IOException
	 */
	public static String readFileContent(String filename) throws IOException {
		FileReader in = new FileReader(filename);
		StringBuilder contents = new StringBuilder();
		char[] buffer = new char[4096];
		int read = 0;
		try {
			do {
				contents.append(buffer, 0, read);
				read = in.read(buffer);
			} while (read >= 0);
			return contents.toString();
		} finally {
			in.close();
		}
	}

	/**
	 * 读取文件中的内容
	 * 
	 * @param file
	 *            需要读取内容的文件
	 * @return 文件中的内容
	 * @throws IOException
	 */
	public static String readFileContent(File file) throws IOException {
		FileReader in = new FileReader(file);
		StringBuilder contents = new StringBuilder();
		char[] buffer = new char[4096];
		int read = 0;
		try {
			do {
				contents.append(buffer, 0, read);
				read = in.read(buffer);
			} while (read >= 0);
			return contents.toString();
		} finally {
			in.close();
		}
	}

	/**
	 * InputStream中的内容转成字符串。一般是把控制台输出流转成字符串，如获取一个命令的控制台输出的字符串。
	 * 
	 * @param inputStream
	 * @return
	 */
	public static String inputStreamToString(InputStream inputStream) {
		if (inputStream == null) {
			System.out.println("InputStream is null");
			return null;
		}

		try {
			int i = -1;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((i = inputStream.read()) != -1) {
				baos.write(i);
			}
			String content = baos.toString();
			return content;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param command
	 * @param log
	 * @return
	 * @deprecated 未完成
	 */
	@Deprecated
	public static boolean executeCommand(String command, File log) {
		// TODO executeCommand()
		return false;
	}

	/**
	 * 
	 * @param shell
	 * @param log
	 * @return
	 * @deprecated 未完成
	 */
	@Deprecated
	public static boolean executeShell(String shell, File log) {
		// TODO executeShell()
		return false;
	}

	public static boolean executeShell(String shell) {
		String command = "/bin/sh " + shell;
		try {
			Process process = Runtime.getRuntime().exec(command);
			int result = process.waitFor();
			if (result != 0) {
				System.out.println("Message:\n");
				System.out
						.println(inputStreamToString(process.getInputStream()));
				System.err.println("Error:\n");
				System.err
						.println(inputStreamToString(process.getErrorStream()));
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
