package com.caijing.dao.ibatis;

import java.util.List;

import com.caijing.dao.FinancialReportDao;
import com.caijing.domain.Report;
import com.caijing.util.CrudDaoDefault;

public class FinancialReportDaoImpl extends CrudDaoDefault implements FinancialReportDao {

	@Override
	public List<Report> getReportsListByStatus(int status, int start, int size) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
