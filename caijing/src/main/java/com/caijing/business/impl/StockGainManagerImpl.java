package com.caijing.business.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.business.StockGainManager;
import com.caijing.cache.MethodCache;
import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.StockGain;
import com.caijing.model.StockPrice;
import com.caijing.util.DateTools;

public class StockGainManagerImpl implements StockGainManager {

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	@Autowired
	@Qualifier("stockPrice")
	private StockPrice sp = null;

	static String[] buys = { "买入", "推荐", "强烈推荐", "长期推荐", "增持", "维持推荐", "买 入 维持", "上调至推荐", "增 持 维持", "买入维持", "买入首次" };

	static String[] sells = { "中性", "维持审慎推荐", "审慎推荐", "减持", "中 性 调低" };

	static HashSet<String> buyset = new HashSet<String>();
	static HashSet<String> sellset = new HashSet<String>();
	static {
		for (String buy : buys) {
			buyset.add(buy);
		}
		for (String sell : sells) {
			sellset.add(sell);
		}
	}

	@MethodCache(expire = 3600 * 4)
	public List<StockGain> getStockGainByAname(String aname, int page) {
		List<RecommendStock> recommendlist = recommendStockDao.getRecommendStocksByAnalyzer(aname, (page - 1) * 20, 20);
		if (recommendlist == null)
			return null;
		List<StockGain> gainlist = new ArrayList<StockGain>(recommendlist.size());
		for (RecommendStock rstock : recommendlist) {
			//			String tmp = rstock.getCreatedate();
			//			if (tmp == null || tmp.trim().length() < 8)
			//				continue;
			//			tmp = tmp.substring(0, 4) + "-" + tmp.substring(4, 6) + "-" + tmp.substring(6, 8);
			try {
				StockGain sg = sp.getStockGainByPeriod(rstock.getStockcode(), DateTools.transformYYYYMMDDDate(rstock
						.getCreatedate()), DateTools.transformYYYYMMDDDate(new Date()));
				sg.setReportid(rstock.getReportid());
				sg.setGrade(rstock.getGrade());
				sg.setSaname(rstock.getSaname());
				sg.setStockname(rstock.getStockname());
				sg.setObjectprice(rstock.getObjectprice());
				gainlist.add(sg);
			} catch (Exception e) {
				System.out.println("Exception : " + e.getMessage());
				e.printStackTrace();
			}
		}
		return gainlist;
	}

	public RecommendStockDao getRecommendStockDao() {
		return recommendStockDao;
	}

	public void setRecommendStockDao(RecommendStockDao recommendStockDao) {
		this.recommendStockDao = recommendStockDao;
	}

	public StockGain getStockGainByRStock(String stockcode, String start, String end) {
		try {
			StockGain sg = sp.getStockGainByPeriod(stockcode, start, end);
			return sg;
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public StockGain getZSGainByPeriod(String start, String end) {
		StockGain sg = sp.getZSGainByPeriod(start, end);
		return sg;
	}

	public List<StockGain> getStockGainByAnameASC(String aname) {
		HashSet<String> stockdateMap = new HashSet<String>();
		int count = recommendStockDao.getRecommendStockCountsByAnalyzer(aname);
		List<RecommendStock> recommendlist = recommendStockDao.getRecommendStocksByAnalyzerASC(aname, 0, count);
		if (recommendlist == null)
			return null;
		List<StockGain> gainlist = new ArrayList<StockGain>(recommendlist.size());
		for (RecommendStock rstock : recommendlist) {
			System.out.println("rstock:" + rstock.getCreatedate() + "  Stockcode:" + rstock.getStockcode()
					+ "  Stockname:" + rstock.getStockname());
			if (buyset.contains(rstock.getGrade())) {
				//防止多次推荐同一只股票
				if (!stockdateMap.contains(rstock.getStockcode())) {
					stockdateMap.add(rstock.getStockcode());
				} else {
					//					System.out.println("rstock:" + rstock.getCreatedate() + "  Stockname:" + rstock.getStockname());
					System.out.println("StockGainManager sg.getStockcode() is already in the map!");
					continue;
				}
				try {
					StockGain sg = sp
							.getStockGainByPeriod(rstock.getStockcode(), DateTools.transformYYYYMMDDDate(rstock
									.getCreatedate()), DateTools.transformYYYYMMDDDate(new Date()));
					sg.setReportid(rstock.getReportid());
					sg.setGrade(rstock.getGrade());
					sg.setSaname(rstock.getSaname());
					sg.setStockname(rstock.getStockname());
					sg.setObjectprice(rstock.getObjectprice());
					gainlist.add(sg);
				} catch (Exception e) {
					System.out.println("Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return gainlist;
	}

	public StockPrice getSp() {
		return sp;
	}

	public void setSp(StockPrice sp) {
		this.sp = sp;
	}

}
