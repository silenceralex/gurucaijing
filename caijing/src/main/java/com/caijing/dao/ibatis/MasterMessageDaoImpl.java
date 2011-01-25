package com.caijing.dao.ibatis;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.MasterMessageDao;
import com.caijing.util.CrudDaoDefault;

public class MasterMessageDaoImpl extends CrudDaoDefault implements MasterMessageDao {

	@Override
	public List<Map> getMessagesFrom(int masterid, String date, int num) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("currdate", date);
		params.put("num", num);
		params.put("masterid", masterid);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getMessagesFrom", params);
	}

	@Override
	public List<Map> getMessagesByMasteridDate(int masterid, String date) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("currdate", date);
		params.put("masterid", masterid);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getMessagesByMasteridDate", params);
	}

	@Override
	public Integer getCurrentNumByMasterid(int masterid, String date) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("currdate", date);
		params.put("masterid", masterid);
		return (Integer) getSqlMapClientTemplate().queryForObject(this.getNameSpace() + ".getCurrentNumByMasterid",
				params);
	}

	@Override
	public List<Date> getDatesByMasterid(int masterid) {
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getDatesByMasterid", masterid);
	}
}
