package com.caijing.crawl;

public interface AbsExtractor {
	/**
	 * 根据券商研报获取摘要
	 * @param content 研报内容
	 * @param type   研报类型， 
	 * @param saname   所属券商
	 * @return     摘要内容，限制在255char内。
	 */
	public String extractAbs(String content,int type,String saname);
}
