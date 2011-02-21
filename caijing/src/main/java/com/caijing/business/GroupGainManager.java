package com.caijing.business;

import java.util.Date;

import com.caijing.domain.RecommendStock;

public interface GroupGainManager {

	public void extractGroupStock(RecommendStock rs);

	public void fillGroupEarn(String aid);

	public void processGroupStockOutDate(Date endDate);
}
