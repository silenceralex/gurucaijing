package com.caijing.business;

import java.util.List;

import net.sf.json.JSONArray;

import com.caijing.domain.OrderPr;
import com.caijing.domain.Userright;


/**
 * 充值消费流程控制
 * recharge插入 user余额更新 order消费插入 userright权限更新，session刷新
 */
public interface OrderManager {

	long saveOrder(String userid, JSONArray products);

	void saveUserright(String userid, long orderid, List<OrderPr> orderPrs);

	void orderByRecharge(String userid, long rechargeid);

	void orderByRemain(String userid, long orderid);

	List<Userright> getUserrightsByUserid(String userid);

}
