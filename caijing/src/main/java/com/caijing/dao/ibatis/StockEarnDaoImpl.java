package com.caijing.dao.ibatis;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.caijing.dao.StockEarnDao;
import com.caijing.domain.StockEarn;
import com.caijing.util.CrudDaoDefault;

public class StockEarnDaoImpl extends CrudDaoDefault implements StockEarnDao {

	@Override
	public StockEarn getStockEarnByCodeDate(String stockcode, String date) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("stockcode", stockcode);
		params.put("date", date);
		return (StockEarn) this.getSqlMapClientTemplate().queryForObject(
				this.getNameSpace() + ".getStockEarnByCodeDate", params);
	}

	@Override
	public List<StockEarn> getPriceByCodeDate(String stockcode, String date) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("stockcode", stockcode);
		params.put("date", date);
		return (List<StockEarn>) this.getSqlMapClientTemplate().queryForList(
				this.getNameSpace() + ".getPriceByCodeDate", params);
	}

	@Override
	public List<StockEarn> getRatiosByCodeFromDate(String stockcode, String date) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("stockcode", stockcode);
		params.put("date", date);
		return (List<StockEarn>) this.getSqlMapClientTemplate().queryForList(
				this.getNameSpace() + ".getRatiosByCodeFromDate", params);
	}

	@Override
	public float getCurrentPriceByCode(String stockcode) {
		return (Float) this.getSqlMapClientTemplate().queryForObject(this.getNameSpace() + ".getCurrentPriceByCode",
				stockcode);
	}

	public float getNearPriceByCodeDate(String stockcode, Date date) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("stockcode", stockcode);
		params.put("date", date);
		return (Float) this.getSqlMapClientTemplate().queryForObject(this.getNameSpace() + ".getNearPriceByCodeDate",
				params);
	}
}
