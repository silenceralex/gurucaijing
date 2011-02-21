package com.caijing.dao;

import java.util.Date;
import java.util.List;

import com.caijing.domain.RecommendSuccess;
import com.caijing.util.CrudDao;

public interface RecommendSuccessDao extends CrudDao {

	int getRecommendSuccessCountByAid(String aid);

	int getTotalRecommendCountByAid(String aid);

	/**
	 * Ϊ�Ƽ����ɸ����Ƿ�ﵽĿ��۵�״̬��0δ�ﵽ��1Ϊ�ﵽ,2Ϊ��δ����֤ʱ��,3Ϊ��Ŀ���
	 * @param groupStock ��Ҫ���µ�ʵ�壬
	 * @return 
	 */
	int updateIsAchieved(RecommendSuccess recommendSuccess);

	List<RecommendSuccess> getRecommendsBefore(Date date);

	/**
	 * ����aid��ȡ�Ѿ���֤��������
	 * @param aid
	 * @return
	 */
	List<RecommendSuccess> getRecommendsByAid(String aid);

	/**
	 * ĳ����ʦ���д���֤������
	 * @param aid
	 * @return
	 */
	List<RecommendSuccess> getUnvalidateRecommendsByAid(String aid);

	int getRecommendSuccessCountByAidDuring(String aid, String startDate, String endDate);

	int getTotalRecommendCountByAidDuring(String aid, String startDate, String endDate);

	/**
	 * ɾ��ĳ������ʦ������success����
	 * @param aid
	 * @return
	 */
	int deleteByAid(String aid);

	/**
	 * ���д���֤������
	 * @param aid
	 * @return
	 */
	List<RecommendSuccess> getUnvalidateRecommendsBefore(Date endDate);
}
