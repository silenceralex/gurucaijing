package com.caijing.dao;

import java.util.List;

import com.caijing.domain.Report;
import com.caijing.util.CrudDao;

public interface ReportDao extends CrudDao {
	public List<Report> getReportsBySaname(String saname, int start, int offset);

	public List<Report> getReportsBySanameType(String saname, int type, int start, int offset);

	public int getReportsCountBySanameType(String saname, int type);

	public List<Report> getAllReports(int start, int offset);

	public int getAllReportsCount();

	public int getAllReportsCountBySaname(String saname);

	public List<Report> getAllCompanyReports();

	public List<Report> getCompanyReportsBySaname(String saname);

	public List<Report> getCompanyReportsBySanameAfter(String saname, String date);

}
