package com.caijing.dao.ibatis;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.caijing.dao.GroupEarnDao;
import com.caijing.domain.GroupEarn;
import com.caijing.util.CrudDaoDefault;

public class GroupEarnDaoImpl extends CrudDaoDefault implements GroupEarnDao {

	@Override
	public GroupEarn getGroupEarnByIDAndDate(String groupid, String date) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("groupid", groupid);
		params.put("date", date);
		return (GroupEarn) getSqlMapClientTemplate()
				.queryForObject(getNameSpace() + ".getGroupEarnByIDAndDate", params);
	}

	@Override
	public Date getLatestDate() {
		return (Date) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getLatestDate");
	}

	public List<GroupEarn> getGroupEarnRankByDate(Date date) {
		return (List<GroupEarn>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getGroupEarnRankByDate",
				date);
	}

	@Override
	public List<Float> getWeightList(String aid, Date date) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("aid", aid);
		params.put("startdate", date);
		return (List<Float>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getWeightList", params);
	}

}
