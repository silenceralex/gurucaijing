package com.caijing.model;

import java.util.HashSet;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.caijing.business.AnalyzerManager;
import com.caijing.business.GroupGainManager;
import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.AnalyzerSuccessDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.RecommendSuccessDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.RecommendStock;
import com.caijing.util.ContextFactory;
import com.caijing.util.LocalStorage;

/**
 * 处理历史研报相关的组合收益率和成功率的专用类，仅用于历史数据的重复处理
 * @author chenjun
 *
 */
public class HistoryHandler {
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

	public GroupGainManager getGroupGainManager() {
		return groupGainManager;
	}

	public void setGroupGainManager(GroupGainManager groupGainManager) {
		this.groupGainManager = groupGainManager;
	}

	public AnalyzerManager getAnalyzerManager() {
		return analyzerManager;
	}

	public void setAnalyzerManager(AnalyzerManager analyzerManager) {
		this.analyzerManager = analyzerManager;
	}

	public LocalStorage getStorage() {
		return storage;
	}

	public void setStorage(LocalStorage storage) {
		this.storage = storage;
	}

	public RecommendSuccessRatio getRatio() {
		return ratio;
	}

	public void setRatio(RecommendSuccessRatio ratio) {
		this.ratio = ratio;
	}

	public static String[] getAgencys() {
		return agencys;
	}

	public static void setAgencys(String[] agencys) {
		HistoryHandler.agencys = agencys;
	}

	private AnalyzerDao analyzerDao = null;
	private RecommendStockDao recommendStockDao = null;
	private GroupGainManager groupGainManager = null;
	private AnalyzerManager analyzerManager = null;
	private LocalStorage storage = null;
	private RecommendSuccessRatio ratio = null;
	private Caculater caculater = null;

	public Caculater getCaculater() {
		return caculater;
	}

	public void setCaculater(Caculater caculater) {
		this.caculater = caculater;
	}

	//"国泰君安", "招商证券", "安信证券", "广发证券", "国金证券", "国信证券", "长江证券", "华泰证券", "华泰联合", "光大证券",
	//	"中投证券", "中信建投", "申银万国" 

	private static String[] agencys = { "国泰君安", "招商证券", "安信证券", "广发证券", "国金证券", "国信证券", "长江证券", "华泰证券", "华泰联合", "光大证券",
			"中投证券", "中信建投", "申银万国" };

	//TODO 异常的处理
	public void dealOneAnalyzer(Analyzer analyzer) {
		List<RecommendStock> rstocks = recommendStockDao.getRecommendStocksByAnalyzerASC(analyzer.getName(), 0, 500);
		System.out.println("analyzer getName : " + analyzer.getName());
		System.out.println("rstocks size : " + rstocks.size());
		//去重,防止同一天的推荐数据入库
		HashSet<String> uniqSet = new HashSet<String>();
		for (RecommendStock rs : rstocks) {
			String key = analyzer.getAid() + rs.getStockcode() + rs.getCreatedate();
			if (!uniqSet.contains(key)) {
				uniqSet.add(key);
				try {
					groupGainManager.extractGroupStock(rs);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//TODO 处理所有历史研报收益率  
		caculater.processAllHistoryGain(analyzer.getAid());
		//		storage.processHistoryGroupEarn(analyzer.getAid());
		System.out.println("finish deal analyer : " + analyzer.getName());
	}

	public void processAllHistoryReport() {
		System.out.println("agencys size: " + agencys.length);
		for (String agency : agencys) {
			System.out.println("processing agency groupgain: " + agency);
			List<Analyzer> analyzers = analyzerDao.getAnalyzersByAgency(agency);
			for (Analyzer analyzer : analyzers) {
				dealOneAnalyzer(analyzer);
			}
			System.out.println("Finished processing agency groupgain: " + agency);
			System.out.println("Start processing agency successratio: " + agency);
			analyzerManager.handleHistoryRecommendBySA(agency);
			ratio.handleHistorySuccessBySA(agency);
			System.out.println("Finished processing agency successratio: " + agency);
		}
	}

	public static void main(String[] args) {
		ApplicationContext context = ContextFactory.getApplicationContext();
		GroupStockDao groupStockDao = (GroupStockDao) context.getBean("groupStockDao");
		RecommendSuccessDao recommendSuccessDao = (RecommendSuccessDao) context.getBean("recommendSuccessDao");
		RecommendStockDao recommendStockDao = (RecommendStockDao) context.getBean("recommendStockDao");
		StockEarnDao stockEarnDao = (StockEarnDao) context.getBean("stockEarnDao");
		AnalyzerDao analyzerDao = (AnalyzerDao) context.getBean("analyzerDao");
		AnalyzerSuccessDao analyzerSuccessDao = (AnalyzerSuccessDao) context.getBean("analyzerSuccessDao");
		AnalyzerManager analyzerManager = (AnalyzerManager) context.getBean("analyzerManager");
		LocalStorage storage = (LocalStorage) ContextFactory.getBean("localStorage");
		GroupGainManager groupGainManager = (GroupGainManager) ContextFactory.getBean("groupGainManager");
		Caculater caculater = (Caculater) ContextFactory.getBean("caculater");
		RecommendSuccessRatio ratio = new RecommendSuccessRatio();
		ratio.setGroupStockDao(groupStockDao);
		ratio.setStockEarnDao(stockEarnDao);
		ratio.setRecommendSuccessDao(recommendSuccessDao);
		ratio.setAnalyzerDao(analyzerDao);
		ratio.setAnalyzerSuccessDao(analyzerSuccessDao);

		HistoryHandler handler = new HistoryHandler();
		handler.setStorage(storage);
		handler.setAnalyzerDao(analyzerDao);
		handler.setAnalyzerManager(analyzerManager);
		handler.setGroupGainManager(groupGainManager);
		handler.setRatio(ratio);
		handler.setRecommendStockDao(recommendStockDao);
		handler.setCaculater(caculater);
		Analyzer analyzer = analyzerDao.getAnalyzerByName("赵湘鄂");
		handler.dealOneAnalyzer(analyzer);
		//		handler.processAllHistoryReport();
		System.exit(0);
	}
}
