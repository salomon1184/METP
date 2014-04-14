package com.mid.metp.windows.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;

import com.mid.metp.model.Phone;

public class MonkeyStopListener implements ActionListener {

	private final MonkeyRunListener runListener;
	private final DefaultListModel<Phone> targetsModel;

	public MonkeyStopListener(MonkeyRunListener listener,
			DefaultListModel<Phone> phoneList) {
		this.runListener = listener;
		this.targetsModel = phoneList;
	}

	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < this.targetsModel.getSize(); i++) {
			Phone phone = this.targetsModel.getElementAt(i);
			if (this.runListener.getPhoneWorkingThreads().containsKey(phone)) {
				this.runListener.getPhoneWorkingThreads().get(phone)
						.cancel(true);
				this.runListener.getPhoneWorkingThreads().remove(phone);
			}

		}
		//
		// for (MonkeyExecuter executer : this.runListener.getMonkeyExecuters())
		// {
		// executer.cancel(true);
		// }
	}

}
