package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;


import com.caijing.dao.UserDao;
import com.caijing.domain.User;
import com.caijing.util.CrudDaoDefault;

public class UserDaoImpl extends CrudDaoDefault implements UserDao {


	public User getUserByUsername(String username)  {
		return (User)getSqlMapClientTemplate().queryForObject(getNameSpace()+".getUserByUsername",username);
	}

	@SuppressWarnings("unchecked")
	public List<User> searchUsersByNickname(String nickname) {
		return (List<User>)getSqlMapClientTemplate().queryForList(getNameSpace()+".searchUsersByNickname","%"+nickname+"%");
	}

	
	public boolean identify(String username,String password) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("username", username);
		password=DigestUtils.md5Hex(password);
		params.put("username", password);
		if(getSqlMapClientTemplate().queryForObject(getNameSpace()+".identify",params)!=null){
			return true;
		}else{
			return false;
		}
	}
}
