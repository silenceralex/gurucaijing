package com.caijing.dao.ibatis;

import java.util.Date;
import java.util.HashMap;

import com.caijing.dao.GroupEarnDao;
import com.caijing.domain.GroupEarn;
import com.caijing.util.CrudDaoDefault;

public class GroupEarnDaoImpl extends CrudDaoDefault implements GroupEarnDao {

	@Override
	public GroupEarn getGroupEarnByIDAndDate(String groupid, Date date) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("groupid", groupid);
		params.put("date", date);
		return (GroupEarn) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getGroupEarnByIDAndDate", params);
	}

}
