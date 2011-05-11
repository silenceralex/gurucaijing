package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.List;

import com.caijing.dao.MasterDao;
import com.caijing.domain.Master;
import com.caijing.util.CrudDaoDefault;

public class MasterDaoImpl extends CrudDaoDefault implements MasterDao {

	@Override
	public List<Master> getAllMasters(Integer start, Integer length) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("start", start);
		params.put("length", length);
		return (List<Master>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getAllMasters", params);
	}

	@Override
	public Master getMasterByName(String name) {
		return (Master) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getMasterByName", name);
	}

}
