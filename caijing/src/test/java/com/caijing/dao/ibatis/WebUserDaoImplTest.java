package com.caijing.dao.ibatis;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caijing.dao.WebUserDao;
import com.caijing.domain.WebUser;
import com.caijing.util.ContextFactory;

public class WebUserDaoImplTest extends TestCase {
	WebUserDao dao;

	private Log log = LogFactory.getLog(getClass());

	public WebUserDaoImplTest(String s) {
		super(s);
	}

	protected void setUp() throws Exception {
		super.setUp();
		dao = (WebUserDao) ContextFactory.getBean("webUserDao");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetUserByEmail() {
		WebUser user = dao.getUserByEmail("shadowcj@gmail.com");
		assertNotNull("Get a null object!", user);
		assertEquals("应该拿到数据shadow才对", "shadow", user.getNickname());
		System.out.println(ToStringBuilder.reflectionToString(user));
	}

	public void testIdentify() {
		int login = dao.identify("shadowcj@gmail.com", "cj19811022");
		assertTrue(login==1);
	}

	public void testUpdate() {
		WebUser user = dao.getUserByEmail("shadowcj@gmail.com");
		user.setCity("北京");
		user.setProvince("北京");
		user.setMobile("12323450987");
		user.setLmodify(new Date());
		int num = dao.update(user);
		assertTrue(num > 0);
	}

	public static Test suite() {
		String testMethod = System.getProperty("testMethod");
		if (testMethod == null) {
			return new TestSuite(WebUserDaoImplTest.class);
		} else {
			TestSuite suite = new TestSuite();
			suite.addTest(new WebUserDaoImplTest(testMethod));
			return suite;
		}
	}
}
