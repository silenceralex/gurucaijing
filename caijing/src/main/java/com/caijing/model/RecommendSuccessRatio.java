package com.caijing.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.caijing.business.AnalyzerManager;
import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.AnalyzerSuccessDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.RecommendSuccessDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.AnalyzerSuccess;
import com.caijing.domain.RecommendSuccess;
import com.caijing.domain.StockEarn;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;

public class RecommendSuccessRatio {

	private GroupStockDao groupStockDao = null;
	private AnalyzerDao analyzerDao = null;
	private StockEarnDao stockEarnDao = null;
	private RecommendSuccessDao recommendSuccessDao = null;

	private AnalyzerSuccessDao analyzerSuccessDao = null;

	public static String[] years = { "2009", "2010", "2011" };

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

	//	public float getSuccessRatio(String aid) {
	//		List<GroupStock> stocks = groupStockDao.getNameAndCodeByAid(aid);
	//		float total = 0;
	//		float success = 0;
	//		for (GroupStock stock : stocks) {
	//			Calendar cal = Calendar.getInstance();
	//			if (stock.getObjectprice() != 0) {
	//
	//				cal.setTime(stock.getIntime());
	//				System.out.println("recommend date" + DateTools.transformYYYYMMDDDate(stock.getIntime()));
	//				System.out.println("stock:" + stock.getStockname() + stock.getStockcode() + "  objectprice :"
	//						+ stock.getObjectprice());
	//				cal.add(Calendar.MONTH, 6);
	//				if (cal.getTime() == null) {
	//					System.out.println("cal.getTime() is null!");
	//					continue;
	//				}
	//				if (cal.getTime().after(new Date())) {
	//					System.out.println("cal.getTime() after now!");
	//					continue;
	//				}
	//				total++;
	//				StockEarn se = stockEarnDao.getNearPriceByCodeDate(stock.getStockcode(), cal.getTime());
	//
	//				if (se.getPrice() > stock.getObjectprice()) {
	//					success++;
	//				}
	//			}
	//		}
	//		float f = FloatUtil.getTwoDecimal((success / total) * 100);
	//		System.out.println("total:" + total + "   success:" + success);
	//		System.out.println("Success recommend ratio:" + f + "%");
	//		return f;
	//	}

	public void handleHistorySuccessByAnalyzer(Analyzer analyzer) {
		List<RecommendSuccess> list = recommendSuccessDao.getUnvalidateRecommendsByAid(analyzer.getAid());
		System.out.println("list size:" + list.size());
		handleHistorySuccessByList(list);
		int success = recommendSuccessDao.getRecommendSuccessCountByAid(analyzer.getAid());
		int total = recommendSuccessDao.getTotalRecommendCountByAid(analyzer.getAid());
		if (success != 0 && total != 0) {
			float successratio = ((float) success / total) * 100;
			System.out.println("analyzer:" + analyzer.getName() + "  Success recommend ratio:" + successratio + "%");
			analyzer.setLmodify(new Date());
			analyzer.setSuccessratio(successratio);
			analyzerDao.updateSuccessRatio(analyzer);
		} else {
			analyzer.setLmodify(new Date());
			analyzer.setSuccessratio(0);
			analyzerDao.updateSuccessRatio(analyzer);
		}
	}

	public void handleHistorySuccessByList(List<RecommendSuccess> list) {
		Calendar cal = Calendar.getInstance();
		for (RecommendSuccess recommendsuccess : list) {
			if (recommendsuccess.getIsAchieved() == 2) {
				System.out.println("recommend date"
						+ DateTools.transformYYYYMMDDDate(recommendsuccess.getRecommenddate()));
				cal.setTime(recommendsuccess.getRecommenddate());
				cal.add(Calendar.MONTH, 6);

				StockEarn se = stockEarnDao.getNearPriceByCodeDate(recommendsuccess.getStockcode(), cal.getTime());
				//��֤���ڵ�ǰ����֮����ߵ�ǰ��δ��
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
			}
		}

	}

