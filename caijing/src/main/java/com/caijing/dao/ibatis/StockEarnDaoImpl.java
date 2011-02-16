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

	public StockEarn getNearPriceByCodeDate(String stockcode, Date date) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("stockcode", stockcode);
		params.put("date", date);
		return (StockEarn) this.getSqlMapClientTemplate().queryForObject(
				this.getNameSpace() + ".getNearPriceByCodeDate", params);
	}

	@Override
	public List<StockEarn> getRatiosByCodeAndPeriod(String stockcode, Date start, Date end) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("stockcode", stockcode);
		params.put("start", start);
		params.put("end", end);
		return (List<StockEarn>) this.getSqlMapClientTemplate().queryForList(
				this.getNameSpace() + ".getRatiosByCodeAndPeriod", params);
	}

	@Override
	public StockEarn getFormerNearPriceByCodeDate(String stockcode, Date date) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("stockcode", stockcode);
		params.put("date", date);
		return (StockEarn) this.getSqlMapClientTemplate().queryForObject(
				this.getNameSpace() + ".getFormerNearPriceByCodeDate", params);
	}

	@Override
	public List<Date> getDatesByZSFrom(Date startDate, Date endDate) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		return (List<Date>) this.getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getDatesByZSFrom",
				params);
	}

	@Override
	public List<StockEarn> getPriceByCodePeriod(String stockcode, Date startDate, Date endDate) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("stockcode", stockcode);
		return (List<StockEarn>) this.getSqlMapClientTemplate().queryForList(
				this.getNameSpace() + ".getPriceByCodePeriod", params);
	}
}
