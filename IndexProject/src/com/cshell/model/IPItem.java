package com.cshell.model;

import java.util.regex.Pattern;

public class IPItem {
	private String ip;
	private int mask;
	
	private String split[];
	
	public static final Pattern IPv4_Pattern = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");;
	
	public IPItem(String ip, int mask) {
		this.ip = ip;
		this.mask = mask;
		
		splitIpString();
	}
	
	public String getIp() {
		return ip;
	}
	
	public int getMask() {
		return mask;
	}
	
	public boolean isIpValid() {
		if (split == null) return false;
		return true;
	}
	
	/*
	 * For main routing table, the mask is left-handed
	 */
	public boolean isIpAtPrefixMaskMatch(String ip) {
		if(split == null || ip == null || !IPv4_Pattern.matcher(ip).matches()) return false;
		
		String out1 = getPrefixString(this.ip, mask);
		String out2 = getPrefixString(ip, mask);
		
		return out1.equals(out2);
	}
	
	/*
	 * For secondary routing table, the mask is right-handed
	 */
	public boolean isIpAtSuffixMaskMatch(String ip) {
		if(split == null || ip == null || !IPv4_Pattern.matcher(ip).matches()) return false;
		
		String out1 = getSuffixString(this.ip, mask);
		String out2 = getSuffixString(ip, mask);
		
		return out1.equals(out2);
	}
	
	private String getPrefixString(String ip, int mask) {
		StringBuffer outSb = new StringBuffer();
		
		String[] tSplit = ip.split("\\.");
		int cur = 0, left = mask;
		while (left > 8) { // left must > 8, because of the "."
			outSb.append(tSplit[cur] + ".");
			left -= 8;
			cur++;
		}
		
		int lastVal = new Integer(tSplit[cur]);
		lastVal = lastVal >> (8 - left); // left-handed
		outSb.append(lastVal);
		
		cur++;
		while (cur < 4) {
			outSb.append(".0");
			cur++;
		}
		
		return outSb.toString();
	}
	
	private String getSuffixString(String ip, int mask) {
		StringBuffer outSb = new StringBuffer();
		
		String[] tSplit = ip.split("\\.");
		int cur = 0, left = 32 - mask;
		while (left > 8) {
			outSb.append("0.");
			left -= 8;
			cur++;
		}
		
		int lastVal = new Integer(tSplit[cur]);
		lastVal = lastVal - ((lastVal >> (8 - left)) << (8 - left)); // right-handed
		outSb.append(lastVal);
		
		cur++;
		while (cur < 4) {
			outSb.append("." + tSplit[cur]);
			cur++;
		}
		
		return outSb.toString();
	}
	
	private void splitIpString() {
		if (split != null) split = null;
		if (ip == null || mask <0 || mask > 32) return;
		
		if (IPv4_Pattern.matcher(ip).matches()) {
			split = ip.split("\\.");
		}
	}
	
	// test
	public static void main(String[] args) {
		StringBuffer outSb = new StringBuffer();
		
		String[] split = {"192", "168", "198", "254"};
		int cur = 0, left = 32 - 8;
		while (left > 8) {
			outSb.append(split[cur] + ".");
			left -= 8;
			cur++;
		}
		
		int lastVal = new Integer(split[cur]);
		lastVal = lastVal >> (8 - left); // left-handed
		outSb.append(lastVal);
		
		cur++;
		while (cur < 4) {
			outSb.append(".0");
			cur++;
		}
		
		System.out.println(outSb.toString());
		
		/*
		StringBuffer outSb = new StringBuffer();
		
		String[] split = {"192", "168", "198", "236"};
		int cur = 0, left = 32 - 14;
		while (left > 8) {
			outSb.append("0.");
			left -= 8;
			cur++;
		}
		
		int lastVal = new Integer(split[cur]);
		lastVal = lastVal - ((lastVal >> (8 - left)) << (8 - left)); // right-handed
		outSb.append(lastVal);
		
		cur++;
		while (cur < 4) {
			outSb.append("." + split[cur]);
			cur++;
		}
		
		System.out.println(outSb.toString());
		*/
	}
}
