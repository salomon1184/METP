package com.mid.metp.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileZipper {
	public void zip(String zipFileName, File inputFile) throws Exception {
		Log.logFatal("Compressing .....");
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				zipFileName));
		BufferedOutputStream bo = new BufferedOutputStream(out);
		this.zip(out, inputFile, inputFile.getName(), bo);
		bo.close();
		out.close(); // 输出流关闭
		Log.logFatal("Compressed Succeed!");
	}

	public void zip(ZipOutputStream out, File f, String base,
			BufferedOutputStream bo) throws Exception { // 方法重载
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			if (fl.length == 0) {
				out.putNextEntry(new ZipEntry(base + "/")); // 创建zip压缩进入点base
				System.out.println(base + "/");
			}
			for (File element : fl) {
				this.zip(out, element, base + "/" + element.getName(), bo); // 递归遍历子文件夹
			}

		} else {
			out.putNextEntry(new ZipEntry(base)); // 创建zip压缩进入点base
			System.out.println(base);
			FileInputStream in = new FileInputStream(f);
			BufferedInputStream bi = new BufferedInputStream(in);
			int b;
			while ((b = bi.read()) != -1) {
				bo.write(b); // 将字节流写入当前zip目录
			}
			bi.close();
			// in.close(); // 输入流关闭
		}
	}

	static final int BUFFER = 8192;

	private final File zipFile;

	public FileZipper(String pathName) {
		this.zipFile = new File(pathName);
	}

	public void compress(String srcPathName) {
		File file = new File(srcPathName);
		if (!file.exists()) {
			throw new RuntimeException(srcPathName + "不存在！");
		}
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(
					this.zipFile);
			CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,
					new CRC32());
			ZipOutputStream out = new ZipOutputStream(cos);
			String basedir = "";
			this.compress(file, out, basedir);
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 压缩一个目录 */
	private void compressDirectory(File dir, ZipOutputStream out, String basedir) {
		if (!dir.exists()) {
			return;
		}

		File[] files = dir.listFiles();
		for (File file : files) {
			/* 递归 */
			this.compress(file, out, basedir + dir.getName() + "/");
		}
	}

	private void compress(File file, ZipOutputStream out, String basedir) {
		/* 判断是目录还是文件 */
		if (file.isDirectory()) {
			System.out.println("压缩：" + basedir + file.getName());
			this.compressDirectory(file, out, basedir);
		} else {
			System.out.println("压缩：" + basedir + file.getName());
			this.compressFile(file, out, basedir);
		}
	}

	/** 压缩一个文件 */
	private void compressFile(File file, ZipOutputStream out, String basedir) {
		if (!file.exists()) {
			return;
		}
		try {
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			ZipEntry entry = new ZipEntry(basedir + file.getName());
			out.putNextEntry(entry);
			int count;
			byte data[] = new byte[BUFFER];
			while ((count = bis.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			bis.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		String logFolder = "C:\\Users\\defus_000\\Desktop\\METP\\Log\\monkey\\com.quad\\20140316--1535\\";
		File zipFile = new File(logFolder);
		// if (zipFile.exists()) {
		// zipFile.delete();
		// }
		//
		// String dateString = logFolder.substring(logFolder.length() - 15,
		// logFolder.length() - 1);
		// String subFolder = logFolder.substring(0, logFolder.length() - 15);
		// zipFile.createNewFile();

		// System.out.println(dateString);
		// System.out.println(subFolder);
		// new FileZipper()
		// .zip("C:\\Users\\defus_000\\Desktop\\METP\\Log\\monkey\\com.quad\\log.zip",
		// zipFile);
		new FileZipper(
				"C:\\Users\\defus_000\\Desktop\\METP\\Log\\monkey\\com.quad\\log2.zip")
				.compress(logFolder);

	}
}
