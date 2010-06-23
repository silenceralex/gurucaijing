package com.caijing.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ���ڼ���md5�Ĺ�����
 * 
 * @author jun-chen
 * 
 */
public class MD5Utils {
	private static MessageDigest md5;

	static {
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	//ͬ��������ֹ�̲߳���ȫ
	public synchronized static String hash(String plain) {
		byte[] hex_digest = new byte[32];
		try {
			md5.update(plain.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			md5.update(plain.getBytes());
		}
		bytesToHex(md5.digest(), hex_digest);
		String digest = new String(hex_digest);
		return digest;
	}

	/**
	 * Turn 16-byte stream into a human-readable 32-byte hex string
	 */
	private static void bytesToHex(byte[] bytes, byte[] hex) {
		final char lookup[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'a', 'b', 'c', 'd', 'e', 'f' };

		int i, c, j, pos = 0;

		for (i = 0; i < 16; i++) {
			c = bytes[i] & 0xFF;
			j = c >> 4;
			hex[pos++] = (byte) lookup[j];
			j = (c & 0xF);
			hex[pos++] = (byte) lookup[j];
		}
	}
}
