package com.caijing.util;

import java.util.Date;

import com.caijing.model.StockPrice;

public class LocalStorage {

	public static void main(String[] args) {
		//		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
		//
		//		List<RecommendStock> lists = recommendStockDao.getRecommendStocksGroupByCode();
		StockPrice sp = (StockPrice) ContextFactory.getBean("stockPrice");
		//		for (int i = 10; i < lists.size(); i++) {
		//			RecommendStock rs = lists.get(i);
		//			sp.storeStockPrice(rs.getStockcode(), DateTools.transformYYYYMMDDDate(rs.getCreatedate()), DateTools
		//					.transformYYYYMMDDDate(new Date()));
		//		}
		sp.storeStockPrice("000300", 1, "2010-03-19", DateTools.transformYYYYMMDDDate(new Date()));
	}
}
