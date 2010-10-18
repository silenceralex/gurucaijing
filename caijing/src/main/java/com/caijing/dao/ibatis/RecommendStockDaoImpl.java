package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.DiscountStock;
import com.caijing.domain.RecommendStock;
import com.caijing.util.CrudDaoDefault;

public class RecommendStockDaoImpl extends CrudDaoDefault implements RecommendStockDao {

	public RecommendStock getRecommendStockbyReportid(String reportid) {
		return (RecommendStock) getSqlMapClientTemplate().queryForObject(
				getNameSpace() + ".getRecommendStockbyReportid", reportid);
	}

	public int getRecommendStocksCountsbySaname(String saname) {
		return (Integer) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getRecommendStocksCountsbySaname",
				saname);
	}

	public int getAllRecommendStocksCount() {
		return (Integer) getSqlMapClientTemplate().queryForObject(this.getNameSpace() + ".getAllRecommendStocksCount");
	}

	public List<RecommendStock> getRecommendStocks(int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("start", start);
		params.put("offset", offset);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getRecommendStocks", params);
	}

	public List<RecommendStock> getRecommendStocksBySaid(String said) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RecommendStock> getRecommendStocksByStockcode(String stockcode) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("stockcode", stockcode);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getRecommendStocksByStockcode", params);
	}

	public List<RecommendStock> getRecommendStocksByStockname(String stockname) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("stockname", stockname + "%");
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getRecommendStocksByStockname", params);
	}

	public int getAllRecommendCountBySaname(String saname) {
		return (Integer) getSqlMapClientTemplate().queryForObject(
				this.getNameSpace() + ".getAllRecommendCountBySaname", saname);
	}

	public List<RecommendStock> getRecommendStocksBySaname(String saname, int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("start", start);
		params.put("offset", offset);
		params.put("saname", saname);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getRecommendStocksBySaname", params);
	}

	public List<RecommendStock> getGoodRecommendStocksBySaname(String saname, int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("start", start);
		params.put("offset", offset);
		params.put("saname", saname);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getGoodRecommendStocksBySaname", params);
	}

	public List<RecommendStock> getUncompletedRecommendStocksBySaname(String saname, int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("start", start);
		params.put("offset", offset);
		params.put("saname", saname);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getUncompletedRecommendStocksBySaname",
				params);
	}

	public void updateAnalyzer(String aname, String aid) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("aid", aid);
		params.put("aname", "%" + aname + "%");
		getSqlMapClientTemplate().update(this.getNameSpace() + ".updateAnalyzer", params);
	}

	public int getRecommendStockCountsByAnalyzer(String aname) {
		Object obj = getSqlMapClientTemplate().queryForObject(
				this.getNameSpace() + ".getRecommendStockCountsByAnalyzer", "%" + aname + "%");
		if (obj == null) {
			return 0;
		} else {
			return (Integer) obj;
		}
	}

	public List<RecommendStock> getRecommendStocksByAnalyzer(String aname, int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("start", start);
		params.put("offset", offset);
		params.put("aname", "%" + aname + "%");
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getRecommendStocksByAnalyzer", params);
	}

	public int getGoodCounts(String saname) {
		Object obj = getSqlMapClientTemplate().queryForObject(this.getNameSpace() + ".getGoodCounts", saname);
		if (obj == null) {
			return 0;
		} else {
			return (Integer) obj;
		}
	}

	public int getUncompletedCounts(String saname) {
		Object obj = getSqlMapClientTemplate().queryForObject(this.getNameSpace() + ".getUncompletedCounts", saname);
		if (obj == null) {
			return 0;
		} else {
			return (Integer) obj;
		}
	}

	public List<RecommendStock> getProblemData() {
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getProblemData");
	}

	public List<RecommendStock> getRecommendStocksByAnalyzerASC(String aname, int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("start", start);
		params.put("offset", offset);
		params.put("aname", "%" + aname + "%");
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getRecommendStocksByAnalyzerASC", params);
	}

	@Override
	public List<DiscountStock> getDiscountStocks() {
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getDiscountStocks");
	}

}
