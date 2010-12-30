package com.caijing.dao;

import java.util.Date;
import java.util.List;

import com.caijing.domain.GroupStock;
import com.caijing.util.CrudDao;

public interface GroupStockDao extends CrudDao {
	List<GroupStock> getCurrentStockByGroupid(String groupid);

	Date getEarliestIntimeByAid(String aid);

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

	List<GroupStock> getGroupStockListAsc(int start, int length);

	List<GroupStock> getGroupStockListDesc(int start, int length);

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
}
