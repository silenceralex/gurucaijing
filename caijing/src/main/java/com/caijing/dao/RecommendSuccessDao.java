package com.caijing.dao;

import java.util.Date;
import java.util.List;

import com.caijing.domain.RecommendSuccess;
import com.caijing.util.CrudDao;

public interface RecommendSuccessDao extends CrudDao {

	int getRecommendSuccessCountByAid(String aid);

	int getTotalRecommendCountByAid(String aid);

	/**
	 * 为推荐个股更新是否达到目标价的状态，0未达到，1为达到,2为尚未到验证时间,3为无目标价
	 * @param groupStock 需要更新的实体，
	 * @return 
	 */
	int updateIsAchieved(RecommendSuccess recommendSuccess);

	List<RecommendSuccess> getRecommendsBefore(Date date);

	/**
	 * 按照aid获取已经验证过的数据
	 * @param aid
	 * @return
	 */
	List<RecommendSuccess> getRecommendsByAid(String aid);

	/**
	 * 按照aid获取已经验证过的数据
	 * @param aid
	 * @return
	 */
	List<RecommendSuccess> getRecommendsByAidBetween(String aid, String startDate, String endDate);

	/**
	 * 某分析师所有待验证的数据
	 * @param aid
	 * @return
	 */
	List<RecommendSuccess> getUnvalidateRecommendsByAid(String aid);

	int getRecommendSuccessCountByAidDuring(String aid, String startDate, String endDate);

	int getTotalRecommendCountByAidDuring(String aid, String startDate, String endDate);

	/**
	 * 删除某个分析师的所有success数据
	 * @param aid
	 * @return
	 */
	int deleteByAid(String aid);

	/**
	 * 所有待验证的数据
	 * @param aid
	 * @return
	 */
	List<RecommendSuccess> getUnvalidateRecommendsBefore(Date endDate);
}
