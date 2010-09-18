package com.caijing.business.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.business.StockGainManager;
import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.StockGain;
import com.caijing.model.StockPrice;
import com.caijing.util.DateTools;

public class StockGainManagerImpl implements StockGainManager {
	private StockPrice sp = new StockPrice();
	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	public List<StockGain> getStockGainByAname(String aname, int page) {
		List<RecommendStock> recommendlist = recommendStockDao
				.getRecommendStocksByAnalyzer(aname, (page - 1) * 20, 20);
		if (recommendlist == null)
			return null;
		List<StockGain> gainlist = new ArrayList<StockGain>(recommendlist
				.size());
		for (RecommendStock rstock : recommendlist) {
			String tmp = rstock.getCreatedate();
			if (tmp == null || tmp.trim().length() < 8)
				continue;
			tmp = tmp.substring(0, 4) + "-" + tmp.substring(4, 6) + "-"
					+ tmp.substring(6, 8);
			StockGain sg = sp.getStockGainByPeriod(rstock.getStockcode(), tmp,
					DateTools.transformYYYYMMDDDate(new Date()));
			sg.setReportid(rstock.getReportid());
			sg.setGrade(rstock.getGrade());
			sg.setSaname(rstock.getSaname());
			sg.setStockname(rstock.getStockname());
			sg.setObjectprice(rstock.getObjectprice());
			gainlist.add(sg);
		}
		return gainlist;
	}

	public RecommendStockDao getRecommendStockDao() {
		return recommendStockDao;
	}

	public void setRecommendStockDao(RecommendStockDao recommendStockDao) {
		this.recommendStockDao = recommendStockDao;
	}
}
