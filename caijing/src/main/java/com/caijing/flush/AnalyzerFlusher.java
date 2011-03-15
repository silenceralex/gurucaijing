package com.caijing.flush;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.AnalyzerSuccessDao;
import com.caijing.dao.GroupEarnDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.RecommendSuccessDao;
import com.caijing.dao.ReportDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.AnalyzerSuccess;
import com.caijing.domain.GroupEarn;
import com.caijing.domain.GroupStock;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.RecommendSuccess;
import com.caijing.domain.Report;
import com.caijing.domain.StockEarn;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.caijing.util.FloatUtil;

public class AnalyzerFlusher {
	public static String ADMINDIR = "/home/html/analyzer/";

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	@Autowired
	@Qualifier("analyzerSuccessDao")
	private AnalyzerSuccessDao analyzerSuccessDao = null;

	private RecommendSuccessDao recommendSuccessDao = null;

	private ReportDao reportDao = null;

	public ReportDao getReportDao() {
		return reportDao;
	}

	public void setReportDao(ReportDao reportDao) {
		this.reportDao = reportDao;
	}

	public static String PREFIX = "http://51gurus.com";

	public RecommendSuccessDao getRecommendSuccessDao() {
		return recommendSuccessDao;
	}

	public void setRecommendSuccessDao(RecommendSuccessDao recommendSuccessDao) {
		this.recommendSuccessDao = recommendSuccessDao;
	}

	@Autowired
	@Qualifier("stockEarnDao")
	private StockEarnDao stockEarnDao = null;

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

	public GroupEarnDao getGroupEarnDao() {
		return groupEarnDao;
	}

	public void setGroupEarnDao(GroupEarnDao groupEarnDao) {
		this.groupEarnDao = groupEarnDao;
	}

	public AnalyzerDao getAnalyzerDao() {
		return analyzerDao;
	}

	public void setAnalyzerDao(AnalyzerDao analyzerDao) {
		this.analyzerDao = analyzerDao;
	}

	@Autowired
	@Qualifier("groupStockDao")
	private GroupStockDao groupStockDao = null;

	@Autowired
	@Qualifier("groupEarnDao")
	private GroupEarnDao groupEarnDao = null;

	private AnalyzerDao analyzerDao = null;

	public RecommendStockDao getRecommendStockDao() {
		return recommendStockDao;
	}

	public void setRecommendStockDao(RecommendStockDao recommendStockDao) {
		this.recommendStockDao = recommendStockDao;
	}

	public AnalyzerSuccessDao getAnalyzerSuccessDao() {
		return analyzerSuccessDao;
	}

	public void setAnalyzerSuccessDao(AnalyzerSuccessDao analyzerSuccessDao) {
		this.analyzerSuccessDao = analyzerSuccessDao;
	}

