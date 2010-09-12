package com.caijing.model;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.StockGain;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;

public class Caculater {

	private StockPrice sp=new StockPrice();
	
	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;
	
	public void caculateSaname(String saname){
		List<RecommendStock> lists= recommendStockDao.getGoodRecommendStocksBySaname(saname, 0, 10);
		for(RecommendStock rs:lists){
			String date=rs.getCreatedate();
			rs.getSaname();
			String tmp=rs.getCreatedate();
			tmp=tmp.substring(0, 4)+"-"+tmp.substring(4, 6)+"-"+tmp.substring(6,8);
			System.out.println("推荐股票名称:"+rs.getStockname());
			System.out.println("start date:"+tmp);
			rs.getObjectprice();
			Date now=new Date();
			StockGain sg=sp.getStockGainByPeriod(rs.getStockcode(), tmp, DateTools.transformYYYYMMDDDate(new Date()));
			sg.setAnalyzer(rs.getAname());
			sg.setSaname(rs.getSaname());
		}
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
		Caculater caculater=new Caculater();
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
