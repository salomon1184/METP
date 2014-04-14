package com.mid.metp.windows.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileSelectionAdapter implements ActionListener {

	private Component mainFrameWindow;
	private JTextField inputTextFiled;

	public FileSelectionAdapter(JFrame mainFrame, JTextField textField) {
		this.mainFrameWindow = mainFrame;
		this.inputTextFiled = textField;
	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser("./");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"METP File", "apk", "jar", "xml");
		fileChooser.setFileFilter(filter);

		int returnVal = fileChooser.showOpenDialog(this.mainFrameWindow);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String path = fileChooser.getSelectedFile().getAbsolutePath();
			this.inputTextFiled.setText(path);
		}

	}

}
