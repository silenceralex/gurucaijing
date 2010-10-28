package com.caijing.util;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.GroupEarnDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.GroupEarn;
import com.caijing.domain.GroupStock;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.StockEarn;
import com.caijing.model.StockPrice;

public class LocalStorage {

	@Autowired
	@Qualifier("groupGain")
	private GroupGain groupGain = null;

	@Autowired
	@Qualifier("stockEarnDao")
	private StockEarnDao stockEarnDao = null;

	public void localStore() {
		List<RecommendStock> lists = groupGain.getRecommendStockDao().getRecommendStocksGroupByCode();
		for (int i = 0; i < lists.size(); i++) {
			RecommendStock rs = lists.get(i);
			System.out.println("Current process :" + i + " stockcode: " + rs.getStockcode());
			groupGain.getSp().currentPrice(rs.getStockcode());
		}
		groupGain.getSp().currentPrice("000300");

		List<Analyzer> analyzerlist = groupGain.getAnalyzerDao().getAllAnalyzers();
		for (Analyzer analyzer : analyzerlist) {
			storeAnaylzerGain(analyzer);
		}
	}

	private void storeAnaylzerGain(Analyzer analyzer) {
		List<GroupStock> stocks = groupGain.getGroupStockDao().getCurrentStockByGroupid("A" + analyzer.getAid());
		float ratios = 0;
		for (GroupStock stock : stocks) {
			System.out.println("Stock : " + stock.getStockcode());
			StockEarn se = stockEarnDao.getStockEarnByCodeDate(stock.getStockcode(), new Date());
			if (se != null) {
				ratios += se.getRatio();
			}
		}
		GroupEarn tmp = groupGain.getGroupEarnDao().getGroupEarnByIDAndDate("A6EJV66CI",
				DateTools.getYesterday(new Date()));
		GroupEarn ge = new GroupEarn();
		ge.setGroupid("A" + analyzer.getAid());
		ge.setDate(new Date());
		ratios = ratios / stocks.size();
		ge.setRatio(FloatUtil.getTwoDecimal(ratios));
		ge.setWeight(FloatUtil.getTwoDecimal(tmp.getWeight() * (1 + ratios / 100)));
		groupGain.getGroupEarnDao().insert(ge);

	}

	public static void main(String[] args) {
		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");

		//		List<RecommendStock> lists = recommendStockDao.getRecommendStocksGroupByCode();
		StockPrice sp = (StockPrice) ContextFactory.getBean("stockPrice");
		//		for (int i = 0; i < lists.size(); i++) {
		//			System.out.println("Current process :" + i);
		//			RecommendStock rs = lists.get(i);
		//			//			sp.currentPrice(rs.getStockcode());
		//			sp.storeStockPrice(rs.getStockcode(), 0, "2010-10-26", DateTools.transformYYYYMMDDDate(new Date()));
		//		}
		//		System.out.println("lists.size() :" + lists.size());
		//		sp.storeStockPrice("000300", 1, "2010-03-22", DateTools.transformYYYYMMDDDate(new Date()));
		//		sp.currentPrice("000300");
		GroupStockDao groupStockDao = (GroupStockDao) ContextFactory.getBean("groupStockDao");
		StockEarnDao stockEarnDao = (StockEarnDao) ContextFactory.getBean("stockEarnDao");
		GroupEarnDao groupEarnDao = (GroupEarnDao) ContextFactory.getBean("groupEarnDao");
		AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
		List<Analyzer> analyzerlist = analyzerDao.getAllAnalyzers();
		for (Analyzer analyzer : analyzerlist) {
			List<GroupStock> stocks = groupStockDao.getCurrentStockByGroupid("A" + analyzer.getAid());
			float ratios = 0;
			if (stocks == null || stocks.size() == 0) {
				System.out.println("analyzer : " + analyzer.getName() + " have No GroupStock");
				continue;
			}
			try {
				for (GroupStock stock : stocks) {
					System.out.println("Stock : " + stock.getStockcode());
					StockEarn se = stockEarnDao.getStockEarnByCodeDate(stock.getStockcode(), DateTools
							.parseYYYYMMDDDate("2010-10-27"));
					if (se != null) {
						ratios += se.getRatio();
					}
				}
				GroupEarn tmp = groupEarnDao.getGroupEarnByIDAndDate("A" + analyzer.getAid(), DateTools
						.parseYYYYMMDDDate("2010-10-26"));
				GroupEarn ge = new GroupEarn();
				ge.setGroupid("A" + analyzer.getAid());
				ge.setDate(DateTools.parseYYYYMMDDDate("2010-10-27"));
				ratios = ratios / stocks.size();
				System.out.println("Gourp : " + analyzer.getName() + "   gain: " + ratios);
				ge.setRatio(FloatUtil.getTwoDecimal(ratios));
				ge.setWeight(FloatUtil.getTwoDecimal(tmp.getWeight() * (1 + ratios / 100)));
				groupEarnDao.insert(ge);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		//		GroupGain gg = (GroupGain) ContextFactory.getBean("groupGain");
		//		List<StockGain> sgs = gg.getStockGainManager().getStockGainByAnameASC("’≤¡Ë—‡");
		//		for (StockGain sg : sgs) {
		//			System.out.println("Stock : " + sg.getStockcode() + "  " + sg.getStockname() + "  in group date:"
		//					+ sg.getStartdate());
		//		}
		//		gg.processASCStore("’≤¡Ë—‡");
		//		Date yesterday = DateTools.getYesterday(new Date());
		//
		//		System.out.println("Yesterday: " + yesterday);
		//		GroupEarn ge = groupEarnDao.getGroupEarnByIDAndDate("A6EJV66CI", yesterday);
		//		System.out.println("group earn at : " + yesterday + "   ratio:" + ge.getRatio());
		//		AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
		//		GroupGain gg = (GroupGain) ContextFactory.getBean("groupGain");
		//		List<Analyzer> analyzerlist = analyzerDao.getAllAnalyzers();
		//		for (Analyzer analyzer : analyzerlist) {
		//			gg.processASCStore(analyzer.getName());
		//		}
	}

	public GroupGain getGroupGain() {
		return groupGain;
	}

	public void setGroupGain(GroupGain groupGain) {
		this.groupGain = groupGain;
	}

	public StockEarnDao getStockEarnDao() {
		return stockEarnDao;
	}

	public void setStockEarnDao(StockEarnDao stockEarnDao) {
		this.stockEarnDao = stockEarnDao;
	}
}
