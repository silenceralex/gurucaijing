package com.caijing.flush;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
		System.out.println("years.size()  : " + years.size());
		for (int i = 0; i < years.size(); i++) {
			System.out.println("analyzer  : " + years.get(i));
			if (i == 0) {
				flushAnalyzerYear(analyzer, years.get(i), years, true);
			} else {
				flushAnalyzerYear(analyzer, years.get(i), years, false);
			}
			flushAnalyzerSuccessYear(analyzer, years.get(i));
			flushAnalyzerStock(analyzer);
		}
		flushAnalyzerSuccessYear(analyzer, null);
	}

	public void flushAnalyzerYear(Analyzer analyzer, String year, List<String> years, boolean isStart) {
		FloatUtil floatUtil = new FloatUtil();
		List<Analyzer> analyzerList = analyzerDao.getStarAnalyzers();
		String endDate = year + "-12-31";
		String startDay = year + "-01-01";
		boolean isCurrentYear = false;
		if (year.equals(DateTools.getYear(new Date()))) {
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
					DateTools.parseYYYYMMDDDate(endDate));

			//			List<StockEarn> priceList = stockEarnDao.getPriceByCodeDate("000300",
			//					DateTools.transformYYYYMMDDDate(startDate));

			List<StockEarn> priceList = stockEarnDao.getPriceByCodePeriod("000300", startDate,
					DateTools.parseYYYYMMDDDate(endDate));

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
			Collections.reverse(years);
			introvmf.put("yearList", years);
			introvmf.put("ratio", ratio);
			introvmf.put("relativeratio", relativeratio);
			introvmf.put("startweight", startweight);
			introvmf.put("analyzerList", analyzerList);
			introvmf.put("weightList", weightList);
			introvmf.put("startprice", startprice);
			introvmf.put("priceList", priceList);
			introvmf.put("isCurrentYear", isCurrentYear);
			String maxYear = years.get(years.size() - 1);
			introvmf.put("maxYear", maxYear);
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

	public void flushAnalyzerStock(Analyzer analyzer) {
		List<GroupStock> stockDetailList = groupStockDao.getNameAndCodeByAid(analyzer.getAid());
		Map<String, List<StockEarn>> stockDetailMap = new HashMap<String, List<StockEarn>>();
		for (GroupStock stock : stockDetailList) {
			List<StockEarn> stockEarnList = stockEarnDao.getPriceByCodeDate(stock.getStockcode(),
					DateTools.transformYYYYMMDDDate(stock.getIntime()));
			//			List<String> filePathList = recommendStockDao.getFilePathByAid(analyzer.getAid(), stock.getStockcode(), 3);
			//			stock.setFilePathList(filePathList);
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

		stockDetailList = groupStockDao.getOutStocksByAid(analyzer.getAid());
		stockvmf.setTemplate("/template/starstock_his.htm");
		stockvmf.put("floatUtil", new FloatUtil());
		stockvmf.put("dateTools", new DateTools());
		stockvmf.put("analyzer", analyzer);
		stockvmf.put("analyzerList", analyzerList);
		stockvmf.put("stockDetailList", stockDetailList);
		stockvmf.put("startprice", startprice);
		stockvmf.put("priceList", priceList);
		stockvmf.put("stockDetailMap", stockDetailMap);
		stockvmf.save(ADMINDIR + "static/" + analyzer.getAid() + "_hisstock.html");
		System.out.println("write page : " + ADMINDIR + analyzer.getAid() + "_hisstock.html");
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
			Collections.reverse(years);
			vmf.put("yearList", years);
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
		System.out.println("analyzerList size:" + analyzerList.size());
		for (Analyzer analyzer : analyzerList) {
			flushAnalyzer(analyzer);

		}
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
		flusher.setAnalyzerSuccessDao(analyzerSuccessDao);
		flusher.setAnalyzerDao(analyzerDao);
		flusher.setGroupEarnDao(groupEarnDao);
		flusher.setStockEarnDao(stockEarnDao);
		flusher.setGroupStockDao(groupStockDao);
		flusher.setRecommendSuccessDao(recommendSuccessDao);
		flusher.setReportDao(reportDao);
		//		flusher.flushHistorySuccessRank("2009");
		//		flusher.flushHistorySuccessRank("2010");
		//		"周小波" " 付娟"  " 董亚光" "卢平" "黄挺" ,罗鶄 赵湘鄂  叶洮 李凡
		Analyzer analyzer = analyzerDao.getAnalyzerByName("赵湘鄂");
		flusher.flushAnalyzer(analyzer);
		//		flusher.flushAnalyzerStock(analyzer);
		//		flusher.flushAnalyzerYear(analyzer, "2009", true);
		//		flusher.flushAnalyzerYear(analyzer, "2010", false);
		//		flusher.flushAnalyzerYear(analyzer, "2011", false);
		//		flusher.flushAllStarGuruDetail();
		System.exit(0);
	}
}
