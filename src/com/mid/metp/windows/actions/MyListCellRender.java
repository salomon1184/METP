package com.mid.metp.windows.actions;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.mid.metp.model.Phone;
import com.mid.metp.model.TestCase;
import com.mid.metp.util.Helper;

public class MyListCellRender extends DefaultListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 448156300859754649L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		// TODO Auto-generated method stub
		super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
		if (value instanceof Phone) {
			Phone phone = (Phone) value;
			this.setText(phone.toString());
			this.setToolTipText(phone.getId());
		}

		if (value instanceof TestCase) {
			TestCase case1 = (TestCase) value;
			String[] splits = case1.getClassName().split("\\.");
			// System.out.println(case1.getClassName());
			// System.out
			// .println(splits.length + ": " + splits[splits.length - 1]);
			this.setText(splits[splits.length - 1] + "#"
					+ case1.getMethodName());
			// this.setText(case1.getMethodName());
			this.setToolTipText(Helper.combineStrings(case1.getDescription(),
					"_", case1.getAuthor(), "_", case1.getClassName()));
		}

		return this;
	}
}
