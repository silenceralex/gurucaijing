package com.caijing.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.AnalyzerSuccessDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.RecommendSuccessDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.AnalyzerSuccess;
import com.caijing.domain.GroupStock;
import com.caijing.domain.RecommendSuccess;
import com.caijing.domain.StockEarn;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.caijing.util.FloatUtil;

public class RecommendSuccessRatio {

	private GroupStockDao groupStockDao = null;
	private AnalyzerDao analyzerDao = null;
	private StockEarnDao stockEarnDao = null;
	private RecommendSuccessDao recommendSuccessDao = null;

	private AnalyzerSuccessDao analyzerSuccessDao = null;

	public AnalyzerSuccessDao getAnalyzerSuccessDao() {
		return analyzerSuccessDao;
	}

	public void setAnalyzerSuccessDao(AnalyzerSuccessDao analyzerSuccessDao) {
		this.analyzerSuccessDao = analyzerSuccessDao;
	}

	public AnalyzerDao getAnalyzerDao() {
		return analyzerDao;
	}

	public void setAnalyzerDao(AnalyzerDao analyzerDao) {
		this.analyzerDao = analyzerDao;
	}

	public RecommendSuccessDao getRecommendSuccessDao() {
		return recommendSuccessDao;
	}

	public void setRecommendSuccessDao(RecommendSuccessDao recommendSuccessDao) {
		this.recommendSuccessDao = recommendSuccessDao;
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

	public float getSuccessRatio(String aid) {
		List<GroupStock> stocks = groupStockDao.getNameAndCodeByAid(aid);
		float total = 0;
		float success = 0;
		StockPrice sp = new StockPrice();
		for (GroupStock stock : stocks) {
			Calendar cal = Calendar.getInstance();
			if (stock.getObjectprice() != 0) {

				cal.setTime(stock.getIntime());
				System.out.println("recommend date" + DateTools.transformYYYYMMDDDate(stock.getIntime()));
				System.out.println("stock:" + stock.getStockname() + stock.getStockcode() + "  objectprice :"
						+ stock.getObjectprice());
				cal.add(Calendar.MONTH, 6);
				if (cal.getTime() == null) {
					System.out.println("cal.getTime() is null!");
					continue;
				}
				if (cal.getTime().after(new Date())) {
					System.out.println("cal.getTime() after now!");
					continue;
				}
				total++;
				StockEarn se = stockEarnDao.getNearPriceByCodeDate(stock.getStockcode(), cal.getTime());
				String judgedate = DateTools.transformYYYYMMDDDate(cal.getTime());
				//				stockEarnDao
				//				System.out.println("judge date before:" + judgedate);
				//				if (cal.get(cal.DAY_OF_WEEK) == cal.SATURDAY) {
				//					System.out.println("judgedate is SATURDAY!");
				//					cal.add(Calendar.DAY_OF_MONTH, 2);
				//				} else if (cal.get(cal.DAY_OF_WEEK) == cal.SUNDAY) {
				//					System.out.println("judgedate is SUNDAY!");
				//					cal.add(Calendar.DAY_OF_MONTH, 1);
				//				}
				//				judgedate = DateTools.transformYYYYMMDDDate(cal.getTime());
				//				float endprice = sp.fetchhq(stock.getStockcode(), judgedate).getEndprice();
				//				System.out.println("judge date after:" + judgedate + "  endprice:" + se.getPrice());
				if (se.getPrice() > stock.getObjectprice()) {
					success++;
				}
			}
		}
		float f = FloatUtil.getTwoDecimal((success / total) * 100);
		System.out.println("total:" + total + "   success:" + success);
		System.out.println("Success recommend ratio:" + f + "%");
		return f;
	}

	public void handleHistorySuccess() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -6);
		List<RecommendSuccess> list = recommendSuccessDao.getRecommendsBefore(cal.getTime());
		for (RecommendSuccess recommendsuccess : list) {
			if (recommendsuccess.getIsAchieved() == 2) {
				System.out.println("recommend date"
						+ DateTools.transformYYYYMMDDDate(recommendsuccess.getRecommenddate()));
				cal.setTime(recommendsuccess.getRecommenddate());
				cal.add(Calendar.MONTH, 6);

				StockEarn se = stockEarnDao.getNearPriceByCodeDate(recommendsuccess.getStockcode(), cal.getTime());
				//验证期在当前日期之后或者当前尚未打开
				if (cal.getTime().after(new Date()) || se == null) {
					continue;
				}
				String judgedate = DateTools.transformYYYYMMDDDate(cal.getTime());
				System.out.println("judgedate date" + judgedate);
				List<StockEarn> ses = stockEarnDao.getRatiosByCodeAndPeriod(recommendsuccess.getStockcode(),
						recommendsuccess.getRecommenddate(), se.getDate());
				float gain = 1;
				for (StockEarn stockEarn : ses) {
					gain = gain * (1 + stockEarn.getRatio() / 100);
				}
				StockEarn seNear = stockEarnDao.getFormerNearPriceByCodeDate(recommendsuccess.getStockcode(),
						recommendsuccess.getRecommenddate());
				if (seNear == null) {
					System.out.println("###################### code:" + recommendsuccess.getStockcode() + " date:"
							+ recommendsuccess.getRecommenddate());
					continue;
				}
				float recommendprice = seNear.getPrice();
				float judgeprice = recommendprice * gain;
				System.out.println("analyzer:" + recommendsuccess.getAname() + "  recommend stock:"
						+ recommendsuccess.getStockname() + recommendsuccess.getRecommenddate() + "  recommendprice:"
						+ recommendprice + "  gain:" + gain + "  objectprice:" + recommendsuccess.getObjectprice()
						+ "  judgeprice:" + judgeprice);

				if (judgeprice >= recommendsuccess.getObjectprice()) {
					recommendsuccess.setIsAchieved(1);
					recommendsuccess.setValidate(se.getDate());
					recommendsuccess.setValidateprice(judgeprice);
					recommendSuccessDao.updateIsAchieved(recommendsuccess);
					System.out.println("analyzer:" + recommendsuccess.getAname() + "  recommend stock"
							+ recommendsuccess.getStockname() + "  success!");
				} else {
					recommendsuccess.setIsAchieved(0);
					recommendsuccess.setValidate(se.getDate());
					recommendsuccess.setValidateprice(judgeprice);
					recommendSuccessDao.updateIsAchieved(recommendsuccess);
					System.out.println("analyzer:" + recommendsuccess.getAname() + "  recommend stock"
							+ recommendsuccess.getStockname() + "  Failed!");
				}
				System.out.println("judge date after:" + judgedate + "  endprice:" + se.getPrice());
				//			if (se.getPrice() >= recommendsuccess.getObjectprice()) {
				//				recommendsuccess.setIsAchieved(1);
				//				recommendsuccess.setValidate(se.getDate());
				//				recommendsuccess.setValidateprice(se.getPrice());
				//				recommendSuccessDao.updateIsAchieved(recommendsuccess);
				//				System.out.println("analyzer:" + recommendsuccess.getAname() + "  recommend stock"
				//						+ recommendsuccess.getStockname() + "  success!");
				//			} else {
				//				recommendsuccess.setIsAchieved(0);
				//				recommendsuccess.setValidate(se.getDate());
				//				recommendsuccess.setValidateprice(se.getPrice());
				//				recommendSuccessDao.updateIsAchieved(recommendsuccess);
				//				System.out.println("analyzer:" + recommendsuccess.getAname() + "  recommend stock"
				//						+ recommendsuccess.getStockname() + "  Failed!");
				//			}
			}
		}

