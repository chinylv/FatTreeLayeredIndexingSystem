package com.cshell.model;

public class SecondaryRoutingTable extends RoutingTable {
	/*
	 * For secondary routing table, the mask is right-handed
	 */
	public void getOutPortAndIpFromSwitchRules(String ip, PortAndIPDataStructure ds) {
		boolean isOkay = false;
		for (SwitchRule s : switchRules) {
			if (s.isIpAtSuffixMaskMatch(ip)) {
				isOkay = true;
				ds.outPort = s.getPort();
				ds.destIp = s.getDestIp();
				//System.out.println("SecondaryRoutingTable IP: " + s.getIp()
				//		+ ", Mask: " + s.getMask() + ", Port: " + s.getPort());
				break;
			}
		}
		
		// secondary routing table responsible for inter-node traffic
		if (!isOkay) {
			ds.outPort = -1;
			System.out.println("!!Error, IP(" + ip + ") not matched in SecondaryRoutingTable.");
		}
	}
 }
