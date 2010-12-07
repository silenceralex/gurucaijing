package com.caijing.dao.ibatis;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.GroupStockDao;
import com.caijing.domain.GroupStock;
import com.caijing.util.CrudDaoDefault;

public class GroupStockDaoImpl extends CrudDaoDefault implements GroupStockDao {

	@Override
	public List<GroupStock> getCurrentStockByGroupid(String groupid) {
		return (List<GroupStock>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getCurrentStockByGroupid",
				groupid);
	}

	@Override
	public Date getEarliestIntimeByAid(String aid) {
		return (Date) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getEarliestIntimeByAid", aid);
	}

	@Override
	public GroupStock getCurrentStockByGroupidAndStockcode(String groupid, String stockcode) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("groupid", groupid);
		params.put("stockcode", stockcode);
		return (GroupStock) getSqlMapClientTemplate().queryForObject(
				getNameSpace() + ".getCurrentStockByGroupidAndStockcode", params);
	}

	@Override
	public List<GroupStock> getNameAndCodeByAid(String aid) {
		return (List<GroupStock>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getNameAndCodeByAid", aid);
	}

	@Override
	public List<GroupStock> getAllGroupStock() {
		return (List<GroupStock>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getAllGroupStock");
	}

	@Override
	public int updateStockGain(GroupStock groupStock) {
		return getSqlMapClientTemplate().update(getNameSpace() + ".updateStockGain", groupStock);
	}

	@Override
	public List<GroupStock> getGroupStockListAsc(int length) {
		return (List<GroupStock>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getGroupStockListAsc",
				length);
	}

	@Override
	public List<GroupStock> getGroupStockListDesc(int length) {
		return (List<GroupStock>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getGroupStockListDesc",
				length);
	}

	@Override
	public List<String> getRecommendReportids(int start, int length) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("start", start);
		params.put("length", length);
		return (List<String>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getRecommendReportids", params);
	}

	@Override
	public int getRecommendReportCount() {
		return (Integer) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getRecommendReportCount");
	}
}
