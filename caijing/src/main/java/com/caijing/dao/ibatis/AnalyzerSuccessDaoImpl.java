package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.AnalyzerSuccessDao;
import com.caijing.domain.AnalyzerSuccess;
import com.caijing.util.CrudDaoDefault;

public class AnalyzerSuccessDaoImpl extends CrudDaoDefault implements AnalyzerSuccessDao {

	@Override
	public List<AnalyzerSuccess> getAnalyzerRankByYear(String year, int start, int length) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("year", year);
		params.put("start", start);
		params.put("length", length);
		return (List<AnalyzerSuccess>) getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".getAnalyzerRankByYear", params);
	}

	@Override
	public List<AnalyzerSuccess> getAnalyzerRankBySanameYear(String saname, String year, int start, int length) {
		Map<String, Object> params = new HashMap<String, Object>(4);
		params.put("year", year);
		params.put("saname", saname);
		params.put("start", start);
		params.put("length", length);
		return (List<AnalyzerSuccess>) getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".getAnalyzerRankBySanameYear", params);
	}

}
