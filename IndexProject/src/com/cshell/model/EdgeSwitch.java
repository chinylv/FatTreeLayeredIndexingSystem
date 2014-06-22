package com.cshell.model;

public class EdgeSwitch extends Switch {
	private String pod;
	
	public EdgeSwitch() {
		pod = "";
	}
	
	public EdgeSwitch(String ip, String pod) {
		this.ip = ip;
		this.pod = pod;
	}

	public String getPod() {
		return pod;
	}

	public void setPod(String pod) {
		this.pod = pod;
	}
}