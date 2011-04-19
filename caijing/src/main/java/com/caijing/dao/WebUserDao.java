package com.caijing.dao;

import com.caijing.domain.WebUser;
import com.caijing.util.CrudDao;

public interface WebUserDao extends CrudDao {
	public WebUser getUserByEmail(String email);

	public boolean identify(String email, String password);
}
