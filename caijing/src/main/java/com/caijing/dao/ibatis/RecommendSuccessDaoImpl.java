package com.caijing.dao.ibatis;

import java.util.Date;
import java.util.List;

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

}
