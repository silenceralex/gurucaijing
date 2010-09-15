package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.RecommendStock;
import com.caijing.util.CrudDaoDefault;

public class RecommendStockDaoImpl extends CrudDaoDefault implements
		RecommendStockDao {

	public RecommendStock getRecommendStockbyReportid(String reportid) {
		return (RecommendStock) getSqlMapClientTemplate().queryForObject(
				getNameSpace() + ".getRecommendStockbyReportid", reportid);
	}

	public int getRecommendStocksCountsbySaname(String saname) {
		return (Integer) getSqlMapClientTemplate().queryForObject(
				getNameSpace() + ".getRecommendStocksCountsbySaname", saname);
	}

	public int getAllRecommendStocksCount() {
		return (Integer) getSqlMapClientTemplate().queryForObject(
				this.getNameSpace() + ".getAllRecommendStocksCount");
	}

	public List<RecommendStock> getRecommendStocks(int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("start", start);
		params.put("offset", offset);
		return getSqlMapClientTemplate().queryForList(
				this.getNameSpace() + ".getRecommendStocks", params);
	}

	public List<RecommendStock> getRecommendStocksBySaid(String said) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RecommendStock> getRecommendStocksByStockcode(String stockcode) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getAllRecommendCountBySaname(String saname) {
		return (Integer) getSqlMapClientTemplate().queryForObject(
				this.getNameSpace() + ".getAllRecommendCountBySaname", saname);
	}

	public List<RecommendStock> getRecommendStocksBySaname(String saname,
			int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("start", start);
		params.put("offset", offset);
		params.put("saname", saname);
		return getSqlMapClientTemplate().queryForList(
				this.getNameSpace() + ".getRecommendStocksBySaname", params);
	}

	public List<RecommendStock> getRecommendStocksByStockname(String stockname,
			int start, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RecommendStock> getGoodRecommendStocksBySaname(String saname,
			int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("start", start);
		params.put("offset", offset);
		params.put("saname", saname);
		return getSqlMapClientTemplate()
				.queryForList(
						this.getNameSpace() + ".getGoodRecommendStocksBySaname",
						params);
	}

	public List<RecommendStock> getUncompletedRecommendStocksBySaname(
			String saname, int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("start", start);
		params.put("offset", offset);
		params.put("saname", saname);
		return getSqlMapClientTemplate().queryForList(
				this.getNameSpace() + ".getUncompletedRecommendStocksBySaname",
				params);
	}

	public void updateAnalyzer(String aname, String aid) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("aid", aid);
		params.put("aname", "%" + aname + "%");
		getSqlMapClientTemplate().update(
				this.getNameSpace() + ".updateAnalyzer", params);
	}

	public int getRecommendStockCountsByAnalyzer(String aname) {
		Object obj = getSqlMapClientTemplate().queryForObject(
				this.getNameSpace() + ".getRecommendStockCountsByAnalyzer", "%"+aname+"%");
		if (obj == null) {
			return 0;
		}else{
			return (Integer) obj;
		}
	}

	public List<RecommendStock> getRecommendStocksByAnalyzer(String aname,
			int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("start", start);
		params.put("offset", offset);
		params.put("aname", "%"+aname+"%");
		return getSqlMapClientTemplate().queryForList(
				this.getNameSpace() + ".getRecommendStocksByAnalyzer", params);
	}

}
