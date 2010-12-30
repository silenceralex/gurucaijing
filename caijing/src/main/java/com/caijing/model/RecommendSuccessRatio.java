package com.caijing.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.caijing.dao.GroupStockDao;
import com.caijing.dao.RecommendSuccessDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.GroupStock;
import com.caijing.domain.RecommendSuccess;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.caijing.util.FloatUtil;

public class RecommendSuccessRatio {

	private GroupStockDao groupStockDao = null;
	private StockEarnDao stockEarnDao = null;
	private RecommendSuccessDao recommendSuccessDao = null;

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
				float endprice = stockEarnDao.getNearPriceByCodeDate(stock.getStockcode(), cal.getTime());
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
				System.out.println("judge date after:" + judgedate + "  endprice:" + endprice);
				if (endprice > stock.getObjectprice()) {
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
			System.out.println("recommend date" + DateTools.transformYYYYMMDDDate(recommendsuccess.getRecommenddate()));
			cal.setTime(recommendsuccess.getRecommenddate());
			cal.add(Calendar.MONTH, 6);
			float endprice = stockEarnDao.getNearPriceByCodeDate(recommendsuccess.getStockcode(), cal.getTime());
			String judgedate = DateTools.transformYYYYMMDDDate(cal.getTime());
			System.out.println("judge date after:" + judgedate + "  endprice:" + endprice);
			if (endprice >= recommendsuccess.getObjectprice()) {
				recommendsuccess.setIsAchieved(1);
				recommendsuccess.setValidate(cal.getTime());
				recommendSuccessDao.updateIsAchieved(recommendsuccess);
				System.out.println("analyzer:" + recommendsuccess.getAname() + "  recommend stock"
						+ recommendsuccess.getStockname() + "  success!");
			} else {
				recommendsuccess.setIsAchieved(0);
				recommendsuccess.setValidate(cal.getTime());
				recommendSuccessDao.updateIsAchieved(recommendsuccess);
				System.out.println("analyzer:" + recommendsuccess.getAname() + "  recommend stock"
						+ recommendsuccess.getStockname() + "  Failed!");
			}
		}
	}

	public static void main(String[] args) {
		ApplicationContext context = ContextFactory.getApplicationContext();
		GroupStockDao groupStockDao = (GroupStockDao) context.getBean("groupStockDao");
		RecommendSuccessDao recommendSuccessDao = (RecommendSuccessDao) context.getBean("recommendSuccessDao");
		StockEarnDao stockEarnDao = (StockEarnDao) context.getBean("stockEarnDao");
		RecommendSuccessRatio ratio = new RecommendSuccessRatio();
		ratio.setGroupStockDao(groupStockDao);
		ratio.setStockEarnDao(stockEarnDao);
		ratio.setRecommendSuccessDao(recommendSuccessDao);
		//		List<Analyzer> analyzers = analyzerDao.getAnalyzersByAgency("安信证券");
		//		for (Analyzer analyzer : analyzers) {
		//			float f = ratio.getSuccessRatio(analyzer.getAid());
		//
		//			System.out.println("analyzer:" + analyzer.getName() + "  Success recommend ratio:" + f + "%");
		//		}
		//		float f = ratio.getSuccessRatio("6NMO0U38");
		//		System.out.println("analyzer:" + "杨建海" + "  Success recommend ratio:" + f + "%");

		//		AnalyzerManager analyzerManager = (AnalyzerManager) context.getBean("analyzerManager");
		//		analyzerManager.handleHistoryRecommendBySA("安信证券");
		ratio.handleHistorySuccess();
	}
}
