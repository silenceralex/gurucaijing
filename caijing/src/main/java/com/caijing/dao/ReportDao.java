package com.caijing.dao;

import java.util.List;

import com.caijing.domain.Report;
import com.caijing.util.CrudDao;

public interface ReportDao extends CrudDao {
	public List<Report> getReportsBySaname(String saname);

}
