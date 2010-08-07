package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.ReportDao;
import com.caijing.domain.Report;
import com.caijing.util.CrudDaoDefault;

public class ReportDaoImpl extends CrudDaoDefault implements ReportDao {

	@SuppressWarnings("unchecked")
	public List<Report> getReportsBySaname(String saname,int start,int offset) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("saname", saname);
		params.put("start", start);
		params.put("offset", offset);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace()+".getReportsBySanme", params);
	}

	public int getAllReportsCount() {
		return (Integer) getSqlMapClientTemplate().queryForObject(getNameSpace()+".getAllReportsCount");
	}

	@SuppressWarnings("unchecked")
	public List<Report> getAllReports(int start,int offset) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("start", start);
		params.put("offset", offset);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace()+".getAllReports",params);
	}

	public int getAllReportsCountBySaname() {
		return (Integer) getSqlMapClientTemplate().queryForObject(getNameSpace()+".getAllReportsCountBySaname");
	}


}
