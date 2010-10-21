package com.caijing.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.business.StockGainManager;
import com.caijing.business.impl.StockGainManagerImpl;
import com.caijing.cache.MethodCache;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.StockDao;
import com.caijing.domain.GroupPeriod;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.StockGain;
import com.caijing.model.StockPrice;

public class GroupGain {

	static String[] buys = { "买入", "推荐", "强烈推荐", "长期推荐", "增持" };

	static String[] sells = { "中性", "维持审慎推荐", "审慎推荐", "增持" };

	static HashSet<String> buyset = new HashSet<String>();

	static HashSet<String> sellset = new HashSet<String>();
	HashSet<String> groupcode = new HashSet<String>();
	HashMap<String, String> stockmap = new HashMap<String, String>();
	@Autowired
	@Qualifier("stockDao")
	private StockDao dao = null;

	@Autowired
	@Qualifier("stockGainManager")
	private StockGainManager stockGainManager = null;

	@Autowired
	@Qualifier("stockPrice")
	private StockPrice sp = null;

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	public void init() {
		//		List<Stock> list = dao.getAllStock();
		//		for (Stock stock : list) {
		//			if (!stockmap.containsKey(stock.getStockcode())) {
		//				stockmap.put(stock.getStockcode(), stock.getStockname());
		//			}
		//		}
	}

	static {
		for (String buy : buys) {
			buyset.add(buy);
		}
		for (String sell : sells) {
			sellset.add(sell);
		}
	}

	@MethodCache(expire = 3600 * 4)
	public GroupPeriod processASC(String aname) {
		List<StockGain> sgs = stockGainManager.getStockGainByAnameASC(aname);
		HashMap<String, HashMap<String, Float>> stockdateMap = new HashMap<String, HashMap<String, Float>>();
		HashMap<String, String> joinMap = new HashMap<String, String>();
		for (StockGain sg : sgs) {
			HashMap<String, Float> stockearnMap = new HashMap<String, Float>();
			List<String> dates = sg.getPerioddate();
			List<Float> ratios = sg.getPeriodratio();
			joinMap.put(sg.getStartdate(), sg.getStockname());
			System.out.println("stockcode :" + sg.getStockcode() + "  stockname:" + sg.getStockname() + "  join date:"
					+ sg.getStartdate());
			//推荐日之后的交易日开始算起
			for (int i = dates.size() - 1; i >= 0; i--) {
				stockearnMap.put(dates.get(i), ratios.get(i));
				//				System.out.println("Date:"+dates.get(i)+"  ratio:"+ ratios.get(i));
			}
			//防止多次推荐同一只股票
			if (!stockdateMap.containsKey(sg.getStockcode())) {
				stockdateMap.put(sg.getStockcode(), stockearnMap);
			}
		}
		//采用指数的日期，防止首只推荐股票有停牌日期
		StockGain zsgain = sp.getZSGainByPeriod(sgs.get(0).getStartdate(), DateTools.transformYYYYMMDDDate(new Date()));
		List<String> dates = zsgain.getPerioddate();
		//		List<String> dates = sgs.get(0).getPerioddate();
		List<Float> groupratio = new ArrayList<Float>(dates.size());
		List<Float> weights = new ArrayList<Float>(dates.size());
		List<String> perioddates = new ArrayList<String>(dates.size());
		float weight = 100;
		for (int i = dates.size() - 1; i >= 0; i--) {
			float ratio = 0;
			int count = 0;
			for (String code : stockdateMap.keySet()) {
				if (stockdateMap.get(code).containsKey(dates.get(i))) {
					ratio += stockdateMap.get(code).get(dates.get(i));
					count++;
				}
			}
			ratio = ratio / (count * 100);
			System.out.println("ratio at date :" + dates.get(i) + "  is :" + ratio);
			weight = weight * (1 + ratio);
			ratio = ratio * 100;
			groupratio.add(FloatUtil.getTwoDecimal(ratio));
			perioddates.add(dates.get(i));
			weights.add(FloatUtil.getTwoDecimal(weight));
		}
		GroupPeriod gp = new GroupPeriod();
		gp.setStockGains(sgs);
		gp.setFirstdate(sgs.get(0).getStartdate());
		gp.setFirststock(sgs.get(0).getStockname());
		gp.setDates(perioddates);
		gp.setRatios(groupratio);
		gp.setWeights(weights);
		gp.setStockdateMap(stockdateMap);
		gp.setJoinMap(joinMap);
		return gp;
	}

