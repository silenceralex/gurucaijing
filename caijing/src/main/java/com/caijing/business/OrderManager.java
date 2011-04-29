package com.caijing.business;

import java.util.List;
import java.util.Map;

import com.caijing.domain.OrderPr;
import com.caijing.domain.Userright;


/**
 * ��ֵ�������̿���
 * recharge���� user������ order���Ѳ��� userrightȨ�޸��£�sessionˢ��
 */
public interface OrderManager {

	void saveOrder(String userid, long orderid,	List<Map<String, Object>> products);

	void saveUserright(String userid, long orderid, List<OrderPr> orderPrs);

	void orderByRecharge(String userid, long rechargeid, long orderid);

	void orderByRemain(String userid, long orderid);

	List<Userright> getUserrightsByUserid(String userid);

}
