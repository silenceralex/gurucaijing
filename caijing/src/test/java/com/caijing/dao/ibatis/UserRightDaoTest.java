package com.caijing.dao.ibatis;

import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caijing.dao.UserrightDAO;
import com.caijing.domain.Userright;
import com.caijing.util.ContextFactory;

public class UserRightDaoTest extends TestCase {

	private UserrightDAO userrightDao = null;
	private Log log = LogFactory.getLog(getClass());

	public UserRightDaoTest(String s) {
		super(s);
	}

	protected void setUp() throws Exception {
		super.setUp();
		userrightDao = (UserrightDAO) ContextFactory.getBean("userrightDAO");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetIndustriesByUserid() {
		List<String> industyList = userrightDao.getIndustriesByUserid("71D7AOQK", "analyzer");
		List<Userright> users = userrightDao.getUserrightByUserid("71D7AOQK");
		System.out.println("industyList.size():" + industyList.size());
		System.out.println("users():" + users.size());
		for (String industry : industyList) {
			System.out.println("industry:" + industry);
		}
		for (Userright user : users) {
			System.out.println("industry:" + user.getIndustry());
		}
		assertNotNull("Get a null object!", industyList);
		//		assertTrue(industyList.size() > 0);

	}

}