	public GroupPeriod process(String aname) {
		List<String> totaldates = new ArrayList<String>();
		List<Float> weights = new ArrayList<Float>();
		List<Float> ratios = new ArrayList<Float>();
		int count = recommendStockDao.getRecommendStockCountsByAnalyzer(aname);
		List<RecommendStock> stocks = recommendStockDao.getRecommendStocksByAnalyzerASC(aname, 0, count);
		String lastdate = null;
		float weight = 100;
		for (RecommendStock stock : stocks) {
			String grade = stock.getGrade();
			//			String start = stock.getCreatedate();
			String code = stock.getStockcode();
			//			String tmp = stock.getCreatedate();
			//			if (tmp == null || tmp.trim().length() < 8)
			//				continue;
			//			tmp = tmp.substring(0, 4) + "-" + tmp.substring(4, 6) + "-" + tmp.substring(6, 8);
			String tmp = DateTools.transformYYYYMMDDDate(stock.getCreatedate());
			if (!groupcode.contains(code) && buyset.contains(grade)) {
				if (lastdate != null) {
					float ratio = 0;
					String first = (String) groupcode.toArray()[0];
					List dates = sp.getStockGainByPeriod(first, lastdate, tmp).getPerioddate();
					List<Float> groupPrice = new ArrayList<Float>(dates.size());
					for (int i = 0; i < dates.size(); i++) {
						groupPrice.add(new Float(0.0));
					}
					System.out.println("starttime :" + lastdate + "  end :" + tmp);
					for (String scode : groupcode) {
						StockGain sg = sp.getStockGainByPeriod(scode, lastdate, tmp);

						if (groupPrice.size() == sg.getPeriodratio().size()) {
							for (int i = 0; i < groupPrice.size(); i++) {
								System.out.println("scode :" + scode + "   ratio at date :" + dates.get(i) + "  is :"
										+ sg.getPeriodratio().get(i));
								groupPrice.set(i, groupPrice.get(i) + sg.getPeriodratio().get(i));
								System.out.println("total  ratio at date :" + dates.get(i) + "  is :"
										+ groupPrice.get(i));
							}
						}
						ratio += sg.getGain();
					}

					for (int i = dates.size() - 1; i >= 0; i--) {
						totaldates.add((String) dates.get(i));
						ratio = groupPrice.get(i) / (groupcode.size() * 100);
						ratios.add(ratio);
						System.out.println("ratio at date :" + dates.get(i) + "  is :" + ratio);
						weight = weight * (1 + ratio);
						weights.add(weight);
						System.out.println("weight at date :" + dates.get(i) + "  is :" + weight);
					}
					//					weight = weight * (1 + ratio / ((float) 100 * groupcode.size()));
					System.out.println("weight at time :" + tmp + "  is :" + weight);
				}
				groupcode.add(code);
				lastdate = tmp;
			}
		}
		float ratio = 0;
		String first = (String) groupcode.toArray()[0];
		String tmp = DateTools.transformYYYYMMDDDate(new Date());
		//		tmp = tmp.substring(0, 4) + "-" + tmp.substring(4, 6) + "-" + tmp.substring(6, 8);
		List dates = sp.getStockGainByPeriod(first, lastdate, tmp).getPerioddate();
		List<Float> groupPrice = new ArrayList<Float>(dates.size());
		for (int i = 0; i < dates.size(); i++) {
			groupPrice.add(new Float(0.0));
		}
		System.out.println("starttime :" + lastdate + "  end :" + tmp);
		for (String scode : groupcode) {
			StockGain sg = sp.getStockGainByPeriod(scode, lastdate, tmp);
			if (groupPrice.size() == sg.getPeriodratio().size()) {
				for (int i = 0; i < groupPrice.size(); i++) {
					groupPrice.set(i, groupPrice.get(i) + sg.getPeriodratio().get(i));
				}
			}
			ratio += sg.getGain();
		}
		for (int i = dates.size() - 1; i >= 0; i--) {
			totaldates.add((String) dates.get(i));
			ratio = groupPrice.get(i) / (groupcode.size() * 100);
			System.out.println("groupcode.size() :" + groupcode.size());
			ratios.add(ratio);
			System.out.println("ratio at date :" + dates.get(i) + "  is :" + ratio);
			weight = weight * (1 + ratio);
			weights.add(weight);
			System.out.println("weight at date :" + dates.get(i) + "  is :" + weight);
		}

		GroupPeriod gs = new GroupPeriod();
		gs.setDates(totaldates);
		gs.setRatios(ratios);
		gs.setWeights(weights);
		//					weight = weight * (1 + ratio / ((float) 100 * groupcode.size()));
		System.out.println("weight at time :" + tmp + "  is :" + weight);
		return gs;
	}

