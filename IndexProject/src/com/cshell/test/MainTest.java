package com.cshell.test;

import java.util.ArrayList;
import java.util.List;

import com.cshell.model.Server;

public class MainTest {
	public static final long NUMBER_OF_TEST_CASES = 100000;
	
	public void testRoutingFunctionality() {
		System.out.println("Test routing functionality starts-------------------");
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
		
		pt.initConfigurations();
		
		int listSize = serverIpList.size();
		for (int cnt = 0; cnt <= 10000; cnt++) {
			int srcIdx = (int)(Math.random() * (listSize - 1) );
			int destIdx = (int)(Math.random() * (listSize - 1) );
			System.out.println("Test " + cnt + " starts---------------");
			List<String> ipList;
			
			ipList = pt.routeThePath(serverIpList.get(srcIdx), serverIpList.get(destIdx));
			
			for (String ip : ipList) {
				System.out.println(ip);
			}
			System.out.println("Test " + cnt + " ends---------------");
		}
		
		System.out.println("Test routing functionality ends-------------------");
	}
	
	public void testFindingCaptainServerIp() {
		System.out.println("Test finding captain server ip starts-------------------");
		ProjectTest pt = new ProjectTest();
		for (int i = 0; i < 1000; i++) {
			int tmpVal = (int)(Math.random() * (Server.RANGE_OF_KEY - 1) );
			System.out.println(pt.getCaptainServerIp(tmpVal));
		}
		System.out.println("Test finding captain server ip ends-------------------");
	}
	
	public void testGeneratingValues() {
		System.out.println("Test searching values starts-------------------");
		ProjectTest pt = new ProjectTest();
		pt.initConfigurations();
		pt.generateValuesToLocalAndGlobalIndexInZipfDistribution();
		System.out.println("Test searching values ends-------------------");
	}
	
	public void testSearchingKeyValuesInZipfDistribution() {
		System.out.println("Test searching key values in Zipf distribution starts-------------------");
		ProjectTest pt = new ProjectTest();
		
		pt.initConfigurations();
		pt.generateValuesToLocalAndGlobalIndexInZipfDistribution();
		
		Integer[] storedKeys = pt.storedKeys;
		
		int numStoredData = storedKeys.length;
		
		pt.totalFalsePositiveNumber = 0;
		pt.totalHopNumber = 0;
		
		for (int i = 0; i < NUMBER_OF_TEST_CASES; i++) {
			int searchedKey = storedKeys[(int)(Math.random() * (numStoredData - 1))];
			System.out.println("searchedKey: " + searchedKey);
			
			String campIp = Server.getCaptainServerIpByKey(searchedKey);
			Server campServer = pt.getServerByIp(campIp);
			List<String> potentIpList = campServer.getPotentialServerIpsFromGlobalIndex(searchedKey);
			
			System.out.println("Value found: " + pt.findValueOfAKeyFromPossibleServers(searchedKey, potentIpList, campIp));
		}
		
		System.out.println("Total hops: " + pt.totalHopNumber + NUMBER_OF_TEST_CASES);
		System.out.println("Average hops: " + ((pt.totalHopNumber + 0.0) / (NUMBER_OF_TEST_CASES + 0.0) + 1));
		
		System.out.println("Total false positive: " + pt.totalFalsePositiveNumber);
		System.out.println("Average false positive: " + (pt.totalFalsePositiveNumber + 0.0) / (NUMBER_OF_TEST_CASES + 0.0));
		
		System.out.println("Test searching key values in Zipf distribution ends-------------------");
	}
	
	public void testSearchingKeyValuesInUnionDistribution() {
		System.out.println("Test searching key values in Union distribution starts-------------------");
		ProjectTest pt = new ProjectTest();
		
		pt.initConfigurations();
		pt.generateValuesToLocalAndGlobalIndexInUnionDistribution();
		
		Integer[] storedKeys = pt.storedKeys;
		
		int numStoredData = storedKeys.length;
		
		pt.totalFalsePositiveNumber = 0;
		pt.totalHopNumber = 0;
		
		for (int i = 0; i < NUMBER_OF_TEST_CASES; i++) {
			int searchedKey = storedKeys[(int)(Math.random() * (numStoredData - 1))];
			System.out.println("searchedKey: " + searchedKey);
			
			String campIp = Server.getCaptainServerIpByKey(searchedKey);
			Server campServer = pt.getServerByIp(campIp);
			List<String> potentIpList = campServer.getPotentialServerIpsFromGlobalIndex(searchedKey);
			
			System.out.println("Value found: " + pt.findValueOfAKeyFromPossibleServers(searchedKey, potentIpList, campIp));
		}
		
		System.out.println("Total hops: " + pt.totalHopNumber + NUMBER_OF_TEST_CASES); // because of finding the captain
		System.out.println("Average hops: " + ((pt.totalHopNumber + 0.0) / (NUMBER_OF_TEST_CASES + 0.0) + 1));
		
		System.out.println("Total false positive: " + pt.totalFalsePositiveNumber);
		System.out.println("Average false positive: " + (pt.totalFalsePositiveNumber + 0.0) / (NUMBER_OF_TEST_CASES + 0.0));
		
		System.out.println("Test searching key values in Union distribution ends-------------------");
	}

	public static void main(String[] args) {
		MainTest mt = new MainTest();
		//mt.testRoutingFunctionality();
		
		//mt.testFindingCaptainServerIp();
		
		mt.testSearchingKeyValuesInZipfDistribution();
		// mt.testSearchingKeyValuesInUnionDistribution();
	}

}
