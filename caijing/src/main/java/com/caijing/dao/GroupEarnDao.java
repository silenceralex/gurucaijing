package com.caijing.dao;

import java.util.Date;

import com.caijing.domain.GroupEarn;
import com.caijing.util.CrudDao;

public interface GroupEarnDao extends CrudDao {
	GroupEarn getGroupEarnByIDAndDate(String groupid, Date date);
}
