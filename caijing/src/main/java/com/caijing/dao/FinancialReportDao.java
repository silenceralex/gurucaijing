package com.caijing.dao;

import java.util.List;

import com.caijing.domain.Report;
import com.caijing.util.CrudDao;

public interface FinancialReportDao extends CrudDao {

	List<Report> getReportsListByStatus(int status, int start, int size);

}
