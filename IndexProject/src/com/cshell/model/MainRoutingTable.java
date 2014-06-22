package com.cshell.model;

public class MainRoutingTable extends RoutingTable {
	/*
	 * For main routing table, the mask is left-handed
	 */
	public void getOutPortAndIpFromSwitchRules(String ip, PortAndIPDataStructure ds) {
		boolean isOkay = false;
		for (SwitchRule s : switchRules) {
			if (s.isIpAtPrefixMaskMatch(ip)) {
				isOkay = true;
				ds.outPort = s.getPort();
				ds.destIp = s.getDestIp();
				//System.out.println("MainRoutingTable IP: " + s.getIp()
				//		+ ", Mask: " + s.getMask() + ", Port: " + s.getPort());
				break;
			}
		}
		
		// main routing table responsible for intra-node traffic
		if (!isOkay) {
			ds.outPort = -1;
		//	System.out.println("!!Error, IP(" + ip + ") not matched in MainRoutingTable.");
		}
	}
 }
