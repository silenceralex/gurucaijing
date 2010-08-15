package com.caijing.dao;

import java.util.List;

import com.caijing.domain.RecommendStock;
import com.caijing.util.CrudDao;

public interface RecommendStockDao extends CrudDao {
	List<RecommendStock> getRecommendStocksByStockcode(String stockcode);

	List<RecommendStock> getRecommendStocksBySaid(String said);
	
	public List<RecommendStock> getRecommendStocksbySaname(String saname,int start,int offset);
	public int getRecommendStocksCountsbySaname(String saname);
	
	public RecommendStock getRecommendStockbyReportid(String reportid);
}
