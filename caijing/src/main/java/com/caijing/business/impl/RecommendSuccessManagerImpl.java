package com.caijing.business.impl;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import com.caijing.business.RecommendSuccessManager;
import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.AnalyzerSuccessDao;
import com.caijing.dao.RecommendSuccessDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.AnalyzerSuccess;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.RecommendSuccess;
import com.caijing.domain.StockEarn;
import com.caijing.util.DateTools;
import com.caijing.util.GradeUtil;

public class RecommendSuccessManagerImpl implements RecommendSuccessManager, InitializingBean {
	public AnalyzerDao getAnalyzerDao() {
		return analyzerDao;
	}

	public void setAnalyzerDao(AnalyzerDao analyzerDao) {
		this.analyzerDao = analyzerDao;
	}

	public StockEarnDao getStockEarnDao() {
		return stockEarnDao;
	}

	public void setStockEarnDao(StockEarnDao stockEarnDao) {
		this.stockEarnDao = stockEarnDao;
	}

	public RecommendSuccessDao getRecommendSuccessDao() {
		return recommendSuccessDao;
	}

	public void setRecommendSuccessDao(RecommendSuccessDao recommendSuccessDao) {
		this.recommendSuccessDao = recommendSuccessDao;
	}

	public AnalyzerSuccessDao getAnalyzerSuccessDao() {
		return analyzerSuccessDao;
	}

	public void setAnalyzerSuccessDao(AnalyzerSuccessDao analyzerSuccessDao) {
		this.analyzerSuccessDao = analyzerSuccessDao;
	}

	private AnalyzerDao analyzerDao = null;
	private StockEarnDao stockEarnDao = null;
	private RecommendSuccessDao recommendSuccessDao = null;
	private AnalyzerSuccessDao analyzerSuccessDao = null;

	private HashMap<String, Analyzer> analyzerMap = new HashMap<String, Analyzer>();

	@Override
	public void afterPropertiesSet() throws Exception {
		List<Analyzer> analyzers = analyzerDao.getAllAnalyzers();
		for (Analyzer analyzer : analyzers) {
			analyzerMap.put(analyzer.getName(), analyzer);
		}
	}

	@Override
	public void processValidatedRecommendSuccess(Date endDate) {
		List<RecommendSuccess> lists = recommendSuccessDao.getUnvalidateRecommendsBefore(endDate);
		System.out.println("Unvalidate RecommendSuccess before date:" + DateTools.transformYYYYMMDDDate(endDate)
				+ "  size:" + lists.size());
		HashSet<String> analyzerSet = new HashSet<String>();
		Calendar cal = Calendar.getInstance();
		for (RecommendSuccess recommendsuccess : lists) {
			System.out.println("recommend date" + DateTools.transformYYYYMMDDDate(recommendsuccess.getRecommenddate()));
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
			if (!analyzerSet.contains(recommendsuccess.getAid())) {
				analyzerSet.add(recommendsuccess.getAid());
			}

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

		//只对有改变的数据增加成功率
		for (String aid : analyzerSet) {
			Analyzer analyzer = (Analyzer) analyzerDao.select(aid);
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

			//更新当前年段分析师成功率
			String year = DateTools.getYear(new Date());
			success = recommendSuccessDao.getRecommendSuccessCountByAidDuring(analyzer.getAid(), year + "-01-01", year
					+ "-12-31");
			total = recommendSuccessDao.getTotalRecommendCountByAidDuring(analyzer.getAid(), year + "-01-01", year
					+ "-12-31");

			AnalyzerSuccess asuccess = analyzerSuccessDao.getOneAnalyzerSuccess(aid, year);
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
	}

	@Override
	public void extractRecommendSuccess(RecommendStock rs) {
		if (rs.getCreatedate() == null || rs.getGrade() == null) {
			return;
		}
		if (rs.getCreatedate().length() < 8 || GradeUtil.judgeStaus(rs.getGrade()) != 2 || rs.getObjectprice() <= 0) {
			return;
		}
		String[] names = rs.getAname().split("\\s|,|，");
		for (String name : names) {
			name = name.replaceAll("[^\u4e00-\u9fa5]", "");
			if (analyzerMap.containsKey(name)) {
				String aid = analyzerMap.get(name).getAid();
				RecommendSuccess recommend = new RecommendSuccess();
				recommend.setAid(aid);
				recommend.setAname(name);
				recommend.setStockcode(rs.getStockcode());
				recommend.setStockname(rs.getStockname());
				recommend.setObjectprice(rs.getObjectprice());
				try {
					recommend.setRecommenddate(DateTools.parseShortDate(rs.getCreatedate()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				recommend.setReportid(rs.getReportid());
				try {
					recommendSuccessDao.insert(recommend);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
