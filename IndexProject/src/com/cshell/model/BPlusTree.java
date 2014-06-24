package com.cshell.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

interface IBPlusTree {
	   public Object get(Comparable key);
	   public void remove(Comparable key);
	   public void insertOrUpdate(Comparable key, Object obj);
}


public class BPlusTree implements IBPlusTree {
	
	protected BPlusNode root;
	
	/** order，value of M */
	protected int order;
	
	/** head for leaves */
	protected BPlusNode head;
	
	public BPlusNode getHead() {
		return head;
	}

	public void setHead(BPlusNode head) {
		this.head = head;
	}

	public BPlusNode getRoot() {
		return root;
	}

	public void setRoot(BPlusNode root) {
		this.root = root;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public Object get(Comparable key) {
		return root.get(key);
	}

	@Override
	public void remove(Comparable key) {
		root.remove(key, this);
	}

	@Override
	public void insertOrUpdate(Comparable key, Object obj) {
		root.insertOrUpdate(key, obj, this);

	}
	
	public BPlusTree(int order){
		if (order < 3) {
			System.out.print("order must be greater than 2");
			System.exit(0);
		}
		this.order = order;
		root = new BPlusNode(true, true);
		head = root;
	}
	
	// test
	public static void main(String[] args) {
		BPlusTree tree = new BPlusTree(8);
		Random random = new Random();
		long current = System.currentTimeMillis();
		for (int j = 0; j < 1000; j++) {
			for (int i = 0; i < 10; i++) {
				int randomNumber = random.nextInt(10000);
				tree.insertOrUpdate(randomNumber, randomNumber);
			}
/*
			for (int i = 0; i < 10; i++) {
				int randomNumber = random.nextInt(1000);
				tree.remove(randomNumber);
			}
*/
		}

		long duration = System.currentTimeMillis() - current;
		System.out.println("time elpsed for duration: " + duration);
		
		for (int j = 0; j < 1000; j++) {
			int randomNumber = random.nextInt(10000);
			System.out.println(tree.get(randomNumber));
		}
		
		long searchTime = System.currentTimeMillis() - current - duration;
		System.out.println("time elpsed for searching: " + searchTime);
		//int search = 80;
		//System.out.print(tree.get(search));
	}

}
