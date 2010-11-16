package com.caijing.dao.ibatis;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caijing.dao.UserDao;
import com.caijing.domain.User;
import com.caijing.util.ContextFactory;

public class UserDaoImplTest extends TestCase {

	private String temporaryUsername = "ray###insert";
	private long temporaryUid = 1;
	UserDao dao;

	private Log log = LogFactory.getLog(getClass());

	public UserDaoImplTest(String s) {
		super(s);
	}

	protected void setUp() throws Exception {
		super.setUp();
		String insertSql = "insert into user (uid,username,password,nickname,birthday,introduction,default_setting,reg_time,credit) values ("
				+ temporaryUid
				+ ",'"
				+ temporaryUsername
				+ "',md5('abcdefg'),'raynick','1973-09-30','n/a','kick',now(),10)";
		ContextFactory.getJdbcTemplate().execute(insertSql);
		dao = (UserDao) ContextFactory.getBean("userDao");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		String deleteSql = "delete from user where username='" + temporaryUsername + "'";
		ContextFactory.getJdbcTemplate().execute(deleteSql);
	}

	public void testGetUserByUsername() {
		User user = dao.getUserByUsername(temporaryUsername);
		assertNotNull("Get a null object!", user);
		assertEquals("Ӧ���õ�����raynick�Ŷ�", "raynick", user.getNickname());
		assertEquals("Ӧ���õ�����kick�Ŷ�", "kick", user.getDefaultSetting());
		System.out.println(ToStringBuilder.reflectionToString(user));
	}

	public void testSearchUsersByNickname() {
		List<User> users = dao.searchUsersByNickname("ray");
		assertNotNull(users);
		assertTrue("����Ӧ���õ�һ����¼", users.size() > 0);
		System.out.println(ToStringBuilder.reflectionToString(users.get(0)));
	}

	public void testInsert() {
		boolean gotException = false;
		String username = "raymond!?gerald###";
		User user = new User();
		user.setBirthday("1973-06-25");
		user.setCredit(0);
		user.setDefaultSetting("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n"
				+ "<!DOCTYPE sqlMap SYSTEM \"D:\\temp\\sql-map-2.dtd\">\n" + "<sqlMap/>");
		user.setNickname("GeraldBee");
		user.setIntroduction("Patrick gets well along with Gerald.");
		user.setUsername(username);

		assertNotNull(dao);
		try {
			Object uid = dao.insert(user);
			assertNotNull(uid);
			assertTrue("Ӧ���õ���������UIDֵ", (Long) uid > 0);
			System.out.println("uid=" + uid);
		} catch (Exception e) {
			e.printStackTrace();
			gotException = true;
		}
		assertTrue(true);
		// clean up
		String delteSql = "delete from user where username='" + username + "'";
		ContextFactory.getJdbcTemplate().execute(delteSql);
		assertFalse("�������쳣!", gotException);
	}

	public void testSelect() {
		UserDao dao = (UserDao) ContextFactory.getBean("userDao");
		try {
			User user = (User) dao.select(temporaryUid);
			assertNotNull("Get a null object!", user);
			assertEquals("Ӧ���õ���ʱ��������raynick�Ŷ�", "raynick", user.getNickname());
			assertEquals("�����digestӦ�����", DigestUtils.md5Hex("abcdefg"), user.getPassword());
			log.debug(user.getUid() + "-" + user.getUsername() + "-" + user.getNickname());
			System.out.println(ToStringBuilder.reflectionToString(user));

			assertTrue(dao.identify("ray###insert", "abcdefg"));

		} catch (Exception e) {
			e.printStackTrace();
			fail("�Ҳ������ݣ�" + e.getMessage());
		}
	}

	public void testDelete() {
		User user = (User) dao.select(temporaryUid);
		dao.delete(user);
		user = (User) dao.select(temporaryUid);
		assertNull("����Ӧ�ò�����", user);
	}

	public void testUpdate() {
		String newNickname = "Patrick White";
		String newDefaultSetting = "NEWSETTING";
		String newIntroduction = "I'm wonderful";
		String newBirthday = "1978-06-04";
		int newCredit = 360;
		User user;
		user = (User) dao.select(temporaryUid);
		System.out.println("Old User Object:" + ToStringBuilder.reflectionToString(user));
		user.setNickname(newNickname);
		user.setDefaultSetting(newDefaultSetting);
		user.setBirthday(newBirthday);
		user.setCredit(newCredit);
		user.setIntroduction(newIntroduction);
		dao.update(user);
		User updatedSite = (User) dao.select(temporaryUid);
		assertEquals("�ǳ�û�����óɹ���", newNickname, updatedSite.getNickname());
		assertEquals("Ĭ���趨û�����óɹ���", newDefaultSetting, updatedSite.getDefaultSetting());
		assertEquals("����û�����óɹ���", newBirthday, updatedSite.getBirthday());
		assertEquals("Creditû�����óɹ���", newCredit, updatedSite.getCredit());
		assertEquals("����û�����óɹ���", newIntroduction, updatedSite.getIntroduction());
		System.out.println("New User Object:" + ToStringBuilder.reflectionToString(user));
	}

	public static Test suite() {
		String testMethod = System.getProperty("testMethod");
		if (testMethod == null) {
			return new TestSuite(UserDaoImplTest.class);
		} else {
			TestSuite suite = new TestSuite();
			suite.addTest(new UserDaoImplTest(testMethod));
			return suite;
		}
	}
}
