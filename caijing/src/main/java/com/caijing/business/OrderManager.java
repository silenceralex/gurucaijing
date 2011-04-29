package com.caijing.business;

import java.util.List;
import java.util.Map;


/**
 * 充值消费流程控制
 * recharge插入 user余额更新 order消费插入 userright权限更新，session刷新
 */
public interface OrderManager {

	void saveOrder(String userid, long orderid,	List<Map<String, Object>> products);

	void saveUserright(String userid, long orderid, List<Integer> products);

	boolean orderByRemain(String userid, long orderid, List<Integer> products);

	boolean orderByRecharge(String userid, String rechargeid, long orderid);

}
