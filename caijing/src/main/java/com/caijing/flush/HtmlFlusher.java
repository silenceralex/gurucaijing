package com.caijing.flush;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.ColumnArticleDao;
import com.caijing.dao.FinancialReportDao;
import com.caijing.dao.GroupEarnDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.MasterDao;
import com.caijing.dao.MasterMessageDao;
import com.caijing.dao.NoticeDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.RecommendSuccessDao;
import com.caijing.dao.ReportDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.ColumnArticle;
import com.caijing.domain.FinancialReport;
import com.caijing.domain.GroupEarn;
import com.caijing.domain.GroupStock;
import com.caijing.domain.Notice;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.RecommendSuccess;
import com.caijing.domain.Report;
import com.caijing.domain.StockAgencyEntity;
import com.caijing.domain.StockEarn;
import com.caijing.model.StockPrice;
import com.caijing.util.Config;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.caijing.util.FloatUtil;
import com.caijing.util.HtmlUtils;
import com.caijing.util.Paginator;

public class HtmlFlusher {
	public static String ADMINDIR = "/home/html/analyzer/";
	public static String ARTICLEDIR = "/home/html/articles/";
	public static String REPORTDIR = "/home/html/report/";
	public static String FINANCIALREPORTDIR = "/home/html/financialreport/";
	public static String NOTICEDIR = "/home/html/notice/";
	public static String LIVEDIR = "/home/html/live/";
	public static String MasterDIR = "/home/html/master/";
	public static String PREFIX = "http://51gurus.com";
	public static String STARTDATE = "2010-01-01";
	private static int STARTYEAR = 1990;
	private static int ENDYEAR = 2010;

	public static String TOP10 = "/home/html/notice/top10.json";

	@Autowired
	@Qualifier("financialReportDao")
	private FinancialReportDao financialReportDao = null;

	public FinancialReportDao getFinancialReportDao() {
		return financialReportDao;
	}

	public void setFinancialReportDao(FinancialReportDao financialReportDao) {
		this.financialReportDao = financialReportDao;
	}

	@Autowired
	@Qualifier("reportDao")
	private ReportDao reportDao = null;

	@Autowired
	@Qualifier("masterDao")
	private MasterDao masterDao = null;

	public MasterDao getMasterDao() {
		return masterDao;
	}

	public void setMasterDao(MasterDao masterDao) {
		this.masterDao = masterDao;
	}

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
	@Qualifier("masterMessageDao")
	private MasterMessageDao masterMessageDao = null;

	public MasterMessageDao getMasterMessageDao() {
		return masterMessageDao;
	}

	public void setMasterMessageDao(MasterMessageDao masterMessageDao) {
		this.masterMessageDao = masterMessageDao;
	}

	@Autowired
	@Qualifier("noticeDao")
	private NoticeDao noticeDao = null;

	@Autowired
	@Qualifier("recommendSuccessDao")
	private RecommendSuccessDao recommendSuccessDao = null;

	public RecommendSuccessDao getRecommendSuccessDao() {
		return recommendSuccessDao;
	}

	public void setRecommendSuccessDao(RecommendSuccessDao recommendSuccessDao) {
		this.recommendSuccessDao = recommendSuccessDao;
	}

	@Autowired
	@Qualifier("stockPrice")
	private StockPrice sp = null;

