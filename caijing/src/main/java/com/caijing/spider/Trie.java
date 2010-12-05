package com.caijing.spider;

import org.springframework.stereotype.Component;

/**
 * ����suggest��ǰ׺��ѯ��trie���ṹ.
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
		trie.add("����", 2);
		trie.add("����", 3);
		trie.add("�ʲ�ע��", 1);
		trie.add("�ʲ�����", 1);
		trie.add("����", 1);
		trie.add("��Ȩ����", 0);

		String str = "��2010��1��14����12��2�����̣��人����ͨ�Ų�ҵ���Źɷ����޹�˾�ڶ���ɶ�-�人�߿ƹ��пعɼ������޹�˾���ƴӶ����г����ֹ�˾������������ͨ��198��ɣ�ռ��˾�ܹɱ���1%����ֹ2010��12��2�����̣��人�߿ƹ��ƴӶ����г����ֹ�˾������������ͨ��1596.2��ɣ�ռ��˾�ܹɱ���8.06%���������μ��ֺ��人�߿ƹ����й�˾������������ͨ��3350.8��ɣ�ռ��˾�ܹɱ���16.92%";
		//		str = "�����ҩҩҵ�ɷ����޹�˾��2010��12��3���ٿ�2010��ڶ�����ʱ�ɶ���ᣬ��������ͨ�����ڱ����˾�������¼�ѡ�ټ��µ��鰸��";
		str = "���ǹɶ����ֹ���";
		if (trie.search(str)) {
			TrieNode node = trie.searchNode(str);
			System.out.println("match:" + node.getType());
		}
	}
}
