package com.caijing.spider;

import java.util.Map;

/**
 * ��Ϊ��һְ��Ľӿ�Extractorֻ�����ҳ������ȡ�������У�ͨ�������ƴ������ȡ����Ƶ�ļ���������ַ
 * 
 * @author jun-chen
 * 
 */
public interface Extractor {

	/**
	 * ͨ��ҳ���ȡ����Ƶ������������Ƶurl�����ݣ������ص�
	 * 
	 * @param properties
	 *            ҳ���ȡ������key-value��
	 * @return ��������video_url
	 */
	public String extractVideoUrl(Map<String, String> properties);

	public void setDownEncode(String charset);

}
