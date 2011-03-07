package com.caijing.dao;

import java.util.List;

import com.caijing.domain.FinancialReport;
import com.caijing.util.CrudDao;

public interface FinancialReportDao extends CrudDao {

	List<FinancialReport> getReportsListByStatus(int status, int start, int size);

}
