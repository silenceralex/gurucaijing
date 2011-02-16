package com.caijing.model;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.GroupEarnDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.GroupEarn;
import com.caijing.domain.GroupStock;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.StockEarn;
import com.caijing.domain.StockGain;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.caijing.util.FloatUtil;

public class Caculater {

	private StockPrice sp = new StockPrice();

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	@Autowired
	@Qualifier("stockEarnDao")
	private StockEarnDao stockEarnDao = null;

	private GroupEarnDao groupEarnDao = null;

	public GroupEarnDao getGroupEarnDao() {
		return groupEarnDao;
	}

	public void setGroupEarnDao(GroupEarnDao groupEarnDao) {
		this.groupEarnDao = groupEarnDao;
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

	public AnalyzerDao getAnalyzerDao() {
		return analyzerDao;
	}

	public void setAnalyzerDao(AnalyzerDao analyzerDao) {
		this.analyzerDao = analyzerDao;
	}

	private GroupStockDao groupStockDao = null;
	private AnalyzerDao analyzerDao = null;

	public void caculateSaname(String saname) {
		List<RecommendStock> lists = recommendStockDao.getGoodRecommendStocksBySaname(saname, 0, 10);
		for (RecommendStock rs : lists) {
			String date = rs.getCreatedate();
			rs.getSaname();
			String tmp = rs.getCreatedate();
			tmp = tmp.substring(0, 4) + "-" + tmp.substring(4, 6) + "-" + tmp.substring(6, 8);
			System.out.println("推荐股票名称:" + rs.getStockname());
			System.out.println("start date:" + tmp);
			rs.getObjectprice();
			Date now = new Date();
			StockGain sg = sp.getStockGainByPeriod(rs.getStockcode(), tmp, DateTools.transformYYYYMMDDDate(new Date()));
			sg.setAnalyzer(rs.getAname());
			sg.setSaname(rs.getSaname());
		}
	}

	public void processHistoryGain(String aid, String year) {
		//TODO  make year date
		String endDate = year + "-12-31";
		try {
			Date startDate = groupStockDao.getEarliestIntimeByAidFrom(aid, DateTools.parseYYYYMMDDDate("2008-01-01"));
			Date now = new Date();
			List<GroupStock> stocks = groupStockDao.getCurrentStockByGroupid(aid);
			HashMap<String, HashMap<Date, Float>> stockdateMap = new HashMap<String, HashMap<Date, Float>>();
			for (GroupStock stock : stocks) {
				Date in = stock.getIntime();
				Calendar cal = Calendar.getInstance();
				cal.setTime(in);
				cal.add(Calendar.YEAR, 1);
				Date outtime = cal.getTime();
				//outtime 若在当前时间内则已经过期
				if (outtime.before(DateTools.parseYYYYMMDDDate(endDate))) {
					stock.setOuttime(outtime);
					stock.setStatus(-1);
					groupStockDao.updateOutOfDate(stock);
				}
				List<StockEarn> ses = stockEarnDao.getRatiosByCodeAndPeriod(stock.getStockcode(), stock.getIntime(),
						outtime);
				HashMap<Date, Float> map = new HashMap<Date, Float>();
				for (StockEarn se : ses) {
					map.put(se.getDate(), se.getRatio());
				}
				stockdateMap.put(stock.getStockcode(), map);
			}
			//TODO  至最后一个交易日截止
			List<Date> dates = stockEarnDao.getDatesByZSFrom(startDate, DateTools.parseYYYYMMDDDate(endDate));
			float weight = 100;
			for (Date date : dates) {
				GroupEarn ge = new GroupEarn();
				float ratio = 0;
				int count = 0;

				for (String key : stockdateMap.keySet()) {
					if (stockdateMap.get(key).get(date) != null) {
						ratio += stockdateMap.get(key).get(date);
						count++;
					}
				}
				if (count != 0) {
					ratio = ratio / (count * 100);
				}
				System.out.println("ratio at date :" + DateTools.transformYYYYMMDDDate(date) + "  is :" + ratio);
				weight = weight * (1 + ratio);
				ratio = ratio * 100;
				ge.setDate(date);
				ge.setGroupid(aid);
				ge.setRatio(FloatUtil.getTwoDecimal(ratio));
				ge.setWeight(weight);
				groupEarnDao.insert(ge);
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
		Caculater caculater = new Caculater();
		caculater.setRecommendStockDao(recommendStockDao);
		caculater.caculateSaname("申银万国");

	}

	public RecommendStockDao getRecommendStockDao() {
		return recommendStockDao;
	}

	public void setRecommendStockDao(RecommendStockDao recommendStockDao) {
		this.recommendStockDao = recommendStockDao;
	}

}
