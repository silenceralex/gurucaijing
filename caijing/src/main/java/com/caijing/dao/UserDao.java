package com.caijing.dao;

import java.util.List;

import com.caijing.domain.User;
import com.caijing.util.CrudDao;

public interface UserDao extends CrudDao {
	public User getUserByUsername(String username) ;
	public List<User> searchUsersByNickname(String nickname) ;
}
