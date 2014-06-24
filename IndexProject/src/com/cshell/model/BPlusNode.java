package com.cshell.model;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class BPlusNode {
	
	protected boolean isLeaf;
	
	protected boolean isRoot;

	/** father node */
	protected BPlusNode parent;
	
	/** previous node of a leaf */
	protected BPlusNode previous;
	
	/** next node of a leaf */
	protected BPlusNode next;	
	
	/** the keys for a node */
	protected List<Entry<Comparable, Object>> entries;
	
	/** children node */
	protected List<BPlusNode> children;
	
	public BPlusNode(boolean isLeaf) {
		this.isLeaf = isLeaf;
		entries = new ArrayList<Entry<Comparable, Object>>();
		
		if (!isLeaf) {
			children = new ArrayList<BPlusNode>();
		}
	}

	public BPlusNode(boolean isLeaf, boolean isRoot) {
		this(isLeaf);
		this.isRoot = isRoot;
	}
	
	public Object get(Comparable key) {
		
		// if it is a leaf
		if (isLeaf) {
			for (Entry<Comparable, Object> entry : entries) {
				if (entry.getKey().compareTo(key) == 0) {
					// return the value
					return entry.getValue();
				}
			}
			// not found
			return null;
			
		// if not leaf
		}else {
			// if smaller than the left most
			if (key.compareTo(entries.get(0).getKey()) <= 0) {
				return children.get(0).get(key);
			// if bigger than the right most
			} else if (key.compareTo(entries.get(entries.size()-1).getKey()) >= 0) {
				return children.get(children.size()-1).get(key);
			// others
			} else {
				for (int i = 0; i < entries.size(); i++) {
					if (entries.get(i).getKey().compareTo(key) <= 0 && entries.get(i+1).getKey().compareTo(key) > 0) {
						return children.get(i).get(key);
					}
				}
			}
		}
		
		return null;
	}
	
	public void insertOrUpdate(Comparable key, Object obj, BPlusTree tree){
		// if a leaf
		if (isLeaf){
			// do not need to split
			if (contains(key) || entries.size() < tree.getOrder()){
				insertOrUpdate(key, obj);
				if (parent != null) {
					// update parent
					parent.updateInsert(tree);
				}

			// need to split
			}else {
				// split into two
				BPlusNode left = new BPlusNode(true);
				BPlusNode right = new BPlusNode(true);
				// set the link
				if (previous != null){
					previous.setNext(left);
					left.setPrevious(previous);
				}
				if (next != null) {
					next.setPrevious(right);
					right.setNext(next);
				}
				if (previous == null){
					tree.setHead(left);
				}

				left.setNext(right);
				right.setPrevious(left);
				previous = null;
				next = null;
				
				int leftSize = (tree.getOrder() + 1) / 2 + (tree.getOrder() + 1) % 2; 
				int rightSize = (tree.getOrder() + 1) / 2;
				// copy the new node
				insertOrUpdate(key, obj);
				for (int i = 0; i < leftSize; i++){
					left.getEntries().add(entries.get(i));
				}
				for (int i = 0; i < rightSize; i++){
					right.getEntries().add(entries.get(leftSize + i));
				}
				
				// if not root
				if (parent != null) {
					// adjust
					int index = parent.getChildren().indexOf(this);
					parent.getChildren().remove(this);
					left.setParent(parent);
					right.setParent(parent);
					parent.getChildren().add(index,left);
					parent.getChildren().add(index + 1, right);
					setEntries(null);
					setChildren(null);
					
					parent.updateInsert(tree);
					setParent(null);
				// if root
				}else {
					isRoot = false;
					BPlusNode parent = new BPlusNode(false, true);
					tree.setRoot(parent);
					left.setParent(parent);
					right.setParent(parent);
					parent.getChildren().add(left);
					parent.getChildren().add(right);
					setEntries(null);
					setChildren(null);
					
					parent.updateInsert(tree);
				}
				

			}
			
		// if not leaf
		}else {
			if (key.compareTo(entries.get(0).getKey()) <= 0) {
				children.get(0).insertOrUpdate(key, obj, tree);
			}else if (key.compareTo(entries.get(entries.size()-1).getKey()) >= 0) {
				children.get(children.size()-1).insertOrUpdate(key, obj, tree);
			}else {
				for (int i = 0; i < entries.size(); i++) {
					if (entries.get(i).getKey().compareTo(key) <= 0 && entries.get(i+1).getKey().compareTo(key) > 0) {
						children.get(i).insertOrUpdate(key, obj, tree);
						break;
					}
				}	
			}
		}
	}
	
	/** update after insertion */
	protected void updateInsert(BPlusTree tree){

		validate(this, tree);
		
		// split the node	
		if (children.size() > tree.getOrder()) {
			BPlusNode left = new BPlusNode(false);
			BPlusNode right = new BPlusNode(false);

			int leftSize = (tree.getOrder() + 1) / 2 + (tree.getOrder() + 1) % 2;
			int rightSize = (tree.getOrder() + 1) / 2;
			for (int i = 0; i < leftSize; i++){
				left.getChildren().add(children.get(i));
				left.getEntries().add(new SimpleEntry(children.get(i).getEntries().get(0).getKey(), null));
				children.get(i).setParent(left);
			}
			for (int i = 0; i < rightSize; i++){
				right.getChildren().add(children.get(leftSize + i));
				right.getEntries().add(new SimpleEntry(children.get(leftSize + i).getEntries().get(0).getKey(), null));
				children.get(leftSize + i).setParent(right);
			}
			
			// if not root
			if (parent != null) {
				int index = parent.getChildren().indexOf(this);
				parent.getChildren().remove(this);
				left.setParent(parent);
				right.setParent(parent);
				parent.getChildren().add(index,left);
				parent.getChildren().add(index + 1, right);
				setEntries(null);
				setChildren(null);
				
				parent.updateInsert(tree);
				setParent(null);
			// if root
			}else {
				isRoot = false;
				BPlusNode parent = new BPlusNode(false, true);
				tree.setRoot(parent);
				left.setParent(parent);
				right.setParent(parent);
				parent.getChildren().add(left);
				parent.getChildren().add(right);
				setEntries(null);
				setChildren(null);
				
				parent.updateInsert(tree);
			}
		}
	}
	
	/** adjust the node */
	protected static void validate(BPlusNode node, BPlusTree tree) {
		
		if (node.getEntries().size() == node.getChildren().size()) {
			for (int i = 0; i < node.getEntries().size(); i++) {
				Comparable key = node.getChildren().get(i).getEntries().get(0).getKey();
				if (node.getEntries().get(i).getKey().compareTo(key) != 0) {
					node.getEntries().remove(i);
					node.getEntries().add(i, new SimpleEntry(key, null));
					if(!node.isRoot()){
						validate(node.getParent(), tree);
					}
				}
			}
			// > M/2 && <M && >2
		} else if (node.isRoot() && node.getChildren().size() >= 2 
				||node.getChildren().size() >= tree.getOrder() / 2 
				&& node.getChildren().size() <= tree.getOrder()
				&& node.getChildren().size() >= 2) {
			node.getEntries().clear();
			for (int i = 0; i < node.getChildren().size(); i++) {
				Comparable key = node.getChildren().get(i).getEntries().get(0).getKey();
				node.getEntries().add(new SimpleEntry(key, null));
				if (!node.isRoot()) {
					validate(node.getParent(), tree);
				}
			}
		}
	}
	
	/** update after remove */
	protected void updateRemove(BPlusTree tree) {
		
		validate(this, tree);

		// merge
		if (children.size() < tree.getOrder() / 2 || children.size() < 2) {
			if (isRoot) {
				if (children.size() >= 2) {
					return;
				} else {
					BPlusNode root = children.get(0);
					tree.setRoot(root);
					root.setParent(null);
					root.setRoot(true);
					setEntries(null);
					setChildren(null);
				}
			} else {
				int currIdx = parent.getChildren().indexOf(this);
				int prevIdx = currIdx - 1;
				int nextIdx = currIdx + 1;
				BPlusNode previous = null, next = null;
				if (prevIdx >= 0) {
					previous = parent.getChildren().get(prevIdx);
				}
				if (nextIdx < parent.getChildren().size()) {
					next = parent.getChildren().get(nextIdx);
				}
				
				if (previous != null 
						&& previous.getChildren().size() > tree.getOrder() / 2
						&& previous.getChildren().size() > 2) {
					int idx = previous.getChildren().size() - 1;
					BPlusNode borrow = previous.getChildren().get(idx);
					previous.getChildren().remove(idx);
					borrow.setParent(this);
					children.add(0, borrow);
					validate(previous, tree);
					validate(this, tree);
					parent.updateRemove(tree);
					
				} else if (next != null	
						&& next.getChildren().size() > tree.getOrder() / 2
						&& next.getChildren().size() > 2) {
					BPlusNode borrow = next.getChildren().get(0);
					next.getChildren().remove(0);
					borrow.setParent(this);
					children.add(borrow);
					validate(next, tree);
					validate(this, tree);
					parent.updateRemove(tree);
					
				} else {
					if (previous != null 
							&& (previous.getChildren().size() <= tree.getOrder() / 2 || previous.getChildren().size() <= 2)) {
						
						for (int i = previous.getChildren().size() - 1; i >= 0; i--) {
							BPlusNode child = previous.getChildren().get(i);
							children.add(0, child);
							child.setParent(this);
						}
						previous.setChildren(null);
						previous.setEntries(null);
						previous.setParent(null);
						parent.getChildren().remove(previous);
						validate(this, tree);
						parent.updateRemove(tree);
						
					} else if (next != null	
							&& (next.getChildren().size() <= tree.getOrder() / 2 || next.getChildren().size() <= 2)) {

						for (int i = 0; i < next.getChildren().size(); i++) {
							BPlusNode child = next.getChildren().get(i);
							children.add(child);
							child.setParent(this);
						}
						next.setChildren(null);
						next.setEntries(null);
						next.setParent(null);
						parent.getChildren().remove(next);
						validate(this, tree);
						parent.updateRemove(tree);
					}
				}
			}
		}
	}
	
	public void remove(Comparable key, BPlusTree tree){
		if (isLeaf){
			
			if (!contains(key)){
				return;
			}
			
			if (isRoot) {
				remove(key);
			}else {
				if (entries.size() > tree.getOrder() / 2 && entries.size() > 2) {
					remove(key);
				}else {
					if (previous != null 
							&& previous.getEntries().size() > tree.getOrder() / 2
							&& previous.getEntries().size() > 2
							&& previous.getParent() == parent) {
						int size = previous.getEntries().size();
						Entry<Comparable, Object> entry = previous.getEntries().get(size - 1);
						previous.getEntries().remove(entry);
						entries.add(0, entry);
						remove(key);
					}else if (next != null 
							&& next.getEntries().size() > tree.getOrder() / 2
							&& next.getEntries().size() > 2
							&& next.getParent() == parent) {
						Entry<Comparable, Object> entry = next.getEntries().get(0);
						next.getEntries().remove(entry);
						entries.add(entry);
						remove(key);
					}else {
						if (previous != null 
								&& (previous.getEntries().size() <= tree.getOrder() / 2 || previous.getEntries().size() <= 2)
								&& previous.getParent() == parent) {
							for (int i = previous.getEntries().size() - 1; i >=0; i--) {
								entries.add(0, previous.getEntries().get(i));
							}
							remove(key);
							previous.setParent(null);
							previous.setEntries(null);
							parent.getChildren().remove(previous);
							if (previous.getPrevious() != null) {
								BPlusNode temp = previous;
								temp.getPrevious().setNext(this);
								previous = temp.getPrevious();
								temp.setPrevious(null);
								temp.setNext(null);							
							}else {
								tree.setHead(this);
								previous.setNext(null);
								previous = null;
							}
						}else if(next != null 
								&& (next.getEntries().size() <= tree.getOrder() / 2 || next.getEntries().size() <= 2)
								&& next.getParent() == parent){
							for (int i = 0; i < next.getEntries().size(); i++) {
								entries.add(next.getEntries().get(i));
							}
							remove(key);
							next.setParent(null);
							next.setEntries(null);
							parent.getChildren().remove(next);
							if (next.getNext() != null) {
								BPlusNode temp = next;
								temp.getNext().setPrevious(this);
								next = temp.getNext();
								temp.setPrevious(null);
								temp.setNext(null);
							}else {
								next.setPrevious(null);
								next = null;
							}
						}
					}
				}
				parent.updateRemove(tree);
			}
		}else {
			if (key.compareTo(entries.get(0).getKey()) <= 0) {
				children.get(0).remove(key, tree);
			}else if (key.compareTo(entries.get(entries.size()-1).getKey()) >= 0) {
				children.get(children.size()-1).remove(key, tree);
			}else {
				for (int i = 0; i < entries.size(); i++) {
					if (entries.get(i).getKey().compareTo(key) <= 0 && entries.get(i+1).getKey().compareTo(key) > 0) {
						children.get(i).remove(key, tree);
						break;
					}
				}	
			}
		}
	}
	

	protected boolean contains(Comparable key) {
		for (Entry<Comparable, Object> entry : entries) {
			if (entry.getKey().compareTo(key) == 0) {
				return true;
			}
		}
		return false;
	}
	

	protected void insertOrUpdate(Comparable key, Object obj){
		Entry<Comparable, Object> entry = new SimpleEntry<Comparable, Object>(key, obj);
		if (entries.size() == 0) {
			entries.add(entry);
			return;
		}
		
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).getKey().compareTo(key) == 0) {
				entries.get(i).setValue(obj);
				return;
			}else if (entries.get(i).getKey().compareTo(key) > 0){
				if (i == 0) {
					entries.add(0, entry);
					return;
				}else {
					entries.add(i, entry);
					return;
				}
			}
		}
		entries.add(entries.size(), entry);
	}
	
	protected void remove(Comparable key){
		int index = -1;
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).getKey().compareTo(key) == 0) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			entries.remove(index);
		}
	}
	
	public BPlusNode getPrevious() {
		return previous;
	}

	public void setPrevious(BPlusNode previous) {
		this.previous = previous;
	}

	public BPlusNode getNext() {
		return next;
	}

	public void setNext(BPlusNode next) {
		this.next = next;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public BPlusNode getParent() {
		return parent;
	}

	public void setParent(BPlusNode parent) {
		this.parent = parent;
	}

	public List<Entry<Comparable, Object>> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry<Comparable, Object>> entries) {
		this.entries = entries;
	}

	public List<BPlusNode> getChildren() {
		return children;
	}

	public void setChildren(List<BPlusNode> children) {
		this.children = children;
	}
	
	public boolean isRoot() {
		return isRoot;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("isRoot: ");
		sb.append(isRoot);
		sb.append(", ");
		sb.append("isLeaf: ");
		sb.append(isLeaf);
		sb.append(", ");
		sb.append("keys: ");
		for (Entry entry : entries){
			sb.append(entry.getKey());
			sb.append(", ");
		}
		sb.append(", ");
		return sb.toString();
		
	}

}
