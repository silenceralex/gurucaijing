package com.caijing.dao;

import java.util.Date;
import java.util.List;

import com.caijing.domain.StockEarn;
import com.caijing.util.CrudDao;

public interface StockEarnDao extends CrudDao {
	public StockEarn getStockEarnByCodeDate(String stockcode, String date);

	public List<StockEarn> getPriceByCodeDate(String stockcode, String date);

	public List<StockEarn> getRatiosByCodeFromDate(String stockcode, String date);

	public float getCurrentPriceByCode(String stockcode);

	/**
	 * 获取距离某日期最近的有交易价格的交易日，stockcode股票的价格，用于成功率计算
	 * @param stockcode 股票代码
	 * @param date  
	 * @return
	 */
	public StockEarn getNearPriceByCodeDate(String stockcode, Date date);

}
