package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.RecommendStock;
import com.caijing.util.CrudDaoDefault;

public class RecommendStockDaoImpl extends CrudDaoDefault implements
		RecommendStockDao {

	public List<RecommendStock> getRecommendStocksBySaid(String said) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RecommendStock> getRecommendStocksByStockcode(String stockcode) {
		// TODO Auto-generated method stub
		return null;
	}

	public RecommendStock getRecommendStockbyReportid(String reportid) {
		return (RecommendStock) getSqlMapClientTemplate().queryForObject(
				getNameSpace() + ".getRecommendStockbyReportid", reportid);
	}

	public int getRecommendStocksCountsbySaname(String saname) {
		return (Integer) getSqlMapClientTemplate().queryForObject(
				getNameSpace() + ".getRecommendStocksCountsbySaname", saname);
	}

	public List<RecommendStock> getRecommendStocksbySaname(String saname,
			int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("saname", saname);
		params.put("start", start);
		params.put("offset", offset);
		return getSqlMapClientTemplate().queryForList(
				this.getNameSpace() + ".getRecommendStocksbySaname", params);
	}

}
