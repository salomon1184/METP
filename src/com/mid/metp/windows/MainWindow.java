package com.mid.metp.windows;

import java.awt.EventQueue;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.mid.metp.model.Phone;
import com.mid.metp.model.TestCase;
import com.mid.metp.util.Log;
import com.mid.metp.util.PhoneRetriever;
import com.mid.metp.util.TestCaseRetriever;
import com.mid.metp.windows.actions.FileSelectionAdapter;
import com.mid.metp.windows.actions.JListSelectedActionListener;
import com.mid.metp.windows.actions.MonkeyMakeReportListener;
import com.mid.metp.windows.actions.MonkeyRunListener;
import com.mid.metp.windows.actions.MonkeyStopListener;
import com.mid.metp.windows.actions.MyListCellRender;
import com.mid.metp.windows.actions.UiaRunListener;
import com.mid.metp.windows.actions.UiaStopListener;

public class MainWindow {

	private JFrame frame;
	private JTextField textField_MonkeyApk;
	private JTextField textField_TestRound;
	private JTextField textField_RoundTime;
	private JTextField textField_Uia_Apk;
	private JTextField textField_Uia_Jar;
	private JTextField textField_Uia_TestRound;
	private JButton btnStart;
	private JButton btnStop;
	private final DefaultListModel<Phone> phoneUnSelectedListModel = new DefaultListModel<Phone>();
	private final DefaultListModel<Phone> phoneSelectedListModel = new DefaultListModel<Phone>();
	private final DefaultListModel<TestCase> testcaseSelectedListModel = new DefaultListModel<TestCase>();
	private final DefaultListModel<TestCase> testCaseListModel = new DefaultListModel<TestCase>();
	private JCheckBox chckbxLogcat;
	private JCheckBox chckbxScreencap;
	private JCheckBox chckbxPerfmonitor;
	private JTextField textField_Uia_Xml;
	private final JButton btnMakeReport = new JButton("Make Report");

	// private String packageName;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		this.initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.frame = new JFrame();
		this.frame.setBounds(100, 100, 900, 840);
		this.frame.setResizable(false);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setTitle("METP");
		this.frame.getContentPane().setLayout(null);

		int defaultWidth = 890;

		// ***************
		JTabbedPane tabbedPane_Config = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_Config.setLocation(0, 0);
		tabbedPane_Config.setSize(890, 376);

		this.frame.getContentPane().add(tabbedPane_Config);

		JPanel panel_Monkey = new JPanel();
		tabbedPane_Config.addTab("Monkey", null, panel_Monkey, null);
		panel_Monkey.setLayout(null);
		// ***************Monkey Panel*******************//
		JLabel lblApk = new JLabel(" Target Apk:");
		lblApk.setHorizontalAlignment(SwingConstants.RIGHT);
		lblApk.setBounds(0, 11, 77, 14);
		panel_Monkey.add(lblApk);

		this.textField_MonkeyApk = new JTextField();
		this.textField_MonkeyApk.setBounds(81, 8, 611, 20);
		panel_Monkey.add(this.textField_MonkeyApk);
		this.textField_MonkeyApk.setColumns(10);
		// Document apkDocument = this.textField_MonkeyApk.getDocument();
		// apkDocument.addDocumentListener(new MyDocumentListener());

		JLabel lblTestRound = new JLabel("Test Round:");
		lblTestRound.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTestRound.setBounds(0, 42, 77, 14);
		panel_Monkey.add(lblTestRound);

		this.textField_TestRound = new JTextField();
		this.textField_TestRound.setText("10");
		this.textField_TestRound.setBounds(81, 39, 77, 20);
		panel_Monkey.add(this.textField_TestRound);
		this.textField_TestRound.setColumns(10);

		JButton btnMonkeyBrowse = new JButton("Browse");
		btnMonkeyBrowse.addActionListener(new FileSelectionAdapter(this.frame,
				this.textField_MonkeyApk));
		btnMonkeyBrowse.setBounds(702, 7, 89, 23);
		panel_Monkey.add(btnMonkeyBrowse);

		JLabel lblRoundTime = new JLabel("Round Time:");
		lblRoundTime.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRoundTime.setBounds(259, 42, 89, 14);
		panel_Monkey.add(lblRoundTime);

		this.textField_RoundTime = new JTextField();
		this.textField_RoundTime.setText("3600");
		this.textField_RoundTime.setBounds(358, 39, 129, 20);
		panel_Monkey.add(this.textField_RoundTime);
		this.textField_RoundTime.setColumns(10);

