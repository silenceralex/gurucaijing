package com.caijing.business;

import java.util.List;

import com.caijing.domain.StockGain;

public interface StockGainManager {
	List<StockGain> getStockGainByAname(String aname, int page);

	StockGain getStockGainByRStock(String stockcode, String start, String end);

}