	@Autowired
	@Qualifier("config")
	private Config config = null;

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public void flushOneGuruDetail(Analyzer analyzer, List<Analyzer> analyzerList) {
		DateTools dateTools = new DateTools();
		FloatUtil floatUtil = new FloatUtil();
		try {
			//生成分析师intro页面
			String aid = analyzer.getAid();
			Date startDate = null;
			try {
				startDate = groupStockDao.getEarliestIntimeByAidFrom(aid, DateTools.parseYYYYMMDDDate(STARTDATE));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (startDate == null) {
				System.out.println("analyzer  : " + analyzer.getName() + "  " + aid + " is null");
				return;
			}
			List<GroupEarn> weightList = groupEarnDao.getWeightList(aid, startDate);
			float startprice = stockEarnDao
					.getStockEarnByCodeDate("000300", DateTools.transformYYYYMMDDDate(startDate)).getPrice();
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
						currratio = (1 + stockEarnList.get(i - 1).getCurrratio()) * (1 + stockEarn.getRatio() / 100)
								- 1;
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
			List<RecommendStock> stockList = recommendStockDao.getRecommendStocksByAnalyzer(analyzer.getName(), 0, 15);

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

	public void flushStarGuruDetail() {
		List<Analyzer> analyzerList = analyzerDao.getStarAnalyzers();
		if (analyzerList != null && analyzerList.size() > 0) {
			for (Analyzer analyzer : analyzerList) {
				flushOneGuruDetail(analyzer, analyzerList);
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
				Date startDate = null;
				try {
					startDate = groupStockDao.getEarliestIntimeByAidFrom(analyzer.getAid(),
							DateTools.parseYYYYMMDDDate(STARTDATE));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("analyzer: " + analyzer.getName() + "  startDate:" + startDate.toString());
				startDateMap.put(analyzer.getAid(), DateTools.transformYYYYMMDDDate(startDate));
				List<GroupEarn> weightList = groupEarnDao.getWeightList(analyzer.getAid(), startDate);
				groupEarnMap.put(analyzer.getAid(), weightList);
				float startprice = stockEarnDao.getNearPriceByCodeDate("000300", startDate).getPrice();
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

	public void flushSuccessRank() {
		DateTools dateTools = new DateTools();
		FloatUtil floatUtil = new FloatUtil();
		//		List<Analyzer> analyzers = analyzerDao.getSuccessRankedAnalyzersByAgency("安信证券");
		List<Analyzer> analyzers = analyzerDao.getSuccessRankedAnalyzers();
		List<Analyzer> disAnalyzers = new ArrayList<Analyzer>();
		System.out.println("rank size : " + analyzers.size());
		for (Analyzer analyzer : analyzers) {
			if (analyzer.getAid() == null) {
				System.out.println("analyzer.getAid() is null! ");
				continue;
			}
			int success = recommendSuccessDao.getRecommendSuccessCountByAid(analyzer.getAid());
			int total = recommendSuccessDao.getTotalRecommendCountByAid(analyzer.getAid());
			analyzer.setSuccess(success);
			analyzer.setTotal(total);
			//排出推荐1篇的
			if (total > 1) {
				disAnalyzers.add(analyzer);
			}
			flushOneSuccess(analyzer);
		}
		System.out.println("disAnalyzers size : " + disAnalyzers.size());
		for (int current = 1; current <= 3; current++) {
			List<Analyzer> analyzerList = disAnalyzers.subList((current - 1) * 20, current * 20);
			try {
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/anayzerSucRank.htm");
				vmf.put("dateTools", dateTools);
				vmf.put("floatUtil", floatUtil);
				vmf.put("currDate", DateTools.transformYYYYMMDDDate(new Date()));
				vmf.put("currdate", new Date());
				vmf.put("start", (current - 1) * 20);
				vmf.put("current", current);
				vmf.put("page", 3);
				vmf.put("analyzerList", analyzerList);
				vmf.save(ADMINDIR + "successrank_" + current + ".html");
				System.out.println("write page : " + ADMINDIR + "successrank_" + current + ".html");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating discount stock html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void flushOneSuccess(Analyzer analyzer) {
		FloatUtil floatUtil = new FloatUtil();

		String ratio = "" + floatUtil.getTwoDecimalNumber(analyzer.getSuccessratio()) + "%";
		try {
			System.out.println("write page : " + analyzer.getAid());
			List<RecommendSuccess> recommends = recommendSuccessDao.getRecommendsByAid(analyzer.getAid());
			for (RecommendSuccess recommend : recommends) {
				System.out.println("write page Reportid: " + recommend.getReportid());
				Report report = (Report) reportDao.select(recommend.getReportid());
				String url = PREFIX + report.getFilepath();
				recommend.setReporturl(url);
			}

			VMFactory vmf = new VMFactory();
			vmf.setTemplate("/template/starsuc.htm");
			vmf.put("dateTools", new DateTools());
			vmf.put("floatUtil", floatUtil);
			vmf.put("analyzer", analyzer);
			vmf.put("currdate", new Date());
			vmf.put("aname", analyzer.getName());
			vmf.put("ratio", ratio);
			vmf.put("recommends", recommends);
			vmf.save(ADMINDIR + "static/" + analyzer.getAid() + "_success.html");
			System.out.println("write page : " + ADMINDIR + "static/" + analyzer.getAid() + "_success.html");
		} catch (Exception e) {
			System.out.println("===> exception !!");
			System.out.println("While generating discount stock html --> GET ERROR MESSAGE: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void flushReportLab() {
		DateTools dateTools = new DateTools();
		int type = 1;
		int size = 20;
		//		int total = reportDao.getReportsCountByType(type);
		//		int page = total % size == 0 ? total / size : total / size + 1;
		int page = 50;
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
				vmf.save(REPORTDIR + "reportLab_" + current + ".htm");
				System.out.println("write page : " + REPORTDIR + "reportLab_" + current + ".htm");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating reportlab html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void flushFinancialReportLab() {
		DateTools dateTools = new DateTools();
		int status = 0;
		int size = 20;
		//		int total = reportDao.getReportsCountByType(type);
		//		int page = total % size == 0 ? total / size : total / size + 1;
		int page = 10;
		int current = 1;
		List<String> years = new ArrayList<String>();
		for (int i = ENDYEAR; i >= STARTYEAR; i--) {
			years.add("" + i);
		}

		for (; current <= page; current++) {
			int start = (current - 1) * size;
			try {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("status", status);
				params.put("start", start);
				params.put("size", size);
				params.put("orderby", "year desc,type desc");
				List<FinancialReport> reportList = financialReportDao.getReportsList(params);
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/financialreportlab.htm");
				vmf.put("dateTools", dateTools);
				vmf.put("current", current);
				vmf.put("years", years);
				System.out.println("years.size() : " + years.size());
				vmf.put("page", page);
				vmf.put("reportList", reportList);
				vmf.save(FINANCIALREPORTDIR + "financialreportLab_" + current + ".html");
				System.out.println("write page : " + FINANCIALREPORTDIR + "financialreportLab_" + current + ".html");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating financialreportlab html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void flushStockResearch() {
		DateTools dateTools = new DateTools();
		//		int type = 1;
		int size = 20;
		//		int total = groupStockDao.getRecommendReportCount();
		//		int total = recommendStockDao.getResearchRecommendStockCounts();
		//		int page = total % size == 0 ? total / size : total / size + 1;
		//最多刷新20页数据出来
		int page = 20;
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
		if (page > 20) {
			page = 20;
		}
		int current = 1;
		for (; current <= page; current++) {
			int start = (current - 1) * size;
			try {
				List<Notice> noticeList = noticeDao.getNotices(start, size);
				for (Notice notice : noticeList) {
					flushOneNotice(notice);
				}
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/notice/noticeList.htm");
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
			vmf.setTemplate("/template/notice/noticeContent.htm");
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

	/**
	 * 刷新挣钱排行榜，折价股票榜
	 * @param isAsc  true为折价榜
	 * @param type  1为月度，2为季度，3为半年，4为年度
	 */
	public void flushStarOnSale(boolean isAsc, int type) {
		DateTools dateTools = new DateTools();
		FloatUtil floatUtil = new FloatUtil();
		List<GroupStock> groupStockList = null;
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		Date start = null;
		switch (type) {
		case 1:
			cal.add(Calendar.MONTH, -1);
			start = cal.getTime();
			System.out.println("start time : " + dateTools.transformMMDDDate(start));
			break;
		case 2:
			cal.add(Calendar.MONTH, -3);
			start = cal.getTime();
			System.out.println("start time : " + dateTools.transformMMDDDate(start));
			break;
		case 3:
			cal.add(Calendar.MONTH, -6);
			start = cal.getTime();
			System.out.println("start time : " + dateTools.transformMMDDDate(start));
			break;
		case 4:
			cal.add(Calendar.YEAR, -1);
			start = cal.getTime();
			System.out.println("start time : " + dateTools.transformMMDDDate(start));
			break;
		}
		int total = groupStockDao.getGroupStockCountBetween(start, now);
		System.out.println("total : " + total);
		int page = 0;
		if (total <= 0)
			return;
		if (0 < total && total <= 20) {
			page = 1;
		} else if (20 < total && total <= 40) {
			page = 2;
		} else {
			page = 3;
		}
		int size = 20;
		for (int current = 1; current <= page; current++) {
			if (isAsc) {
				groupStockList = groupStockDao.getGroupStockListAsc((current - 1) * size, size,
						dateTools.transformYYYYMMDDDate(start));
			} else {
				groupStockList = groupStockDao.getGroupStockListDesc((current - 1) * size, size,
						dateTools.transformYYYYMMDDDate(start));
			}
			System.out.println("groupStockList.size() : " + groupStockList.size());
			//按照inreportid是唯一key
			Map<String, List<StockEarn>> stockDetailMap = new HashMap<String, List<StockEarn>>();
			Map<String, List<StockEarn>> stockEarnMap = new HashMap<String, List<StockEarn>>();
			Map<String, Float> startPriceMap = new HashMap<String, Float>();
			for (GroupStock stock : groupStockList) {
				System.out.println("groupid : " + stock.getGroupid() + "  groupname:" + stock.getGroupname()
						+ "  getInreportid:" + stock.getInreportid());

				float startprice = stockEarnDao.getFormerNearPriceByCodeDate("000300", stock.getIntime()).getPrice();
				startPriceMap.put(stock.getInreportid(), startprice);
				List<StockEarn> priceList = stockEarnDao.getPriceByCodeDate("000300",
						DateTools.transformYYYYMMDDDate(stock.getIntime()));
				stockEarnMap.put(stock.getInreportid(), priceList);

				List<StockEarn> stockEarnList = stockEarnDao.getPriceByCodeDate(stock.getStockcode(),
						DateTools.transformYYYYMMDDDate(stock.getIntime()));

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
				stockDetailMap.put(stock.getInreportid(), stockEarnList);
			}
			VMFactory vmf = new VMFactory();
			vmf.put("type", type);
			vmf.put("dateTools", dateTools);
			vmf.put("currdate", now);
			vmf.put("floatUtil", floatUtil);
			vmf.put("start", (current - 1) * 20);
			vmf.put("page", page);
			vmf.put("current", current);
			vmf.put("groupStockList", groupStockList);
			vmf.put("stockDetailMap", stockDetailMap);
			vmf.put("startPriceMap", startPriceMap);
			vmf.put("stockEarnMap", stockEarnMap);
			if (isAsc) {
				vmf.setTemplate("/template/staronsale.htm");
				if (type == 1) {
					vmf.save(ADMINDIR + "stardiscount_1_" + current + ".html");
					System.out.println("write page : " + ADMINDIR + "stardiscount_1_" + current + ".html");
				} else if (type == 2) {
					vmf.save(ADMINDIR + "stardiscount_2_" + current + ".html");
					System.out.println("write page : " + ADMINDIR + "stardiscount_2_" + current + ".html");
				} else if (type == 3) {
					vmf.save(ADMINDIR + "stardiscount_3_" + current + ".html");
					System.out.println("write page : " + ADMINDIR + "stardiscount_3_" + current + ".html");
				} else if (type == 4) {
					vmf.save(ADMINDIR + "stardiscount_4_" + current + ".html");
					System.out.println("write page : " + ADMINDIR + "stardiscount_4_" + current + ".html");
				}
			} else {
				vmf.setTemplate("/template/earnRank.htm");
				if (type == 1) {
					vmf.save(ADMINDIR + "earnrank_1_" + current + ".html");
					System.out.println("write page : " + ADMINDIR + "earnrank_1_" + current + ".html");
				} else if (type == 2) {
					vmf.save(ADMINDIR + "earnrank_2_" + current + ".html");
					System.out.println("write page : " + ADMINDIR + "earnrank_2_" + current + ".html");
				} else if (type == 3) {
					vmf.save(ADMINDIR + "earnrank_3_" + current + ".html");
					System.out.println("write page : " + ADMINDIR + "earnrank_3_" + current + ".html");
				} else if (type == 4) {
					vmf.save(ADMINDIR + "earnrank_4_" + current + ".html");
					System.out.println("write page : " + ADMINDIR + "earnrank_4_" + current + ".html");
				}
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
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.YEAR, -1);
			//默认挣钱排行榜采用近一年来的榜单
			List<GroupStock> groupStockList = groupStockDao.getGroupStockListDesc(0, 10,
					dateTools.transformYYYYMMDDDate(cal.getTime()));
			Date lastdate = groupEarnDao.getLatestDate();
			//			AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
			//			List<Analyzer> analyzerList = analyzerDao.getAnalyzerRankList(DateTools.transformYYYYMMDDDate(lastdate), 0,
			//					10);
			File file = new File(AnalyzerFlusher.TOP10);
			String analyzerjson = FileUtils.readFileToString(file);
			JSONArray jsonArray = JSONArray.fromObject(analyzerjson);
			//			JsonConfig jsonConfig = new JsonConfig();
			//			jsonConfig.setExcludes(new String[] { "level", "status", "info", "ptime", "lmodify", "image_url",
			//					"position", "successratio" });
			//			jsonConfig.setIgnoreDefaultExcludes(false);
			System.out.println("analyzerjson:" + analyzerjson);
			List<Analyzer> analyzerList = JSONArray.toList(jsonArray, Analyzer.class);

			file = new File(TOP10);
			String noticejson = FileUtils.readFileToString(file);
			jsonArray = JSONArray.fromObject(noticejson);

			System.out.println("noticejson:" + noticejson);
			List<Notice> noticetop10 = JSONArray.toList(jsonArray, Notice.class);

			//			System.out.println("analyzerList.get(0).getWeight():" + analyzerList.get(0).getWeight());
			List<ColumnArticle> dsyp = columnArticleDao.getColumnArticleByType(1, 0, 4);
			List<ColumnArticle> hgdt = columnArticleDao.getColumnArticleByType(2, 0, 8);
			List<ColumnArticle> cjzl = columnArticleDao.getABSArticlesByType(0, 0, 6);
			//			dsyp = alertUrl(dsyp);
			//			hgdt = alertUrl(hgdt);
			//			cjzl = alertUrl(cjzl);
			alertUrl(dsyp);
			alertUrl(hgdt);
			alertUrl(cjzl);
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
			List<Notice> noticeList = noticeDao.getNotices(0, 6);
			System.out.println("noticeList size: " + noticeList.size());
			VMFactory vmf = new VMFactory();
			vmf.setTemplate("/template/home.htm");
			vmf.put("dateTools", dateTools);
			vmf.put("currdate", lastdate);
			vmf.put("floatUtil", floatUtil);
			vmf.put("noticeList", noticeList);
			vmf.put("dsyp", dsyp);
			vmf.put("hgdt", hgdt);
			vmf.put("cjzl", cjzl);
			vmf.put("noticeTop", noticetop10);
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

	private void alertUrl(List<ColumnArticle> articles) {
		//		List<ColumnArticle> retlist = new ArrayList<ColumnArticle>(articles.size());
		for (int i = 0; i < articles.size(); i++) {
			//			String url = "http://51gurus.com/cms/";
			//			String date = DateTools.transformDateDetail(articles.get(i).getPtime());
			//			System.out.println("date:" + date);
			//			String[] strs = date.split("-");
			//			System.out.println("strs length:" + strs);
			//			if (strs.length == 3) {
			//				url += strs[0] + "/" + strs[1] + "/" + articles.get(i).getCmsid() + ".shtml";
			//				articles.get(i).setLink(url);
			//				System.out.println("url:" + articles.get(i).getLink());
			//				retlist.add(articles.get(i));
			//			} else {
			//				System.out.println("Date Format Parse ERROR!");
			//			}
			String link = getLink(articles.get(i));
			articles.get(i).setLink(link);
		}
		//		return retlist;
	}

	public void flushOneArticle(ColumnArticle article) {
		String ptime = DateTools.transformYYYYMMDDDate(article.getPtime());
		VMFactory introvmf = new VMFactory();
		introvmf.setTemplate("/template/content.htm");
		introvmf.put("article", article);
		introvmf.put("ptime", ptime);
		String category = "";
		switch (article.getType()) {
		case 0:
			category = "大师专栏";
			break;
		case 1:
			category = "大势研判";
			break;
		case 2:
			category = "宏观动态";
			break;
		case 3:
			category = "草根博客";
			break;
		}
		introvmf.put("category", category);
		introvmf.save(ARTICLEDIR + article.getType() + "/" + DateTools.getYear(article.getPtime()) + "/"
				+ DateTools.getMonth(article.getPtime()) + "/" + article.getAid() + ".html");
		System.out.println("write page : " + ARTICLEDIR + article.getType() + "/"
				+ DateTools.getYear(article.getPtime()) + "/" + DateTools.getMonth(article.getPtime()) + "/"
				+ article.getAid() + ".html");
	}

	public void flushArticleList(int type) {
		long start = System.currentTimeMillis();
		Paginator<Report> paginator = new Paginator<Report>();
		paginator.setPageSize(10);
		int total = columnArticleDao.getAllArticleCountByType(type);
		int totalpage = (total / 10 + 1) > 10 ? 10 : (total / 10 + 1);
		for (int i = 0; i < 10 && i < totalpage; i++) {
			List<ColumnArticle> articles = columnArticleDao.getColumnArticleByType(type, i * 10, 10);
			for (ColumnArticle article : articles) {
				article.setLink(getLink(article));
				flushOneArticle(article);
			}
			paginator.setCurrentPageNumber(i);
			paginator.setUrl("http://51gurus.com/articles/" + type + "/list_$number$.html");
			try {
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/articleList.htm");
				String category = "";
				switch (type) {
				case 0:
					category = "大师专栏";
					break;
				case 1:
					category = "大势研判";
					break;
				case 2:
					category = "宏观动态";
					break;
				case 3:
					category = "草根博客";
					break;
				}
				vmf.put("category", category);
				vmf.put("dateTools", new DateTools());
				vmf.put("articlelist", articles);
				vmf.put("page", totalpage);
				vmf.put("current", i + 1);
				vmf.put("ctype", type);
				vmf.put("paginatorLink", paginator.getPageNumberList());
				vmf.save(ARTICLEDIR + type + "/" + "list_" + (i + 1) + ".html");
				System.out.println("write page : " + ARTICLEDIR + type + "/" + "list_" + (i + 1) + ".html");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating reportlab html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Flush type :" + type + " use time:" + (end - start) / 1000 + " seconds!");

	}

	private String getLink(ColumnArticle article) {
		String linkprefix = "http://51gurus.com/articles/" + article.getType() + "/";
		String link = linkprefix + DateTools.getYear(article.getPtime()) + "/" + DateTools.getMonth(article.getPtime())
				+ "/" + article.getAid() + ".html";
		return link;
	}

	public void flushNoticeRank() {
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.MONTH, -6);
		Date startDate = cal.getTime();
		System.out.println("half year startDate:" + startDate.toString());
		List<Notice> notices = noticeDao.getActiveNoticeStocks(startDate, new Date());
		System.out.println("half year raw notices size:" + notices.size());
		flushNoticeList(notices, 0);

		cal.add(Calendar.MONTH, 3);
		startDate = cal.getTime();
		System.out.println("quater year startDate:" + startDate.toString());
		notices = noticeDao.getActiveNoticeStocks(startDate, new Date());
		System.out.println("quater raw notices size:" + notices.size());
		flushNoticeList(notices, 1);

		cal.add(Calendar.MONTH, 2);
		startDate = cal.getTime();
		System.out.println("month year startDate:" + startDate.toString());
		notices = noticeDao.getActiveNoticeStocks(startDate, new Date());
		System.out.println("month raw notices size:" + notices.size());
		flushNoticeList(notices, 2);
	}

	public void flushNoticeRank(int type) {
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.MONTH, -6);
		Date startDate = cal.getTime();
		List<Notice> notices = noticeDao.getNoticeStocksByType(type, startDate, new Date());
		System.out.println("raw notices size:" + notices.size());

	}

	/**
	 * 
	 * @param notices
	 * @param type   0代表半年，1代表季度，2代表阅读
	 */
	private void flushNoticeList(List<Notice> notices, int type) {
		List<Notice> noticeStocks = new ArrayList<Notice>();
		HashSet<String> duplicatSet = new HashSet<String>();
		Map<String, List<StockEarn>> stockDetailMap = new HashMap<String, List<StockEarn>>();
		Map<String, List<StockEarn>> stockEarnMap = new HashMap<String, List<StockEarn>>();
		Map<String, Float> startPriceMap = new HashMap<String, Float>();
		for (Notice notice : notices) {
			//去重 + notice.getDate()
			String key = notice.getStockcode();
			if (duplicatSet.contains(key)) {
				continue;
			} else {
				duplicatSet.add(key);
			}
			List<StockEarn> stockEarnList = stockEarnDao.getRatiosByCodeAndPeriod(notice.getStockcode(),
					notice.getDate(), new Date());
			if (stockEarnList == null || stockEarnList.size() == 0) {
				continue;
			}
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
			notice.setGain(stockEarnList.get(stockEarnList.size() - 1).getCurrratio() * 100);
			noticeStocks.add(notice);

			stockDetailMap.put(notice.getId(), stockEarnList);
			float startprice = stockEarnDao.getFormerNearPriceByCodeDate("000300", notice.getDate()).getPrice();
			startPriceMap.put(notice.getId(), startprice);
			List<StockEarn> priceList = stockEarnDao.getPriceByCodeDate("000300",
					DateTools.transformYYYYMMDDDate(notice.getDate()));
			stockEarnMap.put(notice.getId(), priceList);
		}
		System.out.println("Discard duplicated notices size:" + noticeStocks.size());
		Collections.sort(noticeStocks);

		if (type == 0) {
			List<Notice> noticetop10 = noticeStocks.subList(0, 10);
			for (Notice notice : noticetop10) {
				System.out.println("notice :" + notice.getType());
			}
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setExcludes(new String[] { "stockcode", "title", "id", "date", "url", "content" });
			jsonConfig.setIgnoreDefaultExcludes(false);
			String noticejson = JSONArray.fromObject(noticetop10, jsonConfig).toString();
			System.out.println("noticejson:" + noticejson);
			File file = new File(TOP10);
			try {
				FileUtils.writeStringToFile(file, noticejson);
			} catch (IOException e1) {
				System.out.println("Write top10 file catch Exception:" + e1.getMessage());
				e1.printStackTrace();
			}
		}
		int total = noticeStocks.size();
		int page = 0;
		if (total <= 0)
			return;

		if (0 < total && total < 100) {
			page = total % 20 == 0 ? total / 20 : (total / 20 + 1);
		} else {
			page = 5;
		}
		for (int current = 1; current <= page; current++) {
			try {
				VMFactory vmf = new VMFactory();
				vmf.put("type", type);
				vmf.put("dateTools", new DateTools());
				vmf.put("htmlUtil", new HtmlUtils());
				vmf.put("currdate", new Date());
				vmf.put("floatUtil", new FloatUtil());
				vmf.put("start", (current - 1) * 20);
				vmf.put("page", page);
				vmf.put("current", current);
				vmf.put("noticeStocks", noticeStocks.subList((current - 1) * 20, current * 20));
				vmf.put("stockDetailMap", stockDetailMap);
				vmf.put("startPriceMap", startPriceMap);
				vmf.put("stockEarnMap", stockEarnMap);

				//刷新均采用htm的后缀一遍进行权限访问
				if (type == 0) {
					vmf.setTemplate("/template/notice/noticeRank_h.htm");
					vmf.save(NOTICEDIR + "hrank_" + current + ".htm");
					System.out.println("write page : " + NOTICEDIR + "hrank_" + current + ".html");
				} else if (type == 1) {
					vmf.setTemplate("/template/notice/noticeRank_q.htm");
					vmf.save(NOTICEDIR + "qrank_" + current + ".htm");
					System.out.println("write page : " + NOTICEDIR + "qrank_" + current + ".html");
				} else if (type == 2) {
					vmf.setTemplate("/template/notice/noticeRank_m.htm");
					vmf.save(NOTICEDIR + "mrank_" + current + ".htm");
					System.out.println("write page : " + NOTICEDIR + "mrank_" + current + ".html");
				}
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating discount stock html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void flushIndustryList() {
		List<String> industryList = analyzerDao.getAllIndustry();
		Map<String, List<Analyzer>> map = new HashMap<String, List<Analyzer>>();
		for (String industry : industryList) {
			List<Analyzer> analyzers = analyzerDao.getAnalyzersByIndustry(industry, 0, 5);
			map.put(industry, analyzers);
		}
		try {
			VMFactory vmf = new VMFactory();
			vmf.put("industryList", industryList);
			vmf.put("analyzerMap", map);
			vmf.setTemplate("/template/industryList.htm");
			vmf.save(ADMINDIR + "industry" + ".html");
			System.out.println("write page : " + ADMINDIR + "industry" + ".html");
		} catch (Exception e) {
			System.out.println("===> exception !!");
			System.out.println("While generating flushIndustryList --> GET ERROR MESSAGE: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		HtmlFlusher flusher = (HtmlFlusher) ContextFactory.getBean("htmlFlush");
		//		flusher.flushFinancialReportLab();
		//		AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
		//		List<Analyzer> analyzerList = analyzerDao.getStarAnalyzers();
		//		System.out.println("star analyzerList size:" + analyzerList.size());
		//		List<Analyzer> unstarAnalyzers = analyzerDao.getUnStarAnalyzers();
		//		System.out.println("unstarAnalyzers size:" + unstarAnalyzers);
		//		for (Analyzer analyzer : unstarAnalyzers) {
		//			System.out.println("unstarAnalyzers name:" + analyzer.getName());
		//			flusher.flushOneGuruDetail(analyzer, analyzerList);
		//		}
		//		flusher.flushStarGuruDetail();
		//		flusher.flushAnalyzerRank();
		flusher.flushReportLab();
		flusher.flushStockResearch();
		flusher.flushStockAgency();
		//		flusher.flushStarOnSale();
		//		flusher.flushNotice();
		//				flusher.flushStarOnSale(false);
		//		flusher.flushStarOnSale(true, 4);
		//		flusher.flushStarOnSale(true, 3);
		//		flusher.flushStarOnSale(true, 2);
		//		flusher.flushStarOnSale(true, 1);
		//		flusher.flushStarOnSale(false, 4);
		//		flusher.flushStarOnSale(false, 3);
		//		flusher.flushStarOnSale(false, 2);
		//		flusher.flushStarOnSale(false, 1);
		//		flusher.flushNoticeRank(0);
		//				flusher.flushAnalyzerRank();
		//		flusher.flushStockResearch();
		//		flusher.flushStockAgency();
		//		flusher.flushNotice();
		//		flusher.flushStarGuruDetail();
		//		flusher.flushAnalyzerRank();
		//		flusher.flushStarOnSale(true);
		//		flusher.flushStarOnSale(false);
		//		Analyzer analyzer = (Analyzer) flusher.getAnalyzerDao().select("6IHTNVCA");
		//		System.out.println("analyzer : " + analyzer.getSuccessratio());
		//				flusher.flushOneSuccess(analyzer);
		//		flusher.flushSuccessRank();
		//		flusher.flushLiveStatic();
		//		flusher.flushMasterInfo();

		//				flusher.flushNotice();
		flusher.flushNoticeRank();
		flusher.flushIndex();
		//		flusher.flushNoticeRank(0);
		//		flusher.flushNoticeRank(1);
		//		flusher.flushNoticeRank(2);
		//		flusher.flushFinancialReportLab();
		//		flusher.flushSuccessRank();
		//		flusher.flushArticleList(0);
		//		flusher.flushArticleList(1);
		//		flusher.flushArticleList(2);
		//		flusher.flushArticleList(3);
		//		flusher.flushIndustryList();
		System.exit(0);
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
