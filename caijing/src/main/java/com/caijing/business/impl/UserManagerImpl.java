package com.caijing.business.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import com.caijing.business.UserManager;
import com.caijing.dao.UserDao;
import com.caijing.domain.LoginSession;
import com.caijing.domain.User;
import com.caijing.exception.LoginFailedException;
import com.caijing.exception.UserNotFoundException;

public class UserManagerImpl implements UserManager {
	private UserDao userDao;
	public LoginSession login(String username, String password)
			throws LoginFailedException {
		if (StringUtils.isEmpty(password)) {
			throw new LoginFailedException("Empty password is not allowed");
		}
		final User user=userDao.getUserByUsername(username);
		if (user==null) {
			throw new LoginFailedException("User "+username+" does not exist.");
		}
		if (DigestUtils.md5Hex(password).equals(user.getPassword())) {
			return new LoginSession() {
				public int getAccountStatus() {
					return 0;
				}
				public int getPermission() {
					return 0;
				}
				public User getUser() {
					return user;
				}
			};
		} else {
			throw new LoginFailedException("Username and password does not match.");
		}
	}
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	public User getUserByUsername(String username) throws UserNotFoundException {
		User user=userDao.getUserByUsername(username);
		if (user==null) {
			throw new UserNotFoundException("user not found for username "+username);
		}
		return user;
	}

}
