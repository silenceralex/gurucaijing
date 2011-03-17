package com.caijing.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.caijing.business.GroupGainManager;
import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.ibatis.AnalyzerDaoImpl;

/**
 * 按照分析师为计算单元进行的本地化处理的计算
 * @author chenjun
 *
 */
public class AnalyzerFetcher {

	private AnalyzerDao analyzerDao = null;

	private RecommendStockDao recommendStockDao = null;
	private GroupGainManager groupGainManager = null;

	private static String[] agencys = { "申银万国", "国泰君安", "招商证券", "安信证券", "广发证券", "国金证券", "国信证券", "长江证券", "华泰证券", "华泰联合",
			"光大证券", "中投证券", "中信建投" };

	private LocalStorage storage = null;

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
		//		List<Analyzer> analyzers = analyzerDao.getAnalyzersByAgency("安信证券");
		//		for (Analyzer analyzer : analyzers) {
		//		Analyzer analyzer = analyzerDao.getAnalyzerByName("赵金厚");
		//		List<RecommendStock> rstocks = recommendStockDao.getRecommendStocksByAnalyzerASC(analyzer.getName(), 0, 200);
		//仅仅计算非明星的分析师
		//			if (analyzer.getLevel() == 0) {
		//				List<RecommendStock> rstocks = recommendStockDao.getRecommendStocksByAnalyzerASC(analyzer.getName(), 0,
		//						500);
		//		System.out.println("analyzer getName : " + analyzer.getName());
		//		System.out.println("rstocks size : " + rstocks.size());
		//		for (RecommendStock rs : rstocks) {
		//			groupGainManager.extractGroupStock(rs);
		//		}
		//		String aid = analyzer.getAid();
		//				String aid = analyzerDao.getAnalyzerByName(aname).getAid();
		//		LocalStorage storage = (LocalStorage) ContextFactory.getBean("localStorage");
		//		storage.processHistoryGroupEarn(aid);
		//			}
		//		}
		//		RecommendStock rs = recommendStockDao.getRecommendStockbyReportid("6SR0VFJN");
		//		groupGainManager.extractGroupStock(rs);
		HashSet<String> nodupSet = new HashSet<String>();

		for (String agency : agencys) {
			List<String> names = recommendStockDao.getDistinctAnalyzersBySaname(agency);
			for (String name : names) {
				String[] strs = name.split("\\s|,|，");
				for (String str : strs) {
					if (str.length() >= 2 && str.length() <= 4) {
						String key = str + "##" + agency;
						if (!nodupSet.contains(key)) {
							System.out.println("key :" + key);
							FileUtil.appendWrite("/home/app/analyzer.txt", key);
							nodupSet.add(key);
						}
					}
				}
			}
		}
	}
}
