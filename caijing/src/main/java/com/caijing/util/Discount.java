package com.caijing.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.cache.MethodCache;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.DiscountStock;
import com.caijing.domain.GroupStock;
import com.caijing.domain.RecommendStock;
import com.caijing.model.StockPrice;

public class Discount {

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	@Autowired
	@Qualifier("groupStockDao")
	private GroupStockDao groupStockDao = null;

	public GroupStockDao getGroupStockDao() {
		return groupStockDao;
	}

	public void setGroupStockDao(GroupStockDao groupStockDao) {
		this.groupStockDao = groupStockDao;
	}

	@Autowired
	@Qualifier("stockPrice")
	private StockPrice sp = null;

	private static String STARTDATE = "2010-01-01";

	@MethodCache(expire = 3600 * 2)
	public List<DiscountStock> getDiscountStocks() {
		List<GroupStock> groupStockList = groupStockDao.getGroupStockListAsc(0, 20, STARTDATE);
		List<DiscountStock> discounts = new ArrayList<DiscountStock>(groupStockList.size());

		for (GroupStock groupstock : groupStockList) {
			DiscountStock stock = new DiscountStock();
			RecommendStock rs = recommendStockDao.getRecommendStockbyReportid(groupstock.getInreportid());
			stock.setSaname(rs.getSaname());
			stock.setGrade(rs.getGrade());
			stock.setCurrentprice(groupstock.getCurrentprice());
			stock.setAname(groupstock.getGroupname());
			stock.setCreatedate(DateTools.transformYYYYMMDDDate(groupstock.getIntime()));
			stock.setDiscountratio(groupstock.getGain());
			stock.setRecommendprice(groupstock.getInprice());
			stock.setStockcode(groupstock.getStockcode());
			stock.setStockname(groupstock.getStockname());
			stock.setReportid(groupstock.getInreportid());
			discounts.add(stock);
		}
		return discounts;
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
		List<DiscountStock> discounts = gg.getDiscountStocks();
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
