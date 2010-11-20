package com.caijing.flush;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.GroupEarnDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.ReportDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.DiscountStock;
import com.caijing.domain.GroupEarn;
import com.caijing.domain.GroupStock;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;
import com.caijing.domain.StockEarn;
import com.caijing.model.StockPrice;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.caijing.util.Discount;
import com.caijing.util.FloatUtil;

public class HtmlFlusher {
	public static String ADMINDIR = "/home/html/analyzer/";
	public static String REPORTDIR = "/home/html/report/";

	//	public static String TemplateDIR = "/home/html/";

	public boolean flushDiscount() {
		try {
			Discount gg = new Discount();
			RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
			StockPrice sp = (StockPrice) ContextFactory.getBean("stockPrice");
			gg.setRecommendStockDao(recommendStockDao);
			gg.setSp(sp);
			List<DiscountStock> discounts = gg.process();
			VMFactory vmf = new VMFactory();
			vmf.setTemplate("/admin/discount.htm");
			vmf.put("discountlist", discounts);
			vmf.save(ADMINDIR + "discount.html");
			System.out.println("write page : " + ADMINDIR + "discount.html");
			return true;
		} catch (Exception e) {
			System.out.println("===> exception !!");
			System.out.println("While generating discount stock html --> GET ERROR MESSAGE: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public void flushStarGuruDetail() {
		AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
		List<Analyzer> analyzerList = analyzerDao.getAllAnalyzers();
		if (analyzerList != null && analyzerList.size() > 0) {
			DateTools dateTools = new DateTools();
			FloatUtil floatUtil = new FloatUtil();
			for (Analyzer analyzer : analyzerList) {
				try {
					//生成分析师intro页面
					String aid = analyzer.getAid();
					GroupStockDao groupStockDao = (GroupStockDao) ContextFactory.getBean("groupStockDao");
					Date startDate = groupStockDao.getEarliestIntimeByAid(aid);

					GroupEarnDao groupEarnDao = (GroupEarnDao) ContextFactory.getBean("groupEarnDao");
					List<GroupEarn> weightList = groupEarnDao.getWeightList(aid, startDate);

					StockEarnDao stockEarnDao = (StockEarnDao) ContextFactory.getBean("stockEarnDao");
					float startprice = stockEarnDao.getStockEarnByCodeDate("000300",
							DateTools.transformYYYYMMDDDate(startDate)).getPrice();
					List<StockEarn> priceList = stockEarnDao.getPriceByCodeDate("000300", DateTools
							.transformYYYYMMDDDate(startDate));

					VMFactory introvmf = new VMFactory();
					introvmf.setTemplate("/template/starintro.htm");
					introvmf.put("floatUtil", floatUtil);
					introvmf.put("dateTools", dateTools);
					introvmf.put("analyzer", analyzer);
					introvmf.put("analyzerList", analyzerList);
					introvmf.put("weightList", weightList);
					introvmf.put("startprice", startprice);
					introvmf.put("priceList", priceList);
					introvmf.save(ADMINDIR + "static/" + aid + "_intro.html");
					System.out.println("write page : " + ADMINDIR + aid + "_intro.html");

					//生成分析师stock页面
					RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory
							.getBean("recommendStockDao");
					List<GroupStock> stockDetailList = groupStockDao.getNameAndCodeByAid(aid);
					Map<String, List<StockEarn>> stockDetailMap = new HashMap<String, List<StockEarn>>();
					for (GroupStock stock : stockDetailList) {
						List<StockEarn> stockEarnList = stockEarnDao.getPriceByCodeDate(stock.getStockcode(), DateTools
								.transformYYYYMMDDDate(stock.getIntime()));
						List<String> filePathList = recommendStockDao.getFilePathByAid(aid, stock.getStockcode(), 3);
						stock.setFilePathList(filePathList);
						for (int i = 0; i < stockEarnList.size(); i++) {
							StockEarn stockEarn = stockEarnList.get(i);
							float currratio = 0;
							if (i == 0) {
								currratio = stockEarn.getRatio() / 100;
							} else {
								currratio = (1 + stockEarnList.get(i - 1).getCurrratio())
										* (1 + stockEarn.getRatio() / 100) - 1;
							}
							stockEarn.setCurrratio(currratio);
						}
						stockDetailMap.put(stock.getStockcode(), stockEarnList);
					}

					VMFactory stockvmf = new VMFactory();
					stockvmf.setTemplate("/template/starstock.htm");
					stockvmf.put("floatUtil", floatUtil);
					stockvmf.put("dateTools", dateTools);
					stockvmf.put("analyzer", analyzer);
					stockvmf.put("analyzerList", analyzerList);
					stockvmf.put("stockDetailList", stockDetailList);
					stockvmf.put("startprice", startprice);
					stockvmf.put("priceList", priceList);
					stockvmf.put("stockDetailMap", stockDetailMap);
					stockvmf.save(ADMINDIR + "static/" + aid + "_stock.html");
					System.out.println("write page : " + ADMINDIR + aid + "_stock.html");

					//生成分析师report页面
					List<RecommendStock> stockList = recommendStockDao.getRecommendStocksByAnalyzer(analyzer.getName(),
							0, 15);

					VMFactory reportvmf = new VMFactory();
					reportvmf.setTemplate("/template/starreport.htm");
					reportvmf.put("floatUtil", floatUtil);
					reportvmf.put("dateTools", dateTools);
					reportvmf.put("analyzer", analyzer);
					reportvmf.put("analyzerList", analyzerList);
					reportvmf.put("stockList", stockList);
					reportvmf.save(ADMINDIR + "static/" + aid + "_report.html");
					System.out.println("write page : " + ADMINDIR + aid + "_report.html");
				} catch (Exception e) {
					System.out.println("===> exception !! ：" + analyzer.getAid() + "  name : " + analyzer.getName());
					System.out.println("While generating stars stock html --> GET ERROR MESSAGE: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	public void flushAnalyzerRank() {
		DateTools dateTools = new DateTools();
		FloatUtil floatUtil = new FloatUtil();
		AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
		GroupStockDao groupStockDao = (GroupStockDao) ContextFactory.getBean("groupStockDao");
		GroupEarnDao groupEarnDao = (GroupEarnDao) ContextFactory.getBean("groupEarnDao");
		StockEarnDao stockEarnDao = (StockEarnDao) ContextFactory.getBean("stockEarnDao");
		Date date = DateTools.getToday();
		List<Analyzer> analyzerList = analyzerDao.getAnalyzerRankList(DateTools.transformYYYYMMDDDate(date), 10);
		Map<String, String> startDateMap = new HashMap<String, String>();
		Map<String, List<GroupEarn>> groupEarnMap = new HashMap<String, List<GroupEarn>>();
		Map<String, List<StockEarn>> stockEarnMap = new HashMap<String, List<StockEarn>>();
		Map<String, Float> startPriceMap = new HashMap<String, Float>();
		for (Analyzer analyzer : analyzerList) {
			Date startDate = groupStockDao.getEarliestIntimeByAid(analyzer.getAid());
			startDateMap.put(analyzer.getAid(), DateTools.transformYYYYMMDDDate(startDate));
			List<GroupEarn> weightList = groupEarnDao.getWeightList(analyzer.getAid(), startDate);
			groupEarnMap.put(analyzer.getAid(), weightList);
			float startprice = stockEarnDao
					.getStockEarnByCodeDate("000300", DateTools.transformYYYYMMDDDate(startDate)).getPrice();
			startPriceMap.put(analyzer.getAid(), startprice);
			List<StockEarn> priceList = stockEarnDao.getPriceByCodeDate("000300", DateTools
					.transformYYYYMMDDDate(startDate));
			stockEarnMap.put(analyzer.getAid(), priceList);
		}
		try {
			VMFactory vmf = new VMFactory();
			vmf.setTemplate("/template/analyzerrank.htm");
			vmf.put("dateTools", dateTools);
			vmf.put("floatUtil", floatUtil);
			vmf.put("currDate", DateTools.transformYYYYMMDDDate(date));
			vmf.put("analyzerList", analyzerList);
			vmf.put("startDateMap", startDateMap);
			vmf.put("groupEarnMap", groupEarnMap);
			vmf.put("stockEarnMap", stockEarnMap);
			vmf.put("startPriceMap", startPriceMap);
			vmf.save(ADMINDIR + "rank.html");
			System.out.println("write page : " + ADMINDIR + "rank.html");
		} catch (Exception e) {
			System.out.println("===> exception !!");
			System.out.println("While generating discount stock html --> GET ERROR MESSAGE: " + e.getMessage());
			e.printStackTrace();
		}

	}

	public void flushReportLab() {
		DateTools dateTools = new DateTools();
		ReportDao reportDao = (ReportDao) ContextFactory.getBean("reportDao");
		int type = 1;
		int size = 20;
		int total = reportDao.getReportsCountByType(type);
		int page = total % size == 0 ? total / size : total / size + 1;
		int current = 1;
		for (; current <= page; current++) {
			int start = (current - 1) * size;
			try {
				List<Report> reportList = reportDao.getReportsListByType(type, start, size);
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/reportlab.htm");
				vmf.put("dateTools", dateTools);
				vmf.put("current", current);
				vmf.put("page", page);
				vmf.put("reportList", reportList);
				vmf.save(REPORTDIR + "reportLab_" + current + ".html");
				System.out.println("write page : " + REPORTDIR + "reportLab_" + current + ".html");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating reportlab html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void flushStarOnSale() {
		DateTools dateTools = new DateTools();
		FloatUtil floatUtil = new FloatUtil();
		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
		GroupStockDao groupStockDao = (GroupStockDao) ContextFactory.getBean("groupStockDao");
		StockEarnDao stockEarnDao = (StockEarnDao) ContextFactory.getBean("stockEarnDao");
		GroupEarnDao groupEarnDao = (GroupEarnDao) ContextFactory.getBean("groupEarnDao");
		List<GroupStock> groupStockList = groupStockDao.getGroupStockList(10);
		Map<String, String> filePathMap = new HashMap<String, String>();
		Map<String, List<StockEarn>> stockDetailMap = new HashMap<String, List<StockEarn>>();
		Map<String, List<GroupEarn>> groupEarnMap = new HashMap<String, List<GroupEarn>>();
		Map<String, List<StockEarn>> stockEarnMap = new HashMap<String, List<StockEarn>>();
		Map<String, Float> startPriceMap = new HashMap<String, Float>();
		for (GroupStock stock : groupStockList) {
			RecommendStock recommendStock = recommendStockDao.getRecommendStockbyReportid(stock.getInreportid());
			filePathMap.put(stock.getInreportid(), recommendStock.getFilepath());

			Date startDate = groupStockDao.getEarliestIntimeByAid(stock.getGroupid());
			List<GroupEarn> weightList = groupEarnDao.getWeightList(stock.getGroupid(), startDate);
			groupEarnMap.put(stock.getGroupid(), weightList);
			float startprice = stockEarnDao
					.getStockEarnByCodeDate("000300", DateTools.transformYYYYMMDDDate(startDate)).getPrice();
			startPriceMap.put(stock.getGroupid(), startprice);
			List<StockEarn> priceList = stockEarnDao.getPriceByCodeDate("000300", DateTools
					.transformYYYYMMDDDate(startDate));
			stockEarnMap.put(stock.getGroupid(), priceList);

			List<StockEarn> stockEarnList = stockEarnDao.getPriceByCodeDate(stock.getStockcode(), DateTools
					.transformYYYYMMDDDate(stock.getIntime()));
			List<String> filePathList = recommendStockDao.getFilePathByAid(stock.getGroupid(), stock.getStockcode(), 3);
			stock.setFilePathList(filePathList);
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
		VMFactory vmf = new VMFactory();
		vmf.setTemplate("/template/staronsale.htm");
		vmf.put("dateTools", dateTools);
		vmf.put("floatUtil", floatUtil);
		vmf.put("filePathMap", filePathMap);
		vmf.put("groupStockList", groupStockList);
		vmf.put("stockDetailMap", stockDetailMap);
		vmf.put("groupEarnMap", groupEarnMap);
		vmf.put("startPriceMap", startPriceMap);
		vmf.put("stockEarnMap", stockEarnMap);
		vmf.save(ADMINDIR + "starDiscount.html");
		System.out.println("write page : " + ADMINDIR + "starDiscount.html");
	}

	public static void main(String[] args) {
		HtmlFlusher flusher = new HtmlFlusher();
		flusher.flushStarGuruDetail();
		flusher.flushAnalyzerRank();
		flusher.flushReportLab();
		flusher.flushStarOnSale();
	}
}
