package com.mid.metp.result;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.mid.metp.model.ApkInfo;
import com.mid.metp.model.MonkeyFailInfo;
import com.mid.metp.model.MonkeyResult;
import com.mid.metp.model.MonkeyRunInfo;
import com.mid.metp.model.Phone;
import com.mid.metp.util.Helper;
import com.mid.metp.util.Utility;

public class MonkeyResultHandler {

	private final String resultFolder;
	private final List<Phone> targets;
	private final MonkeyRunInfo runInfo;
	private String[] logFiles;
	final File rootPath;

	public MonkeyResultHandler(MonkeyRunInfo runInfo, List<Phone> targets,
			String resultFolder) {
		this.runInfo = runInfo;
		this.targets = targets;
		this.resultFolder = resultFolder;
		this.rootPath = new File(this.resultFolder);

		this.logFiles = this.rootPath.list(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				if (!dir.equals(MonkeyResultHandler.this.rootPath)) {
					return false;
				}

				if (!name.endsWith(".log")) {
					return false;
				}
				return true;
			}
		});

	}

	public List<MonkeyResult> getMonkeyResults() {
		List<MonkeyResult> monkeyResults = new ArrayList<MonkeyResult>();
		for (String logFile : this.logFiles) {
			MonkeyResult result = new MonkeyResult();

			result.setPhone(logFile.substring(0, logFile.length() - 4));
			File file = new File(this.resultFolder + File.separator + logFile);
			BufferedReader logFileReader;
			int failCount = 0;
			int successCount = 0;
			List<MonkeyFailInfo> failInfos = new ArrayList<MonkeyFailInfo>();
			try {
				logFileReader = new BufferedReader(new FileReader(file));
				String resultLine = "";
				while ((resultLine = logFileReader.readLine()) != null) {
					String[] splits = resultLine.split("	");
					if (splits[1].equalsIgnoreCase("FAIL")) {
						failCount++;
						MonkeyFailInfo failInfo = new MonkeyFailInfo();
						failInfo.setType(splits[3]);
						failInfo.setRound(Integer.parseInt(splits[0]));
						failInfo.setPackageName(splits[4]);
						failInfo.setShortMessage(splits[5]);

						failInfos.add(failInfo);
					} else {
						successCount++;
					}
				}

				result.setFails(failInfos);
				result.setFailures(failCount);
				result.setSuccesses(successCount);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			monkeyResults.add(result);
		}

		return monkeyResults;
	}

	/**
	 * 替换HTML report模板中相关格式，生成monkey report FIXME: 格式需要进一步优化
	 * 
	 * @return
	 */
	public String generateHtmlReport() {

		if (!this.rootPath.exists() || (this.logFiles.length == 0)) {
			JOptionPane.showMessageDialog(null,
					"Please make sure Monkey run or phone exsited",
					"Failed to find log files", JOptionPane.ERROR_MESSAGE);
			return "";
		}

		int failureCount = 0;
		StringBuilder resultBuilder = new StringBuilder();
		String monkeyReportTemplateFile = Helper.combineStrings(".",
				File.separator, "res", File.separator, "monkeyreport.html");
		File templateFile = new File(monkeyReportTemplateFile);
		ApkInfo apkInfo = this.runInfo.getApk();
		List<MonkeyResult> monkeyResults = this.getMonkeyResults();
		try {
			BufferedReader br = new BufferedReader(new FileReader(templateFile));
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.contains("Tag_ApkInfo_AppName")) {
					line = line.replace("Tag_ApkInfo_AppName",
							apkInfo.getApkLabel());
				}
				if (line.contains("Tag_ApkInfo_PackageName")) {
					line = line.replace("Tag_ApkInfo_PackageName",
							apkInfo.getPackageName());
				}

				if (line.contains("Tag_ApkInfo_ApkFile")) {
					line = line.replace("Tag_ApkInfo_ApkFile",
							apkInfo.getApkName());
				}

				if (line.contains("Tag_ApkInfo_ApkVersion")) {
					line = line.replace("Tag_ApkInfo_ApkVersion",
							apkInfo.getVersion());
				}

				if (line.contains("Tag_ApkInfo_Debuggable")) {
					line = line.replace("Tag_ApkInfo_Debuggable",
							Boolean.toString(apkInfo.isDebuggable()));
				}

				if (line.contains("Tag_Test_Round")) {
					line = line.replace("Tag_Test_Round",
							Integer.toString(this.runInfo.getTestRound()));
				}

				if (line.contains("Tag_Test_Time")) {
					line = line.replace("Tag_Test_Time",
							Integer.toString(this.runInfo.getTestTime()));
				}

				if (line.contains("Tag_Results_Time")) {
					line = line.replace("Tag_Results_Time",
							Utility.getFormatDate());
				}

				if (line.contains("Tag_Device_Summary")) {

					StringBuilder builder = new StringBuilder();
					for (MonkeyResult monkeyResult : monkeyResults) {
						builder.append("<tr>");
						builder.append("<td>" + monkeyResult.getPhone()
								+ "</td>");
						builder.append("<td style=\"border: 1px solid 6ebb92; min-width: 80px\">"
								+ monkeyResult.getSuccesses() + "</td>");
						builder.append("<td style=\"background-color: e8767a; min-width: 80px;\">"
								+ monkeyResult.getFailures() + "</td>");
						builder.append("<tr>");
						// <tr>
						// <td
						// style="border: 1px solid 6ebb92; min-width: 80px">47</td>
						// <td
						// style="background-color: e8767a; min-width: 80px;">3</td>
						// <td>LGE__google__Nexus4__4.4.2__00651063d64aa22b</td>
						// </tr>

					}
					line = line.replace("Tag_Device_Summary",
							builder.toString());

				}
				if (line.contains("Tag_Results_Details")) {
					StringBuilder builder = new StringBuilder();
					int index = 0;
					for (MonkeyResult monkeyResult : monkeyResults) {
						if (monkeyResult.getFailures() > 0) {
							for (MonkeyFailInfo failInfo : monkeyResult
									.getFails()) {
								failureCount++;
								index++;
								// #E8767A;
								// "<font color=\"e8767a\" face=\"monospace\" size=\"48\">FAIL:"
								builder.append("<tr style=\"background: #E8767A;\">");
								builder.append("<td>");
								builder.append(index);
								builder.append("</td><td>");
								builder.append(monkeyResult.getPhone());
								builder.append("</td><td>");
								builder.append(failInfo.getRound());
								builder.append("</td><td>");
								builder.append(failInfo.getType());
								builder.append("</td><td>");
								builder.append(failInfo.getPackageName());
								builder.append("</td><td>");
								builder.append(failInfo.getShortMessage());
								builder.append("</td>");
								builder.append("</tr>");
							}

						}
					}
					line = line.replace("Tag_Results_Details",
							builder.toString());
				}
				// if (line.contains("Tag_Results_Details")) {
				// StringBuilder sBuilder = new StringBuilder();
				// int index = 0;
				// for (String logFile : this.logFiles) {
				// File file = new File(this.resultFolder + File.separator
				// + logFile);
				// BufferedReader logFileReader = new BufferedReader(
				// new FileReader(file));
				// String resultLine = "";
				// while ((resultLine = logFileReader.readLine()) != null) {
				// String[] splits = resultLine.split("	");
				// if (splits[1].equalsIgnoreCase("FAIL")) {
				// String phone = file.getName().substring(0,
				// file.getName().length() - 4);
				// failureCount++;
				// index++;
				// sBuilder.append("<tr style=\"background-color: e8767a;\">");
				// sBuilder.append("<td>");
				// sBuilder.append(index);
				// sBuilder.append("</td><td>");
				// sBuilder.append(phone);
				// sBuilder.append("</td><td>");
				// sBuilder.append(splits[0]);
				// sBuilder.append("</td><td>");
				// sBuilder.append(splits[2]);
				// sBuilder.append("</td><td>");
				// sBuilder.append(splits[3]);
				// sBuilder.append("</td><td>");
				// sBuilder.append(splits[4]);
				// sBuilder.append("</td>");
				// sBuilder.append("</tr>");
				// }
				// }
				// logFileReader.close();
				// }
				// if ((sBuilder.toString() != "")
				// || (sBuilder.toString() != null)) {
				// line = line.replace("Tag_Results_Details",
				// sBuilder.toString());
				// } else {
				// line = line
				// .replace("Tag_Results_Details",
				// "<font color=\"green\" face=\"monospace\" size=\"48\">ALL PASS</font>");
				// }
				// }
				resultBuilder.append(line);
			}
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		String content = resultBuilder.toString();
		String result = "";
		if (failureCount > 0) {
			result = content.replace("Tag_TEST_RESULT",
					"<font color=\"e8767a\" face=\"monospace\" size=\"48\">FAIL:"
							+ failureCount + "</font>");
		} else {
			result = content
					.replace("Tag_TEST_RESULT",
							"<font color=\"green\" face=\"monospace\" size=\"48\">ALL PASS</font>");
		}

		return result;
	}

	public void generateReportFile(String reportContent) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					this.resultFolder + File.separator + "result.html"));
			bw.write(reportContent.toString());
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		List<Phone> targets = new ArrayList<Phone>();
		MonkeyRunInfo runInfo = new MonkeyRunInfo();
		ApkInfo apkInfo = new ApkInfo();
		apkInfo.setApkLabel("QUAD");
		apkInfo.setApkName("QAUD");
		apkInfo.setFullPath("fasdf");
		apkInfo.setDebuggable(false);
		apkInfo.setPackageName("adfa");
		apkInfo.setVersion("4.2");
		runInfo.setApk(apkInfo);
		runInfo.setTestRound(10);
		runInfo.setTestTime(3600);
		MonkeyResultHandler handler = new MonkeyResultHandler(runInfo, targets,
				"C:\\Users\\defus_000\\Desktop\\METP\\Log\\monkey\\com.quad\\20140414--1626");
		String contentString = handler.generateHtmlReport();
		handler.generateReportFile(contentString);
		// "D:\\Workspaces_Eclipse\\METP\\Log\\monkey\\com.quad\\20131028--1405\\");
		// handler.generateReportFile(handler.generateHtmlReport());

	}
}
