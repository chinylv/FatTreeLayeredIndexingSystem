package com.cshell.model;

public class PortAndIPDataStructure {
	public String destIp;
	public int outPort;
	
	public PortAndIPDataStructure() {
		destIp = "-1";
		outPort = -1;
	}
	
	public PortAndIPDataStructure(String destIp, int outPort) {
		this.destIp = destIp;
		this.outPort = outPort;
	}
}
