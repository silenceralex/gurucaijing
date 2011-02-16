package com.caijing.business;

import com.caijing.domain.Analyzer;

public interface AnalyzerManager {

	public void fetchAnalyzersFromSA(String saname);

	/**
	 * ����һ��ȯ�̵ķ���ʦ�Ƽ��б���recommendStock����recommendSuccess
	 * @param saname
	 */
	public void handleHistoryRecommendBySA(String saname);

	/**
	 * ����������ʦ��recommendStock����recommendSuccess
	 * @param analyzer
	 */
	public void handleHistoryRecommendByAnalyzer(Analyzer analyzer);

}
