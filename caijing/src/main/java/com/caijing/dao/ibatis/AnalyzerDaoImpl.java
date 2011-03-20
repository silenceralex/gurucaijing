package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.AnalyzerDao;
import com.caijing.domain.Analyzer;
import com.caijing.util.CrudDaoDefault;

public class AnalyzerDaoImpl extends CrudDaoDefault implements AnalyzerDao {

	@SuppressWarnings("unchecked")
	public List<Analyzer> getAnalyzersByAgency(String agency) {
		return (List<Analyzer>) getSqlMapClientTemplate()
				.queryForList(getNameSpace() + ".getAnalyzersByAgency", agency);
	}

	public List<Analyzer> getAllAnalyzers() {
		return (List<Analyzer>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getAllAnalyzers");
	}

	public List<Analyzer> getStarAnalyzers() {
		return (List<Analyzer>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getStarAnalyzers");
	}

	@Override
	public Analyzer getAnalyzerByName(String name) {
		return (Analyzer) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getAnalyzerByName", name);
	}

	@Override
	public List<Analyzer> getAnalyzerRankList(String date, int start, int length) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", date);
		params.put("length", length);
		params.put("start", start);
		return (List<Analyzer>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getAnalyzerRankList", params);
	}

	@Override
	public void updateSuccessRatio(Analyzer analyzer) {
		getSqlMapClientTemplate().update(getNameSpace() + ".updateSuccessRatio", analyzer);

	}

	@Override
	public List<Analyzer> getSuccessRankedAnalyzersByAgency(String agency) {
		return (List<Analyzer>) getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".getSuccessRankedAnalyzersByAgency", agency);
	}

	@Override
	public List<Analyzer> getSuccessRankedAnalyzers() {
		return (List<Analyzer>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getSuccessRankedAnalyzers");
	}

	@Override
	public List<Analyzer> getAnalyzersAfter(String date) {
		return (List<Analyzer>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getAnalyzersAfter", date);
	}

	@Override
	public List<Analyzer> getUnStarAnalyzers() {
		return (List<Analyzer>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getUnStarAnalyzers");
	}

	@Override
	public List<String> getAllIndustry() {
		return (List<String>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getAllIndustry");
	}

	@Override
	public List<Analyzer> getAnalyzersByIndustry(String industry, int start, int length) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("industry", industry);
		params.put("length", length);
		params.put("start", start);
		return (List<Analyzer>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getAnalyzersByIndustry",
				params);
	}

	@Override
	public int getAnalyzersCountByIndustry(String industry) {
		return (Integer) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getAnalyzersCountByIndustry",
				industry);
	}
}
