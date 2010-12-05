package com.caijing.spider;

import org.springframework.stereotype.Component;

/**
 * 用于suggest的前缀查询的trie树结构.
 * @author jun-chen
 *
 */
@Component("trie")
public class Trie {
	private TrieNode root = new TrieNode();
	private static final int MAX = 10;

	private TrieNode add(byte[] strBytes, int index, TrieNode node) {
		if (index >= strBytes.length) {
			return node;
		}
		int indexInNode = strBytes[index] & 0xFF;
		TrieNode trieNode = node.getChildAt(indexInNode);
		if (trieNode == null) {
			trieNode = new TrieNode();
			node.addChild(indexInNode, trieNode);
		}
		return add(strBytes, ++index, trieNode);
	}

	private boolean search(byte[] strBytes, int index, TrieNode node) {
		if (index == strBytes.length) {
			return false;
		}
		int indexInNode = strBytes[index] & 0xFF;
		node = node.getChildAt(indexInNode);
		if (node == null) {
			return search(strBytes, ++index, root);
		} else if (node.getValue()) {
			return true;
		}
		return search(strBytes, ++index, node);
	}

	private TrieNode searchNode(byte[] strBytes, int index, TrieNode node) {
		if (index == strBytes.length) {
			return null;
		}
		int indexInNode = strBytes[index] & 0xFF;
		node = node.getChildAt(indexInNode);
		if (node == null) {
			return searchNode(strBytes, ++index, root);
		} else if (node.getValue()) {
			return node;
		}
		return searchNode(strBytes, ++index, node);
	}

	public TrieNode searchNode(String tag) {
		return searchNode(tag.getBytes(), 0, root);
	}

	private boolean search(String tag) {
		return search(tag.getBytes(), 0, root);
	}

	public void add(String tagName, int type) {
		TrieNode node = add(tagName.getBytes(), 0, root);
		node.setValue(true);
		node.setType(type);
		//		node.setValue(tag);
	}

	public static void main(String[] args) {
		Trie trie = new Trie();
		trie.add("增持", 2);
		trie.add("减持", 3);
		trie.add("资产注入", 1);
		trie.add("资产重组", 1);
		trie.add("并购", 1);
		trie.add("股权激励", 0);

		String str = "自2010年1月14日至12月2日收盘，武汉长江通信产业集团股份有限公司第二大股东-武汉高科国有控股集团有限公司共计从二级市场减持公司无限售条件流通股198万股，占公司总股本的1%。截止2010年12月2日收盘，武汉高科共计从二级市场减持公司无限售条件流通股1596.2万股，占公司总股本的8.06%。　　本次减持后，武汉高科共持有公司无限售条件流通股3350.8万股，占公司总股本的16.92%";
		//		str = "天津天药药业股份有限公司于2010年12月3日召开2010年第二次临时股东大会，会议审议通过关于变更公司独立董事及选举监事的议案。";
		str = "刊登股东减持公告";
		if (trie.search(str)) {
			TrieNode node = trie.searchNode(str);
			System.out.println("match:" + node.getType());
		}
	}
}
