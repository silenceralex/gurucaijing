package com.caijing.dao.ibatis;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.RecommendSuccessDao;
import com.caijing.domain.RecommendSuccess;
import com.caijing.util.CrudDaoDefault;

public class RecommendSuccessDaoImpl extends CrudDaoDefault implements RecommendSuccessDao {

	@Override
	public int getRecommendSuccessCountByAid(String aid) {
		return (Integer) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getRecommendSuccessCountByAid",
				aid);
	}

	@Override
	public int getTotalRecommendCountByAid(String aid) {
		return (Integer) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getTotalRecommendCountByAid", aid);
	}

	@Override
	public int updateIsAchieved(RecommendSuccess recommendSuccess) {
		return (Integer) getSqlMapClientTemplate().update(getNameSpace() + ".updateIsAchieved", recommendSuccess);
	}

	@Override
	public List<RecommendSuccess> getRecommendsBefore(Date date) {
		return (List<RecommendSuccess>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getRecommendsBefore",
				date);
	}

	@Override
	public List<RecommendSuccess> getRecommendsByAid(String aid) {
		return (List<RecommendSuccess>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getRecommendsByAid",
				aid);
	}

	@Override
	public int getRecommendSuccessCountByAidDuring(String aid, String startDate, String endDate) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("aid", aid);
		return (Integer) getSqlMapClientTemplate().queryForObject(
				getNameSpace() + ".getRecommendSuccessCountByAidDuring", params);
	}

	@Override
	public int getTotalRecommendCountByAidDuring(String aid, String startDate, String endDate) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("aid", aid);
		return (Integer) getSqlMapClientTemplate().queryForObject(
				getNameSpace() + ".getTotalRecommendCountByAidDuring", params);
	}

	@Override
	public int deleteByAid(String aid) {
		return (Integer) getSqlMapClientTemplate().delete(getNameSpace() + ".deleteByAid", aid);
	}

	@Override
	public List<RecommendSuccess> getUnvalidateRecommendsByAid(String aid) {
		return (List<RecommendSuccess>) getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".getUnvalidateRecommendsByAid", aid);
	}

	@Override
	public List<RecommendSuccess> getUnvalidateRecommendsBefore(Date endDate) {
		return (List<RecommendSuccess>) getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".getUnvalidateRecommendsBefore", endDate);
	}

}
