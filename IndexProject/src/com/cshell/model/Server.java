package com.cshell.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Server {
	private String ip;
	private String pod;
	private String switchIp;
	private BPlusTree localIndex;
	private BitSet[] globalIndex;
	
	private static String[] captains;
	
	public static final int RANGE_OF_KEY = 1280000;
	public static final int NUM_OF_SERVER = 128;
	public static final int HANDLE_UNIT_OF_SERVER = RANGE_OF_KEY / NUM_OF_SERVER; // handle range for every captain server
	public static final int ORDER_OF_BPLUS_TREE = 8;
	public static final int GLOBAL_INDEX_KEY_BLOCK_SIZE = 100; // a block handles from 0~99, 100~199, ...
	// since every captain handles a certain range of data, and data is block-formatted, number of block should be calculated
	public static final int NUMBER_OF_GLOBAL_BLOCKS = HANDLE_UNIT_OF_SERVER / GLOBAL_INDEX_KEY_BLOCK_SIZE;
	
	public int START_VALUE = -1;
	
	// public static globalIndex;
	
	public Server() {
		ip = "";
		pod = "";
		switchIp = "";
		
		initGlobalAndLocalIndex();
	}
	
	public Server(String ip, String pod, String switchIp) {
		this.ip = ip;
		this.pod = pod;
		this.switchIp = switchIp;
		
		calculateStartValueOfCamptain(this.ip);
		
		initGlobalAndLocalIndex();
	}
	
	public static String getCaptainServerIpByKey(int key) {
		if (key < 0 || key >= RANGE_OF_KEY) {
			return null;
		}
		
		if (captains == null) {
			generateCaptains();
		}
		
		int serverIdx = key / HANDLE_UNIT_OF_SERVER; // starting from 0
		
		return captains[serverIdx];
	}
	
	public static String getCaptainServerIpByServerOrder(int index) {
		if (index < 0 || index >= NUM_OF_SERVER) {
			return null;
		}
		
		if (captains == null) {
			generateCaptains();
		}
		
		return captains[index];
	}
	
	public List<String> getPotentialServerIpsFromGlobalIndex(int key) {
		if (key < START_VALUE || key >= START_VALUE + HANDLE_UNIT_OF_SERVER) {
			return null;
		}
		List<String> ipList = new ArrayList<String>();
		
		BitSet bs = getBitSetFromGlobalIndex(key);
		
		String outString = bs.toString();
		outString = outString.substring(1, outString.length() - 1);
		//System.out.println(outString);
		String[] outArr = outString.split(", ");
		for (int i = 0; i < outArr.length; i++) {
			ipList.add(getCaptainServerIpByServerOrder(new Integer(outArr[i])));
		}
		
		return ipList;
	}
	
	public void insertValueIntoLocalIndex(int key, int value) {
		localIndex.insertOrUpdate(key, value);
	}
	
	public int getValueFromLocalIndex(int key) {
		Object out = localIndex.get(key);
		if (out == null) {
			return -1;
		}
		return (Integer) out;
	}
	
	public void setGlobalIndexBitSet(int i, BitSet bs) {
		if (i >= 0 && i < globalIndex.length) {
			globalIndex[i] = bs;
		}
	}
	
	public void cleanAllValuesOfLocalIndex() {
		localIndex = null;
		localIndex = new BPlusTree(ORDER_OF_BPLUS_TREE);
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
		calculateStartValueOfCamptain(this.ip);
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
	
	private static void generateCaptains() {
		List<String> serverList = new ArrayList<String>();
		// Server switch IP: 10.pod.switch.ID (pod = 0~7, switch = 0~3, ID = 2~5)
		for (int pod = 0; pod <= 7; pod++) { // pod id
			for (int swd = 0; swd <= 3; swd++) { // switch id
				for (int sid = 2; sid <= 5; sid++) { // server ID
					serverList.add("10." + pod + "." + swd + "." + sid);
				}
			}
		}
		captains = new String[NUM_OF_SERVER];
		serverList.toArray(captains);
	}
	
	private void initGlobalAndLocalIndex() {
		localIndex = new BPlusTree(ORDER_OF_BPLUS_TREE);
		
		/*
		 * For global index, we use BitSet to create an hash array
		 * 
		 * The column number equals to server's number &
		 * the row number equals to the number of data unit (RANGE_OF_KEY / GLOBAL_INDEX_UNIT_RANGE)
		 * so that every row consists of all server list.
		 */
		//List<BitSet> globalIndexList = new ArrayList<BitSet>();
		//for (int i = 0; i < NUMBER_OF_GLOBAL_BLOCKS; i++) {
		//	globalIndexList.add(new BitSet(NUM_OF_SERVER));
		//}
		//globalIndexList.toArray(globalIndex);
		globalIndex = new BitSet[NUMBER_OF_GLOBAL_BLOCKS];
	}
	
	private void calculateStartValueOfCamptain(String ip) {
		// Server switch IP: 10.pod.switch.ID (pod = 0~7, switch = 0~3, ID = 2~5)
		String[] tSplit = ip.split("\\.");
		int podId = new Integer(tSplit[1]);
		int switchId = new Integer(tSplit[2]);
		int sId = new Integer(tSplit[3]);
		int serverId = podId * 16 + switchId * 4 + (sId - 1) - 1;
		
		START_VALUE = HANDLE_UNIT_OF_SERVER * serverId;
	}
	
	private BitSet getBitSetFromGlobalIndex(int key) {
		int rowIdx = (key - START_VALUE) / GLOBAL_INDEX_KEY_BLOCK_SIZE;
		return globalIndex[rowIdx];
	}
}