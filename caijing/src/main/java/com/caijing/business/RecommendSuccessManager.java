package com.caijing.business;

import java.util.Date;

import com.caijing.domain.RecommendStock;

/**
 * ���ڴ�����ʷ�б�����֤������ÿ�ռ��㵽��֤�ڵ��Ƽ��ɹ���
 * @author chenjun
 *
 */
public interface RecommendSuccessManager {
	/**
	 * ��ԭ���Ƽ�������endDate֮ǰ�����Ѿ�����֤�ڣ���RecommendSuccess���гɹ�������֤
	 * @param endDate  ��ֹ����
	 */
	public void processValidatedRecommendSuccess(Date endDate);

	public void extractRecommendSuccess(RecommendStock rs);
}
