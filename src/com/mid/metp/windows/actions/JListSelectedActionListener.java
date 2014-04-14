package com.mid.metp.windows.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class JListSelectedActionListener implements ActionListener {

	private JList sourceList;
	private JList targetList;
	private DefaultListModel sourceListModel;
	private DefaultListModel targetListModel;

	public JListSelectedActionListener(JList formList, JList toList,
			DefaultListModel fromListModel, DefaultListModel toListModel) {
		sourceList = formList;
		targetList = toList;
		sourceListModel = fromListModel;
		targetListModel = toListModel;
	}

	public void actionPerformed(ActionEvent e) {

		for (Object listItem : sourceList.getSelectedValuesList()) {

			sourceListModel.removeElement(listItem);
			targetListModel.addElement(listItem);
		}
	}
}
