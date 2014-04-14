package com.mid.metp.windows.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.mid.metp.workflow.UiAutomatorExecuter;

public class UiaStopListener implements ActionListener {

	private UiaRunListener uiaRunListener;

	public UiaStopListener(UiaRunListener uiaRunListener) {
		super();
		this.uiaRunListener = uiaRunListener;
	}

	public void actionPerformed(ActionEvent e) {
		for (UiAutomatorExecuter executer : this.uiaRunListener
				.getUiaExecuters()) {
			executer.cancel(true);
		}

	}

}
