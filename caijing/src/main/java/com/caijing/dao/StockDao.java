package com.caijing.dao;

import java.util.List;

import com.caijing.domain.Stock;
import com.caijing.util.CrudDao;

public interface StockDao extends CrudDao {
	public List<Stock> getAllStock();
}
