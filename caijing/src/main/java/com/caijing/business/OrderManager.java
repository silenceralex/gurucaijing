package com.caijing.business;

import java.util.List;

import com.caijing.domain.Order;

/**
 * 充值消费流程控制
 * recharge插入 user余额更新 order消费插入 userright权限更新，session刷新
 */
public interface OrderManager {

	/**
	 * recharge插入 user余额更新
	 */
	public void handleRecharge(String userid, int type, float cash);
	
	/**
	 * order消费插入 userright权限更新
	 */
	void handleOrder(String userid, List<Order> orders);

	/**
	 * 非充值购买流程 recharge插入 user余额更新 order消费插入 userright权限更新，session刷新
	 */
//	boolean orderByRemain();
//	public boolean orderByRecharge(String userid, int type, float cash);

}
