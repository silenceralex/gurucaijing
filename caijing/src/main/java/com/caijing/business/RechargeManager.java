package com.caijing.business;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.caijing.domain.Recharge;
import com.caijing.domain.WebUser;

public interface RechargeManager {

	public Recharge recharge(String userid, int type, float cash, Long orderid);

	@Transactional
	public WebUser confirm(long rcid, int status);

	public List<Recharge> getRechargeByUserid(String userid);

	public float getTotalByUserid(String userid);
}
