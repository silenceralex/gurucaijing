package com.caijing.dao.ibatis;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.caijing.dao.FinancialReportDao;
import com.caijing.domain.FinancialReport;
import com.caijing.util.ContextFactory;

public class FinancialReportDaoImplTest {

	FinancialReportDao dao;

	protected void setUp() throws Exception {
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
	public void getReportsListByStatus_A$int$int$int() throws Exception {
		FinancialReportDao target = dao;
		int status = 0;
		int start = 0;
		int size = 10;
		List<FinancialReport> actual = target.getReportsListByStatus(status, start, size);
		List<FinancialReport> expected = null;
		assertEquals(expected, actual);
	}

}
