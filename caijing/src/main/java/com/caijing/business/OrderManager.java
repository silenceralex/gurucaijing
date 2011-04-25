package com.caijing.business;

import java.util.List;

import com.caijing.domain.Order;

/**
 * ��ֵ�������̿���
 * recharge���� user������ order���Ѳ��� userrightȨ�޸��£�sessionˢ��
 */
public interface OrderManager {

	/**
	 * recharge���� user������
	 */
	public void handleRecharge(String userid, int type, float cash);
	
	/**
	 * order���Ѳ��� userrightȨ�޸���
	 */
	void handleOrder(String userid, List<Order> orders);

	/**
	 * �ǳ�ֵ�������� recharge���� user������ order���Ѳ��� userrightȨ�޸��£�sessionˢ��
	 */
//	boolean orderByRemain();
//	public boolean orderByRecharge(String userid, int type, float cash);

}
