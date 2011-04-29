package com.caijing.business.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import com.caijing.domain.OrderPr;
import com.caijing.domain.Product;
import com.caijing.domain.Recharge;
import com.caijing.domain.Userright;
import com.caijing.domain.WebUser;

@Transactional(readOnly = false)
@Service("orderManager")
// 对业务类进行事务增强的标注
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

	/**
	 * 订单处理流程： 
	 * 1. 充值，insert Recharge，该充值未激活 
	 * 2.等待接收银行的反馈，充值激活，updateRemainMoney(+) 
	 * 3. 根据Recharge记录的订单id, 激活订单,updateRemainMoney(-), 添加产品权限
	 */
	@Override
	public boolean orderByRecharge(String userid, long rechargeid, long orderid) {
		Recharge recharge = (Recharge) rechargeDao.select(rechargeid);
		if (recharge.getStatus() == 1) {
			Order order = orderDao.selectWithOrderPr(orderid);
			List<OrderPr> orderPrs = order.getOrderPrs();
			List<Integer> products = null;
			if (orderPrs != null) {
				products = new LinkedList<Integer>();
				for (OrderPr orderPr : orderPrs) {
					Integer pid = orderPr.getPid();
					products.add(pid);
				}
			}
			boolean isok = orderByRemain(userid, orderid, products);
			return isok;
		} else {
			return false;
		}
	}

	/**
	 * 1. 激活订单 2. updateRemainMoney(-) 3. 添加产品权限
	 * 
	 * @param userid
	 * @param orderid
	 * @return
	 */
	@Override
	public boolean orderByRemain(String userid, long orderid,
			List<Integer> products) {
		// 获取订单总金额
		Order order = (Order) orderDao.select(orderid);
		float sum = order.getCost();

		// 扣除user余额(需要进行验证，以防负金额出现)
		WebUser user = ((WebUser) webUserDao.select(orderid));
		float remainMoney = user.getRemain();
		if (remainMoney >= sum) {
			webUserDao.updateRemainMoney(userid, sum * -1);

			// 更新userright权限
			saveUserright(userid, orderid, products);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 更新用户权限
	 * 
	 * @param userid
	 * @param orderid
	 * @param products
	 * @return
	 */
	@Override
	public void saveUserright(String userid, long orderid,
			List<Integer> products) {
		for (Integer pid : products) {
			Product product = (Product) productDAO.select(pid);
			String[] paths = product.getRightpaths().split("\\s+");
			int continuedmonth = product.getContinuedmonth();

			for (String path : paths) {
				Userright right = new Userright();
				right.setUid(userid);
				right.setPath(path);
				right = ((Userright) userrightDao.select(right));

				Date now = new Date();

				if (right != null) {
					if (right.getValid() == 1) {
						Date todate = right.getTodate();
						Date fromdate = right.getFromdate();
						boolean isAfter = right.getTodate().after(now);
						if (isAfter) {
							fromdate = fromdate; // TODO
							Calendar cstart = Calendar.getInstance();
							cstart.setTime(todate);
							cstart.add(Calendar.MONTH, continuedmonth);
							todate = cstart.getTime();
						} else {
							fromdate = now;
							Calendar cstart = Calendar.getInstance();
							cstart.setTime(fromdate);
							cstart.add(Calendar.MONTH, continuedmonth);
							todate = cstart.getTime();
						}
						right.setFromdate(fromdate);
						right.setTodate(todate);
						right.setValid((byte) 1);
						// right.setIndustry(industry); //TODO
						userrightDao.updateSelective(right);
					}
				} else {
					right = new Userright();
					right.setUid(userid);
					right.setPath(path);
					right.setFromdate(now);
					// right.setIndustry(industry); //TODO
					Calendar cstart = Calendar.getInstance();
					cstart.setTime(now);
					cstart.add(Calendar.MONTH, continuedmonth);
					right.setTodate(cstart.getTime());
					right.setValid((byte) 1);
					userrightDao.insert(right);
				}
			}
		}
	}

	/**
	 * 保存订单
	 */
	@Override
	public void saveOrder(String userid, long orderid,
			List<Map<String, Object>> products) {
		Date ctime = new Date();
		// 订单总金额
		float sum = 0;

		// insert orderPr
		for (Map<String, Object> product : products) {
			Integer pid = (Integer) product.get("pid");
			Integer num = (Integer) product.get("num");
			OrderPr orderPr = new OrderPr();
			orderPr.setOrderid(orderid);
			orderPr.setPid(pid);
			orderPr.setIndustryid((Integer) product.get("industryid"));
			orderPr.setNum(num);
			float price = ((Product) productDAO.select(pid)).getPrice();
			orderPr.setCost(price * num);
			orderPr.setCtime(ctime);
			orderDao.insertOrderPr(orderPr);
			sum += price * num;
		}

		// insert order
		Order order = new Order();
		order.setUserid(userid);
		order.setOrderid(orderid);
		order.setCtime(ctime);
		order.setLmodify(ctime);
		order.setStatus((byte) 0);
		order.setCost(sum);
		orderDao.insert(order);
	}
}
