package com.caijing.business.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import com.caijing.business.GroupGainManager;
import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.GroupStock;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.StockEarn;
import com.caijing.model.StockPrice;
import com.caijing.util.DateTools;
import com.caijing.util.FloatUtil;
import com.caijing.util.GradeUtil;

public class GroupGainManagerImpl implements GroupGainManager, InitializingBean {

	static String[] buys = { "买入", "推荐", "强烈推荐", "长期推荐", "增持", "维持推荐", "买 入 维持", "上调至推荐", "增 持 维持", "买入维持", "买入首次" };

	static String[] sells = { "中性", "维持审慎推荐", "审慎推荐", "减持", "中 性 调低" };

	private HashSet<String> buyset = new HashSet<String>();

	private HashSet<String> sellset = new HashSet<String>();

	private HashMap<String, Analyzer> analyzerMap = new HashMap<String, Analyzer>();

	private AnalyzerDao analyzerDao = null;

	private GroupStockDao groupStockDao = null;

	private RecommendStockDao recommendStockDao = null;

	private StockEarnDao stockEarnDao = null;

	private StockPrice sp = null;

	@Override
	public void afterPropertiesSet() throws Exception {
		List<Analyzer> analyzers = analyzerDao.getAllAnalyzers();
		for (Analyzer analyzer : analyzers) {
			analyzerMap.put(analyzer.getName(), analyzer);
		}
		for (String buy : buys) {
			buyset.add(buy);
		}
		for (String sell : sells) {
			sellset.add(sell);
		}
	}

	//	@Override
	//	public void fillGroupEarn(String aid) {
	//		Date startdate = groupStockDao.getEarliestIntimeByAid(aid);
	//		List<GroupStock> stocks=groupStockDao.getGroupStockListAsc(0, 1);
	//		if(stocks!=null&&stocks.size()>0){
	//			stocks.get(0).getStockcode();
	//		}
	//		groupStockDao.
	//
	//	}

	public void extractGroupStock(RecommendStock rs) {
		String[] names = rs.getAname().split("\\s|,");
		for (String name : names) {
			name = name.replaceAll("[^\u4e00-\u9fa5]", "");
			if (analyzerMap.containsKey(name)) {
				GroupStock gs = new GroupStock();
				String aid = analyzerMap.get(name).getAid();
				gs.setGroupid(aid);
				gs.setGroupname(name);
				gs.setStockcode(rs.getStockcode());
				recommendStockDao.updateAnalyzerByReportid(rs.getReportid(), aid);
				GroupStock oldstock = groupStockDao.getCurrentStockByGroupidAndStockcode(aid, rs.getStockcode());

				try {
					//					if (buyset.contains(rs.getGrade()) && (oldstock == null)) {
					if (GradeUtil.judgeStaus(rs.getGrade()) == 2 && (oldstock == null)) {
						gs.setIntime(DateTools.parseShortDate(rs.getCreatedate()));
						gs.setInreportid(rs.getReportid());
						gs.setObjectprice(rs.getObjectprice());
						StockEarn se = stockEarnDao.getStockEarnByCodeDate(rs.getStockcode(),
								DateTools.transformYYYYMMDDDateFromStr(rs.getCreatedate()));

						float inprice = 0;
						if (se != null) {
							inprice = se.getPrice();
						} else {
							inprice = stockEarnDao.getNearPriceByCodeDate(rs.getStockcode(),
									DateTools.parseYYYYMMDDDate(rs.getCreatedate())).getPrice();
							//							inprice = sp.fetchhq(rs.getStockcode(),
							//									DateTools.transformYYYYMMDDDateFromStr(rs.getCreatedate())).getEndprice();
						}

						gs.setInprice(inprice);
						try {
							groupStockDao.insert(gs);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						List<StockEarn> stockEarns = stockEarnDao.getRatiosByCodeFromDate(rs.getStockcode(),
								rs.getCreatedate());
						float gain = 1;
						for (int i = 0; i < stockEarns.size(); i++) {
							gain = (1 + stockEarns.get(i).getRatio() / 100) * gain;
						}
						gs.setCurrentprice(stockEarns.get(stockEarns.size() - 1).getPrice());
						gs.setGain(FloatUtil.getTwoDecimal((gain - 1) * 100));
						gs.setLtime(stockEarns.get(stockEarns.size() - 1).getDate());
						groupStockDao.updateStockGain(gs);
					}
					//					if (sellset.contains(rs.getGrade()) && (oldstock != null)) {
					if (GradeUtil.judgeStaus(rs.getGrade()) == 1 && (oldstock != null)) {
						if (oldstock.getIntime().before(DateTools.parseShortDate(rs.getCreatedate()))) {
							oldstock.setOuttime(DateTools.parseShortDate(rs.getCreatedate()));
							oldstock.setOutreportid(rs.getReportid());
							groupStockDao.update(oldstock);
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public AnalyzerDao getAnalyzerDao() {
		return analyzerDao;
	}

	public void setAnalyzerDao(AnalyzerDao analyzerDao) {
		this.analyzerDao = analyzerDao;
	}

	public GroupStockDao getGroupStockDao() {
		return groupStockDao;
	}

	public void setGroupStockDao(GroupStockDao groupStockDao) {
		this.groupStockDao = groupStockDao;
	}

	public RecommendStockDao getRecommendStockDao() {
		return recommendStockDao;
	}

	public void setRecommendStockDao(RecommendStockDao recommendStockDao) {
		this.recommendStockDao = recommendStockDao;
	}

	public StockEarnDao getStockEarnDao() {
		return stockEarnDao;
	}

	public void setStockEarnDao(StockEarnDao stockEarnDao) {
		this.stockEarnDao = stockEarnDao;
	}

	public StockPrice getSp() {
		return sp;
	}

	public void setSp(StockPrice sp) {
		this.sp = sp;
	}

	@Override
	public void fillGroupEarn(String aid) {
		// TODO Auto-generated method stub

	}

}