	public void handleHistorySuccessBySA(String saname) {
		List<Analyzer> analyzers = analyzerDao.getAnalyzersByAgency(saname);
		for (Analyzer analyzer : analyzers) {
			handleHistorySuccessByAnalyzer(analyzer);
			for (String year : years) {
				handlerOneAnalyzerYearSuccess(analyzer, year);
			}
		}
	}

	private void handlerOneAnalyzerYearSuccess(Analyzer analyzer, String year) {
		String startDate = year + "-01-01";
		String endDate = year + "-12-31";
		int success = recommendSuccessDao.getRecommendSuccessCountByAidDuring(analyzer.getAid(), startDate, endDate);
		int total = recommendSuccessDao.getTotalRecommendCountByAidDuring(analyzer.getAid(), startDate, endDate);
		if (total == 0) {
			return;
		}
		AnalyzerSuccess asuccess = analyzerSuccessDao.getOneAnalyzerSuccess(analyzer.getAid(), year);
		if (asuccess == null) {
			asuccess = new AnalyzerSuccess();
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
		} else {
			asuccess.setTotal(total);
			asuccess.setSuccess(success);
			float successratio = 0;
			if (success != 0 && total != 0) {
				successratio = ((float) success / total) * 100;
			}
			asuccess.setSuccessratio(successratio);
			analyzerSuccessDao.update(asuccess);
		}
	}

	public void handleYearSuccess(String saname, String year) {
		List<Analyzer> analyzers = analyzerDao.getAnalyzersByAgency(saname);
		for (Analyzer analyzer : analyzers) {
			handlerOneAnalyzerYearSuccess(analyzer, year);
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
		//		List<Analyzer> analyzers = analyzerDao.getAnalyzersByAgency("����֤ȯ");
		//		for (Analyzer analyzer : analyzers) {
		//			float f = ratio.getSuccessRatio(analyzer.getAid());
		//
		//			System.out.println("analyzer:" + analyzer.getName() + "  Success recommend ratio:" + f + "%");
		//		}
		//		float f = ratio.getSuccessRatio("6NMO0U38");
		//		System.out.println("analyzer:" + "���" + "  Success recommend ratio:" + f + "%");

		AnalyzerManager analyzerManager = (AnalyzerManager) context.getBean("analyzerManager");
		analyzerManager.handleHistoryRecommendBySA("�������");
		analyzerManager.handleHistoryRecommendBySA("����֤ȯ");
		analyzerManager.handleHistoryRecommendBySA("��̩����");
		analyzerManager.handleHistoryRecommendBySA("�㷢֤ȯ");
		ratio.handleHistorySuccessBySA("�������");
		ratio.handleHistorySuccessBySA("����֤ȯ");
		ratio.handleHistorySuccessBySA("��̩����");
		ratio.handleHistorySuccessBySA("�㷢֤ȯ");
		//		ratio.handleHistorySuccess();
		//		String anames = "ͯѱ ���� ������ ղ���� ���ٽ� ��Դ ̷־�� ��ѧ�� ��ʤ�� ������ ����� ʯ�� ֣�ι� ��С�� ������ ���� ���ĸ� ����";
		//		String[] names = anames.split(" ");
		//		for (String name : names) {
		//			Analyzer analyzer = analyzerDao.getAnalyzerByName(name);
		//			//		Analyzer analyzer = analyzerDao.getAnalyzerByName("�Խ��");
		//			//			ratio.getRecommendSuccessDao().deleteByAid(analyzer.getAid());
		//			//		analyzerManager.handleHistoryRecommendByAnalyzer(analyzer);
		//			ratio.handleHistorySuccessByAnalyzer(analyzer);
		//		}

		//		ratio.handleYearSuccess("����֤ȯ", "2009");
		System.exit(0);
	}
}
