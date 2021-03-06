package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.ReportDao;
import com.caijing.domain.Report;
import com.caijing.util.CrudDaoDefault;

public class ReportDaoImpl extends CrudDaoDefault implements ReportDao {

	@SuppressWarnings("unchecked")
	public List<Report> getReportsBySaname(String saname, int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("saname", saname);
		params.put("start", start);
		params.put("offset", offset);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getReportsBySaname", params);
	}

	public int getAllReportsCount() {
		return (Integer) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getAllReportsCount");
	}

	@SuppressWarnings("unchecked")
	public List<Report> getAllReports(int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("start", start);
		params.put("offset", offset);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getAllReports", params);
	}

	public int getAllReportsCountBySaname(String saname) {
		return (Integer) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getAllReportsCountBySaname",
				saname);
	}

	@SuppressWarnings("unchecked")
	public List<Report> getAllCompanyReports() {
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getAllCompanyReports");
	}

	public List<Report> getCompanyReportsBySaname(String saname) {
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getCompanyReportsBySaname", saname);
	}

	public List<Report> getCompanyReportsBySanameAfter(String saname, String date) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("saname", saname);
		params.put("date", date);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getCompanyReportsBySanameAfter", params);
	}

	@Override
	public List<Report> getReportsBySanameType(String saname, int type, int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(4);
		params.put("saname", saname);
		params.put("type", type);
		params.put("start", start);
		params.put("offset", offset);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getReportsBySanameType", params);
	}

	@Override
	public int getReportsCountBySanameType(String saname, int type) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("saname", saname);
		params.put("type", type);
		return (Integer) getSqlMapClientTemplate().queryForObject(this.getNameSpace() + ".getReportsCountBySanameType",
				params);
	}

	@Override
	public int getReportsCountByType(int type) {
		return (Integer) getSqlMapClientTemplate().queryForObject(this.getNameSpace() + ".getReportsCountByType", type);
	}

	@Override
	public List<Report> getReportsListByType(int type, int offset, int length) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("type", type);
		params.put("offset", offset);
		params.put("length", length);
		return (List<Report>) getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getReportsListByType",
				params);
	}

	@Override
	public List<Report> selectByMultiKey(String saname, String stockcode, String createdate) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("saname", saname);
		params.put("stockcode", stockcode);
		params.put("createtime", createdate);
		return (List<Report>) getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".selectByMultiKey",	params);
	}

	@Override
	public int updateByPrimaryKeySelective(Map<String, Object> parms) {
		int rows = getSqlMapClientTemplate().update(this.getNameSpace()+".updateByPrimaryKeySelective", parms);
        return rows;
	}
}
