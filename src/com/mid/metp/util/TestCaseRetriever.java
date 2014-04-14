package com.mid.metp.util;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import com.mid.metp.model.ApkInfo;
import com.mid.metp.model.TestCase;

/**
 * 用于从测试包apk,或者Jar中反射获取所有的测试方法
 * 
 * @author defu
 * 
 */
public class TestCaseRetriever extends SwingWorker<List<TestCase>, TestCase> {

	private DefaultListModel listModel;
	private String jarFile;
	private ArrayList<TestCase> testCaseList = new ArrayList<TestCase>();
	private ApkInfo apkInfo;

	public TestCaseRetriever(JTextField txtApk, DefaultListModel model,
			JTextField txtJar) {
		this.apkInfo = new AndroidUtility(Utility.getMachineType())
				.getApkInfo(txtApk.getText());

		if (this.apkInfo.getPackageName() == null
				|| this.apkInfo.getPackageName() == "") {

			Log.logError("解析失败");
			JOptionPane.showMessageDialog(null, "请重新选择apk包,然后选择Jar包", "无法确定包名",
					JOptionPane.ERROR_MESSAGE);

			return;
		}

		this.listModel = model;
		this.jarFile = txtJar.getText();
	}

	@Override
	protected List<TestCase> doInBackground() throws Exception {
		// todo: 由于jar包里包含了dex，所以需要dex2jar将解压出来的class.dex转换成新的jar文件，从中解析出来
		// 然后反射出里边特定annotation的方法展示出来，这个要费不少精力啊。。。。
		// E:\Android Anti-source\dex2jar-0.0.9.15\lib>java -Xms512m -Xmx1024m
		// -cp
		// dex-translator-0.0.9.15.jar;asm-all-3.3.1.jar;commons-lite-1.15.jar;dex-ir-1.12.jar;
		// dex-reader-1.15.jar;dex-tools-0.0.9.15.jar;dx.jar;jar-rename-1.6.jar;jasmin-p2.5.jar
		// com.googlecode.dex2jar.v3.Main classes.dex 亲测好使
		// 注意linux和mac下jar包间分号应该为冒号

		JarReflector reflector = new JarReflector(this.jarFile);

		// Log.log("packageName: " + this.apkInfo.getPackageName());
		this.testCaseList = reflector.retrieveUiautomatorCase(this.apkInfo
				.getPackageName());
		// for (int i = 0; i < 10; i++) {
		// TestCase case1 = new TestCase();
		// case1.setName("testcase" + i);
		// case1.setDescription("你好啊，妹子" + i);
		// this.publish(case1);
		// }

		return this.testCaseList;
	}

	@Override
	protected void process(List<TestCase> chunks) {
		// TODO Auto-generated method stub
		super.process(chunks);

	}

	@Override
	protected void done() {
		for (TestCase testcase : this.testCaseList) {
			this.listModel.addElement(testcase);
		}
		super.done();
	}

}
