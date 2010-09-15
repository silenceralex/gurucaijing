package com.caijing.dao.ibatis;

import java.util.List;

import com.caijing.dao.AnalyzerDao;
import com.caijing.domain.Analyzer;
import com.caijing.util.CrudDaoDefault;

public class AnalyzerDaoImpl extends CrudDaoDefault implements AnalyzerDao {

	@SuppressWarnings("unchecked")
	public List<Analyzer> getAnalyzersByAgency(String agency) {
		return (List<Analyzer>)getSqlMapClientTemplate().queryForList(getNameSpace()+".getAnalyzersByAgency",agency);
	}

	public List<Analyzer> getAllAnalyzers() {
		return (List<Analyzer>)getSqlMapClientTemplate().queryForList(getNameSpace()+".getAllAnalyzers");
	}

}
