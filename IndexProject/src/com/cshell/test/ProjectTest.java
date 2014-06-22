package com.cshell.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.cshell.model.AggregationSwitch;
import com.cshell.model.CoreSwitch;
import com.cshell.model.EdgeSwitch;
import com.cshell.model.PortAndIPDataStructure;
import com.cshell.model.Server;
import com.cshell.model.Switch;

public class ProjectTest {
	public Hashtable<String, Switch> switchHashtable;
	public Hashtable<String, Server> serverHashtable;
	
	private FileReader fReader;
	private BufferedReader buffReader;
	
	public ProjectTest() {
		switchHashtable = new Hashtable<String, Switch>();
		serverHashtable = new Hashtable<String, Server>();
	}
	
	public void initConfigurations() {
		try {
			readIpConfigurations();
			readRoutingConfigurations();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> routeThePath(String srcIp, String destIp) {
		List<String> ipList = new ArrayList<String>();
		ipList.add(srcIp);
		Server srcServer = getServerByIp(srcIp);
		Switch pathSwitch = getSwitchByIp(srcServer.getSwitchIp());
		
		
		PortAndIPDataStructure ds = new PortAndIPDataStructure(srcServer.getSwitchIp(), -1);
		while (!"-1".equals(ds.destIp)) {
			pathSwitch = getSwitchByIp(ds.destIp);
			ipList.add(ds.destIp);
			
			ds.destIp = "-1";
			ds.outPort = -1;
			pathSwitch.getOutPortAndIpFromSwitchRules(destIp, ds);
		}
		
		ipList.add(destIp);
		
		return ipList;
	}
	
	public Switch getSwitchByIp(String ip) {
		Switch s = switchHashtable.get(ip);
		return s;
	}
	
	public Server getServerByIp(String ip) {
		Server s = serverHashtable.get(ip);
		return s;
	}
	
	private void readIpConfigurations() throws IOException {
		readHostIpConfigurations();
		readCoreSwitchIpConfigurations();
		readAggregationSwitchIpConfigurations();
		readEdgeSwitchIpConfigurations();
	}
	
	private void readRoutingConfigurations() throws IOException {
		readCoreSwitchRoutingConfigurations();
		readAggregationSwitchRoutingConfigurations();
		readEdgeSwitchRoutingConfigurations();
	}
	
	private void insertServerIntoHashTable(String ip, Server s) {
		if (ip != null && s != null) {
			serverHashtable.put(ip, s);
		}
	}
	
	private void insertSwitchIntoHashTable(String ip, Switch s) {
		if (ip != null && s != null) {
			switchHashtable.put(ip, s);
		}
	}
	
	private void readHostIpConfigurations() throws IOException {
		fReader = new FileReader("input/ip/Host_IP.txt");
		buffReader = new BufferedReader(fReader);
		
		String content;
		String[] split;
		
		for (content = buffReader.readLine(); content != null; content = buffReader.readLine()) {
			// System.out.println(content);
			
			// Server IP: 10.pod.switch.ID
			split = content.split("\\.");
			// Edge switch IP: 10.pod.switch.1
			String edgeIp = split[0] + "." + split[1] + "." + split[2] + ".1";
			
			Server server = new Server(content, split[1], edgeIp);
			
			insertServerIntoHashTable(content, server);
		}
		buffReader.close();
		fReader.close();
	}
	
	private void readCoreSwitchIpConfigurations() throws IOException {
		fReader = new FileReader("input/ip/Core_Layer_IP.txt");
		buffReader = new BufferedReader(fReader);
		
		String content;
		
		for (content = buffReader.readLine(); content != null; content = buffReader.readLine()) {
			// System.out.println(content);
			
			// Core switch IP: 10.8.i.j   i,j={1,2,3,4}
			CoreSwitch cs = new CoreSwitch(content);
			insertSwitchIntoHashTable(content, cs);
		}
		buffReader.close();
		fReader.close();
	}
	
	private void readAggregationSwitchIpConfigurations() throws IOException {
		fReader = new FileReader("input/ip/Aggregation_Layer_IP.txt");
		buffReader = new BufferedReader(fReader);
		
		String content;
		String[] split;
		
		for (content = buffReader.readLine(); content != null; content = buffReader.readLine()) {
			// System.out.println(content);
			
			// Aggregation switch IP: 10.pod.switch.1
			split = content.split("\\.");
			
			AggregationSwitch as = new AggregationSwitch(content, split[1]);
			
			insertSwitchIntoHashTable(content, as);
		}
		buffReader.close();
		fReader.close();
	}
	
	private void readEdgeSwitchIpConfigurations() throws IOException {
		fReader = new FileReader("input/ip/Edge_Layer_IP.txt");
		buffReader = new BufferedReader(fReader);
		
		String content;
		String[] split;
		
		for (content = buffReader.readLine(); content != null; content = buffReader.readLine()) {
			// System.out.println(content);
			
			// Edge switch IP: 10.pod.switch.1
			split = content.split("\\.");
			
			EdgeSwitch es = new EdgeSwitch(content, split[1]);
			
			insertSwitchIntoHashTable(content, es);
		}
		buffReader.close();
		fReader.close();
	}
	
	private void readCoreSwitchRoutingConfigurations() throws IOException {
		fReader = new FileReader("input/route/Core_Layer_Route.txt");
		buffReader = new BufferedReader(fReader);
		
		String content;
		String[] split;
		
		// Core switch IP: 10.8.i.j   i,j={1,2,3,4}
		// Every 8 lines per switch
		// Ex. 10.0.0.0/16,0,10.0.4.1
		for (int row = 1; row <= 4; row++) {
			for (int col = 1; col <= 4; col++) {
				int cnt = 0;
				while(cnt < 8) {
					content = buffReader.readLine();
					split = content.split(",");
					String[] addr = split[0].split("/"); // ip and mask
					
					String switchIp = "10.8." + row + "." + col;
					Switch sw = getSwitchByIp(switchIp);
					
					sw.insertSwitchRuleIntoMainRoutingTable(addr[0], new Integer(addr[1]), new Integer(split[1]), split[2]);
					cnt++;
				}
			}
		}
		
		buffReader.close();
		fReader.close();
	}
	
	private void readAggregationSwitchRoutingConfigurations() throws IOException {
		fReader = new FileReader("input/route/Aggregation_Layer_Route.txt");
		buffReader = new BufferedReader(fReader);
		
		String content;
		String[] split;
		
		// Aggregation switch IP: 10.pod.switch.1
		// Every 8 lines per switch, first 4 the mainTable, rest the secondTable
		// Ex. 10.0.0.0/24,0,10.0.0.1
		for (int pod = 0; pod <= 7; pod++) { // pod id
			for (int swd = 4; swd <= 7; swd++) { // switch id
				int cnt = 0;
				while(cnt < 8) {
					content = buffReader.readLine();
					split = content.split(",");
					String[] addr = split[0].split("/"); // ip and mask
					
					String switchIp = "10." + pod + "." + swd + ".1";
					Switch sw = getSwitchByIp(switchIp);
					
					if (cnt < 4) {
						sw.insertSwitchRuleIntoMainRoutingTable(addr[0], new Integer(addr[1]), new Integer(split[1]), split[2]);
					} else {
						sw.insertSwitchRuleIntoSecondaryRoutingTable(addr[0], new Integer(addr[1]), new Integer(split[1]), split[2]);
					}
					
					cnt++;
				}
			}
		}
		
		buffReader.close();
		fReader.close();
	}
	
	private void readEdgeSwitchRoutingConfigurations() throws IOException {
		fReader = new FileReader("input/route/Edge_Layer_Route.txt");
		buffReader = new BufferedReader(fReader);
		
		String content;
		String[] split;
		
		// Edge switch IP: 10.pod.switch.1
		// Every 8 lines per switch, first 4 the mainTable, rest the secondTable
		// Ex. 10.0.0.2/32,0,-1
		for (int pod = 0; pod <= 7; pod++) { // pod id
			for (int swd = 0; swd <= 3; swd++) { // switch id
				int cnt = 0;
				while(cnt < 8) {
					content = buffReader.readLine();
					split = content.split(",");
					String[] addr = split[0].split("/"); // ip and mask
					
					String switchIp = "10." + pod + "." + swd + ".1";
					Switch sw = getSwitchByIp(switchIp);
					
					if (cnt < 4) {
						sw.insertSwitchRuleIntoMainRoutingTable(addr[0], new Integer(addr[1]), new Integer(split[1]), split[2]);
					} else {
						sw.insertSwitchRuleIntoSecondaryRoutingTable(addr[0], new Integer(addr[1]), new Integer(split[1]), split[2]);
					}
					
					cnt++;
				}
			}
		}
		
		buffReader.close();
		fReader.close();
	}

	
	public static void main(String[] args) {
		System.out.println("Test starts-------------------");
		ProjectTest pt = new ProjectTest();
		
		List<String> ipList;
		
		pt.initConfigurations();
		ipList = pt.routeThePath("10.0.1.2", "10.0.1.3");
		
		for (String ip : ipList) {
			System.out.println(ip);
		}
		
		
		System.out.println("Test ends-------------------");
	}

}