	@MethodCache(expire = 3600 * 4)
	public List<GroupPeriod> processGroupPeriod(String aname) {
		List<GroupPeriod> gps = new ArrayList<GroupPeriod>();
		int count = recommendStockDao.getRecommendStockCountsByAnalyzer(aname);
		List<RecommendStock> stocks = recommendStockDao.getRecommendStocksByAnalyzerASC(aname, 0, count);
		String lastdate = null;
		float weight = 100;

		for (RecommendStock stock : stocks) {
			String grade = stock.getGrade();
			//			String start = stock.getCreatedate();
			String code = stock.getStockcode();
			//			String tmp = stock.getCreatedate();
			//			if (tmp == null || tmp.trim().length() < 8)
			//				continue;
			//			tmp = tmp.substring(0, 4) + "-" + tmp.substring(4, 6) + "-" + tmp.substring(6, 8);
			String tmp = DateTools.transformYYYYMMDDDate(stock.getCreatedate());
			if (!groupcode.contains(code) && buyset.contains(grade)) {
				if (lastdate != null) {
					float ratio = 0;
					String first = (String) groupcode.toArray()[0];
					List dates = sp.getStockGainByPeriod(first, lastdate, tmp).getPerioddate();
					List<Float> groupPrice = new ArrayList<Float>(dates.size());
					for (int i = 0; i < dates.size(); i++) {
						groupPrice.add(new Float(0.0));
					}
					GroupPeriod gp = new GroupPeriod();
					System.out.println("starttime :" + lastdate + "  end :" + tmp);
					HashMap<String, StockGain> stockInGroup = new HashMap<String, StockGain>();
					for (String scode : groupcode) {
						StockGain sg = sp.getStockGainByPeriod(scode, lastdate, tmp);
						sg.setStockname(stockmap.get(scode));
						//加入group
						stockInGroup.put(scode, sg);
						if (groupPrice.size() == sg.getPeriodratio().size()) {
							for (int i = 0; i < groupPrice.size(); i++) {
								System.out.println("scode :" + scode + "   ratio at date :" + dates.get(i) + "  is :"
										+ sg.getPeriodratio().get(i));
								groupPrice.set(i, groupPrice.get(i) + sg.getPeriodratio().get(i));
								System.out.println("total  ratio at date :" + dates.get(i) + "  is :"
										+ groupPrice.get(i));
							}
						}
						ratio += sg.getGain();
					}
					gp.setStockInGroup(stockInGroup);
					List<String> perioddates = new ArrayList<String>();
					List<Float> weights = new ArrayList<Float>();
					List<Float> ratios = new ArrayList<Float>();

					for (int i = dates.size() - 1; i >= 0; i--) {
						perioddates.add((String) dates.get(i));
						ratio = groupPrice.get(i) / (groupcode.size() * 100);
						ratios.add(ratio);
						System.out.println("ratio at date :" + dates.get(i) + "  is :" + ratio);
						weight = weight * (1 + ratio);
						weights.add(weight);
						System.out.println("weight at date :" + dates.get(i) + "  is :" + weight);
					}
					gp.setDates(perioddates);
					gp.setRatios(ratios);
					gp.setWeights(weights);
					gps.add(gp);
					//					weight = weight * (1 + ratio / ((float) 100 * groupcode.size()));
					System.out.println("weight at time :" + tmp + "  is :" + weight);
				}
				groupcode.add(code);

				lastdate = tmp;
			}
		}
		float ratio = 0;
		String first = (String) groupcode.toArray()[0];
		String tmp = DateTools.transformYYYYMMDDDate(new Date());
		//		tmp = tmp.substring(0, 4) + "-" + tmp.substring(4, 6) + "-" + tmp.substring(6, 8);
		List dates = sp.getStockGainByPeriod(first, lastdate, tmp).getPerioddate();
		List<Float> groupPrice = new ArrayList<Float>(dates.size());
		for (int i = 0; i < dates.size(); i++) {
			groupPrice.add(new Float(0.0));
		}
		GroupPeriod gp = new GroupPeriod();

		System.out.println("starttime :" + lastdate + "  end :" + tmp);
		HashMap<String, StockGain> stockInGroup = new HashMap<String, StockGain>();
		for (String scode : groupcode) {
			StockGain sg = sp.getStockGainByPeriod(scode, lastdate, tmp);
			//加入group
			stockInGroup.put(scode, sg);
			if (groupPrice.size() == sg.getPeriodratio().size()) {
				for (int i = 0; i < groupPrice.size(); i++) {
					groupPrice.set(i, groupPrice.get(i) + sg.getPeriodratio().get(i));
				}
			}
			ratio += sg.getGain();
		}
		gp.setStockInGroup(stockInGroup);
		List<String> perioddates = new ArrayList<String>();
		List<Float> weights = new ArrayList<Float>();
		List<Float> ratios = new ArrayList<Float>();
		for (int i = dates.size() - 1; i >= 0; i--) {
			perioddates.add((String) dates.get(i));
			ratio = groupPrice.get(i) / (groupcode.size() * 100);
			System.out.println("groupcode.size() :" + groupcode.size());
			ratios.add(ratio);
			System.out.println("ratio at date :" + dates.get(i) + "  is :" + ratio);
			weight = weight * (1 + ratio);
			weights.add(weight);
			System.out.println("weight at date :" + dates.get(i) + "  is :" + weight);
		}
		gp.setDates(perioddates);
		gp.setRatios(ratios);
		gp.setWeights(weights);
		gps.add(gp);
		//					weight = weight * (1 + ratio / ((float) 100 * groupcode.size()));
		System.out.println("weight at time :" + tmp + "  is :" + weight);
		return gps;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GroupGain gg = new GroupGain();
		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
		StockGainManagerImpl stockGainManager = (StockGainManagerImpl) ContextFactory.getBean("stockGainManager");
		gg.setRecommendStockDao(recommendStockDao);
		gg.setStockGainManager(stockGainManager);
		gg.init();
		//		gg.process("赵强");
		gg.processASC("赵强");
	}

	public RecommendStockDao getRecommendStockDao() {
		return recommendStockDao;
	}

	public void setRecommendStockDao(RecommendStockDao recommendStockDao) {
		this.recommendStockDao = recommendStockDao;
	}

	public StockDao getDao() {
		return dao;
	}

	public void setDao(StockDao dao) {
		this.dao = dao;
	}

	public StockGainManager getStockGainManager() {
		return stockGainManager;
	}

	public void setStockGainManager(StockGainManager stockGainManager) {
		this.stockGainManager = stockGainManager;
	}

	public StockPrice getSp() {
		return sp;
	}

	public void setSp(StockPrice sp) {
		this.sp = sp;
	}

}