		MonkeyRunListener listener = new MonkeyRunListener(
				MainWindow.this.textField_MonkeyApk,
				MainWindow.this.phoneSelectedListModel,
				MainWindow.this.textField_TestRound,
				MainWindow.this.textField_RoundTime,
				MainWindow.this.btnMakeReport);

		this.btnMakeReport.addActionListener(new MonkeyMakeReportListener(
				listener));

		JPanel panel_UiAutomator = new JPanel();
		tabbedPane_Config.addTab("UiAutomator", null, panel_UiAutomator, null);
		panel_UiAutomator.setLayout(null);

		JPanel panel_Robotium = new JPanel();
		tabbedPane_Config.addTab("Robotium", null, panel_Robotium, null);

		// 增加tab切换事件响应，切换时自动改变start按钮的action响应。
		tabbedPane_Config.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				// MonkeyRunListener monkeyListener = new MonkeyRunListener(
				// MainWindow.this.textField_MonkeyApk,
				// MainWindow.this.phoneSelectedListModel,
				// MainWindow.this.testcaseSelectedListModel);
				//
				// UiaRunListener uiaListener = new UiaRunListener();
				JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
				int selectedIndex = tabbedPane.getSelectedIndex();

				for (ActionListener al : MainWindow.this.btnStart
						.getActionListeners()) {
					MainWindow.this.btnStart.removeActionListener(al);
				}

