package com.caijing.flush;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.ColumnArticleDao;
import com.caijing.dao.GroupEarnDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.NoticeDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.ReportDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.ColumnArticle;
import com.caijing.domain.DiscountStock;
import com.caijing.domain.GroupEarn;
import com.caijing.domain.GroupStock;
import com.caijing.domain.Notice;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;
import com.caijing.domain.StockAgencyEntity;
import com.caijing.domain.StockEarn;
import com.caijing.model.StockPrice;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.caijing.util.Discount;
import com.caijing.util.FloatUtil;
import com.caijing.util.HtmlUtils;

public class HtmlFlusher {
	public static String ADMINDIR = "/home/html/analyzer/";
	public static String REPORTDIR = "/home/html/report/";
	public static String NOTICEDIR = "/home/html/notice/";

	@Autowired
	@Qualifier("reportDao")
	private ReportDao reportDao = null;

	@Autowired
	@Qualifier("stockEarnDao")
	private StockEarnDao stockEarnDao = null;

	@Autowired
	@Qualifier("groupStockDao")
	private GroupStockDao groupStockDao = null;

	@Autowired
	@Qualifier("groupEarnDao")
	private GroupEarnDao groupEarnDao = null;

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	@Autowired
	@Qualifier("analyzerDao")
	private AnalyzerDao analyzerDao = null;

	@Autowired
	@Qualifier("columnArticleDao")
	private ColumnArticleDao columnArticleDao = null;

	@Autowired
	@Qualifier("noticeDao")
	private NoticeDao noticeDao = null;

	@Autowired
	@Qualifier("stockPrice")
	private StockPrice sp = null;

