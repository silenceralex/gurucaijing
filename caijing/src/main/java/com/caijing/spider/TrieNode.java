package com.caijing.spider;

/**
 * trie树的节点的数据结构.
 * @author jun-chen
 *
 */
public class TrieNode {
	private boolean value = false;

	private int type = -1;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return this.value;
	}

	private final int CHILD = 256;
	private TrieNode[] child = new TrieNode[CHILD];

	public void addChild(int index, TrieNode tn) {
		this.child[index] = tn;
	}

	public TrieNode getChildAt(int index) {
		if (index > CHILD - 1 || index < 0) {
			return null;
		}
		return this.child[index];
	}
}
