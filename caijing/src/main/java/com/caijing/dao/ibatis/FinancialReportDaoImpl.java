package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.FinancialReportDao;
import com.caijing.domain.FinancialReport;
import com.caijing.util.CrudDaoDefault;

public class FinancialReportDaoImpl extends CrudDaoDefault implements FinancialReportDao {

	@Override
	public List<FinancialReport> getReportsListByStatus(int status, int start, int size) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("status", status);
		params.put("offset", start);
		params.put("length", size);
		return (List<FinancialReport>) getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getReportsListByStatus", params);
	}
	
	
}
