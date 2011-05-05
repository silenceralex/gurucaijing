package com.caijing.business;

import java.util.List;

import net.sf.json.JSONArray;

import com.caijing.domain.OrderPr;
import com.caijing.domain.Userright;


/**
 * ��ֵ�������̿���
 * recharge���� user������ order���Ѳ��� userrightȨ�޸��£�sessionˢ��
 */
public interface OrderManager {

	long saveOrder(String userid, JSONArray products);

	void saveUserright(String userid, long orderid, List<OrderPr> orderPrs);

	void orderByRecharge(String userid, long rechargeid);

	void orderByRemain(String userid, long orderid);

	List<Userright> getUserrightsByUserid(String userid);

}
