package com.caijing.flush;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.AnalyzerSuccessDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.AnalyzerSuccess;
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
			try {
				VMFactory vmf = new VMFactory();
				vmf.setTemplate("/template/anayzerHistorySucRank.htm");
				vmf.put("dateTools", dateTools);
				vmf.put("floatUtil", floatUtil);
				vmf.put("year", year);
				vmf.put("start", (current - 1) * 20);
				vmf.put("current", current);
				vmf.put("page", 2);
				vmf.put("analyzerList", anlyzers);
				vmf.save(ADMINDIR + year + "/successrank_" + current + ".html");
				System.out.println("write page : " + ADMINDIR + year + "/successrank_" + current + ".html");
			} catch (Exception e) {
				System.out.println("===> exception !!");
				System.out.println("While generating discount stock html --> GET ERROR MESSAGE: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		AnalyzerSuccessFlusher flusher = new AnalyzerSuccessFlusher();
		AnalyzerSuccessDao analyzerSuccessDao = (AnalyzerSuccessDao) ContextFactory.getBean("analyzerSuccessDao");
		flusher.setAnalyzerSuccessDao(analyzerSuccessDao);
		flusher.flushHistorySuccessRank("2009");
		flusher.flushHistorySuccessRank("2010");
	}
}
