package com.caijing.business;

import java.util.Date;

import com.caijing.domain.RecommendStock;

/**
 * 用于处理历史研报的验证，用于每日计算到验证期的推荐成功率
 * @author chenjun
 *
 */
public interface RecommendSuccessManager {
	/**
	 * 对原本推荐日期在endDate之前（即已经到验证期）的RecommendSuccess进行成功与否的验证
	 * @param endDate  截止日期
	 */
	public void processValidatedRecommendSuccess(Date endDate);

	public void extractRecommendSuccess(RecommendStock rs);
}
