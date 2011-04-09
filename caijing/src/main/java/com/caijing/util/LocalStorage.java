package com.caijing.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.business.GroupGainManager;
import com.caijing.business.RecommendSuccessManager;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.GroupEarn;
import com.caijing.domain.GroupStock;
import com.caijing.domain.Stock;
import com.caijing.domain.StockEarn;
import com.caijing.flush.AnalyzerFlusher;
import com.caijing.flush.HtmlFlusher;
import com.caijing.flush.MasterFlusher;
import com.caijing.model.StockReloader;

public class LocalStorage {

	@Autowired
	@Qualifier("groupGain")
	private GroupGain groupGain = null;

	@Autowired
	@Qualifier("stockEarnDao")
	private StockEarnDao stockEarnDao = null;

	@Autowired
	@Qualifier("groupStockDao")
	private GroupStockDao groupStockDao = null;

	@Autowired
	@Qualifier("stockReloader")
	private StockReloader stockReloader = null;

	private RecommendSuccessManager recommendSuccessManager = null;

	private GroupGainManager groupGainManager = null;

	public GroupGainManager getGroupGainManager() {
		return groupGainManager;
	}

	public void setGroupGainManager(GroupGainManager groupGainManager) {
		this.groupGainManager = groupGainManager;
	}

	public RecommendSuccessManager getRecommendSuccessManager() {
		return recommendSuccessManager;
	}

	public void setRecommendSuccessManager(RecommendSuccessManager recommendSuccessManager) {
		this.recommendSuccessManager = recommendSuccessManager;
	}

	public StockReloader getStockReloader() {
		return stockReloader;
	}

	public void setStockReloader(StockReloader stockReloader) {
		this.stockReloader = stockReloader;
	}

	private HtmlFlusher htmlFlush = null;

	private MasterFlusher masterFlush = null;

	public MasterFlusher getMasterFlush() {
		return masterFlush;
	}

	public void setMasterFlush(MasterFlusher masterFlush) {
		this.masterFlush = masterFlush;
	}

	private AnalyzerFlusher analyzerFlusher = null;

	public AnalyzerFlusher getAnalyzerFlusher() {
		return analyzerFlusher;
	}

	public void setAnalyzerFlusher(AnalyzerFlusher analyzerFlusher) {
		this.analyzerFlusher = analyzerFlusher;
	}

	public HtmlFlusher getHtmlFlush() {
		return htmlFlush;
	}

	public void setHtmlFlush(HtmlFlusher htmlFlush) {
		this.htmlFlush = htmlFlush;
	}

	public void localStore() {
		stockReloader.reload();
		List<Stock> lists = stockReloader.getStockDao().getAllStock();

		if (!groupGain.getSp().isWorkDay()) {
			return;
		}
		//		List<RecommendStock> lists = groupGain.getRecommendStockDao().getRecommendStocksGroupByCode();
		for (int i = 0; i < lists.size(); i++) {
			System.out.println("Current process :" + i + " stockcode: " + lists.get(i).getStockcode());
			groupGain.getSp().currentPrice(lists.get(i).getStockcode());
		}
		groupGain.getSp().currentPrice("000300");

		//TODO groupstock表到期的处理，recommmendsuccess验证处理
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, -1);
		Date recommenddate = cal.getTime();
		groupGainManager.processGroupStockOutDate(recommenddate);
		//
		cal.add(Calendar.MONTH, 6);
		Date successvalidate = cal.getTime();
		//TODO recommmendsuccess验证处理，分析师成功率的更新
		recommendSuccessManager.processValidatedRecommendSuccess(successvalidate);

		List<Analyzer> analyzerlist = groupGain.getAnalyzerDao().getStarAnalyzers();
		Date date = groupGain.getGroupEarnDao().getLatestDate();
		for (Analyzer analyzer : analyzerlist) {
			storeAnaylzerGain(analyzer, date);
		}

