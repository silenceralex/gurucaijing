package com.caijing.dao.ibatis;

import java.util.List;

import com.caijing.dao.RechargeDao;
import com.caijing.domain.Recharge;
import com.caijing.util.CrudDaoDefault;

public class RechargeDaoImpl extends CrudDaoDefault implements RechargeDao {

	@Override
	public float getTotalRechargeByUser(String userid) {
		Object total = getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getTotalRechargeByUser", userid);
		if (total == null)
			return 0;
		else {
			return (Float) total;
		}
	}

	@Override
	public List<Recharge> getRechargeByUser(String userid) {
		return (List<Recharge>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getRechargeByUser", userid);
	}

}
