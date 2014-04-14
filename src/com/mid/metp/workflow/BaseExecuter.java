package com.mid.metp.workflow;

import java.util.List;

import javax.swing.SwingWorker;

import com.mid.metp.model.Phone;
import com.mid.metp.model.Result;
import com.mid.metp.model.TestCase;

public abstract class BaseExecuter extends SwingWorker<List<Result>, Result> {

	protected Phone target;
	protected List<TestCase> cases;
	protected String apkfile;
	protected String packageName;
	protected String resultBasePath;

	public BaseExecuter(String apkfile, String packageName, Phone phone,
			List<TestCase> testCases) {

		this.target = phone;
		this.apkfile = apkfile;
		this.packageName = packageName;
		this.cases = testCases;
	}
}
