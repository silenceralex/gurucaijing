package com.caijing.dao;

import java.util.List;

import com.caijing.domain.Recharge;
import com.caijing.util.CrudDao;

public interface RechargeDao extends CrudDao {
	public float getTotalRechargeByUser(String userid);

	public int getCountByUserid(String userid);

	public List<Recharge> getRechargeByUser(String userid);
}
