package com.caijing.flush;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.AnalyzerSuccessDao;
import com.caijing.dao.GroupEarnDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.AnalyzerSuccess;
import com.caijing.domain.GroupEarn;
import com.caijing.domain.StockEarn;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.caijing.util.FloatUtil;

public class AnalyzerSuccessFlusher {
	public static String ADMINDIR = "/home/html/analyzer/";

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	@Autowired
	@Qualifier("analyzerSuccessDao")
	private AnalyzerSuccessDao analyzerSuccessDao = null;

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

	public void flushAnalyzerYear(Analyzer analyzer, String year, boolean isStart) {
		FloatUtil floatUtil = new FloatUtil();
		List<Analyzer> analyzerList = analyzerDao.getStarAnalyzers();
		String endDate = year + "-12-31";
		String startDay = year + "-01-01";
		try {
			//生成分析师intro页面
			String aid = analyzer.getAid();
			Date startDate = null;
			float startprice = 0;
			float startweight = 100;
			try {
				if (isStart) {
					startDate = groupStockDao.getEarliestIntimeByAidFrom(aid, DateTools.parseYYYYMMDDDate(startDay));
					startprice = stockEarnDao.getStockEarnByCodeDate("000300",
							DateTools.transformYYYYMMDDDate(startDate)).getPrice();
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

			VMFactory introvmf = new VMFactory();
			introvmf.setTemplate("/template/starintro_y.htm");
			introvmf.put("floatUtil", floatUtil);
			introvmf.put("dateTools", new DateTools());
			introvmf.put("analyzer", analyzer);
			introvmf.put("year", year);
			introvmf.put("ratio", ratio);
			introvmf.put("startweight", startweight);
			introvmf.put("analyzerList", analyzerList);
			introvmf.put("weightList", weightList);
			introvmf.put("startprice", startprice);
			introvmf.put("priceList", priceList);
			System.out.println("weightList size :" + weightList.size());
			System.out.println("priceList size :" + priceList.size());
			introvmf.save(ADMINDIR + "static/" + aid + "_" + year + "_intro.html");
			System.out.println("write page : " + ADMINDIR + aid + "_" + year + "_intro.html");
		} catch (Exception e) {
			System.out.println("===> exception !! ：" + analyzer.getAid() + "  name : " + analyzer.getName());
			System.out.println("While generating stars stock html --> GET ERROR MESSAGE: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		AnalyzerSuccessFlusher flusher = new AnalyzerSuccessFlusher();
		AnalyzerSuccessDao analyzerSuccessDao = (AnalyzerSuccessDao) ContextFactory.getBean("analyzerSuccessDao");
		GroupStockDao groupStockDao = (GroupStockDao) ContextFactory.getBean("groupStockDao");
		GroupEarnDao groupEarnDao = (GroupEarnDao) ContextFactory.getBean("groupEarnDao");
		StockEarnDao stockEarnDao = (StockEarnDao) ContextFactory.getBean("stockEarnDao");
		AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
		flusher.setAnalyzerSuccessDao(analyzerSuccessDao);
		flusher.setAnalyzerDao(analyzerDao);
		flusher.setGroupEarnDao(groupEarnDao);
		flusher.setStockEarnDao(stockEarnDao);
		flusher.setGroupStockDao(groupStockDao);

		//		flusher.flushHistorySuccessRank("2009");
		//		flusher.flushHistorySuccessRank("2010");
		Analyzer analyzer = analyzerDao.getAnalyzerByName("赵金厚");
		flusher.flushAnalyzerYear(analyzer, "2009", true);
		flusher.flushAnalyzerYear(analyzer, "2010", false);
		flusher.flushAnalyzerYear(analyzer, "2011", false);
		System.exit(0);
	}
}
