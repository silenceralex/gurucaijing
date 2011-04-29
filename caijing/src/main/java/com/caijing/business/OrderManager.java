package com.caijing.business;

import java.util.List;
import java.util.Map;


/**
 * ��ֵ�������̿���
 * recharge���� user������ order���Ѳ��� userrightȨ�޸��£�sessionˢ��
 */
public interface OrderManager {

	void saveOrder(String userid, long orderid,	List<Map<String, Object>> products);

	void saveUserright(String userid, long orderid, List<Integer> products);

	boolean orderByRemain(String userid, long orderid, List<Integer> products);

	boolean orderByRecharge(String userid, String rechargeid, long orderid);

}
