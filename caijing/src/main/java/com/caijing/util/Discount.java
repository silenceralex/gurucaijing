package com.caijing.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.cache.MethodCache;
import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.DiscountStock;
import com.caijing.domain.StockGain;
import com.caijing.model.StockPrice;

public class Discount {
	static String[] buys = { "买入", "推荐", "强烈推荐", "长期推荐", "增持" };

	static HashSet<String> buyset = new HashSet<String>();

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	@Autowired
	@Qualifier("stockPrice")
	private StockPrice sp = null;

	static {
		for (String buy : buys) {
			buyset.add(buy);
		}
	}

	@MethodCache(expire = 3600 * 2)
	public List<DiscountStock> process() {
		List<DiscountStock> discounts = recommendStockDao.getDiscountStocks();
		List<DiscountStock> retlist = new ArrayList<DiscountStock>();
		int i = 0;
		for (DiscountStock discount : discounts) {
			if (discount.getGrade() != null && buyset.contains(discount.getGrade())) {
				String date = DateTools.transformYYYYMMDDDate(discount.getCreatedate());
				try {
					StockGain sg = sp.getStockGainByPeriod(discount.getStockcode(), date, DateTools
							.transformYYYYMMDDDate(new Date()));
					if (sg.getGain() < 0) {
						discount.setDiscountratio(0 - sg.getGain());
						discount.setRecommendprice(sg.getStartprice());
						discount.setCurrentprice(sg.getEndprice());
						retlist.add(discount);
						i++;
					}
				} catch (Exception e) {
					System.out.println("Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		System.out.println("discount <0  records :" + i);
		Collections.sort(retlist);
		return retlist;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Discount gg = new Discount();
		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
		StockPrice sp = (StockPrice) ContextFactory.getBean("stockPrice");
		gg.setRecommendStockDao(recommendStockDao);
		gg.setSp(sp);
		List<DiscountStock> discounts = gg.process();
		System.out.println("discount <0  records :" + discounts.size());
		for (DiscountStock discount : discounts) {
			System.out.println("discount getRid:" + discount.getReportid());
			System.out.println("discount getAname:" + discount.getAname());
			System.out.println("discount getSaname:" + discount.getSaname());
			System.out.println("discount getStockname:" + discount.getStockname());
			System.out.println("discount getStockcode:" + discount.getStockcode());
			System.out.println("discount getCreatedate:" + discount.getCreatedate());
			System.out.println("discount getGrade:" + discount.getGrade());
			System.out.println("discount getDiscountratio:" + discount.getDiscountratio());
		}
		//		List<RecommendStock> stocks = recommendStockDao.getRecommendStocksByStockcode("000002");
		//		for (RecommendStock stock : stocks) {
		//			System.out.println("discount getRid:" + stock.getReportid());
		//			System.out.println("discount getAname:" + stock.getAname());
		//			System.out.println("discount getSaname:" + stock.getSaname());
		//			System.out.println("discount getStockname:" + stock.getStockname());
		//			System.out.println("discount getStockcode:" + stock.getStockcode());
		//			System.out.println("discount getCreatedate:" + stock.getCreatedate());
		//			System.out.println("discount getGrade:" + stock.getGrade());
		//			System.out.println("discount getTitle:" + stock.getTitle());
		//		}
	}

	public RecommendStockDao getRecommendStockDao() {
		return recommendStockDao;
	}

	public void setRecommendStockDao(RecommendStockDao recommendStockDao) {
		this.recommendStockDao = recommendStockDao;
	}

	public StockPrice getSp() {
		return sp;
	}

	public void setSp(StockPrice sp) {
		this.sp = sp;
	}
}
