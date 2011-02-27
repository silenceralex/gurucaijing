package com.caijing.model;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.GroupEarnDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.GroupEarn;
import com.caijing.domain.GroupStock;
import com.caijing.domain.StockEarn;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.caijing.util.FloatUtil;

/**
 * 用于计算历史的收益率
 * @author chenjun
 *
 */
public class Caculater {

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

	private GroupStockDao groupStockDao = null;

	//处理某个分析师所有的历史收益率计算问题
	public void processAllHistoryGain(String aid) {
		try {
			Date startDate = groupStockDao.getEarliestIntimeByAidFrom(aid, DateTools.parseYYYYMMDDDate("2008-01-01"));
			Date now = new Date();
			//			List<GroupStock> stocks = groupStockDao.getCurrentStockByGroupid(aid);
			//所有groupstock
			List<GroupStock> stocks = groupStockDao.getAllStockByGroupid(aid);
			HashMap<String, HashMap<Date, Float>> stockdateMap = new HashMap<String, HashMap<Date, Float>>();
			for (GroupStock stock : stocks) {
				HashMap<Date, Float> map = new HashMap<Date, Float>();

				//已经是调出的状态
				if (stock.getStatus() == 0) {
					StockEarn se = stockEarnDao.getNearPriceByCodeDate(stock.getStockcode(), stock.getOuttime());
					//验证期在当前日期之后或者当前尚未打开
					if (se != null) {
						List<StockEarn> ses = stockEarnDao.getRatiosByCodeAndPeriod(stock.getStockcode(),
								stock.getIntime(), se.getDate());
						float gain = 1;
						for (StockEarn stockEarn : ses) {
							gain = gain * (1 + stockEarn.getRatio() / 100);
							map.put(stockEarn.getDate(), stockEarn.getRatio());
						}
						stock.setLtime(new Date());
						groupStockDao.updateOutGain(stock);
						stockdateMap.put(stock.getStockcode(), map);
						continue;
					}
				} else {
					Date in = stock.getIntime();
					Calendar cal = Calendar.getInstance();
					cal.setTime(in);
					cal.add(Calendar.YEAR, 1);
					Date outtime = cal.getTime();
					//outtime 若在当前时间内则已经过期
					StockEarn se = stockEarnDao.getNearPriceByCodeDate(stock.getStockcode(), cal.getTime());
					//该stock已经过期
					if (cal.getTime().before(now) && se != null) {
						List<StockEarn> ses = stockEarnDao.getRatiosByCodeAndPeriod(stock.getStockcode(),
								stock.getIntime(), se.getDate());
						float gain = 1;
						for (StockEarn stockEarn : ses) {
							gain = gain * (1 + stockEarn.getRatio() / 100);
							map.put(stockEarn.getDate(), stockEarn.getRatio());
						}
						stock.setOuttime(se.getDate());
						stock.setCurrentprice(se.getPrice());
						stock.setStatus(-1);
						stock.setGain(FloatUtil.getTwoDecimal((gain - 1) * 100));
						stock.setLtime(new Date());
						System.out.println("updateOutOfDate stock intime : " + stock.getIntime() + " stockcode:"
								+ stock.getStockcode());
						groupStockDao.updateOutOfDate(stock);
						//						System.out.println("stock :" + stock.getInreportid() + " code:" + stock.getStockcode()
						//								+ "  outdate:" + se.getDate() + "  gain:" + gain);
						stockdateMap.put(stock.getStockcode(), map);
						continue;
					}
				}

				List<StockEarn> ses = stockEarnDao.getRatiosByCodeAndPeriod(stock.getStockcode(), stock.getIntime(),
						now);
				for (StockEarn stockEarn : ses) {
					map.put(stockEarn.getDate(), stockEarn.getRatio());
				}
				stockdateMap.put(stock.getStockcode(), map);
			}

			//TODO  至最后一个交易日截止
			List<Date> dates = stockEarnDao.getDatesByZSFrom(startDate, now);
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
			e.printStackTrace();
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
				now = now.before(DateTools.parseYYYYMMDDDate(endDate)) ? now : DateTools.parseYYYYMMDDDate(endDate);
				System.out.println(now.toString());
				if (outtime.before(now)) {
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
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
		GroupStockDao groupStockDao = (GroupStockDao) ContextFactory.getBean("groupStockDao");
		GroupEarnDao groupEarnDao = (GroupEarnDao) ContextFactory.getBean("groupEarnDao");
		StockEarnDao stockEarnDao = (StockEarnDao) ContextFactory.getBean("stockEarnDao");
		//		AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
		Caculater caculater = (Caculater) ContextFactory.getBean("caculater");
		//		caculater.setGroupEarnDao(groupEarnDao);

		//		//		caculater.caculateSaname("申银万国");
		caculater.processHistoryGain("6O3M6JBM", "2009");
		//
		//		caculater.processHistoryGain("6IHTNVCA", "2011");

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(1298125702439l);
		DateTools.transformYYYYMMDDDate(cal.getTime());
		System.out.println("Date:" + DateTools.transformYYYYMMDDDate(cal.getTime()));
		System.out.println("Date:" + System.currentTimeMillis());
		System.exit(0);

	}

	//	public RecommendStockDao getRecommendStockDao() {
	//		return recommendStockDao;
	//	}
	//
	//	public void setRecommendStockDao(RecommendStockDao recommendStockDao) {
	//		this.recommendStockDao = recommendStockDao;
	//	}

}
