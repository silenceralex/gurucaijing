package com.caijing.dao.ibatis;

import java.util.List;


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

}
