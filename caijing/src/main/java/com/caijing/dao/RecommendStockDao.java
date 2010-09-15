package com.caijing.dao;

import java.util.List;

import com.caijing.domain.RecommendStock;
import com.caijing.util.CrudDao;

public interface RecommendStockDao extends CrudDao {
	public List<RecommendStock> getRecommendStocksByStockcode(String stockcode);

	public List<RecommendStock> getRecommendStocksByStockname(String stockname,
			int start, int offset);

	public List<RecommendStock> getRecommendStocksBySaid(String said);

	public List<RecommendStock> getRecommendStocksBySaname(String saname,
			int start, int offset);

	public int getRecommendStocksCountsbySaname(String saname);

	public int getAllRecommendStocksCount();

	public int getAllRecommendCountBySaname(String saname);

	public List<RecommendStock> getRecommendStocks(int start, int offset);

	public RecommendStock getRecommendStockbyReportid(String reportid);

	public void updateAnalyzer(String aname, String aid);

	public List<RecommendStock> getGoodRecommendStocksBySaname(String saname,
			int start, int offset);

	public List<RecommendStock> getUncompletedRecommendStocksBySaname(
			String saname, int start, int offset);

	public List<RecommendStock> getRecommendStocksByAnalyzer(String aname,
			int start, int offset);

	public int getRecommendStockCountsByAnalyzer(String aname);
}
