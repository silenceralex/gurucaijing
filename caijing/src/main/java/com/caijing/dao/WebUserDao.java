package com.caijing.dao;

import com.caijing.domain.WebUser;
import com.caijing.util.CrudDao;

public interface WebUserDao extends CrudDao {
	public WebUser getUserByEmail(String email);

	//-1为失败，0为尚未激活，1为成功
	public int identify(String email, String password);
	
	public boolean activate(String uid);

	/**
	 * 根据money的增加或减少（正负号）来更新remain余额
	 * @param userid
	 * @param money
	 * @return
	 */
	boolean updateRemainMoney(String userid, float money);
}
