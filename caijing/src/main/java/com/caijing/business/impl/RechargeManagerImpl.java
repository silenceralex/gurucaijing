package com.caijing.business.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.caijing.business.RechargeManager;
import com.caijing.dao.RechargeDao;
import com.caijing.dao.WebUserDao;
import com.caijing.domain.Recharge;
import com.caijing.domain.WebUser;
import com.caijing.util.ServerUtil;

@Component("rechargeManager")
public class RechargeManagerImpl implements RechargeManager {

	@Autowired
	@Qualifier("webUserDao")
	private WebUserDao webUserDao = null;

	@Autowired
	@Qualifier("rechargeDao")
	private RechargeDao rechargeDao = null;

	private static final Log logger = LogFactory.getLog(RechargeManagerImpl.class);

	@Override
	public Recharge recharge(String userid, int type, float cash, Long orderid) {
		Recharge recharge = new Recharge();
		recharge.setRcid(ServerUtil.getOrderID(userid));
		recharge.setUserid(userid);
		recharge.setType(type);
		recharge.setCash(cash);
		recharge.setOrderid(orderid);
		try {
			rechargeDao.insert(recharge);
			logger.debug("userid:" + userid + "  recharge success!");
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("userid:" + userid + "  recharge failed!");
		}
		return (Recharge) rechargeDao.select(recharge.getRcid());
	}

	public WebUser confirm(long rcid, int status) {
		Recharge recharge = (Recharge) rechargeDao.select(rcid);
		recharge.setStatus(status);
		recharge.setLmodify(new Date());
		rechargeDao.update(recharge);
		if (status == 1) {
			WebUser user = (WebUser) webUserDao.select(recharge.getUserid());
			user.setRemain(user.getRemain() + recharge.getCash());
			user.setLmodify(new Date());
			webUserDao.update(user);
		}
		return (WebUser) webUserDao.select(recharge.getUserid());
	}

	@Override
	public List<Recharge> getRechargeByUserid(String userid) {
		return rechargeDao.getRechargeByUser(userid);
	}

	@Override
	public float getTotalByUserid(String userid) {
		return rechargeDao.getTotalRechargeByUser(userid);
	}

	@Override
	public int getCountByUserid(String userid) {
		return rechargeDao.getCountByUserid(userid);
	}

}
