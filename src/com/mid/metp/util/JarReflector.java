package com.mid.metp.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.mid.metp.Constants;
import com.mid.metp.model.TestCase;
import com.renren.uia.annotation.Description;
import com.renren.uia.annotation.TargetApp;

/**
 * 从jar包反射出testcase （直接将jar 用工具dex2jar转换成jar 然后反射就行了，无需将jar解压）
 * 
 * @author defu
 * 
 */
public class JarReflector {

	private String sourceJarFile;
	private String dex2JarToolPath = Helper.combineStrings(".", File.separator,
			"res", File.separator, "dex2jar", File.separator);
	private String desJarFile;

	private static ArrayList<String> jarFile = new ArrayList<String>();
	static {

		jarFile.add("asm-all-3.3.1.jar");
		jarFile.add("commons-lite-1.15.jar");
		jarFile.add("dex-ir-1.12.jar");
		jarFile.add("dex-reader-1.15.jar");
		jarFile.add("dex-tools-0.0.9.15.jar");
		jarFile.add("dex-translator-0.0.9.15.jar");
		jarFile.add("dx.jar");
		jarFile.add("jar-rename-1.6.jar");
		jarFile.add("jasmin-p2.5.jar");
	}

	public JarReflector(String filePath) {
		this.sourceJarFile = filePath;
		String[] splits = this.sourceJarFile.split(File.separator
				+ File.separator);
		String file = splits[splits.length - 1];
		String path = this.sourceJarFile.substring(0,
				this.sourceJarFile.length() - file.length());
		String desFileName = file.substring(0, file.length() - 4)
				+ "_dex2jar.jar";
		this.desJarFile = path + desFileName;

		// System.out.println(path);
		// System.out.println(file);
		// System.out.println(desFileName);
		Log.log("解压后的文件：" + this.desJarFile);
	}

	public ArrayList<TestCase> retrieveUiautomatorCase(String targetApp) {
		// this.unZipJarFile(this.jarFilePath, "D:\\demo\\");
		Log.log("使用Dex2Jar转化jar包...");
		this.convertDex2Jar(new File(this.sourceJarFile));

		return this.getTestCase(this.desJarFile, targetApp);
	}

	private void convertDex2Jar(File dexFile) {
		// E:\Android Anti-source\dex2jar-0.0.9.15\lib>java -Xms512m -Xmx1024m
		// -cp
		// dex-translator-0.0.9.15.jar;asm-all-3.3.1.jar;commons-lite-1.15.jar;dex-ir-1.12.jar;
		// dex-reader-1.15.jar;dex-tools-0.0.9.15.jar;dx.jar;jar-rename-1.6.jar;jasmin-p2.5.jar
		// com.googlecode.dex2jar.v3.Main classes.dex 亲测好使
		// 注意linux和mac下jar包间分号应该为冒号

		String split;
		String os = System.getProperty("os.name");

		if (os.startsWith(Constants.WIN_OSNAME)) {
			split = ";";

		} else {
			split = ":";
		}

		StringBuilder cmdBuilder = new StringBuilder();
		cmdBuilder.append("java -Xms512m -Xmx1024m -cp ");

		for (String jar : jarFile) {
			cmdBuilder.append(this.dex2JarToolPath);
			cmdBuilder.append(jar);
			cmdBuilder.append(split);
		}

		cmdBuilder.append(" com.googlecode.dex2jar.v3.Main ");
		cmdBuilder.append(dexFile.getAbsolutePath());

		// Log.log(cmdBuilder.toString());
		AndroidUtility.executeCommand(cmdBuilder.toString(), "Convert jar");
	}

