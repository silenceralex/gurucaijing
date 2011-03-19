package com.caijing.model;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.RecommendSuccessDao;
import com.caijing.dao.StockDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.RecommendSuccess;
import com.caijing.domain.Stock;
import com.caijing.util.ContextFactory;
import com.caijing.util.UrlDownload;

public class StockReloader {
	private UrlDownload down = new UrlDownload();
	private StockDao stockDao = null;

	private AnalyzerDao analyzerDao = null;
	private RecommendSuccessDao recommendSuccessDao = null;

	public RecommendSuccessDao getRecommendSuccessDao() {
		return recommendSuccessDao;
	}

	public void setRecommendSuccessDao(RecommendSuccessDao recommendSuccessDao) {
		this.recommendSuccessDao = recommendSuccessDao;
	}

	public AnalyzerDao getAnalyzerDao() {
		return analyzerDao;
	}

	public void setAnalyzerDao(AnalyzerDao analyzerDao) {
		this.analyzerDao = analyzerDao;
	}

	public StockDao getStockDao() {
		return stockDao;
	}

	public void setStockDao(StockDao stockDao) {
		this.stockDao = stockDao;
	}

	private String url = "http://quotes.money.163.com/dev/output/industry_caihui.php";

	public void reload() {
		down.setCharset("utf-8");
		try {
			String a = down.load(url);
			String[] lines = a.split("\\n");
			for (int i = 1; i < lines.length; i++) {
				String line = lines[i];
				String[] strs = line.split(",");
				if (strs.length == 4) {
					Stock stock = new Stock();
					System.out.println("Stockcode£∫" + strs[0].trim() + "  stockname:" + strs[1].trim() + "  industryid"
							+ strs[2].trim() + "  industry:" + strs[3].trim());
					stock.setStockcode(strs[0].trim());
					stock.setStockname(strs[1].trim());
					stock.setIndustryid(strs[2].trim());
					stock.setIndustry(strs[3].trim());
					if (stockDao.update(stock) == 0) {
						stockDao.insert(stock);
					}
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reloadAnayzerIndustry() {
		List<Analyzer> analyzers = analyzerDao.getUnStarAnalyzers();
		System.out.println("analyzers£∫" + analyzers.size());
		//			analyzerDao.getAnalyzersByAgency("∞≤–≈÷§»Ø");
		for (Analyzer analyzer : analyzers) {
			if (analyzer.getIndustry() == null || analyzer.getIndustry().trim().length() == 0) {
				List<RecommendSuccess> recommends = recommendSuccessDao.getRecommendsByAid(analyzer.getAid());
				if (recommends == null || recommends.size() == 0)
					continue;
				Stock stock = (Stock) stockDao.select(recommends.get(0).getStockcode());
				analyzer.setIndustry(stock.getIndustry());
				analyzerDao.update(analyzer);
				System.out.println("set analyzer£∫" + analyzer.getName() + "  industry:" + stock.getIndustry());
			}
		}
		System.out.println("set all analyzer industry over!");
	}

	public static void main(String[] args) {
		StockDao stockDao = (StockDao) ContextFactory.getBean("stockDao");
		AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
		RecommendSuccessDao recommendSuccessDao = (RecommendSuccessDao) ContextFactory.getBean("recommendSuccessDao");
		StockReloader reloader = new StockReloader();
		reloader.setStockDao(stockDao);
		reloader.setAnalyzerDao(analyzerDao);
		reloader.setRecommendSuccessDao(recommendSuccessDao);
		//		reloader.reload();
		reloader.reloadAnayzerIndustry();
		System.exit(0);
	}
}
