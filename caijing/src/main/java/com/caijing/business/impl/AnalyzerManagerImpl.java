package com.caijing.business.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.business.AnalyzerManager;
import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.RecommendSuccessDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.RecommendSuccess;
import com.caijing.util.DateTools;
import com.caijing.util.GradeUtil;
import com.caijing.util.ServerUtil;

public class AnalyzerManagerImpl implements AnalyzerManager {

	@Autowired
	@Qualifier("analyzerDao")
	AnalyzerDao analyzerDao = null;

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	@Autowired
	@Qualifier("recommendSuccessDao")
	private RecommendSuccessDao recommendSuccessDao = null;

	public RecommendSuccessDao getRecommendSuccessDao() {
		return recommendSuccessDao;
	}

	public void setRecommendSuccessDao(RecommendSuccessDao recommendSuccessDao) {
		this.recommendSuccessDao = recommendSuccessDao;
	}

	private HashMap<String, Analyzer> analyzerMap = new HashMap<String, Analyzer>();

	public AnalyzerDao getAnalyzerDao() {
		return analyzerDao;
	}

	public void setAnalyzerDao(AnalyzerDao analyzerDao) {
		this.analyzerDao = analyzerDao;
	}

	public RecommendStockDao getRecommendStockDao() {
		return recommendStockDao;
	}

	public void setRecommendStockDao(RecommendStockDao recommendStockDao) {
		this.recommendStockDao = recommendStockDao;
	}

	public void afterPropertiesSet() throws Exception {
		List<Analyzer> analyzers = analyzerDao.getAllAnalyzers();
		for (Analyzer analyzer : analyzers) {
			analyzerMap.put(analyzer.getName(), analyzer);
		}
	}

	@Override
	public void fetchAnalyzersFromSA(String saname) {
		List<String> anames = recommendStockDao.getDistinctAnalyzersBySaname(saname);
		HashSet<String> nameSet = new HashSet<String>();
		for (String aname : anames) {
			String[] strs = aname.split("\\s|,");
			for (String str : strs) {
				String analyzer = str.replaceAll("[^\u4e00-\u9fa5]", "");
				if (!nameSet.contains(analyzer)) {
					nameSet.add(analyzer);
				}
			}
		}
		List<Analyzer> analyzers = analyzerDao.getAnalyzersByAgency(saname);
		HashSet<String> existNameSet = new HashSet<String>();
		for (Analyzer analyzer : analyzers) {
			if (!existNameSet.contains(analyzer.getName())) {
				existNameSet.add(analyzer.getName());
			}
		}
		for (Object str : nameSet.toArray()) {
			System.out.println("analyzer:" + str);
			if (!existNameSet.contains(str)) {
				Analyzer analyzer = new Analyzer();
				analyzer.setAid(ServerUtil.getid());
				analyzer.setAgency(saname);
				analyzer.setName((String) str);
				analyzer.setPtime(new Date());
				analyzer.setLmodify(new Date());
				analyzer.setIndustry("");
				analyzer.setLevel(0);
				analyzerDao.insert(analyzer);
			}
		}
	}

	@Override
	public void handleHistoryRecommendBySA(String saname) {
		List<Analyzer> analyzers = analyzerDao.getAnalyzersByAgency(saname);
		for (Analyzer analyzer : analyzers) {
			handleHistoryRecommendByAnalyzer(analyzer);
		}
	}

	public void handleHistoryRecommendByAnalyzer(Analyzer analyzer) {
		//ШЅжи
		HashSet<String> uniqSet = new HashSet<String>();
		List<RecommendStock> list = recommendStockDao.getRecommendStocksByAnalyzer(analyzer.getName(), 0, 1000);
		for (RecommendStock recommendstock : list) {
			if (GradeUtil.judgeStaus(recommendstock.getGrade()) == 2 && recommendstock.getObjectprice() > 0) {
				if (recommendstock.getCreatedate() == null) {
					continue;
				}
				RecommendSuccess recommend = new RecommendSuccess();
				recommend.setAid(analyzer.getAid());
				recommend.setAname(analyzer.getName());
				recommend.setStockcode(recommendstock.getStockcode());
				recommend.setStockname(recommendstock.getStockname());
				recommend.setObjectprice(recommendstock.getObjectprice());
				try {
					recommend.setRecommenddate(DateTools.parseShortDate(recommendstock.getCreatedate()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				recommend.setReportid(recommendstock.getReportid());
				String key = analyzer.getAid() + recommendstock.getStockcode() + recommendstock.getCreatedate();
				if (!uniqSet.contains(key)) {
					recommendSuccessDao.insert(recommend);
					uniqSet.add(key);
				}
			}
		}
	}
}
