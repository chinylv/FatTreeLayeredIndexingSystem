package com.cshell.test;

import java.util.ArrayList;
import java.util.List;

public class MainTest {
	
	public void testRoutingFunctionality() {
		System.out.println("Test starts-------------------");
		ProjectTest pt = new ProjectTest();
		
		List<String> serverIpList = new ArrayList<String>();
		
		for (int pod = 0; pod <= 7; pod++) {
			for (int sw = 0; sw <= 3; sw++) {
				for (int sid = 2; sid <= 5; sid++) {
					String tmpIp = "10." + pod + "." + sw + "." + sid;
					serverIpList.add(tmpIp);
				}
			}
		}
		
		int listSize = serverIpList.size();
		for (int cnt = 0; cnt <= 10000; cnt++) {
			int srcIdx = (int)(Math.random() * (listSize - 1) );
			int destIdx = (int)(Math.random() * (listSize - 1) );
			System.out.println("Test " + cnt + " starts---------------");
			List<String> ipList;
			
			pt.initConfigurations();
			ipList = pt.routeThePath(serverIpList.get(srcIdx), serverIpList.get(destIdx));
			
			for (String ip : ipList) {
				System.out.println(ip);
			}
			System.out.println("Test " + cnt + " ends---------------");
		}
		
		
		
		System.out.println("Test ends-------------------");
	}

	public static void main(String[] args) {
		MainTest mt = new MainTest();
		mt.testRoutingFunctionality();
	}

}
