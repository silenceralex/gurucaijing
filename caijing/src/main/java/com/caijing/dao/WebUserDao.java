package com.caijing.dao;

import com.caijing.domain.WebUser;
import com.caijing.util.CrudDao;

public interface WebUserDao extends CrudDao {
	public WebUser getUserByEmail(String email);

	public boolean identify(String email, String password);

	/**
	 * 根据money的增加或减少（正负号）来更新remain余额
	 * @param userid
	 * @param money
	 * @return
	 */
	boolean updateRemainMoney(String userid, float money);
}
