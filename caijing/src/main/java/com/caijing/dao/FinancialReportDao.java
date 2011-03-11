package com.caijing.dao;

import java.util.List;
import java.util.Map;

import com.caijing.domain.FinancialReport;
import com.caijing.util.CrudDao;

public interface FinancialReportDao extends CrudDao {

	/**
	 * @param params
	 *            int status, String year, String stockcode, String stockname,
	 *            int start, int size
	 * @return
	 */
	List<FinancialReport> getReportsList(Map<String, Object> params);

}
