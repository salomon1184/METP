package com.mid.metp.util;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;

import com.mid.metp.model.Phone;

/**
 * 获取插在该电脑手机列表
 * 
 * @author defu
 * 
 */
public class PhoneRetriever extends SwingWorker<List<Phone>, Phone> {

	private DefaultListModel listModel;

	public PhoneRetriever(DefaultListModel model) {
		this.listModel = model;
	}

	@Override
	protected void process(List<Phone> chunks) {

		super.process(chunks);

		// for (Phone phoneId : chunks) {
		// this.listModel.addElement(phoneId);
		// }
	}

	@Override
	protected List<Phone> doInBackground() throws Exception {
		AndroidUtility uitility = new AndroidUtility(Utility.getMachineType());
		return uitility.getPhones();
	}

	@Override
	protected void done() {
		super.done();
		try {
			List<Phone> phones = this.get();
			for (Phone phone : phones) {
				this.listModel.addElement(phone);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
