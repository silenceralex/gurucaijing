package com.caijing.dao;

import java.util.Date;
import java.util.List;

import com.caijing.domain.GroupStock;
import com.caijing.util.CrudDao;

public interface GroupStockDao extends CrudDao {
	List<GroupStock> getCurrentStockByGroupid(String groupid);

	Date getEarliestIntimeByAid(String aid);

	/**
	 * �ķ����������Ƽ�����������״��
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
	 * �ķ�����������groupstock������
	 * @param groupid
	 * @param stockcode
	 * @return
	 */
	GroupStock getStockByGroupidAndStockcode(String groupid, String stockcode);

	/**
	 * Ϊ�Ƽ����ɸ����Ƿ�ﵽĿ��۵�״̬��0δ�ﵽ��1Ϊ�ﵽ,2Ϊ��δ����֤ʱ��,3Ϊ��Ŀ���
	 * @param groupStock ��Ҫ���µ�ʵ�壬
	 * @return 
	 */
	int updateObjectAchieved(GroupStock groupStock);
}
