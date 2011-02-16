package com.caijing.business;

import com.caijing.domain.Analyzer;

public interface AnalyzerManager {

	public void fetchAnalyzersFromSA(String saname);

	/**
	 * 处理一个券商的分析师推荐研报从recommendStock插入recommendSuccess
	 * @param saname
	 */
	public void handleHistoryRecommendBySA(String saname);

	/**
	 * 处理单个分析师从recommendStock插入recommendSuccess
	 * @param analyzer
	 */
	public void handleHistoryRecommendByAnalyzer(Analyzer analyzer);

}
