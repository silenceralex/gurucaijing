package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.UserrightDAO;
import com.caijing.domain.Userright;
import com.caijing.util.CrudDaoDefault;

public class UserrightDAOImpl extends CrudDaoDefault implements UserrightDAO {

	@Override
	public List<String> getIndustriesByUserid(String uid, String path) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("uid", uid);
		params.put("path", path);
		return (List<String>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getIndustriesByUserid", params);
	}

	@Override
	public List<Userright> getUserrightByUserid(String userid) {
		return (List<Userright>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getUserrightByUserid",
				userid);
	}

	@Override
	public boolean updateSelective(Userright userright) {
		int count = getSqlMapClientTemplate().update(getNameSpace() + ".updateSelective", userright);
		return count == 1;
	}

	@Override
	public List<String> getMasteridsByUserid(String userid) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("uid", userid);
		return (List<String>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getMasteridsByUserid", params);
	}
}