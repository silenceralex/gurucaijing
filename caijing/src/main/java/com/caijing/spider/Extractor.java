package com.caijing.spider;

import java.util.Map;

/**
 * 作为单一职责的接口Extractor只负责从页面能爬取的属性中，通过规则和拼接来抽取出视频文件的真正地址
 * 
 * @author jun-chen
 * 
 */
public interface Extractor {

	/**
	 * 通过页面获取的视频属性来解析视频url的内容，核心重点
	 * 
	 * @param properties
	 *            页面获取的属性key-value对
	 * @return 解析出的video_url
	 */
	public String extractVideoUrl(Map<String, String> properties);

	public void setDownEncode(String charset);

}
