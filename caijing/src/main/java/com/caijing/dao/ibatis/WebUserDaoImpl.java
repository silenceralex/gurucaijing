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
		return (WebUser) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getUserByEmail", email);
	}

	@Override
	public boolean identify(String email, String password) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("email", email);
		password = DigestUtils.md5Hex(password);
		params.put("password", password);
		if (getSqlMapClientTemplate().queryForObject(getNameSpace() + ".identify", params) != null) {
			return true;
		} else {
			return false;
		}
	}

}
