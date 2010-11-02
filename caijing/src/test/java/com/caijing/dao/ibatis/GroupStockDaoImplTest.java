package com.caijing.dao.ibatis;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caijing.dao.GroupStockDao;
import com.caijing.domain.GroupStock;
import com.caijing.util.ContextFactory;

public class GroupStockDaoImplTest extends TestCase {

	GroupStockDao dao = null;

	private Log log = LogFactory.getLog(getClass());

	public GroupStockDaoImplTest(String s) {
		super(s);
	}

	protected void setUp() throws Exception {
		super.setUp();
		dao = (GroupStockDao) ContextFactory.getBean("groupStockDao");
		String deleteSql = "insert into groupstock (groupid,groupname,stockcode,intime) values('123123','chenjun','234234','20101101')";
		ContextFactory.getJdbcTemplate().execute(deleteSql);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		String deleteSql = "delete from groupstock where groupid='123123' and stockcode='234234' and intime='20101101'";
		ContextFactory.getJdbcTemplate().execute(deleteSql);
	}

	public void testUpdate() {
		GroupStock gs = new GroupStock();
		gs.setGroupid("123123");
		gs.setStockcode("234234");
		gs.setOuttime(new Date());
		Integer ret = (Integer) dao.update(gs);
		assertEquals(ret.intValue(), 1);
	}

	public static Test suite() {
		String testMethod = System.getProperty("testMethod");
		if (testMethod == null) {
			return new TestSuite(GroupStockDaoImplTest.class);
		} else {
			TestSuite suite = new TestSuite();
			suite.addTest(new GroupStockDaoImplTest(testMethod));
			return suite;
		}
	}
}
