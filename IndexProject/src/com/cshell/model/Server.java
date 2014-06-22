package com.cshell.model;

public class Server {
	private String ip;
	private String pod;
	private String switchIp;
	
	// public static globalIndex;
	
	public Server() {
		ip = "";
		pod = "";
		switchIp = "";
	}
	
	public Server(String ip, String pod, String switchIp) {
		this.ip = ip;
		this.pod = pod;
		this.switchIp = switchIp;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPod() {
		return pod;
	}

	public void setPod(String pod) {
		this.pod = pod;
	}
	
	public String getSwitchIp() {
		return switchIp;
	}

	public void setSwitchIp(String switchIp) {
		this.switchIp = switchIp;
	}

	public boolean isIpValid() {
		if (!IPItem.IPv4_Pattern.matcher(ip).matches()) {
			System.out.println("Server IP not valid! IP: " + ip);
			return false;
		}
		return true;
	}
	
}