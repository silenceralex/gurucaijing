package com.caijing.util;

import java.util.List;

import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.ibatis.AnalyzerDaoImpl;
import com.caijing.domain.Analyzer;

public class AnalyzerFetcher {

	public static void main(String[] args) {
		AnalyzerDao analyzerDao = (AnalyzerDaoImpl) ContextFactory
				.getBean("analyzerDao");
		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory
				.getBean("recommendStockDao");
		List<Analyzer> analyzers = analyzerDao.getAllAnalyzers();
		int i=0;
		for (Analyzer analyzer : analyzers) {
			recommendStockDao.updateAnalyzer(analyzer.getName(), analyzer
					.getAid());
			i++;
		}
	}
}
