package com.caijing.dao.ibatis;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.caijing.dao.ReportDao;
import com.caijing.domain.Report;
import com.caijing.util.CrudDaoDefault;

public class ReportDaoImpl extends CrudDaoDefault implements ReportDao {

	@Override
	public List<Report> getReportsBySaname(String saname) {
		// TODO Auto-generated method stub
		return null;
	}

}
