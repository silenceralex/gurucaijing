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

	/**
	 *  ��ȡĳ��Ʊ���������ʣ�ǰ�����
	 * @param stockcode
	 * @param start
	 * @param end
	 * @return
	 */
	public List<StockEarn> getRatiosByCodeAndPeriod(String stockcode, Date start, Date end);

	/**
	 * ��ȡĳ��Ʊ���������ʣ�ǰ�պ��
	 * @param stockcode
	 * @param start
	 * @param end
	 * @return
	 */
	public List<StockEarn> getRatiosByCodeInPeriod(String stockcode, Date start, Date end);

}
