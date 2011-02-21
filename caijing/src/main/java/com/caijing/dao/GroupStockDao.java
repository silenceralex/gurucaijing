package com.caijing.dao;

import java.util.Date;
import java.util.List;

import com.caijing.domain.GroupStock;
import com.caijing.util.CrudDao;

public interface GroupStockDao extends CrudDao {
	/**
	 * 获取所有当前分析师的组合股票
	 * @param groupid
	 * @return
	 */
	List<GroupStock> getCurrentStockByGroupid(String groupid);

	/**
	 * 获取某分析师所有组合股票数据，包括调出和过期的。
	 * @param groupid
	 * @return
	 */
	List<GroupStock> getAllStockByGroupid(String groupid);

	/**
	 * 获取某个时间段的组合股票数据，用于计算年度组合收益率
	 * @param groupid
	 * @param startdate
	 * @param enddate
	 * @return
	 */
	List<GroupStock> getCurrentStockByGroupidAndPeriod(String groupid, Date startdate, Date enddate);

	Date getEarliestIntimeByAidFrom(String aid, Date date);

	/**
	 * 改方法不包括推荐后又卖出的状况
	 * @param groupid
	 * @param stockcode
	 * @return
	 */
	GroupStock getCurrentStockByGroupidAndStockcode(String groupid, String stockcode);

	List<GroupStock> getNameAndCodeByAid(String aid);

	List<GroupStock> getAllGroupStock();

	int updateStockGain(GroupStock groupStock);

	List<GroupStock> getGroupStockListAsc(int start, int length, String startDate);

	List<GroupStock> getGroupStockListDesc(int start, int length, String startDate);

	List<String> getRecommendReportids(int start, int length);

	int getRecommendReportCount();

	/**
	 * 改方法包括所有groupstock的数据
	 * @param groupid
	 * @param stockcode
	 * @return
	 */
	GroupStock getStockByGroupidAndStockcode(String groupid, String stockcode);

	/**
	 * 为推荐个股更新是否达到目标价的状态，0未达到，1为达到,2为尚未到验证时间,3为无目标价
	 * @param groupStock 需要更新的实体，
	 * @return 
	 */
	int updateObjectAchieved(GroupStock groupStock);

	/**
	 * 更新过期的groupstock
	 * @param groupstock
	 * @return
	 */
	int updateOutOfDate(GroupStock groupstock);

	/**
	 * 更新调出的gourpstock
	 * @param groupstock
	 * @return
	 */
	int updateOutGain(GroupStock groupstock);

	/**
	 * 获取某个时间点之前，仍旧还在组合中的股票
	 * @param endDate  时间点
	 * @return
	 */
	List<GroupStock> getCurrentStocksBefore(Date endDate);
}
