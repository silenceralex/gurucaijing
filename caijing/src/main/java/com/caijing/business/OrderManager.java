package com.caijing.business;

/**
 * 充值消费流程控制
 */
public interface OrderManager {

	/**
	 * 充值购买流程 recharge插入 user余额更新 order消费插入 userright权限更新，session刷新
	 */
	public boolean orderByRecharge();
	
	
}
