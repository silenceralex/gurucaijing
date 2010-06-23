package com.caijing.business;

import com.caijing.domain.LoginSession;
import com.caijing.domain.User;
import com.caijing.exception.LoginFailedException;
import com.caijing.exception.UserNotFoundException;

public interface UserManager {
	public LoginSession login(String username, String password) throws LoginFailedException ;
	public User getUserByUsername(String username) throws UserNotFoundException;
}
