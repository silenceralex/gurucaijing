package com.caijing.dao;

import java.util.List;

import com.caijing.domain.Analyzer;
import com.caijing.domain.DiscountStock;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.StockAgencyEntity;
import com.caijing.util.CrudDao;

public interface RecommendStockDao extends CrudDao {
	public List<RecommendStock> getRecommendStocksByStockcode(String stockcode);

	public List<RecommendStock> getRecommendStocksByStockname(String stockname);

	public List<RecommendStock> getRecommendStocksBySaid(String said);

	public List<RecommendStock> getRecommendStocksBySaname(String saname, int start, int offset);

	public int getRecommendStocksCountsbySaname(String saname);

	public int getAllRecommendStocksCount();

	public int getAllRecommendCountBySaname(String saname);

	public List<RecommendStock> getRecommendStocks(int start, int offset);

	public RecommendStock getRecommendStockbyReportid(String reportid);

	public void updateAnalyzer(String aname, String aid);

	public int getGoodCounts(String saname);

	public List<RecommendStock> getGoodRecommendStocksBySaname(String saname, int start, int offset);

	public int getUncompletedCounts(String saname);

	public List<RecommendStock> getUncompletedRecommendStocksBySaname(String saname, int start, int offset);

	public List<RecommendStock> getRecommendStocksByAnalyzer(String aname, int start, int offset);

	public List<RecommendStock> getRecommendStocksByAnalyzerASC(String aname, int start, int offset);

	public int getRecommendStockCountsByAnalyzer(String aname);

	public List<RecommendStock> getProblemData();

	public List<DiscountStock> getDiscountStocks();

	public List<RecommendStock> getRecommendStocksGroupByCode();

	public void updateAnalyzerByReportid(String reportid, String aid);

	public List<String> getFilePathByAid(String aid, String stockcode, int length);

	public List<RecommendStock> getRecommendStocksByReportids(List<String> ids);

	public int getResearchRecommendStockCounts();

	public List<RecommendStock> getResearchRecommendStocks(int start, int length);

	public List<RecommendStock> getGradeRecommendStocks();

	/**
	 * 更新推荐状态位
	 * @param recommendid
	 * @param status
	 * @return
	 */
	public int updateStatus(String recommendid, int status);

	/**
	 * 获取结构一致之选的数据，按照推荐家数排序
	 * @param start  
	 * @param length
	 * @return
	 */
	public List<StockAgencyEntity> getTopStockAgency(int start, int length);

	/**
	 * 获取结构一致之选的数据，按照推荐家数排序
	 * @param start  
	 * @param length
	 * @param starttime 起始日期 如20101001
	 * @return
	 */
	public List<StockAgencyEntity> getTopStockAgencyAfter(int start, int length, String starttime);

	/**
	 * 获取推荐某个股票的所有机构名称
	 * @param stockcode
	 * @return
	 */
	public List<String> getSanamesByStockcode(String stockcode);

	/**
	 * 获取某个股票的在推荐或者卖出的状态上的数目
	 * @param stockcode
	 * @param status
	 * @return
	 */
	public int getRecommendCountsByStockcodeAndStatus(String stockcode, int status);

	/**
	 * 按照推荐或者卖出状态的个股研报详细信息
	 * @param status
	 * @param start
	 * @param length
	 * @return
	 */
	public List<RecommendStock> getRecommendStockByStatus(String stockcode, int status, int start, int length);

	/**
	 * 获取某个券商的去重的分析师名字
	 * @param saname
	 * @return
	 */
	public List<String> getDistinctAnalyzersBySaname(String saname);

	public List<RecommendStock> getObjectStocksByAnalyzer(Analyzer analyzer, int start, int offset);
	//	public List<RecommendStock> getRecentMonthRecommendStockByStatus(String stockcode, int status, int start, int length);
}
