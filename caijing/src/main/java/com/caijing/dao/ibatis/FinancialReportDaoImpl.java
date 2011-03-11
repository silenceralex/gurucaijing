package com.caijing.dao.ibatis;

import java.util.List;
import java.util.Map;

import com.caijing.dao.FinancialReportDao;
import com.caijing.domain.FinancialReport;
import com.caijing.util.CrudDaoDefault;

public class FinancialReportDaoImpl extends CrudDaoDefault implements FinancialReportDao {
	
	@Override
	public List<FinancialReport> getReportsList(Map<String, Object> params) {
		return (List<FinancialReport>) getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getReportsList", params);
	}
	
}
