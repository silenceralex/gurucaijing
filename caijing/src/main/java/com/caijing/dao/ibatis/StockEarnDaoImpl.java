package com.caijing.dao.ibatis;

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
	public List<Float> getPriceByCodeDate(String stockcode, String date) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("stockcode", stockcode);
		params.put("date", date);
		return (List<Float>) this.getSqlMapClientTemplate().queryForObject(this.getNameSpace() + ".getPriceByCodeDate",
				params);
	}

}
