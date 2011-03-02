package com.caijing.dao;

import java.util.Date;
import java.util.List;

import com.caijing.domain.StockEarn;
import com.caijing.util.CrudDao;

public interface StockEarnDao extends CrudDao {
	public StockEarn getStockEarnByCodeDate(String stockcode, String date);

	public List<StockEarn> getPriceByCodeDate(String stockcode, String date);

	public List<StockEarn> getPriceByCodePeriod(String stockcode, Date startDate, Date endDate);

	public List<StockEarn> getRatiosByCodeFromDate(String stockcode, String date);

	public List<Date> getDatesByZSFrom(Date startDate, Date endDate);

	public float getCurrentPriceByCode(String stockcode);

	/**
	 * 获取从某日期开始往后最近的有交易价格的交易日，stockcode股票的价格，用于成功率计算
	 * @param stockcode 股票代码
	 * @param date  
	 * @return
	 */
	public StockEarn getNearPriceByCodeDate(String stockcode, Date date);

	/**
	 * 获取从某日期开始往前最近的有交易价格的交易日，stockcode股票的价格，用于成功率计算
	 * @param stockcode 股票代码
	 * @param date  
	 * @return
	 */
	public StockEarn getFormerNearPriceByCodeDate(String stockcode, Date date);

	/**
	 *  获取某股票区间收益率，前开后闭
	 * @param stockcode
	 * @param start
	 * @param end
	 * @return
	 */
	public List<StockEarn> getRatiosByCodeAndPeriod(String stockcode, Date start, Date end);

	/**
	 * 获取某股票区间收益率，前闭后闭
	 * @param stockcode
	 * @param start
	 * @param end
	 * @return
	 */
	public List<StockEarn> getRatiosByCodeInPeriod(String stockcode, Date start, Date end);

}