		List<Analyzer> analyzers = analyzerDao.getAllAnalyzers();
		//		List<Analyzer> analyzers = analyzerDao.getAnalyzersByAgency("申银万国");
		for (Analyzer analyzer : analyzers) {
			int success = recommendSuccessDao.getRecommendSuccessCountByAid(analyzer.getAid());
			int total = recommendSuccessDao.getTotalRecommendCountByAid(analyzer.getAid());
			if (success != 0 && total != 0) {
				float successratio = ((float) success / total) * 100;
				System.out
						.println("analyzer:" + analyzer.getName() + "  Success recommend ratio:" + successratio + "%");
				analyzer.setLmodify(new Date());
				analyzer.setSuccessratio(successratio);
				analyzerDao.updateSuccessRatio(analyzer);
			} else {
				analyzer.setLmodify(new Date());
				analyzer.setSuccessratio(0);
				analyzerDao.updateSuccessRatio(analyzer);
			}
		}
	}

	public void handleYearSuccess(String saname, String year) {
		String startDate = year + "-01-01";
		String endDate = year + "-12-31";
		List<Analyzer> analyzers = analyzerDao.getAnalyzersByAgency(saname);
		for (Analyzer analyzer : analyzers) {
			int success = recommendSuccessDao
					.getRecommendSuccessCountByAidDuring(analyzer.getAid(), startDate, endDate);
			int total = recommendSuccessDao.getTotalRecommendCountByAidDuring(analyzer.getAid(), startDate, endDate);
			AnalyzerSuccess asuccess = new AnalyzerSuccess();
			asuccess.setAid(analyzer.getAid());
			asuccess.setAname(analyzer.getName());
			asuccess.setTotal(total);
			asuccess.setSuccess(success);
			asuccess.setYear(year);
			float successratio = 0;
			if (success != 0 && total != 0) {
				successratio = ((float) success / total) * 100;
			}
			asuccess.setSuccessratio(successratio);
			analyzerSuccessDao.insert(asuccess);
		}
	}

	public static void main(String[] args) {
		ApplicationContext context = ContextFactory.getApplicationContext();
		GroupStockDao groupStockDao = (GroupStockDao) context.getBean("groupStockDao");
		RecommendSuccessDao recommendSuccessDao = (RecommendSuccessDao) context.getBean("recommendSuccessDao");
		StockEarnDao stockEarnDao = (StockEarnDao) context.getBean("stockEarnDao");
		AnalyzerDao analyzerDao = (AnalyzerDao) context.getBean("analyzerDao");
		AnalyzerSuccessDao analyzerSuccessDao = (AnalyzerSuccessDao) context.getBean("analyzerSuccessDao");
		RecommendSuccessRatio ratio = new RecommendSuccessRatio();
		ratio.setGroupStockDao(groupStockDao);
		ratio.setStockEarnDao(stockEarnDao);
		ratio.setRecommendSuccessDao(recommendSuccessDao);
		ratio.setAnalyzerDao(analyzerDao);
		ratio.setAnalyzerSuccessDao(analyzerSuccessDao);
		//		List<Analyzer> analyzers = analyzerDao.getAnalyzersByAgency("安信证券");
		//		for (Analyzer analyzer : analyzers) {
		//			float f = ratio.getSuccessRatio(analyzer.getAid());
		//
		//			System.out.println("analyzer:" + analyzer.getName() + "  Success recommend ratio:" + f + "%");
		//		}
		//		float f = ratio.getSuccessRatio("6NMO0U38");
		//		System.out.println("analyzer:" + "杨建海" + "  Success recommend ratio:" + f + "%");

		//		AnalyzerManager analyzerManager = (AnalyzerManager) context.getBean("analyzerManager");
		//		analyzerManager.handleHistoryRecommendBySA("申银万国");
		//		analyzerManager.handleHistoryRecommendBySA("招商证券");
		//		analyzerManager.handleHistoryRecommendBySA("国泰君安");
		//		analyzerManager.handleHistoryRecommendBySA("广发证券");

		//		ratio.handleHistorySuccess();
		ratio.handleYearSuccess("广发证券", "2009");
	}
}
