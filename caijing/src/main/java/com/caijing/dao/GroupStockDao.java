package com.caijing.dao;

import java.util.Date;
import java.util.List;

import com.caijing.domain.GroupStock;
import com.caijing.util.CrudDao;

public interface GroupStockDao extends CrudDao {
	/**
	 * ��ȡ���е�ǰ����ʦ����Ϲ�Ʊ
	 * @param groupid
	 * @return
	 */
	List<GroupStock> getCurrentStockByGroupid(String groupid);

	/**
	 * ��ȡĳ����ʦ������Ϲ�Ʊ���ݣ����������͹��ڵġ�
	 * @param groupid
	 * @return
	 */
	List<GroupStock> getAllStockByGroupid(String groupid);

	/**
	 * ��ȡĳ��ʱ��ε���Ϲ�Ʊ���ݣ����ڼ���������������
	 * @param groupid
	 * @param startdate
	 * @param enddate
	 * @return
	 */
	List<GroupStock> getCurrentStockByGroupidAndPeriod(String groupid, Date startdate, Date enddate);

	Date getEarliestIntimeByAidFrom(String aid, Date date);

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

	List<GroupStock> getGroupStockListAsc(int start, int length, String startDate);

	List<GroupStock> getGroupStockListDesc(int start, int length, String startDate);

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

	/**
	 * ���¹��ڵ�groupstock
	 * @param groupstock
	 * @return
	 */
	int updateOutOfDate(GroupStock groupstock);

	/**
	 * ���µ�����gourpstock
	 * @param groupstock
	 * @return
	 */
	int updateOutGain(GroupStock groupstock);

	/**
	 * ��ȡĳ��ʱ���֮ǰ���Ծɻ�������еĹ�Ʊ
	 * @param endDate  ʱ���
	 * @return
	 */
	List<GroupStock> getCurrentStocksBefore(Date endDate);
}
