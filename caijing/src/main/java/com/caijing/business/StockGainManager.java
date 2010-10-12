package com.caijing.business;

import java.util.List;

import com.caijing.domain.StockGain;

public interface StockGainManager {
	List<StockGain> getStockGainByAname(String aname, int page);

	List<StockGain> getStockGainByAnameASC(String aname);

	StockGain getZSGainByPeriod(String startdate, String enddate);

	StockGain getStockGainByRStock(String stockcode, String start, String end);

}