				if (selectedIndex == 0) {
					// Log.log("切换到Monkey 配置运行界面");
					MonkeyRunListener startListener = new MonkeyRunListener(
							MainWindow.this.textField_MonkeyApk,
							MainWindow.this.phoneSelectedListModel,
							MainWindow.this.textField_TestRound,
							MainWindow.this.textField_RoundTime,
							MainWindow.this.btnMakeReport);
					MainWindow.this.btnStart.addActionListener(startListener);

					MainWindow.this.btnStop
							.addActionListener(new MonkeyStopListener(
									startListener,
									MainWindow.this.phoneUnSelectedListModel)); // 将不同测试框架的stop事件响应分开处理是为了以后扩展
					MainWindow.this.btnMakeReport
							.addActionListener(new MonkeyMakeReportListener(
									startListener));

				} else if (selectedIndex == 1) {

					// Log.log("切换到UiAutomator配置运行界面");
					UiaRunListener runListener = new UiaRunListener(
							MainWindow.this.textField_Uia_Apk,
							MainWindow.this.textField_Uia_Jar,
							MainWindow.this.textField_Uia_Xml,
							MainWindow.this.phoneSelectedListModel,
							MainWindow.this.testcaseSelectedListModel,
							MainWindow.this.textField_Uia_TestRound,
							MainWindow.this.chckbxLogcat,
							MainWindow.this.chckbxPerfmonitor,
							MainWindow.this.chckbxScreencap,
							MainWindow.this.btnMakeReport);
					MainWindow.this.btnStart.addActionListener(runListener);
					MainWindow.this.btnStop
							.addActionListener(new UiaStopListener(runListener));

				} else {
					// Log.log("切换到Robotium配置运行界面");
				}

			}
		});

		// *******************UiAutomator Panel**************
		JLabel lblTargetApk = new JLabel("Target Apk:");
		lblTargetApk.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTargetApk.setBounds(10, 11, 72, 14);
		panel_UiAutomator.add(lblTargetApk);

		this.textField_Uia_Apk = new JTextField();
		this.textField_Uia_Apk.setBounds(92, 8, 651, 20);
		panel_UiAutomator.add(this.textField_Uia_Apk);
		this.textField_Uia_Apk.setColumns(10);
		// Document uiaApkDocument = this.textField_Uia_Apk.getDocument();
		// uiaApkDocument.addDocumentListener(new MyDocumentListener());

		JLabel lblTestJar = new JLabel("Test Jar:");
		lblTestJar.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTestJar.setBounds(20, 36, 61, 14);
		panel_UiAutomator.add(lblTestJar);

		this.textField_Uia_Jar = new JTextField();
		Document document = this.textField_Uia_Jar.getDocument();
		document.addDocumentListener(new DocumentListener() {

			public void removeUpdate(DocumentEvent e) {
				// this.handle(e);
			}

			public void insertUpdate(DocumentEvent e) {
				this.handle(e);
			}

			private void handle(DocumentEvent e) {
				try {
					String file = e.getDocument().getText(0,
							e.getDocument().getLength());
					Log.log("解析测试Jar包中的测试用例......");
					TestCaseRetriever retriever = new TestCaseRetriever(
							MainWindow.this.textField_Uia_Apk,
							MainWindow.this.testCaseListModel,
							MainWindow.this.textField_Uia_Jar);
					retriever.execute();
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			public void changedUpdate(DocumentEvent e) {
				this.handle(e);
			}
		});

		this.textField_Uia_Jar.setBounds(92, 33, 239, 20);
		panel_UiAutomator.add(this.textField_Uia_Jar);
		this.textField_Uia_Jar.setColumns(10);

		JLabel lblParamXml = new JLabel("Param xml:");
		lblParamXml.setBounds(440, 35, 67, 17);
		panel_UiAutomator.add(lblParamXml);

		this.textField_Uia_Xml = new JTextField();
		this.textField_Uia_Xml.setBounds(510, 33, 233, 20);
		panel_UiAutomator.add(this.textField_Uia_Xml);
		this.textField_Uia_Xml.setColumns(10);

		JButton btnBrowse_Uia_Apk = new JButton("Browse");
		btnBrowse_Uia_Apk.addActionListener(new FileSelectionAdapter(
				this.frame, this.textField_Uia_Apk));
		btnBrowse_Uia_Apk.setBounds(774, 7, 89, 23);
		panel_UiAutomator.add(btnBrowse_Uia_Apk);

		JButton btnBrowse_Uia_Jar = new JButton("Browse");
		btnBrowse_Uia_Jar.setBounds(341, 32, 89, 23);
		panel_UiAutomator.add(btnBrowse_Uia_Jar);
		btnBrowse_Uia_Jar.addActionListener(new FileSelectionAdapter(
				this.frame, this.textField_Uia_Jar));

		JButton btnBrowse_Uia_xml = new JButton("Browse");
		btnBrowse_Uia_xml.setBounds(774, 32, 89, 23);
		panel_UiAutomator.add(btnBrowse_Uia_xml);
		btnBrowse_Uia_xml.addActionListener(new FileSelectionAdapter(
				this.frame, this.textField_Uia_Xml));

		JSeparator separator_4 = new JSeparator();
		separator_4.setOrientation(SwingConstants.VERTICAL);
		separator_4.setBounds(741, 64, 2, 273);
		panel_UiAutomator.add(separator_4);

		JLabel lblTestRound_1 = new JLabel("Test Round:");
		lblTestRound_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTestRound_1.setBounds(762, 93, 72, 14);
		panel_UiAutomator.add(lblTestRound_1);

		this.textField_Uia_TestRound = new JTextField();
		this.textField_Uia_TestRound.setText("1");
		this.textField_Uia_TestRound.setBounds(777, 118, 86, 20);
		panel_UiAutomator.add(this.textField_Uia_TestRound);
		this.textField_Uia_TestRound.setColumns(10);

		this.chckbxLogcat = new JCheckBox("Logcat");
		this.chckbxLogcat.setBounds(774, 160, 72, 23);
		panel_UiAutomator.add(this.chckbxLogcat);

		this.chckbxScreencap = new JCheckBox("ScreenCap");
		this.chckbxScreencap.setBounds(774, 203, 103, 23);
		panel_UiAutomator.add(this.chckbxScreencap);

		this.chckbxPerfmonitor = new JCheckBox("PerfMonitor");
		this.chckbxPerfmonitor.setBounds(774, 252, 109, 23);
		panel_UiAutomator.add(this.chckbxPerfmonitor);

		int scrollPaneY = 60;
		int scrollPaneWidth = 300;
		int scroolPaneHeight = 160;
		JScrollPane scrollPane_TestCase = new JScrollPane();
		scrollPane_TestCase.setBounds(10, 60, 300, 277);
		panel_UiAutomator.add(scrollPane_TestCase);
		JList list_TestCase = new JList(this.testCaseListModel);
		scrollPane_TestCase.setViewportView(list_TestCase);
		list_TestCase.setCellRenderer(new MyListCellRender());

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(409, 60, 300, 276);
		panel_UiAutomator.add(scrollPane);
		JList list_TestCaseSelected = new JList(this.testcaseSelectedListModel);
		scrollPane.setViewportView(list_TestCaseSelected);
		list_TestCaseSelected.setCellRenderer(new MyListCellRender());

		int scrollPaneX = 480;

		int buttonX = 320;
		JButton button = new JButton("->");
		button.setHorizontalAlignment(SwingConstants.LEADING);
		button.setBounds(320, 145, 52, 23);
		panel_UiAutomator.add(button);

		JButton button_1 = new JButton("<-");
		button_1.setBounds(320, 252, 52, 23);
		panel_UiAutomator.add(button_1);

		button.addActionListener(new JListSelectedActionListener(list_TestCase,
				list_TestCaseSelected, this.testCaseListModel,
				this.testcaseSelectedListModel));
		button_1.addActionListener(new JListSelectedActionListener(
				list_TestCaseSelected, list_TestCase,
				this.testcaseSelectedListModel, this.testCaseListModel));

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 387, 890, 2);
		this.frame.getContentPane().add(separator);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(0, 594, 890, 2);
		this.frame.getContentPane().add(separator_1);

		// *****************Common Config Panel***********
		PhoneRetriever phoneRetriever = new PhoneRetriever(
				this.phoneUnSelectedListModel);
		phoneRetriever.execute();

		JScrollPane scrollPane_Phone = new JScrollPane();
		scrollPane_Phone.setBounds(10, 400, 300, 115);
		this.frame.getContentPane().add(scrollPane_Phone);
		JList list_Machine = new JList(this.phoneUnSelectedListModel);
		scrollPane_Phone.setViewportView(list_Machine);
		list_Machine.setCellRenderer(new MyListCellRender());
		list_Machine.setAutoscrolls(true);
		list_Machine.setValueIsAdjusting(true);

		JScrollPane scrollPane_Phone_Selected = new JScrollPane();
		scrollPane_Phone_Selected
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_Phone_Selected.setBounds(411, 400, 300, 115);
		this.frame.getContentPane().add(scrollPane_Phone_Selected);

		JList list_MachineSelected = new JList(this.phoneSelectedListModel);
		scrollPane_Phone_Selected.setViewportView(list_MachineSelected);
		list_MachineSelected.setCellRenderer(new MyListCellRender());

		JButton btnNewButton = new JButton("->");
		btnNewButton.setBounds(320, 435, 50, 23);
		this.frame.getContentPane().add(btnNewButton);
		btnNewButton.setHorizontalAlignment(SwingConstants.LEADING);

		JButton btnNewButton_1 = new JButton("<-");
		btnNewButton_1.setBounds(320, 469, 50, 23);
		this.frame.getContentPane().add(btnNewButton_1);

		btnNewButton.addActionListener(new JListSelectedActionListener(
				list_Machine, list_MachineSelected,
				this.phoneUnSelectedListModel, this.phoneSelectedListModel));
		btnNewButton_1.addActionListener(new JListSelectedActionListener(
				list_MachineSelected, list_Machine,
				this.phoneSelectedListModel, this.phoneUnSelectedListModel));

		JCheckBox chckbxNewCheckBox = new JCheckBox("Send Email");
		chckbxNewCheckBox.setBounds(780, 423, 97, 23);
		this.frame.getContentPane().add(chckbxNewCheckBox);

		JSeparator separator_3 = new JSeparator();
		separator_3.setBounds(0, 526, 890, 2);
		this.frame.getContentPane().add(separator_3);

		this.btnStart = new JButton("Start");
		this.btnStart.setBounds(10, 549, 89, 23);
		this.btnStart.addActionListener(listener);
		this.frame.getContentPane().add(this.btnStart);

		this.btnStop = new JButton("Stop");
		this.btnStop.addActionListener(new MonkeyStopListener(listener,
				this.phoneUnSelectedListModel));
		this.btnStop.setBounds(189, 549, 89, 23);
		this.frame.getContentPane().add(this.btnStop);

		// this.btnMakeReport = new JButton("Make Report");
		this.btnMakeReport.setBounds(377, 549, 110, 23);
		this.frame.getContentPane().add(this.btnMakeReport);

		JLabel lbl_Status = new JLabel("");
		lbl_Status.setBounds(497, 547, 363, 28);
		this.frame.getContentPane().add(lbl_Status);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(744, 387, 2, 141);
		this.frame.getContentPane().add(separator_2);
		separator_2.setOrientation(SwingConstants.VERTICAL);

		// ************************底部 Result Panel****************

		// JScrollPane panel_Output = new JScrollPane();
		// panel_Output
		// .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		// JTextPane textPane_Output = new JTextPane();
		// panel_Output.setViewportView(textPane_Output);

		JTabbedPane tabbedPane_Result = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_Result.setBounds(0, 600, 890, 211);
		this.frame.getContentPane().add(tabbedPane_Result);
		tabbedPane_Result.addTab("Output", null, ConsolePane2.getInstance(),
				null);
		JPanel panel_Result = new JPanel();
		tabbedPane_Result.addTab("Result", null, panel_Result, null);

	}
}
