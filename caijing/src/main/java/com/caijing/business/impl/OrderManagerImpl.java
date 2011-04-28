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
//��ҵ�������������ǿ�ı�ע
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
		//recharge����
		rechargeManager.recharge(userid, type, cash);
		//user�������
		webUserDao.updateRemainMoney(userid, cash);
	}

	/**
	 * ������
	 * @param orders
	 * @return
	 */
	@Override
	public void handleOrder(String userid, List<Order> orders) {
		//����money������
		float sum = 0;

		//order���Ѳ���
		for (Order order : orders) {
			orderDao.insert(order);
			sum += order.getCost();
		}
		//user������
		webUserDao.updateRemainMoney(userid, sum * -1);

		//userrightȨ�޸���

	}
	
	/** 
	 * �����������̣�
	 * 1. ��ֵ��insert Recharge���ó�ֵδ����
	 * 2. �ȴ��������еķ�������ֵ���updateRemainMoney(+)
	 * 3. ����Recharge��¼�Ķ���id, �����, updateRemainMoney(-), ��Ӳ�ƷȨ��
	 */
	public boolean orderByRecharge(String userid, String rechargeid, String orderid){
		
		
		return false;
		
	}
	
	/**
	 * 1. �����
	 * 2. updateRemainMoney(-)
	 * 3. ��Ӳ�ƷȨ��
	 * @param userid
	 * @param orderid
	 * @return
	 */
	public boolean orderByRemain(String userid, String orderid){
		//��ȡ�����ܽ��
		float sum = getOrderMoney(orderid);
		//�۳�user���
		webUserDao.updateRemainMoney(userid, sum * -1);
		//����userrightȨ��
		
		
		return true;
		
	}
	
	public float getOrderMoney(String orderid){
		float sum = 0;
		return sum;
	}
}
