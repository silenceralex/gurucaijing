package com.caijing.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.GroupStock;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.StockGain;
import com.caijing.model.StockPrice;

public class GroupGain {

	String[] buys = { "买入", "推荐", "强烈推荐", "长期推荐", "增持" };

	String[] sells = { "中性", "维持审慎推荐", "审慎推荐", "增持" };

	HashSet<String> buyset = new HashSet<String>();

	HashSet<String> sellset = new HashSet<String>();
	HashSet<String> groupcode = new HashSet<String>();

	//	@Autowired
	//	@Qualifier("stockGainManager")
	//	private StockGainManager stockGainManager = null;

	private StockPrice sp = new StockPrice();
	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	public void init() {
		for (String buy : buys) {
			buyset.add(buy);
		}
		for (String sell : sells) {
			sellset.add(sell);
		}
	}

	public GroupStock process(String aname) {
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
			String tmp = stock.getCreatedate();
			if (tmp == null || tmp.trim().length() < 8)
				continue;
			tmp = tmp.substring(0, 4) + "-" + tmp.substring(4, 6) + "-" + tmp.substring(6, 8);
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

		GroupStock gs = new GroupStock();
		gs.setDates(totaldates);
		gs.setRatios(ratios);
		gs.setWeights(weights);
		//					weight = weight * (1 + ratio / ((float) 100 * groupcode.size()));
		System.out.println("weight at time :" + tmp + "  is :" + weight);
		return gs;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GroupGain gg = new GroupGain();
		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
		gg.setRecommendStockDao(recommendStockDao);
		gg.init();
		gg.process("赵强");
	}

	public RecommendStockDao getRecommendStockDao() {
		return recommendStockDao;
	}

	public void setRecommendStockDao(RecommendStockDao recommendStockDao) {
		this.recommendStockDao = recommendStockDao;
	}

}