	private ArrayList<TestCase> getTestCase(String jarFile, String targetApp) {

		Log.log("开始解析.......");
		ArrayList<TestCase> testCases = new ArrayList<TestCase>();
		try {
			File f = new File(jarFile);// 通过将给定路径名字符串转换为抽象路径名来创建一个新
										// File 实例
			URL url1 = f.toURI().toURL();
			URLClassLoader myClassLoader = new URLClassLoader(
					new URL[] { url1 }, Thread.currentThread()
							.getContextClassLoader());

			// 通过jarFile 和JarEntry得到所有的类
			JarFile jar = new JarFile(jarFile);
			Enumeration<JarEntry> enumFiles = jar.entries();// 返回 zip 文件条目的枚举
			JarEntry entry;
			Class aTarget = Class
					.forName("com.renren.uia.annotation.TargetApp");
			Class aDescription = Class
					.forName("com.renren.uia.annotation.Description");
			while (enumFiles.hasMoreElements()) {// 测试此枚举是否包含更多的元素。
				entry = enumFiles.nextElement();
				if (entry.getName().indexOf("META-INF") < 0) {
					String classFullName = entry.getName();

					if (classFullName.indexOf(".class") > 0
							&& classFullName.contains("Test")
							&& !classFullName.contains("uiautomator")
							&& !classFullName.contains("UiAutomator")) { // 找到所有类定义文件
						String className = classFullName.substring(0,
								classFullName.length() - 6).replace("/", ".");
						// System.out.println(className);
						Class<?> myClass = myClassLoader.loadClass(className);
						// System.out.println(className); // 打印类名

						// 获取所有标有TargetApp 注解的类
						if (myClass.isAnnotationPresent(aTarget)
								&& ((TargetApp) myClass.getAnnotation(aTarget))
										.value().contains(targetApp)) {

							// Log.log(Boolean.toString(((TargetApp) myClass
							// .getAnnotation(aTarget)).value().contains(
							// targetApp)));

							// System.out.println("found");
							// 通过getDeclaredMethods得到类中包含public的方法
							Method publicMethod[] = myClass.getMethods();

							for (Method pm : publicMethod) {
								if (pm.getName().indexOf("test") == 0) {// 获取所有以test开头的方法

									TestCase t = new TestCase();

									String sm = pm.getName();

									t.setClassName(className);
									t.setMethodName(sm);
									t.setName(className + "#" + sm);

									Description description = pm
											.getAnnotation(aDescription);
									if (description != null) {
										t.setAuthor(description.author());
										t.setDescription(description.summary());

									}

									// System.out.println(t.getAuthor());
									// System.out.println(t.getDescription());
									testCases.add(t);
									// if ("fasdfsd".indexOf(sm) < 0) { //
									// 打印除默认方法外的方法
									// System.out.println(pm.toString().substring(
									// pm.toString().indexOf(className)));
									// }
								}
							}
						}
					}
				}

			}
		} catch (Exception e) {
			Log.logError("解析测试用例--失败, 错误信息： ");
			Log.logError(e);
		}

		Log.log("解析测试用例--成功");
		return testCases;
	}

	public static void main(String[] args) {
		JarReflector reflector = new JarReflector(
				"C:\\Users\\defus_000\\Desktop\\uiautomatorTest.jar");
		reflector.retrieveUiautomatorCase("com.quad");

	}

	/**
	 * 将jar包解压到特定文件夹下
	 * 
	 * @param jarFilePath
	 * @param outPutPath
	 */
	private void unZipJarFile(String jarFilePath, String outPutPath) {

		try {

			JarFile originalJarFile = new JarFile(jarFilePath);

			Enumeration<JarEntry> enumFiles = originalJarFile.entries();
			while (enumFiles.hasMoreElements()) {
				JarEntry jarEntry = enumFiles.nextElement();
				if (jarEntry.isDirectory()) {
					continue;
				}

				String outputFilePath = outPutPath + jarEntry.getName();
				File outputFile = new File(outputFilePath);
				Utility.makeDir(outputFilePath);
				this.writeFile(originalJarFile.getInputStream(jarEntry),
						outputFile);
			}
			originalJarFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeFile(InputStream ips, File outputFile) throws IOException {
		OutputStream ops = new BufferedOutputStream(new FileOutputStream(
				outputFile));
		try {
			byte[] buffer = new byte[1024];
			int nBytes = 0;
			while ((nBytes = ips.read(buffer)) > 0) {
				ops.write(buffer, 0, nBytes);
			}
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			try {
				if (null != ops) {
					ops.flush();
					ops.close();
				}
			} catch (IOException ioe) {
				throw ioe;
			} finally {
				if (null != ips) {
					ips.close();
				}
			}
		}
	}
}
