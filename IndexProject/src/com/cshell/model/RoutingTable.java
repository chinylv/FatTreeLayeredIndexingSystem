package com.cshell.model;

import java.util.ArrayList;
import java.util.List;

public class RoutingTable {
	protected List<SwitchRule> switchRules;
	
	public RoutingTable() {
		switchRules = new ArrayList<SwitchRule>();
	}
	
	public void insertSwitchRule(String ip, int mask, int port, String destIp) {
		switchRules.add(new SwitchRule(ip, mask, port, destIp));
	}
	
	public int getNumberOfSwitchRules() {
		return switchRules.size();
	}
	
	public void deleteSwitchRule(int index) {
		if (index > 0) switchRules.remove(index);
	}
	
	public void clearSwitchRules() {
		switchRules.clear();
	}
 }
