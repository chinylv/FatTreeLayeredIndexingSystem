package com.cshell.model;

public class AggregationSwitch extends Switch {
	private String pod;
	
	public AggregationSwitch() {
		pod = "";
	}
	
	public AggregationSwitch(String ip, String pod) {
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