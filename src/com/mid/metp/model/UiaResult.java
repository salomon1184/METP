package com.mid.metp.model;

import java.util.ArrayList;
import java.util.HashMap;

public class UiaResult {

	private ApkInfo apk;
	private ArrayList<Phone> targets = new ArrayList<Phone>();
	private HashMap<TestCase, ArrayList<UiaFailDetails>> failDetails = new HashMap<TestCase, ArrayList<UiaFailDetails>>();

}
