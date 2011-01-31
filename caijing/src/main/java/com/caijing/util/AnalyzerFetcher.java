package com.caijing.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.caijing.business.GroupGainManager;
import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.ibatis.AnalyzerDaoImpl;
import com.caijing.domain.Analyzer;
import com.caijing.domain.RecommendStock;

public class AnalyzerFetcher {

	public String getfirstPage(String txtpath) {
		try {
			PDDocument document = PDDocument.load(txtpath);
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setSortByPosition(false);
			stripper.setStartPage(1);
			stripper.setEndPage(1);
			StringWriter sw = new StringWriter();
			stripper.writeText(document, sw);
			System.out.println("first page: " + sw.toString());
			return sw.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void process(String saname) {

	}

	public static void main(String[] args) {
		AnalyzerDao analyzerDao = (AnalyzerDaoImpl) ContextFactory.getBean("analyzerDao");
		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
		GroupGainManager groupGainManager = (GroupGainManager) ContextFactory.getBean("groupGainManager");

		//		List<Analyzer> analyzers = analyzerDao.getAllAnalyzers();
		//		List<Analyzer> analyzers = analyzerDao.getAnalyzersByAgency("安信证券");
		//		int i = 0;
		//		for (Analyzer analyzer : analyzers) {
		//			//			recommendStockDao.updateAnalyzer(analyzer.getName(), analyzer.getAid());
		//			List<RecommendStock> rstocks = recommendStockDao
		//					.getRecommendStocksByAnalyzerASC(analyzer.getName(), 0, 100);
		//			System.out.println("rstocks size : " + rstocks.size());
		//			for (RecommendStock rs : rstocks) {
		//				groupGainManager.extractGroupStock(rs);
		//			}
		//			i++;
		//		}
		//韩振国 陈运红 贺国文 石磊 尹沿技 王薇 赵宇杰  李宏鹏 徐颖真
		//		String aname = "贺平鸽";
		//		List<Analyzer> analyzers = analyzerDao.getAnalyzersAfter("2011-01-19 22:04:56");

		//批量处理某个券商的分析师的收益率的计算结果
		List<Analyzer> analyzers = analyzerDao.getAnalyzersByAgency("安信证券");
		for (Analyzer analyzer : analyzers) {
			//仅仅计算非明星的分析师
			if (analyzer.getLevel() == 0) {
				List<RecommendStock> rstocks = recommendStockDao.getRecommendStocksByAnalyzerASC(analyzer.getName(), 0,
						500);
				System.out.println("analyzer getName : " + analyzer.getName());
				System.out.println("rstocks size : " + rstocks.size());
				for (RecommendStock rs : rstocks) {
					groupGainManager.extractGroupStock(rs);
				}
				String aid = analyzer.getAid();
				//				String aid = analyzerDao.getAnalyzerByName(aname).getAid();
				LocalStorage storage = (LocalStorage) ContextFactory.getBean("localStorage");
				storage.processHistoryGroupEarn(aid);
			}
		}
	}
}
