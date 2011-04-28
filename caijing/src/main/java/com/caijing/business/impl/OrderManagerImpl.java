package com.caijing.business.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caijing.business.OrderManager;
import com.caijing.business.RechargeManager;
import com.caijing.dao.OrderDao;
import com.caijing.dao.ProductDAO;
import com.caijing.dao.RechargeDao;
import com.caijing.dao.UserrightDAO;
import com.caijing.dao.WebUserDao;
import com.caijing.domain.Order;

@Transactional(readOnly = false)
@Service("orderManager")
//对业务类进行事务增强的标注
public class OrderManagerImpl implements OrderManager {

	@Autowired
	@Qualifier("webUserDao")
	private WebUserDao webUserDao = null;

	@Autowired
	@Qualifier("rechargeDao")
	private RechargeDao rechargeDao;

	@Autowired
	@Qualifier("orderDao")
	private OrderDao orderDao;

	@Autowired
	@Qualifier("userrightDAO")
	private UserrightDAO userrightDao;

	@Autowired
	@Qualifier("productDAO")
	private ProductDAO productDAO;

	@Autowired
	@Qualifier("rechargeManager")
	private RechargeManager rechargeManager = null;

	private static final Log logger = LogFactory.getLog(OrderManagerImpl.class);

	@Override
	public void handleRecharge(String userid, int type, float cash) {
		//recharge插入
		rechargeManager.recharge(userid, type, cash);
		//user余额增加
		webUserDao.updateRemainMoney(userid, cash);
	}

	/**
	 * 处理订单
	 * @param orders
	 * @return
	 */
	@Override
	public void handleOrder(String userid, List<Order> orders) {
		//消费money的总数
		float sum = 0;

		//order消费插入
		for (Order order : orders) {
			orderDao.insert(order);
			sum += order.getCost();
		}
		//user余额减少
		webUserDao.updateRemainMoney(userid, sum * -1);

		//userright权限更新

	}
	
	/** 
	 * 订单处理流程：
	 * 1. 充值，insert Recharge，该充值未激活
	 * 2. 等待接收银行的反馈，充值激活，updateRemainMoney(+)
	 * 3. 根据Recharge记录的订单id, 激活订单, updateRemainMoney(-), 添加产品权限
	 */
	public boolean orderByRecharge(String userid, String rechargeid, String orderid){
		
		
		return false;
		
	}
	
	/**
	 * 1. 激活订单
	 * 2. updateRemainMoney(-)
	 * 3. 添加产品权限
	 * @param userid
	 * @param orderid
	 * @return
	 */
	public boolean orderByRemain(String userid, String orderid){
		//获取订单总金额
		float sum = getOrderMoney(orderid);
		//扣除user余额
		webUserDao.updateRemainMoney(userid, sum * -1);
		//更新userright权限
		
		
		return true;
		
	}
	
	public float getOrderMoney(String orderid){
		float sum = 0;
		return sum;
	}
}
