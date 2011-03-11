package com.caijing.dao.ibatis;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.caijing.dao.FinancialReportDao;
import com.caijing.domain.FinancialReport;
import com.caijing.util.ContextFactory;

public class FinancialReportDaoImplTest {

	FinancialReportDao dao;

	@Before
	public void setUp() throws Exception {
		dao = (FinancialReportDao) ContextFactory.getBean("financialReportDao");
	}
	
	@Test
	public void type() throws Exception {
		assertNotNull(FinancialReportDaoImpl.class);
	}

	@Test
	public void instantiation() throws Exception {
		FinancialReportDao target = dao;
		assertNotNull(target);
	}

	@Test
	public void getReportsList_A$Map() throws Exception {
		FinancialReportDao target = dao;
		int status = 0;
		int start = 0;
		int size = 10;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		params.put("start", start);
		params.put("size", size);
		params.put("orderby", "year desc");
		List<FinancialReport> actual = target.getReportsList(params);
		assertTrue(actual.size()>0);
	}

}
