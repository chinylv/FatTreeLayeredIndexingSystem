package com.cshell.model;

public class Switch {
	public static final int PORT_COUNT = 8;
	
	protected String ip;
	protected MainRoutingTable mainTable;
	protected SecondaryRoutingTable secondTable;
	
	public Switch() {
		ip = "";
		mainTable = new MainRoutingTable();
		secondTable = new SecondaryRoutingTable();
	}
	
	public Switch(String ip) {
		this.ip = ip;
		mainTable = new MainRoutingTable();
		secondTable = new SecondaryRoutingTable();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	/*
	 * For Core Switch, the first half ports linked to first half pods
	 * second half linked to the second half
	 */
	
	/*
	 * For Aggregation or Edge Switch, the first half linked to the lower switch/server
	 * second half linked to the upper switch
	 */
	public void insertSwitchRuleIntoMainRoutingTable(String ip, int mask, int port, String destIp) {
		mainTable.insertSwitchRule(ip, mask, port, destIp);
	}
	
	public void insertSwitchRuleIntoSecondaryRoutingTable(String ip, int mask, int port, String destIp) {
		secondTable.insertSwitchRule(ip, mask, port, destIp);
	}
	
	public void getOutPortAndIpFromSwitchRules(String ip, PortAndIPDataStructure ds) {
		mainTable.getOutPortAndIpFromSwitchRules(ip, ds);
		if (ds.outPort < 0) {
			secondTable.getOutPortAndIpFromSwitchRules(ip, ds);
		}
	}
}