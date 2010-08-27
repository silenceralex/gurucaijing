package com.caijing.dao.ibatis;

import java.util.List;

import com.caijing.dao.StockDao;
import com.caijing.domain.Stock;
import com.caijing.util.CrudDaoDefault;

public class StockDaoImpl extends CrudDaoDefault implements StockDao {
	public List<Stock> getAllStock(){
		return this.getSqlMapClientTemplate().queryForList(this.getNameSpace()+".getAllStock");
	}
}
