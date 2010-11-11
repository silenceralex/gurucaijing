package com.caijing.dao;

import java.util.List;

import com.caijing.domain.StockEarn;
import com.caijing.util.CrudDao;

public interface StockEarnDao extends CrudDao {
	public StockEarn getStockEarnByCodeDate(String stockcode, String date);

	public List<Float> getPriceByCodeDate(String stockcode, String date);
}
