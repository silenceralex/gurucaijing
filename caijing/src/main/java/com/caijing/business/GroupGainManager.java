package com.caijing.business;

import com.caijing.domain.RecommendStock;

public interface GroupGainManager {

	public void extractGroupStock(RecommendStock rs);

	public void fillGroupEarn(String aid);
}
