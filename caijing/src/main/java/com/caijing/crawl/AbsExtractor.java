package com.caijing.crawl;

public interface AbsExtractor {
	/**
	 * ����ȯ���б���ȡժҪ
	 * @param content �б�����
	 * @param type   �б����ͣ� 
	 * @param saname   ����ȯ��
	 * @return     ժҪ���ݣ�������255char�ڡ�
	 */
	public String extractAbs(String content,int type,String saname);
}
