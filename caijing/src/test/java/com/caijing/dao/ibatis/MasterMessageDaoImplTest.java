package com.caijing.dao.ibatis;

import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caijing.dao.MasterMessageDao;
import com.caijing.domain.MasterMessage;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.caijing.util.ServerUtil;

public class MasterMessageDaoImplTest extends TestCase {

	MasterMessageDao dao = null;

	private Log log = LogFactory.getLog(getClass());

	public MasterMessageDaoImplTest(String s) {
		super(s);
	}

	protected void setUp() throws Exception {
		super.setUp();
		dao = (MasterMessageDao) ContextFactory.getBean("masterMessageDao");
		//		String deleteSql = "insert into groupstock (groupid,groupname,stockcode,intime) values('123123','chenjun','234234','20101101')";
		//		ContextFactory.getJdbcTemplate().execute(deleteSql);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		//		String deleteSql = "delete from groupstock where groupid='123123' and stockcode='234234' and intime='20101101'";
		//		ContextFactory.getJdbcTemplate().execute(deleteSql);
	}

	public void testInsert() {
		MasterMessage mm = new MasterMessage();
		mm.setContent("ÏÂÎçÒª±©µø");
		mm.setCurrdate(new Date());
		mm.setMessageid(ServerUtil.getid());
		mm.setPtime("13:10");
		mm.setMasterid(2074);
		Object obj = dao.insert(mm);
		//		assertNotNull(obj);
	}

	public void testGetMessagesByMasteridDate() {
		String date = DateTools.transformYYYYMMDDDate(new Date());
		List<Map> mms = dao.getMessagesByMasteridDate(2074, date);
		assertNotNull(mms);
		System.out.println("size:" + mms.size());
		for (Map mm : mms) {
			//			assertEquals(mm.getMasterid(), 2074);
			System.out.println("ptime:" + mm.get("ptime") + "  content:" + mm.get("content"));
		}
	}

	public void testGetMessagesFrom() {
		String date = DateTools.transformYYYYMMDDDate(new Date());
		List<Map> mms = dao.getMessagesFrom(2074, date, 3);
		assertNotNull(mms);
		System.out.println("size:" + mms.size());
		for (Map mm : mms) {
			//			assertEquals(mm.getMasterid(), 2074);
			System.out.println("ptime:" + mm.get("ptime") + "  content:" + mm.get("content"));
		}
	}

	public static Test suite() {
		String testMethod = System.getProperty("testMethod");
		if (testMethod == null) {
			return new TestSuite(MasterMessageDaoImplTest.class);
		} else {
			TestSuite suite = new TestSuite();
			suite.addTest(new MasterMessageDaoImplTest(testMethod));
			return suite;
		}
	}
}
