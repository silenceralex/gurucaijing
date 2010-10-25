package com.caijing.util;

import java.util.List;

import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.RecommendStock;
import com.caijing.model.StockPrice;

public class LocalStorage {

	public static void main(String[] args) {
		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");

		List<RecommendStock> lists = recommendStockDao.getRecommendStocksGroupByCode();
		StockPrice sp = (StockPrice) ContextFactory.getBean("stockPrice");
		//		for (int i = 0; i < lists.size(); i++) {
		//			System.out.println("Current process :" + i);
		//			RecommendStock rs = lists.get(i);
		//			sp.currentPrice(rs.getStockcode());
		//			//			sp.storeStockPrice(rs.getStockcode(), 0, "2010-10-20", DateTools.transformYYYYMMDDDate(new Date()));
		//		}
		//		System.out.println("lists.size() :" + lists.size());
		//		sp.storeStockPrice("000300", 1, "2010-03-22", DateTools.transformYYYYMMDDDate(new Date()));
		sp.currentPrice("000300");
		//		AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
		//		GroupGain gg = (GroupGain) ContextFactory.getBean("groupGain");
		//		List<Analyzer> analyzerlist = analyzerDao.getAllAnalyzers();
		//		for (Analyzer analyzer : analyzerlist) {
		//			gg.processASCStore(analyzer.getName());
		//		}
	}
}
