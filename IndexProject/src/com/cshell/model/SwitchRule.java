package com.cshell.model;

public class SwitchRule {
	private IPItem ipItem;
	private int port;
	private String destIp;
	
	public SwitchRule(String ip, int mask, int port, String destIp) {
		ipItem = new IPItem(ip, mask);
		this.port = port;
		this.destIp = destIp;
	}
	
	public String getIp() {
		return ipItem.getIp();
	}
	
	public int getMask() {
		return ipItem.getMask();
	}
	
	public int getPort() {
		return port;
	}
	
	public String getDestIp() {
		return destIp;
	}
	
	public boolean isRuleValid() {
		if (port < 0 || port > Switch.PORT_COUNT) {
			System.out.println("SwitchRule Port number not valid! Port: " + port);
			return false;
		}
		if (!ipItem.isIpValid()) {
			System.out.println("SwitchRule IP Item not valid! IP: " + ipItem.getIp() + ", Port: " + ipItem.getMask());
			return false;
		}
		if ("-1".equals(destIp) || IPItem.IPv4_Pattern.matcher(destIp).matches()) {
			return true;
		}
		System.out.println("SwitchRule Dest IP not valid! Dest IP: " + destIp);
		return false;
	}
	
	public boolean isIpAtPrefixMaskMatch(String ip) {
		return ipItem.isIpAtPrefixMaskMatch(ip);
	}
	
	public boolean isIpAtSuffixMaskMatch(String ip) {
		return ipItem.isIpAtSuffixMaskMatch(ip);
	}
 }
