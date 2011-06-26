package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.caijing.dao.WebUserDao;
import com.caijing.domain.WebUser;
import com.caijing.util.CrudDaoDefault;

public class WebUserDaoImpl extends CrudDaoDefault implements WebUserDao {

	@Override
	public WebUser getUserByEmail(String email) {
		return (WebUser) getSqlMapClientTemplate().queryForObject(
				getNameSpace() + ".getUserByEmail", email);
	}

	@Override
	public int identify(String email, String password) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("email", email);
		password = DigestUtils.md5Hex(password);
		params.put("password", password);
		WebUser user = (WebUser) getSqlMapClientTemplate().queryForObject(
				getNameSpace() + ".identify", params);
		if (user != null) {
			return 1;
		} else if (user.getStatus() == 0) {
			return 0;
		} else
			return -1;
	}

	@Override
	public boolean updateRemainMoney(String userid, float money) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("uid", userid);
		params.put("money", money);
		int row = getSqlMapClientTemplate().update(
				getNameSpace() + ".updateRemainMoney", params);
		return row == 1;
	}

	@Override
	public boolean activate(String uid) {
		getSqlMapClientTemplate().update(getNameSpace() + ".activate", uid);
		return true;
	}

}