		//抓取完毕直接进行刷新动作
		//		htmlFlush.flushStarGuruDetail();
		analyzerFlusher.flushAllStarGuruDetail();
		analyzerFlusher.flushAnalyzerRank();
		//		htmlFlush.flushAnalyzerRank();
		htmlFlush.flushReportLab();
		htmlFlush.flushStockResearch();
		htmlFlush.flushStockAgency();
		//		htmlFlush.flushLiveStatic();
		//		htmlFlush.flushMasterInfo();
		masterFlush.flushLiveStatic();
		masterFlush.flushArchive();
		htmlFlush.flushSuccessRank();
		htmlFlush.flushStarOnSale(true, 4);
		htmlFlush.flushStarOnSale(true, 3);
		htmlFlush.flushStarOnSale(true, 2);
		htmlFlush.flushStarOnSale(true, 1);
		htmlFlush.flushStarOnSale(false, 4);
		htmlFlush.flushStarOnSale(false, 3);
		htmlFlush.flushStarOnSale(false, 2);
		htmlFlush.flushStarOnSale(false, 1);
		//		htmlFlush.flushStarOnSale(true);
		//		htmlFlush.flushStarOnSale(false);
	}

	private void storeAnaylzerGain(Analyzer analyzer, Date date) {
		try {
			List<GroupStock> stocks = groupGain.getGroupStockDao().getCurrentStockByGroupid(analyzer.getAid());
			float ratios = 0;
			for (GroupStock stock : stocks) {
				StockEarn se = stockEarnDao.getStockEarnByCodeDate(stock.getStockcode(),
						DateTools.transformYYYYMMDDDate(new Date()));
				if (se != null) {
					ratios += se.getRatio();
				}
				System.out.println("Stock : " + stock.getStockcode() + " ratio:" + se.getRatio());
				stock.setCurrentprice(se.getPrice());
				float gain = (1 + stock.getGain() / 100) * (1 + se.getRatio() / 100);
				stock.setGain(FloatUtil.getTwoDecimal((gain - 1) * 100));
				stock.setLtime(new Date());
				groupStockDao.updateStockGain(stock);
			}
			GroupEarn tmp = groupGain.getGroupEarnDao().getGroupEarnByIDAndDate(analyzer.getAid(),
					DateTools.transformYYYYMMDDDate(date));

			GroupEarn ge = new GroupEarn();
			ge.setGroupid(analyzer.getAid());
			ge.setDate(new Date());
			System.out.println("ratios : " + ratios + "  stocks.size():" + stocks.size());
			ratios = ratios / stocks.size();
			ge.setRatio(FloatUtil.getTwoDecimal(ratios));
			ge.setWeight(FloatUtil.getTwoDecimal(tmp.getWeight() * (1 + ratios / 100)));
			groupGain.getGroupEarnDao().insert(ge);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void storeGroupStockGain() {
		List<GroupStock> stocks = groupStockDao.getAllGroupStock();
		for (GroupStock stock : stocks) {
			List<StockEarn> stockEarns = stockEarnDao.getRatiosByCodeFromDate(stock.getStockcode(),
					DateTools.transformYYYYMMDDDate(stock.getIntime()));
			float gain = 1;
			for (int i = 0; i < stockEarns.size(); i++) {
				gain = (1 + stockEarns.get(i).getRatio() / 100) * gain;
			}
			stock.setCurrentprice(stockEarns.get(stockEarns.size() - 1).getPrice());
			stock.setGain(FloatUtil.getTwoDecimal((gain - 1) * 100));
			stock.setLtime(stockEarns.get(stockEarns.size() - 1).getDate());
			groupStockDao.updateStockGain(stock);
		}
	}

	public void processHistoryGroupEarn(String aid) {
		Date startDate = null;
		try {
			startDate = groupStockDao.getEarliestIntimeByAidFrom(aid, DateTools.parseYYYYMMDDDate("2010-01-01"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<GroupStock> stocks = groupStockDao.getCurrentStockByGroupid(aid);
		HashMap<String, HashMap<Date, Float>> stockdateMap = new HashMap<String, HashMap<Date, Float>>();
		for (GroupStock stock : stocks) {
			List<StockEarn> ses = stockEarnDao.getPriceByCodeDate(stock.getStockcode(),
					DateTools.transformYYYYMMDDDate(stock.getIntime()));
			HashMap<Date, Float> map = new HashMap<Date, Float>();
			for (StockEarn se : ses) {
				map.put(se.getDate(), se.getRatio());
			}
			stockdateMap.put(stock.getStockcode(), map);
		}
		List<Date> dates = stockEarnDao.getDatesByZSFrom(startDate, new Date());
		float weight = 100;
		for (Date date : dates) {
			GroupEarn ge = new GroupEarn();
			float ratio = 0;
			int count = 0;
			for (String key : stockdateMap.keySet()) {
				if (stockdateMap.get(key).get(date) != null) {
					ratio += stockdateMap.get(key).get(date);
					count++;
				}
			}
			if (count != 0) {
				ratio = ratio / (count * 100);
			}
			System.out.println("ratio at date :" + DateTools.transformYYYYMMDDDate(date) + "  is :" + ratio);
			weight = weight * (1 + ratio);
			ratio = ratio * 100;
			ge.setDate(date);
			ge.setGroupid(aid);
			ge.setRatio(FloatUtil.getTwoDecimal(ratio));
			ge.setWeight(weight);
			groupGain.getGroupEarnDao().insert(ge);
		}

	}

	public static void main(String[] args) {
		//		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");

		//		List<RecommendStock> lists = recommendStockDao.getRecommendStocksGroupByCode();
		//		StockPrice sp = (StockPrice) ContextFactory.getBean("stockPrice");
		//		for (int i = 0; i < lists.size(); i++) {
		//			System.out.println("Current process :" + i);
		//			RecommendStock rs = lists.get(i);
		//			//			sp.currentPrice(rs.getStockcode());
		//			sp.storeStockPrice(rs.getStockcode(), 0, "2010-10-26", DateTools.transformYYYYMMDDDate(new Date()));
		//		}
		//		System.out.println("lists.size() :" + lists.size());
		//		sp.storeStockPrice("000300", 1, "2010-03-22", DateTools.transformYYYYMMDDDate(new Date()));

		//		sp.storeStockPrice("600298", 0, "2010-07-23", "2010-07-24");
		//		sp.currentPrice("000300");
		//		GroupStockDao groupStockDao = (GroupStockDao) ContextFactory.getBean("groupStockDao");
		//		StockEarnDao stockEarnDao = (StockEarnDao) ContextFactory.getBean("stockEarnDao");
		//		GroupEarnDao groupEarnDao = (GroupEarnDao) ContextFactory.getBean("groupEarnDao");
		//		AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
		//		List<Analyzer> analyzerlist = analyzerDao.getAllAnalyzers();
		//		for (Analyzer analyzer : analyzerlist) {
		//			List<GroupStock> stocks = groupStockDao.getCurrentStockByGroupid(analyzer.getAid());
		//			float ratios = 0;
		//			if (stocks == null || stocks.size() == 0) {
		//				System.out.println("analyzer : " + analyzer.getName() + " have No GroupStock");
		//				continue;
		//			}
		//			try {
		//				for (GroupStock stock : stocks) {
		//					System.out.println("Stock : " + stock.getStockcode());
		//					StockEarn se = stockEarnDao.getStockEarnByCodeDate(stock.getStockcode(), DateTools
		//							.parseYYYYMMDDDate("2010-11-01"));
		//					if (se != null) {
		//						ratios += se.getRatio();
		//					}
		//				}
		//				GroupEarn tmp = groupEarnDao.getGroupEarnByIDAndDate( analyzer.getAid(), DateTools
		//						.parseYYYYMMDDDate("2010-10-29"));
		//				GroupEarn ge = new GroupEarn();
		//				ge.setGroupid( analyzer.getAid());
		//				ge.setDate(DateTools.parseYYYYMMDDDate("2010-11-01"));
		//				ratios = ratios / stocks.size();
		//				System.out.println("Gourp : " + analyzer.getName() + "   gain: " + ratios);
		//				ge.setRatio(FloatUtil.getTwoDecimal(ratios));
		//				ge.setWeight(FloatUtil.getTwoDecimal(tmp.getWeight() * (1 + ratios / 100)));
		//				groupEarnDao.insert(ge);
		//			} catch (ParseException e) {
		//				e.printStackTrace();
		//			}
		//		}
		//		GroupGain gg = (GroupGain) ContextFactory.getBean("groupGain");
		//		List<StockGain> sgs = gg.getStockGainManager().getStockGainByAnameASC("詹凌燕");
		//		for (StockGain sg : sgs) {
		//			System.out.println("Stock : " + sg.getStockcode() + "  " + sg.getStockname() + "  in group date:"
		//					+ sg.getStartdate());
		//		}
		//		gg.processASCStore("詹凌燕");
		//		Date yesterday = DateTools.getYesterday(new Date());
		//
		//		System.out.println("Yesterday: " + yesterday);
		//		GroupEarn ge = groupEarnDao.getGroupEarnByIDAndDate("A6EJV66CI", yesterday);
		//		System.out.println("group earn at : " + yesterday + "   ratio:" + ge.getRatio());
		//		AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
		//				GroupGain gg = (GroupGain) ContextFactory.getBean("groupGain");
		//				List<Analyzer> analyzerlist = analyzerDao.getAllAnalyzers();
		//				for (Analyzer analyzer : analyzerlist) {
		//					gg.processASCStore(analyzer.getName());
		//				}
		//		List<Analyzer> analyzerlist = gg.getAnalyzerDao().getAllAnalyzers();
		//		for (Analyzer analyzer : analyzerlist) {
		//			System.out.println("analyzer.getAid() :"  + analyzer.getAid());
		//			GroupEarn tmp = groupEarnDao.getGroupEarnByIDAndDate( analyzer.getAid(), DateTools
		//					.getYesterday(new Date()));
		//			if (tmp == null) {
		//				System.out.println("analyzer.getAid() :" + analyzer.getAid() + "  tmp is null!");
		//			}
		//		}

		LocalStorage storage = (LocalStorage) ContextFactory.getBean("localStorage");
		//		Calendar cal = Calendar.getInstance();
		//		cal.setTime(new Date());
		//		cal.add(Calendar.YEAR, -1);
		//		Date recommenddate = cal.getTime();
		//		storage.getGroupGainManager().processGroupStockOutDate(recommenddate);
		//		cal.add(Calendar.MONTH, -6);
		//		Date successvalidate = cal.getTime();
		//		storage.getRecommendSuccessManager().processValidatedRecommendSuccess(successvalidate);
		//		storage.processHistoryGroupEarn("");
		storage.localStore();
		//		storage.storeGroupStockGain();
		System.exit(0);
	}

	public GroupGain getGroupGain() {
		return groupGain;
	}

	public void setGroupGain(GroupGain groupGain) {
		this.groupGain = groupGain;
	}

	public StockEarnDao getStockEarnDao() {
		return stockEarnDao;
	}

	public void setStockEarnDao(StockEarnDao stockEarnDao) {
		this.stockEarnDao = stockEarnDao;
	}

	public GroupStockDao getGroupStockDao() {
		return groupStockDao;
	}

	public void setGroupStockDao(GroupStockDao groupStockDao) {
		this.groupStockDao = groupStockDao;
	}
}