	public boolean flushDiscount() {
		try {
			Discount gg = new Discount();
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
		List<Analyzer> analyzerList = analyzerDao.getAllAnalyzers();
		if (analyzerList != null && analyzerList.size() > 0) {
			DateTools dateTools = new DateTools();
			FloatUtil floatUtil = new FloatUtil();
			for (Analyzer analyzer : analyzerList) {
				try {
					//生成分析师intro页面
					String aid = analyzer.getAid();
					Date startDate = groupStockDao.getEarliestIntimeByAid(aid);
					List<GroupEarn> weightList = groupEarnDao.getWeightList(aid, startDate);
					float startprice = stockEarnDao.getStockEarnByCodeDate("000300",
							DateTools.transformYYYYMMDDDate(startDate)).getPrice();
					List<StockEarn> priceList = stockEarnDao.getPriceByCodeDate("000300",
							DateTools.transformYYYYMMDDDate(startDate));

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
					//					RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory
					//							.getBean("recommendStockDao");
					List<GroupStock> stockDetailList = groupStockDao.getNameAndCodeByAid(aid);
					Map<String, List<StockEarn>> stockDetailMap = new HashMap<String, List<StockEarn>>();
					for (GroupStock stock : stockDetailList) {
						List<StockEarn> stockEarnList = stockEarnDao.getPriceByCodeDate(stock.getStockcode(),
								DateTools.transformYYYYMMDDDate(stock.getIntime()));
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
		Date date = DateTools.getToday();
		Date lastdate = groupEarnDao.getLatestDate();
		int size = 20;
		for (int current = 1; current <= 2; current++) {
			List<Analyzer> analyzerList = analyzerDao.getAnalyzerRankList(DateTools.transformYYYYMMDDDate(lastdate),
					(current - 1) * size, size);
			System.out.println("Today: " + DateTools.transformYYYYMMDDDate(date) + "  lastday:"
					+ DateTools.transformYYYYMMDDDate(lastdate));

			Map<String, String> startDateMap = new HashMap<String, String>();
			Map<String, List<GroupEarn>> groupEarnMap = new HashMap<String, List<GroupEarn>>();
			Map<String, List<StockEarn>> stockEarnMap = new HashMap<String, List<StockEarn>>();
			Map<String, Float> startPriceMap = new HashMap<String, Float>();
			for (Analyzer analyzer : analyzerList) {
				Date startDate = groupStockDao.getEarliestIntimeByAid(analyzer.getAid());
				startDateMap.put(analyzer.getAid(), DateTools.transformYYYYMMDDDate(startDate));
				List<GroupEarn> weightList = groupEarnDao.getWeightList(analyzer.getAid(), startDate);
				groupEarnMap.put(analyzer.getAid(), weightList);
				float startprice = stockEarnDao.getStockEarnByCodeDate("000300",
						DateTools.transformYYYYMMDDDate(startDate)).getPrice();
				startPriceMap.put(analyzer.getAid(), startprice);
				List<StockEarn> priceList = stockEarnDao.getPriceByCodeDate("000300",
						DateTools.transformYYYYMMDDDate(startDate));
				stockEarnMap.put(analyzer.getAid(), priceList);
			}
			try {
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/analyzerrank.htm");
				vmf.put("dateTools", dateTools);
				vmf.put("floatUtil", floatUtil);
				vmf.put("currDate", DateTools.transformYYYYMMDDDate(date));
				vmf.put("start", (current - 1) * 20);
				vmf.put("current", current);
				vmf.put("page", 2);
				vmf.put("analyzerList", analyzerList);
				vmf.put("startDateMap", startDateMap);
				vmf.put("groupEarnMap", groupEarnMap);
				vmf.put("stockEarnMap", stockEarnMap);
				vmf.put("startPriceMap", startPriceMap);
				vmf.save(ADMINDIR + "rank_" + current + ".html");
				System.out.println("write page : " + ADMINDIR + "rank_" + current + ".html");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating discount stock html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void flushReportLab() {
		DateTools dateTools = new DateTools();
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

	public void flushStockResearch() {
		DateTools dateTools = new DateTools();
		//		int type = 1;
		int size = 20;
		//		int total = groupStockDao.getRecommendReportCount();
		int total = recommendStockDao.getResearchRecommendStockCounts();
		int page = total % size == 0 ? total / size : total / size + 1;
		int current = 1;
		for (; current <= page; current++) {
			int start = (current - 1) * size;
			try {
				//				List<Report> reportList = reportDao.getReportsListByType(type, start, size);
				//				List<String> reportids = groupStockDao.getRecommendReportids(start, size);
				//				List<RecommendStock> recommendstocks = recommendStockDao.getRecommendStocksByReportids(reportids);
				List<RecommendStock> recommendstocks = recommendStockDao.getResearchRecommendStocks(start, size);
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/stockresearch.htm");
				vmf.put("dateTools", dateTools);
				vmf.put("current", current);
				vmf.put("page", page);
				vmf.put("recommendstocks", recommendstocks);
				vmf.save("/home/html/stockresearch/stockResearch_" + current + ".html");
				System.out.println("write page : " + "/home/html/stockresearch/stockResearch_" + current + ".html");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating reportlab html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void flushNotice() {
		DateTools dateTools = new DateTools();
		int size = 20;
		int total = noticeDao.getNoticesCount();
		int page = total % size == 0 ? total / size : total / size + 1;
		int current = 1;
		for (; current <= page; current++) {
			int start = (current - 1) * size;
			try {
				List<Notice> noticeList = noticeDao.getNotices(start, size);
				for (Notice notice : noticeList) {
					flushOneNotice(notice);
				}
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/noticeList.htm");
				vmf.put("dateTools", dateTools);
				vmf.put("current", current);
				vmf.put("page", page);
				vmf.put("noticeList", noticeList);
				vmf.save(NOTICEDIR + "notice_" + current + ".html");
				System.out.println("write page : " + NOTICEDIR + "notice_" + current + ".html");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating reportlab html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void flushOneNotice(Notice notice) {
		System.out.println("flushOneNotice : " + notice.getTitle() + "  " + notice.getStockname());
		DateTools dateTools = new DateTools();
		try {
			VMFactory vmf = new VMFactory();
			vmf.setTemplate("/template/noticeContent.htm");
			vmf.put("dateTools", dateTools);
			vmf.put("notice", notice);
			String filepath = notice.getUrl().replaceAll("http://51gurus.com", "/home/html");
			String dirpath = filepath.substring(0, filepath.lastIndexOf('/'));
			File file = new File(dirpath);
			if (!file.exists()) {
				file.mkdirs();
			}
			vmf.save(filepath);
			System.out.println("write page : " + filepath);
		} catch (Exception e) {
			System.out.println("===> exception !!");
			System.out.println("While generating reportlab html --> GET ERROR MESSAGE: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void flushStarOnSale(boolean isAsc) {
		DateTools dateTools = new DateTools();
		FloatUtil floatUtil = new FloatUtil();
		List<GroupStock> groupStockList = null;
		int size = 20;
		for (int current = 1; current <= 3; current++) {
			if (isAsc) {
				groupStockList = groupStockDao.getGroupStockListAsc((current - 1) * size, size);
			} else {
				groupStockList = groupStockDao.getGroupStockListDesc((current - 1) * size, size);
			}
			Date lastdate = groupEarnDao.getLatestDate();
			System.out.println("groupStockList.size() : " + groupStockList.size());
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
				float startprice = stockEarnDao.getStockEarnByCodeDate("000300",
						DateTools.transformYYYYMMDDDate(startDate)).getPrice();
				startPriceMap.put(stock.getGroupid(), startprice);
				List<StockEarn> priceList = stockEarnDao.getPriceByCodeDate("000300",
						DateTools.transformYYYYMMDDDate(startDate));
				stockEarnMap.put(stock.getGroupid(), priceList);

				List<StockEarn> stockEarnList = stockEarnDao.getPriceByCodeDate(stock.getStockcode(),
						DateTools.transformYYYYMMDDDate(stock.getIntime()));
				List<String> filePathList = recommendStockDao.getFilePathByAid(stock.getGroupid(),
						stock.getStockcode(), 3);
				stock.setFilePathList(filePathList);
				for (int i = 0; i < stockEarnList.size(); i++) {
					StockEarn stockEarn = stockEarnList.get(i);
					float currratio = 0;
					if (i == 0) {
						currratio = stockEarn.getRatio() / 100;
					} else {
						currratio = (1 + stockEarnList.get(i - 1).getCurrratio()) * (1 + stockEarn.getRatio() / 100)
								- 1;
					}
					stockEarn.setCurrratio(currratio);
				}
				stockDetailMap.put(stock.getStockcode(), stockEarnList);
			}
			VMFactory vmf = new VMFactory();

			vmf.put("dateTools", dateTools);
			vmf.put("currdate", lastdate);
			vmf.put("floatUtil", floatUtil);
			vmf.put("start", (current - 1) * 20);
			vmf.put("page", 3);
			vmf.put("current", current);
			vmf.put("filePathMap", filePathMap);
			vmf.put("groupStockList", groupStockList);
			vmf.put("stockDetailMap", stockDetailMap);
			vmf.put("groupEarnMap", groupEarnMap);
			vmf.put("startPriceMap", startPriceMap);
			vmf.put("stockEarnMap", stockEarnMap);
			if (isAsc) {
				vmf.setTemplate("/template/staronsale.htm");
				vmf.save(ADMINDIR + "starDiscount_" + current + ".html");
				System.out.println("write page : " + ADMINDIR + "starDiscount_" + current + ".html");
			} else {
				vmf.setTemplate("/template/earnRank.htm");
				vmf.save(ADMINDIR + "starEarn_" + current + ".html");
				System.out.println("write page : " + ADMINDIR + "starEarn_" + current + ".html");
			}
		}
	}

	public void flushStockAgency() {
		FloatUtil floatUtil = new FloatUtil();
		int size = 20;
		Date lastdate = groupEarnDao.getLatestDate();
		DateTools dateTools = new DateTools();
		String yearStart = dateTools.getYearStart();
		String halfYearStart = dateTools.getHalfYearStart();
		String quarterStart = dateTools.getQuarterStart();
		String monthStart = dateTools.getMonthStart();
		List<StockAgencyEntity> entitys = recommendStockDao.getTopStockAgencyAfter(0, 100, yearStart);
		int totalpage = 0;
		if (entitys.size() > 100) {
			totalpage = 5;
		} else {
			totalpage = entitys.size() / 20 + 1;
		}
		for (int current = 1; current <= totalpage; current++) {
			//			List<StockAgencyEntity> entitys = recommendStockDao.getTopStockAgency((current - 1) * size, size);
			//			for (StockAgencyEntity entity : entitys) {
			List<StockAgencyEntity> pageentitys = new ArrayList<StockAgencyEntity>();
			for (int i = (current - 1) * 20; i < current * 20 && i < entitys.size(); i++) {
				StockAgencyEntity entity = entitys.get(i);
				List<String> sanames = recommendStockDao.getSanamesByStockcode(entity.getStockcode());
				String tmp = "";
				for (String saname : sanames) {
					tmp += saname + " ";
				}
				entity.setSanames(tmp.trim());
				System.out.println("Stockname:" + entity.getStockname() + "  sanames:" + tmp.trim());
				System.out.println("Stockname:" + entity.getStockname() + "  sacounts:" + entity.getSacounts());
				float price = stockEarnDao.getCurrentPriceByCode(entity.getStockcode());
				entity.setCurrentprice(price);
				pageentitys.add(entity);
				System.out.println("Stockname:" + entity.getStockname() + "  price:" + price);
			}
			try {
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/stockagency.htm");
				vmf.put("dateTools", dateTools);
				vmf.put("currdate", lastdate);
				vmf.put("page", totalpage);
				vmf.put("current", current);
				vmf.put("floatUtil", floatUtil);
				vmf.put("stockAgencyList", pageentitys);
				vmf.put("htmlUtil", new HtmlUtils());
				vmf.put("period", "年度");
				vmf.save("/home/html/stockagency/" + current + ".html");
				System.out.println("write page : " + "/home/html/stockagency/" + current + ".html");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating home html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}

		entitys = recommendStockDao.getTopStockAgencyAfter(0, 100, halfYearStart);
		totalpage = 0;
		if (entitys.size() > 100) {
			totalpage = 5;
		} else {
			totalpage = entitys.size() / 20 + 1;
		}
		for (int current = 1; current <= totalpage; current++) {
			//			List<StockAgencyEntity> entitys = recommendStockDao.getTopStockAgency((current - 1) * size, size);
			//			for (StockAgencyEntity entity : entitys) {
			List<StockAgencyEntity> pageentitys = new ArrayList<StockAgencyEntity>();
			for (int i = (current - 1) * 20; i < current * 20 && i < entitys.size(); i++) {
				StockAgencyEntity entity = entitys.get(i);
				List<String> sanames = recommendStockDao.getSanamesByStockcode(entity.getStockcode());
				String tmp = "";
				for (String saname : sanames) {
					tmp += saname + " ";
				}
				entity.setSanames(tmp.trim());
				System.out.println("Stockname:" + entity.getStockname() + "  sanames:" + tmp.trim());
				System.out.println("Stockname:" + entity.getStockname() + "  sacounts:" + entity.getSacounts());
				float price = stockEarnDao.getCurrentPriceByCode(entity.getStockcode());
				entity.setCurrentprice(price);
				pageentitys.add(entity);
				System.out.println("Stockname:" + entity.getStockname() + "  price:" + price);
			}
			try {
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/stockagency_h.htm");
				vmf.put("dateTools", dateTools);
				vmf.put("currdate", lastdate);
				vmf.put("page", totalpage);
				vmf.put("current", current);
				vmf.put("floatUtil", floatUtil);
				vmf.put("stockAgencyList", pageentitys);
				vmf.put("htmlUtil", new HtmlUtils());
				vmf.put("period", "半年度");
				vmf.save("/home/html/stockagency/halfyear_" + current + ".html");
				System.out.println("write page : " + "/home/html/stockagency/" + current + ".html");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating home html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}

		entitys = recommendStockDao.getTopStockAgencyAfter(0, 100, quarterStart);
		totalpage = 0;
		if (entitys.size() > 100) {
			totalpage = 5;
		} else {
			totalpage = entitys.size() / 20 + 1;
		}
		for (int current = 1; current <= totalpage; current++) {
			//			List<StockAgencyEntity> entitys = recommendStockDao.getTopStockAgency((current - 1) * size, size);
			//			for (StockAgencyEntity entity : entitys) {
			List<StockAgencyEntity> pageentitys = new ArrayList<StockAgencyEntity>();
			for (int i = (current - 1) * 20; i < current * 20 && i < entitys.size(); i++) {
				StockAgencyEntity entity = entitys.get(i);
				List<String> sanames = recommendStockDao.getSanamesByStockcode(entity.getStockcode());
				String tmp = "";
				for (String saname : sanames) {
					tmp += saname + " ";
				}
				entity.setSanames(tmp.trim());
				System.out.println("Stockname:" + entity.getStockname() + "  sanames:" + tmp.trim());
				System.out.println("Stockname:" + entity.getStockname() + "  sacounts:" + entity.getSacounts());
				float price = stockEarnDao.getCurrentPriceByCode(entity.getStockcode());
				entity.setCurrentprice(price);
				pageentitys.add(entity);
				System.out.println("Stockname:" + entity.getStockname() + "  price:" + price);
			}
			try {
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/stockagency_q.htm");
				vmf.put("dateTools", dateTools);
				vmf.put("currdate", lastdate);
				vmf.put("page", totalpage);
				vmf.put("current", current);
				vmf.put("floatUtil", floatUtil);
				vmf.put("stockAgencyList", pageentitys);
				vmf.put("htmlUtil", new HtmlUtils());
				vmf.put("period", "季度");
				vmf.save("/home/html/stockagency/quarter_" + current + ".html");
				System.out.println("write page : " + "/home/html/stockagency/" + current + ".html");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating home html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}
		entitys = recommendStockDao.getTopStockAgencyAfter(0, 100, monthStart);
		totalpage = 0;
		if (entitys.size() > 100) {
			totalpage = 5;
		} else {
			totalpage = entitys.size() / 20 + 1;
		}
		for (int current = 1; current <= totalpage; current++) {
			//			List<StockAgencyEntity> entitys = recommendStockDao.getTopStockAgency((current - 1) * size, size);
			//			for (StockAgencyEntity entity : entitys) {
			List<StockAgencyEntity> pageentitys = new ArrayList<StockAgencyEntity>();
			for (int i = (current - 1) * 20; i < current * 20 && i < entitys.size(); i++) {
				StockAgencyEntity entity = entitys.get(i);
				List<String> sanames = recommendStockDao.getSanamesByStockcode(entity.getStockcode());
				String tmp = "";
				for (String saname : sanames) {
					tmp += saname + " ";
				}
				entity.setSanames(tmp.trim());
				System.out.println("Stockname:" + entity.getStockname() + "  sanames:" + tmp.trim());
				System.out.println("Stockname:" + entity.getStockname() + "  sacounts:" + entity.getSacounts());
				float price = stockEarnDao.getCurrentPriceByCode(entity.getStockcode());
				entity.setCurrentprice(price);
				pageentitys.add(entity);
				System.out.println("Stockname:" + entity.getStockname() + "  price:" + price);
			}
			try {
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/stockagency_m.htm");
				vmf.put("dateTools", dateTools);
				vmf.put("currdate", lastdate);
				vmf.put("page", totalpage);
				vmf.put("current", current);
				vmf.put("floatUtil", floatUtil);
				vmf.put("stockAgencyList", pageentitys);
				vmf.put("htmlUtil", new HtmlUtils());
				vmf.put("period", "一个月");
				vmf.save("/home/html/stockagency/month_" + current + ".html");
				System.out.println("write page : " + "/home/html/stockagency/month_" + current + ".html");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating home html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}

		System.out.println("yearstart : " + yearStart);
		System.out.println("halfyearstart : " + halfYearStart);
		System.out.println("quaterstart : " + quarterStart);
		System.out.println("monthstart : " + monthStart);
	}

	public void flushIndex() {
		DateTools dateTools = new DateTools();
		FloatUtil floatUtil = new FloatUtil();
		HtmlUtils htmlUtil = new HtmlUtils();
		try {
			List<GroupStock> groupStockList = groupStockDao.getGroupStockListDesc(0, 10);
			Date lastdate = groupEarnDao.getLatestDate();
			//			AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
			List<Analyzer> analyzerList = analyzerDao.getAnalyzerRankList(DateTools.transformYYYYMMDDDate(lastdate), 0,
					10);
			List<ColumnArticle> dsyp = columnArticleDao.getColumnArticleByType(1, 3);
			List<ColumnArticle> hgdt = columnArticleDao.getColumnArticleByType(2, 6);
			List<ColumnArticle> cjzl = columnArticleDao.getColumnArticleByType(0, 6);
			dsyp = alertUrl(dsyp);
			hgdt = alertUrl(hgdt);
			cjzl = alertUrl(cjzl);
			//		List<ColumnArticle> articles = columnArticleDao.getColumnArticleByType(1, 3);
			//			List<String> reportids = groupStockDao.getRecommendReportids(0, 3);
			//			List<RecommendStock> recommendstocks = recommendStockDao.getRecommendStocksByReportids(reportids);
			List<RecommendStock> recommendstocks = recommendStockDao.getResearchRecommendStocks(0, 3);
			for (RecommendStock stock : recommendstocks) {
				System.out.println("url: " + "http://51gurus.com" + stock.getFilepath());
				System.out.println("title: " + stock.getTitle());
				System.out.println("aname: " + stock.getAname());
				System.out.println("getGrade: " + stock.getGrade());
				System.out.println("getStockcode: " + stock.getStockcode());
				System.out.println("getStockname: " + stock.getStockname());
			}
			System.out.println("recommendstocks size: " + recommendstocks.size());
			VMFactory vmf = new VMFactory();
			vmf.setTemplate("/template/home.htm");
			vmf.put("dateTools", dateTools);
			vmf.put("currdate", lastdate);
			vmf.put("floatUtil", floatUtil);
			vmf.put("dsyp", dsyp);
			vmf.put("hgdt", hgdt);
			vmf.put("cjzl", cjzl);
			vmf.put("cjzlsize", cjzl.size());
			vmf.put("htmlUtil", htmlUtil);
			vmf.put("recommendstocks", recommendstocks);
			vmf.put("groupStockList", groupStockList);
			vmf.put("analyzerList", analyzerList);

			vmf.save("/home/html/index.html");
			System.out.println("write page : " + "/home/html/index.html");
		} catch (Exception e) {
			System.out.println("===> exception !!");
			System.out.println("While generating home html --> GET ERROR MESSAGE: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private List<ColumnArticle> alertUrl(List<ColumnArticle> articles) {
		List<ColumnArticle> retlist = new ArrayList<ColumnArticle>(articles.size());
		for (int i = 0; i < articles.size(); i++) {
			String url = "http://51gurus.com/cms/";
			String date = DateTools.transformDateDetail(articles.get(i).getPtime());
			System.out.println("date:" + date);
			String[] strs = date.split("-");
			System.out.println("strs length:" + strs);
			if (strs.length == 3) {
				url += strs[0] + "/" + strs[1] + "/" + articles.get(i).getCmsid() + ".shtml";
				articles.get(i).setLink(url);
				System.out.println("url:" + articles.get(i).getLink());
				retlist.add(articles.get(i));
			} else {
				System.out.println("Date Format Parse ERROR!");
			}
		}
		return retlist;
	}

	public static void main(String[] args) {
		HtmlFlusher flusher = (HtmlFlusher) ContextFactory.getBean("htmlFlush");
		//		flusher.flushStarGuruDetail();
		//		flusher.flushAnalyzerRank();
		//		flusher.flushReportLab();
		//		flusher.flushStarOnSale();
		//		flusher.flushNotice();
		//		flusher.flushIndex();
		//		flusher.flushStarOnSale(false);
		//		flusher.flushStarOnSale(true);
		//		flusher.flushAnalyzerRank();
		//		flusher.flushStockResearch();
		//		flusher.flushStockAgency();
		flusher.flushNotice();
	}

	public ReportDao getReportDao() {
		return reportDao;
	}

	public void setReportDao(ReportDao reportDao) {
		this.reportDao = reportDao;
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

	public GroupEarnDao getGroupEarnDao() {
		return groupEarnDao;
	}

	public void setGroupEarnDao(GroupEarnDao groupEarnDao) {
		this.groupEarnDao = groupEarnDao;
	}

	public RecommendStockDao getRecommendStockDao() {
		return recommendStockDao;
	}

	public void setRecommendStockDao(RecommendStockDao recommendStockDao) {
		this.recommendStockDao = recommendStockDao;
	}

	public AnalyzerDao getAnalyzerDao() {
		return analyzerDao;
	}

	public void setAnalyzerDao(AnalyzerDao analyzerDao) {
		this.analyzerDao = analyzerDao;
	}

	public ColumnArticleDao getColumnArticleDao() {
		return columnArticleDao;
	}

	public void setColumnArticleDao(ColumnArticleDao columnArticleDao) {
		this.columnArticleDao = columnArticleDao;
	}

	public NoticeDao getNoticeDao() {
		return noticeDao;
	}

	public void setNoticeDao(NoticeDao noticeDao) {
		this.noticeDao = noticeDao;
	}

	public StockPrice getSp() {
		return sp;
	}

	public void setSp(StockPrice sp) {
		this.sp = sp;
	}

}
