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
	public Date getEarliestIntimeByAidFrom(String aid, Date date) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("aid", aid);
		params.put("date", date);
		return (Date) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getEarliestIntimeByAidFrom", params);
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
	public List<GroupStock> getGroupStockListAsc(int start, int length, String startDate) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("start", start);
		params.put("length", length);
		params.put("startDate", startDate);
		return (List<GroupStock>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getGroupStockListAsc",
				params);
	}

	@Override
	public List<GroupStock> getGroupStockListDesc(int start, int length, String startDate) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("start", start);
		params.put("length", length);
		params.put("startDate", startDate);
		return (List<GroupStock>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getGroupStockListDesc",
				params);
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

	@Override
	public GroupStock getStockByGroupidAndStockcode(String groupid, String stockcode) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("groupid", groupid);
		params.put("stockcode", stockcode);
		return (GroupStock) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getStockByGroupidAndStockcode",
				params);
	}

	@Override
	public int updateObjectAchieved(GroupStock groupStock) {
		return getSqlMapClientTemplate().update(getNameSpace() + ".updateObjectAchieved", groupStock);
	}

	@Override
	public List<GroupStock> getCurrentStockByGroupidAndPeriod(String groupid, Date startdate, Date enddate) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("groupid", groupid);
		params.put("startdate", startdate);
		params.put("enddate", enddate);
		return (List<GroupStock>) getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".getCurrentStockByGroupidAndPeriod", params);
	}

	@Override
	public int updateOutOfDate(GroupStock groupstock) {
		return getSqlMapClientTemplate().update(getNameSpace() + ".updateOutOfDate", groupstock);
	}

	@Override
	public List<GroupStock> getCurrentStocksBefore(Date endDate) {
		return (List<GroupStock>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getCurrentStocksBefore",
				endDate);
	}

	@Override
	public List<GroupStock> getAllStockByGroupid(String groupid) {
		return (List<GroupStock>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getAllStockByGroupid",
				groupid);
	}

	@Override
	public int updateOutGain(GroupStock groupstock) {
		return getSqlMapClientTemplate().update(getNameSpace() + ".updateOutGain", groupstock);
	}

	@Override
	public Date getCurrentEarliestIntimeByAid(String aid) {
		return (Date) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getCurrentEarliestIntimeByAid", aid);
	}

	@Override
	public List<GroupStock> getOutStocksByAid(String aid) {
		return (List<GroupStock>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getOutStocksByAid", aid);
	}
}
