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
	 * ��ȡ��ĳ���ڿ�ʼ����������н��׼۸�Ľ����գ�stockcode��Ʊ�ļ۸����ڳɹ��ʼ���
	 * @param stockcode ��Ʊ����
	 * @param date  
	 * @return
	 */
	public StockEarn getNearPriceByCodeDate(String stockcode, Date date);

	/**
	 * ��ȡ��ĳ���ڿ�ʼ��ǰ������н��׼۸�Ľ����գ�stockcode��Ʊ�ļ۸����ڳɹ��ʼ���
	 * @param stockcode ��Ʊ����
	 * @param date  
	 * @return
	 */
	public StockEarn getFormerNearPriceByCodeDate(String stockcode, Date date);

	public List<StockEarn> getRatiosByCodeAndPeriod(String stockcode, Date start, Date end);

}
