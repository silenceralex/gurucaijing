package com.caijing.business.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caijing.business.OrderManager;
import com.caijing.dao.OrderDao;
import com.caijing.dao.ProductDAO;
import com.caijing.dao.RechargeDao;
import com.caijing.dao.UserrightDAO;
import com.caijing.dao.WebUserDao;
import com.caijing.domain.OrderMeta;
import com.caijing.domain.OrderPr;
import com.caijing.domain.Product;
import com.caijing.domain.Recharge;
import com.caijing.domain.Userright;
import com.caijing.domain.WebUser;
import com.caijing.util.ServerUtil;

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

	private static final Log logger = LogFactory.getLog(OrderManagerImpl.class);

	/**
	 * 订单处理流程： 
	 * 1. 充值激活，更新余额 
	 * 2. 根据Recharge记录的订单id, 激活订单,updateRemainMoney(-), 添加产品权限
	 */
	@Override
	public void orderByRecharge(String userid, long rechargeid) {
		Recharge recharge = (Recharge) rechargeDao.select(rechargeid);
		long orderid = recharge.getOrderid();
		int status = recharge.getStatus();
		//待支付状态或支付失败状态
		if (status == 0 || status == -1) {
			//更新用户余额
			float cash = recharge.getCash();
			webUserDao.updateRemainMoney(userid, cash * +1);
			//更新充值记录的状态为成功
			recharge.setStatus(1);
			rechargeDao.update(recharge);
			orderByRemain(userid, orderid);
			return;
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
	public void orderByRemain(String userid, long orderid) {
		OrderMeta order = orderDao.selectWithOrderPr(orderid);

		//检查是否是无效订单
		if (order != null) {
			byte status = order.getStatus();
			if (status == 1) {
				return;
			}
		}

		List<OrderPr> orderPrs = order.getOrderPrs();
		if (orderPrs != null) {
			// 获取订单总金额
			float sum = order.getCost();

			// 扣除user余额(需要进行验证，以防负金额出现)
			WebUser user = ((WebUser) webUserDao.select(userid));
			float remainMoney = user.getRemain();
			if (remainMoney >= sum) {
				webUserDao.updateRemainMoney(userid, sum * -1);

				// 更新userright权限
				saveUserright(userid, orderid, orderPrs);

				//订单支付成功，订单失效
				order.setStatus((byte) 1);
				orderDao.update(order);
				return;
			}
		}
	}

	/**
	 * 更新用户权限
	 */
	@Override
	public void saveUserright(String userid, long orderid, List<OrderPr> orderPrs) {
		for (OrderPr orderPr : orderPrs) {
			Product product = (Product) productDAO.select(orderPr.getPid());
			String[] paths = product.getRightpaths().split("\\s+");
			//单个产品的购买总月份
			int totalmonth = product.getContinuedmonth() * orderPr.getNum();

			for (String path : paths) {
				Userright right = new Userright();
				right.setUid(userid);
				right.setPath(path);
				right.setIndustryid(orderPr.getIndustryid());
				Userright existedright = ((Userright) userrightDao.select(right));

				Date now = new Date();
				if (existedright != null) {
					if (existedright.getValid() == 1) {
						Date todate = existedright.getTodate();
						Date fromdate = existedright.getFromdate();
						boolean isAfter = existedright.getTodate().after(now);
						if (isAfter) {
							//fromdate = fromdate;
							Calendar cstart = Calendar.getInstance();
							cstart.setTime(todate);
							cstart.add(Calendar.MONTH, totalmonth);
							todate = cstart.getTime();
						} else {
							fromdate = now;
							Calendar cstart = Calendar.getInstance();
							cstart.setTime(fromdate);
							cstart.add(Calendar.MONTH, totalmonth);
							todate = cstart.getTime();
						}
						existedright.setFromdate(fromdate);
						existedright.setTodate(todate);
						existedright.setValid((byte) 1);
						userrightDao.updateSelective(existedright);
					}
				} else {
					right.setFromdate(now);
					Calendar cstart = Calendar.getInstance();
					cstart.setTime(now);
					cstart.add(Calendar.MONTH, totalmonth);
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
	public OrderMeta saveOrder(String userid, JSONArray products) {
		Date ctime = new Date();
		// 订单总金额
		float sum = 0;

		// insert orderPr
		long orderid = ServerUtil.getOrderID(userid); //TODO id有问题
		System.out.println("orderid:" + orderid);

		for (int i = 0; i < products.size(); i++) {
			JSONObject product = products.getJSONObject(i);
			Integer productid = product.getInt("productid");
			Integer num = product.getInt("num");
			String industryid = product.getString("industryid");

			OrderPr orderPr = new OrderPr();
			orderPr.setOrderid(orderid);
			orderPr.setPid(productid);
			orderPr.setIndustryid(industryid);
			orderPr.setNum(num);
			Product prod = (Product) productDAO.select(productid);
			if (prod == null)
				continue;
			float price = prod.getPrice();
			orderPr.setCost(price * num);
			orderPr.setCtime(ctime);
			orderDao.insertOrderPr(orderPr);
			sum += price * num;
		}

		// insert order
		OrderMeta order = new OrderMeta();
		order.setUserid(userid);
		order.setOrderid(orderid);
		order.setCtime(ctime);
		order.setLmodify(ctime);
		order.setStatus((byte) 0);
		order.setCost(sum);
		orderDao.insert(order);

		return order;
	}

	/**
	 * 返回用户权限，用于刷新session
	 */
	@Override
	public List<Userright> getUserrightsByUserid(String userid) {
		//		List<Userright> rights = userrightDao.getUserrightByUserid(userid);
		//		List<String> paths = null;
		//		if(rights!=null){
		//			for (Userright right : rights) {
		//				paths.add(right.getPath());
		//			}
		//		}
		return userrightDao.getUserrightByUserid(userid);
	}
}