	public void flushHistorySuccessRank(String year) {
		DateTools dateTools = new DateTools();
		FloatUtil floatUtil = new FloatUtil();
		//		recommendSuccessDao.getRecommendSuccessCountByAidDuring(aid, startDate, endDate)
		List<AnalyzerSuccess> anlyzers = analyzerSuccessDao.getAnalyzerRankByYear(year, 0, 40);
		for (int current = 1; current <= 2; current++) {
			List<AnalyzerSuccess> analyzerList = anlyzers.subList((current - 1) * 20, current * 20);
			try {
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/anayzerHistorySucRank.htm");
				vmf.put("dateTools", dateTools);
				vmf.put("floatUtil", floatUtil);
				vmf.put("year", year);
				vmf.put("start", (current - 1) * 20);
				vmf.put("current", current);
				vmf.put("currdate", new Date());
				vmf.put("page", 2);
				vmf.put("analyzerList", analyzerList);
				vmf.save(ADMINDIR + year + "/successhisrank_" + current + ".html");
				System.out.println("write page : " + ADMINDIR + year + "/successhisrank_" + current + ".html");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating discount stock html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void flushAnalyzer(Analyzer analyzer) {
		List<String> years = analyzerSuccessDao.getYearList(analyzer.getAid());
		System.out.println("years.size()  : " + years.size() + " aid:" + analyzer.getAid());
		for (int i = 0; i < years.size(); i++) {
			System.out.println("analyzer  success: " + years.get(i));
			flushAnalyzerSuccessYear(analyzer, years.get(i));
		}
		Date startDate = null;
		try {
			startDate = groupStockDao.getEarliestIntimeByAidFrom(analyzer.getAid(),
					DateTools.parseYYYYMMDDDate("2008-01-01"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int startYear = Integer.parseInt(DateTools.getYear(startDate));
		int currYear = Integer.parseInt(DateTools.getYear(new Date()));
		//分析师当前组合为空的状况
		if (groupStockDao.getCurrentStockCountByGroupid(analyzer.getAid()) < 1) {
			Date outtime = groupStockDao.getNearestOutTimeByGroupid(analyzer.getAid());
			currYear = Integer.parseInt(DateTools.getYear(outtime));
		}

		List<String> groupYears = new ArrayList<String>();
		for (int i = startYear; i <= currYear; i++) {
			groupYears.add("" + i);
		}
		System.out.println("startYear  : " + startYear + " groupYears size: " + groupYears.size());
		for (int i = 0; i < groupYears.size(); i++) {
			System.out.println("analyzer  group: " + groupYears.get(i));
			if (i == 0) {
				flushAnalyzerYear(analyzer, groupYears.get(0), groupYears, true);
			} else {
				flushAnalyzerYear(analyzer, groupYears.get(i), groupYears, false);
			}
		}
		//组合股票池
		flushCurrentAnalyzerStock(analyzer);
		flushHisAnalyzerStock(analyzer);
		//总体成功率
		flushAnalyzerSuccessYear(analyzer, null);
		flushReport(analyzer);

	}

	public void flushReport(Analyzer analyzer) {
		//生成分析师report页面
		List<RecommendStock> stockList = recommendStockDao.getRecommendStocksByAnalyzer(analyzer.getName(), 0, 15);
		List<Analyzer> analyzerList = analyzerDao.getStarAnalyzers();
		try {
			VMFactory reportvmf = new VMFactory();
			reportvmf.setTemplate("/template/starreport.htm");
			reportvmf.put("floatUtil", new FloatUtil());
			reportvmf.put("dateTools", new DateTools());
			reportvmf.put("analyzer", analyzer);
			reportvmf.put("analyzerList", analyzerList);
			reportvmf.put("stockList", stockList);
			reportvmf.save(ADMINDIR + "static/" + analyzer.getAid() + "_report.html");
			System.out.println("write page : " + ADMINDIR + analyzer.getAid() + "_report.html");
		} catch (Exception e) {
			System.out.println("===> exception !! ：" + analyzer.getAid() + "  name : " + analyzer.getName());
			System.out.println("While generating stars stock html --> GET ERROR MESSAGE: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void flushAnalyzerYear(Analyzer analyzer, String year, List<String> years, boolean isStart) {
		FloatUtil floatUtil = new FloatUtil();
		List<Analyzer> analyzerList = analyzerDao.getStarAnalyzers();
		String endDay = year + "-12-31";
		String startDay = year + "-01-01";
		String maxYear = years.get(years.size() - 1);
		boolean isCurrentYear = false;
		if (year.equals(maxYear)) {
			isCurrentYear = true;
		}
		try {
			//生成分析师intro页面
			String aid = analyzer.getAid();
			Date startDate = null;
			float startprice = 0;
			float startweight = 100;
			try {
				if (isStart) {
					startDate = groupStockDao.getEarliestIntimeByAidFrom(aid, DateTools.parseYYYYMMDDDate(startDay));
					//如果intime在周末则后移300的起始price
					startprice = stockEarnDao.getNearPriceByCodeDate("000300", startDate).getPrice();
					startweight = 100;
				} else {
					startDate = DateTools.parseYYYYMMDDDate(startDay);
					startprice = stockEarnDao.getFormerNearPriceByCodeDate("000300", startDate).getPrice();
					startweight = groupEarnDao.getFormerNearPriceByCodeDate(aid, startDate).getWeight();
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (startDate == null) {
				System.out.println("analyzer  : " + analyzer.getName() + "  " + aid + " is null");
				return;
			}
			//			List<GroupEarn> weightList = groupEarnDao.getWeightList(aid, startDate);
			List<GroupEarn> weightList = groupEarnDao.getWeightListBetween(aid, startDate,
					DateTools.parseYYYYMMDDDate(endDay));

			//为
			Date endDate = weightList.get(weightList.size() - 1).getDate();
			List<StockEarn> priceList = stockEarnDao.getPriceByCodePeriod("000300", startDate, endDate);

			//			float start = weightList.get(0).getWeight();
			float end = weightList.get(weightList.size() - 1).getWeight();
			float ratio = FloatUtil.getTwoDecimal((end - startweight) * 100 / startweight);

			float ratio300 = (priceList.get(priceList.size() - 1).getPrice() - startprice) * 100 / startprice;
			float relativeratio = FloatUtil.getTwoDecimal(ratio - ratio300);
			VMFactory introvmf = new VMFactory();
			introvmf.setTemplate("/template/starintro_y.htm");
			introvmf.put("floatUtil", floatUtil);
			introvmf.put("dateTools", new DateTools());
			introvmf.put("analyzer", analyzer);
			introvmf.put("year", year);

			introvmf.put("maxYear", maxYear);
			System.out.println("maxYear  : " + maxYear);
			ArrayList<String> yearList = new ArrayList<String>();
			yearList.addAll(years);
			Collections.reverse(yearList);
			introvmf.put("yearList", yearList);
			introvmf.put("ratio", ratio);
			introvmf.put("relativeratio", relativeratio);
			introvmf.put("startweight", startweight);
			introvmf.put("analyzerList", analyzerList);
			introvmf.put("weightList", weightList);
			introvmf.put("startprice", startprice);
			introvmf.put("priceList", priceList);
			introvmf.put("isCurrentYear", isCurrentYear);

			System.out.println("isCurrentYear :" + isCurrentYear);
			System.out.println("weightList size :" + weightList.size());
			System.out.println("priceList size :" + priceList.size());
			//当前年度则直接刷出url，非当前年度刷出带year的intro
			if (isCurrentYear) {
				introvmf.save(ADMINDIR + "static/" + aid + "_intro.html");
				System.out.println("write current year page : " + ADMINDIR + aid + "_intro.html");
			} else {
				introvmf.save(ADMINDIR + "static/" + aid + "_" + year + "_intro.html");
				System.out.println("write page : " + ADMINDIR + aid + "_" + year + "_intro.html");
			}
		} catch (Exception e) {
			System.out.println("===> exception !! ：" + analyzer.getAid() + "  name : " + analyzer.getName());
			System.out.println("While generating stars stock html --> GET ERROR MESSAGE: " + e.getMessage());
			e.printStackTrace();
		}
	}

	//	public void flushAnalyzerStock(Analyzer analyzer, boolean isHis) {
	//		List<GroupStock> stockDetailList = null;
	//
	//		if (isHis) {
	//			stockDetailList = groupStockDao.getOutStocksByAid(analyzer.getAid());
	//			if (stockDetailList == null || stockDetailList.size() == 0) {
	//				System.out.println("No history stock! aid :" + analyzer.getAid() + "  name : " + analyzer.getName());
	//				return;
	//			}
	//		} else {
	//			stockDetailList = groupStockDao.getNameAndCodeByAid(analyzer.getAid());
	//			if (stockDetailList == null || stockDetailList.size() == 0) {
	//				System.out.println("No current stock! aid :" + analyzer.getAid() + "  name : " + analyzer.getName());
	//				return;
	//			}
	//		}
	//
	//		Map<String, List<StockEarn>> stockDetailMap = new HashMap<String, List<StockEarn>>();
	//		for (GroupStock stock : stockDetailList) {
	//			List<StockEarn> stockEarnList = stockEarnDao.getPriceByCodeDate(stock.getStockcode(),
	//					DateTools.transformYYYYMMDDDate(stock.getIntime()));
	//			for (int i = 0; i < stockEarnList.size(); i++) {
	//				StockEarn stockEarn = stockEarnList.get(i);
	//				float currratio = 0;
	//				if (i == 0) {
	//					currratio = stockEarn.getRatio() / 100;
	//				} else {
	//					currratio = (1 + stockEarnList.get(i - 1).getCurrratio()) * (1 + stockEarn.getRatio() / 100) - 1;
	//				}
	//				stockEarn.setCurrratio(currratio);
	//			}
	//			stockDetailMap.put(stock.getStockcode(), stockEarnList);
	//		}
	//
	//		Date startDate = groupStockDao.getCurrentEarliestIntimeByAid(analyzer.getAid());
	//
	//		float startprice = stockEarnDao.getNearPriceByCodeDate("000300", startDate).getPrice();
	//		List<StockEarn> priceList = stockEarnDao.getPriceByCodeDate("000300",
	//				DateTools.transformYYYYMMDDDate(startDate));
	//		VMFactory stockvmf = new VMFactory();
	//		stockvmf.put("floatUtil", new FloatUtil());
	//		stockvmf.put("dateTools", new DateTools());
	//		stockvmf.put("analyzer", analyzer);
	//		List<Analyzer> analyzerList = analyzerDao.getStarAnalyzers();
	//		stockvmf.put("analyzerList", analyzerList);
	//		stockvmf.put("stockDetailList", stockDetailList);
	//		stockvmf.put("startprice", startprice);
	//		stockvmf.put("priceList", priceList);
	//		stockvmf.put("stockDetailMap", stockDetailMap);
	//
	//		if (isHis) {
	//			stockvmf.setTemplate("/template/starstock.htm");
	//			stockvmf.save(ADMINDIR + "static/" + analyzer.getAid() + "_stock.html");
	//			System.out.println("write page : " + ADMINDIR + analyzer.getAid() + "_stock.html");
	//		} else {
	//			stockvmf.setTemplate("/template/starstock_his.htm");
	//			stockvmf.save(ADMINDIR + "static/" + analyzer.getAid() + "_hisstock.html");
	//			System.out.println("write page : " + ADMINDIR + analyzer.getAid() + "_hisstock.html");
	//		}
	//
	//	}

	public void flushHisAnalyzerStock(Analyzer analyzer) {
		List<GroupStock> stockDetailList = groupStockDao.getOutStocksByAid(analyzer.getAid());
		if (stockDetailList == null || stockDetailList.size() == 0) {
			System.out.println("No history stock! aid :" + analyzer.getAid() + "  name : " + analyzer.getName());
			return;
		}
		VMFactory stockvmf = new VMFactory();
		stockvmf.put("floatUtil", new FloatUtil());
		stockvmf.put("dateTools", new DateTools());
		stockvmf.put("analyzer", analyzer);
		List<Analyzer> analyzerList = analyzerDao.getStarAnalyzers();
		stockvmf.put("analyzerList", analyzerList);
		stockvmf.put("stockDetailList", stockDetailList);
		stockvmf.setTemplate("/template/starstock_his.htm");
		stockvmf.save(ADMINDIR + "static/" + analyzer.getAid() + "_hisstock.html");
		System.out.println("write page : " + ADMINDIR + analyzer.getAid() + "_hisstock.html");
	}

	public void flushCurrentAnalyzerStock(Analyzer analyzer) {
		if (groupStockDao.getCurrentStockCountByGroupid(analyzer.getAid()) < 1) {
			System.out.println("No current stock! aid :" + analyzer.getAid() + "  name : " + analyzer.getName());
			return;
		}
		List<GroupStock> stockDetailList = groupStockDao.getNameAndCodeByAid(analyzer.getAid());
		Map<String, List<StockEarn>> stockDetailMap = new HashMap<String, List<StockEarn>>();
		for (GroupStock stock : stockDetailList) {
			List<StockEarn> stockEarnList = stockEarnDao.getPriceByCodeDate(stock.getStockcode(),
					DateTools.transformYYYYMMDDDate(stock.getIntime()));
			for (int i = 0; i < stockEarnList.size(); i++) {
				StockEarn stockEarn = stockEarnList.get(i);
				float currratio = 0;
				if (i == 0) {
					currratio = stockEarn.getRatio() / 100;
				} else {
					currratio = (1 + stockEarnList.get(i - 1).getCurrratio()) * (1 + stockEarn.getRatio() / 100) - 1;
				}
				stockEarn.setCurrratio(currratio);
			}
			stockDetailMap.put(stock.getStockcode(), stockEarnList);
		}

		Date startDate = groupStockDao.getCurrentEarliestIntimeByAid(analyzer.getAid());
		float startprice = stockEarnDao.getNearPriceByCodeDate("000300", startDate).getPrice();
		List<StockEarn> priceList = stockEarnDao.getPriceByCodeDate("000300",
				DateTools.transformYYYYMMDDDate(startDate));
		VMFactory stockvmf = new VMFactory();
		stockvmf.setTemplate("/template/starstock.htm");
		stockvmf.put("floatUtil", new FloatUtil());
		stockvmf.put("dateTools", new DateTools());
		stockvmf.put("analyzer", analyzer);
		List<Analyzer> analyzerList = analyzerDao.getStarAnalyzers();
		stockvmf.put("analyzerList", analyzerList);
		stockvmf.put("stockDetailList", stockDetailList);
		stockvmf.put("startprice", startprice);
		stockvmf.put("priceList", priceList);
		stockvmf.put("stockDetailMap", stockDetailMap);
		stockvmf.save(ADMINDIR + "static/" + analyzer.getAid() + "_stock.html");
		System.out.println("write page : " + ADMINDIR + analyzer.getAid() + "_stock.html");
	}

	public void flushAnalyzerSuccessYear(Analyzer analyzer, String year) {
		List<String> years = analyzerSuccessDao.getYearList(analyzer.getAid());
		FloatUtil floatUtil = new FloatUtil();
		VMFactory vmf = new VMFactory();
		vmf.setTemplate("/template/starsuc.htm");
		List<RecommendSuccess> recommends = null;
		String ratio = "";
		if (year != null) {
			String endDate = year + "-12-31";
			String startDate = year + "-01-01";
			AnalyzerSuccess analyzerSuccess = analyzerSuccessDao.getOneAnalyzerSuccess(analyzer.getAid(), year);
			ratio = floatUtil.getTwoDecimalNumber(analyzerSuccess.getSuccessratio()) + "%";
			recommends = recommendSuccessDao.getRecommendsByAidBetween(analyzer.getAid(), startDate, endDate);
			System.out.println(" year : " + year + " recommends: " + recommends.size());
			vmf.put("year", year);
		} else {
			ratio = floatUtil.getTwoDecimalNumber(analyzer.getSuccessratio()) + "%";
			recommends = recommendSuccessDao.getRecommendsByAid(analyzer.getAid());
			System.out.println(" recommends: " + recommends.size());
		}
		try {
			System.out.println("write page : " + analyzer.getAid());
			//			List<RecommendSuccess> recommends = recommendSuccessDao.getRecommendsByAid(analyzer.getAid());
			for (RecommendSuccess recommend : recommends) {
				System.out.println("write page Reportid: " + recommend.getReportid());
				Report report = (Report) reportDao.select(recommend.getReportid());
				String url = PREFIX + report.getFilepath();
				recommend.setReporturl(url);
			}
			vmf.put("dateTools", new DateTools());
			vmf.put("floatUtil", floatUtil);
			vmf.put("analyzer", analyzer);
			vmf.put("currdate", new Date());
			ArrayList<String> yearList = new ArrayList<String>();
			yearList.addAll(years);
			Collections.reverse(yearList);
			vmf.put("yearList", yearList);
			vmf.put("aname", analyzer.getName());
			vmf.put("ratio", ratio);
			vmf.put("recommends", recommends);
			if (year != null) {
				vmf.save(ADMINDIR + "static/" + analyzer.getAid() + "_" + year + "_success.html");
				System.out.println("write page : " + ADMINDIR + "static/" + analyzer.getAid() + "_" + year
						+ "_success.html");
			} else {
				vmf.save(ADMINDIR + "static/" + analyzer.getAid() + "_success.html");
				System.out.println("write page : " + ADMINDIR + "static/" + analyzer.getAid() + "_success.html");
			}
		} catch (Exception e) {
			System.out.println("===> exception !!");
			System.out.println("While generating discount stock html --> GET ERROR MESSAGE: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void flushAllStarGuruDetail() {
		List<Analyzer> analyzerList = analyzerDao.getStarAnalyzers();
		HashSet<String> idSet = new HashSet<String>();
		System.out.println("analyzerList size:" + analyzerList.size());
		for (Analyzer analyzer : analyzerList) {
			flushAnalyzer(analyzer);
			if (!idSet.contains(analyzer.getAid())) {
				idSet.add(analyzer.getAid());
			}
		}
		//刷新哪些有成功率的分析师
		analyzerList = analyzerDao.getSuccessRankedAnalyzers();
		for (Analyzer analyzer : analyzerList) {
			if (!idSet.contains(analyzer.getAid())) {
				flushAnalyzer(analyzer);
				idSet.add(analyzer.getAid());
			}
		}
	}

	public void flushAnalyzerPeriodRank(String year, List<String> groupYears) throws ParseException {
		Date endDate = DateTools.parseYYYYMMDDDate(year + "-12-31");
		Date startDate = DateTools.parseYYYYMMDDDate(year + "-01-01");
		Map<String, List<GroupEarn>> groupEarnMap = new HashMap<String, List<GroupEarn>>();
		Map<String, Float> startWeightMap = new HashMap<String, Float>();
		Map<String, List<StockEarn>> stockEarnMap = new HashMap<String, List<StockEarn>>();
		Map<String, Float> startPriceMap = new HashMap<String, Float>();
		Map<String, String> startDateMap = new HashMap<String, String>();
		List<Analyzer> analyzerList = analyzerDao.getStarAnalyzers();
		for (int i = 0; i < analyzerList.size(); i++) {
			//包括起始当日
			List<GroupEarn> weightList = groupEarnDao.getWeightListBetween(analyzerList.get(i).getAid(), startDate,
					endDate);

			GroupEarn ge = groupEarnDao.getFormerNearPriceByCodeDate(analyzerList.get(i).getAid(), startDate);
			if (weightList == null || weightList.size() == 0) {
				continue;
			}
			//初始年份
			float startWeight = 100;
			if (ge != null) {
				startWeight = ge.getWeight();
				float startprice = stockEarnDao.getFormerNearPriceByCodeDate("000300", startDate).getPrice();
				startPriceMap.put(analyzerList.get(i).getAid(), startprice);
				List<StockEarn> priceList = stockEarnDao.getRatiosByCodeInPeriod("000300", startDate, endDate);
				stockEarnMap.put(analyzerList.get(i).getAid(), priceList);
				//				System.out.println("aid: " + analyzerList.get(i).getAid() + "  startprice size:" + startprice
				//						+ "  priceList :" + priceList.get(0).getPrice());
			} else {
				float startprice = stockEarnDao.getFormerNearPriceByCodeDate("000300", weightList.get(0).getDate())
						.getPrice();
				startPriceMap.put(analyzerList.get(i).getAid(), startprice);
				List<StockEarn> priceList = stockEarnDao.getRatiosByCodeInPeriod("000300", weightList.get(0).getDate(),
						endDate);
				stockEarnMap.put(analyzerList.get(i).getAid(), priceList);
				//				System.out.println("aid: " + analyzerList.get(i).getAid() + "  startprice size:" + startprice
				//						+ "  priceList :" + priceList.get(0).getPrice());
			}
			startDateMap
					.put(analyzerList.get(i).getAid(), DateTools.transformYYYYMMDDDate(weightList.get(0).getDate()));
			startWeightMap.put(analyzerList.get(i).getAid(), startWeight);
			float endWeight = weightList.get(weightList.size() - 1).getWeight();
			//利用weight计算区间收益并排序
			analyzerList.get(i).setWeight((endWeight - startWeight) * 100 / startWeight);
			groupEarnMap.put(analyzerList.get(i).getAid(), weightList);
		}
		Collections.sort(analyzerList);
		ArrayList<String> yearList = new ArrayList<String>();
		yearList.addAll(groupYears);
		Collections.reverse(yearList);
		//		List<StockEarn> priceList = stockEarnDao.getRatiosByCodeInPeriod("000300", startDate, endDate);
		for (int current = 1; current <= 2; current++) {
			try {
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/analyzerrank.htm");
				vmf.put("year", year);
				vmf.put("groupYears", yearList);
				vmf.put("dateTools", new DateTools());
				vmf.put("floatUtil", new FloatUtil());
				vmf.put("currDate", DateTools.transformYYYYMMDDDate(endDate));
				vmf.put("start", (current - 1) * 20);
				vmf.put("current", current);
				vmf.put("page", 2);
				vmf.put("analyzerList", analyzerList.subList((current - 1) * 20, current * 20));
				vmf.put("startDateMap", startDateMap);
				vmf.put("groupEarnMap", groupEarnMap);
				vmf.put("stockEarnMap", stockEarnMap);
				vmf.put("startPriceMap", startPriceMap);
				vmf.put("startWeightMap", startWeightMap);

				vmf.save(ADMINDIR + year + "/rank_" + current + ".html");
				System.out.println("write page : " + ADMINDIR + year + "/rank_" + current + ".html");

			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating discount stock html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void flushAnalyzerRankCountByMonth(int month, List<String> groupYears) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, month);
		System.out.println("Start date" + DateTools.transformYYYYMMDDDate(cal.getTime()));
		//		HashMap<String, GroupEarn> analyzerStartWeights = new HashMap<String, GroupEarn>();
		Map<String, List<GroupEarn>> groupEarnMap = new HashMap<String, List<GroupEarn>>();
		Map<String, Float> startWeightMap = new HashMap<String, Float>();
		List<Analyzer> analyzerList = analyzerDao.getStarAnalyzers();
		for (int i = 0; i < analyzerList.size(); i++) {
			//包括起始当日
			List<GroupEarn> weightList = groupEarnDao.getWeightListBetween(analyzerList.get(i).getAid(), cal.getTime(),
					date);
			GroupEarn ge = groupEarnDao.getFormerNearPriceByCodeDate(analyzerList.get(i).getAid(), cal.getTime());
			if (ge == null) {
				continue;
			}
			float startWeight = ge.getWeight();
			startWeightMap.put(analyzerList.get(i).getAid(), startWeight);
			float endWeight = weightList.get(weightList.size() - 1).getWeight();
			//利用weight计算区间收益并排序
			analyzerList.get(i).setWeight((endWeight - startWeight) * 100 / startWeight);
			groupEarnMap.put(analyzerList.get(i).getAid(), weightList);
		}
		Collections.sort(analyzerList);
		ArrayList<String> yearList = new ArrayList<String>();
		yearList.addAll(groupYears);
		Collections.reverse(yearList);
		List<StockEarn> priceList = stockEarnDao.getRatiosByCodeInPeriod("000300", cal.getTime(), date);
		float startPrice = stockEarnDao.getFormerNearPriceByCodeDate("000300", cal.getTime()).getPrice();
		for (int current = 1; current <= 2; current++) {
			try {
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/analyzerrank_time.htm");
				vmf.put("year", DateTools.getYear(new Date()));
				vmf.put("groupYears", yearList);
				vmf.put("dateTools", new DateTools());
				vmf.put("floatUtil", new FloatUtil());
				vmf.put("currDate", DateTools.transformYYYYMMDDDate(date));
				vmf.put("start", (current - 1) * 20);
				vmf.put("current", current);
				vmf.put("page", 2);
				vmf.put("analyzerList", analyzerList.subList((current - 1) * 20, current * 20));
				vmf.put("startDate", DateTools.transformYYYYMMDDDate(cal.getTime()));
				vmf.put("groupEarnMap", groupEarnMap);
				vmf.put("priceList", priceList);
				vmf.put("startPrice", startPrice);
				vmf.put("startWeightMap", startWeightMap);
				if (month == -1) {
					vmf.put("type", 1);
					vmf.save(ADMINDIR + "monthrank_" + current + ".html");
					System.out.println("write page : " + ADMINDIR + "monthrank_" + current + ".html");
				} else if (month == -3) {
					vmf.put("type", 2);
					vmf.save(ADMINDIR + "quarterrank_" + current + ".html");
					System.out.println("write page : " + ADMINDIR + "quarterrank_" + current + ".html");
				} else if (month == -6) {
					vmf.put("type", 3);
					vmf.save(ADMINDIR + "halfyearrank_" + current + ".html");
					System.out.println("write page : " + ADMINDIR + "halfyearrank__" + current + ".html");
				}
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating discount stock html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void flushAnalyzerRank() {
		int startYear = 2009;
		int currYear = Integer.parseInt(DateTools.getYear(new Date()));
		List<String> groupYears = new ArrayList<String>();
		for (int i = startYear; i <= currYear; i++) {
			groupYears.add("" + i);
		}
		System.out.println("startYear  : " + startYear + " groupYears size: " + groupYears.size());
		for (int i = 0; i < groupYears.size(); i++) {
			try {
				flushAnalyzerPeriodRank(groupYears.get(i), groupYears);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		flushAnalyzerRankCountByMonth(-1, groupYears);
		flushAnalyzerRankCountByMonth(-3, groupYears);
		flushAnalyzerRankCountByMonth(-6, groupYears);
	}

	public static void main(String[] args) {
		AnalyzerFlusher flusher = new AnalyzerFlusher();
		AnalyzerSuccessDao analyzerSuccessDao = (AnalyzerSuccessDao) ContextFactory.getBean("analyzerSuccessDao");
		GroupStockDao groupStockDao = (GroupStockDao) ContextFactory.getBean("groupStockDao");
		GroupEarnDao groupEarnDao = (GroupEarnDao) ContextFactory.getBean("groupEarnDao");
		StockEarnDao stockEarnDao = (StockEarnDao) ContextFactory.getBean("stockEarnDao");
		AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
		RecommendSuccessDao recommendSuccessDao = (RecommendSuccessDao) ContextFactory.getBean("recommendSuccessDao");
		ReportDao reportDao = (ReportDao) ContextFactory.getBean("reportDao");
		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
		flusher.setAnalyzerSuccessDao(analyzerSuccessDao);
		flusher.setAnalyzerDao(analyzerDao);
		flusher.setGroupEarnDao(groupEarnDao);
		flusher.setStockEarnDao(stockEarnDao);
		flusher.setGroupStockDao(groupStockDao);
		flusher.setRecommendSuccessDao(recommendSuccessDao);
		flusher.setReportDao(reportDao);
		flusher.setRecommendStockDao(recommendStockDao);
		//		flusher.flushHistorySuccessRank("2009");
		//		flusher.flushHistorySuccessRank("2010");
		//		"周小波" " 付娟"  " 董亚光" "卢平" "黄挺" ,罗鶄 赵湘鄂  叶洮 李凡衡昆 
		//				Analyzer analyzer = analyzerDao.getAnalyzerByName("苏惠");
		//		Analyzer analyzer = (Analyzer) analyzerDao.select("6EJV66CI");
		//		flusher.flushReport(analyzer);
		//		flusher.flushAnalyzer(analyzer);
		//		flusher.flushAnalyzerStock(analyzer);
		//				flusher.flushAnalyzerYear(analyzer, "2009", true);
		//				flusher.flushAnalyzerYear(analyzer, "2010", false);
		//				flusher.flushAnalyzerYear(analyzer, "2011", false);
		//		System.out.println(groupStockDao.getCurrentStockCountByGroupid("6O3M6IMM"));
		//		Date outtime = groupStockDao.getNearestOutTimeByGroupid("6O3M6IMM");
		//		System.out.println("outtime:" + DateTools.transformYYYYMMDDDate(outtime));
		flusher.flushAllStarGuruDetail();

		//		flusher.flushAnalyzerRankCountByMonth(-1);
		//		flusher.flushAnalyzerRankCountByMonth(-3);
		//		flusher.flushAnalyzerRankCountByMonth(-6);

		flusher.flushAnalyzerRank();

		System.exit(0);
	}
}
