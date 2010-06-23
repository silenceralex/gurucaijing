package com.caijing.business;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.caijing.business.UserManager;
import com.caijing.domain.LoginSession;
import com.caijing.domain.User;
import com.caijing.exception.LoginFailedException;
import com.caijing.exception.UserNotFoundException;
import com.caijing.util.ContextFactory;

import junit.framework.TestCase;

public class UserManagerTest extends TestCase {
	private String temporaryUsername="ray###insert";
	private long temporaryUid=1;
	private UserManager userManager=ContextFactory.getUserManager();	
	protected void setUp() throws Exception {
		super.setUp();
		String insertSql="insert into user (uid,username,password,nickname,birthday,introduction,default_setting,reg_time,credit) values ("+temporaryUid+",'" + temporaryUsername + "',md5('abcdefg'),'raynick','1973-09-30','n/a','kick',now(),10)";
		ContextFactory.getJdbcTemplate().execute(insertSql);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		String deleteSql="delete from user where username='"+temporaryUsername+"'";
		ContextFactory.getJdbcTemplate().execute(deleteSql);
	}

	public void testLogin() {
		String correctPassword="abcdefg",wrongPassword="abcdef";
		try {
			LoginSession loginSession=userManager.login(temporaryUsername, correctPassword);
			assertNotNull(loginSession);
			assertEquals("Ӧ���õ���ȷ���û��ǳ�","raynick", loginSession.getUser().getNickname());
			System.out.println(ToStringBuilder.reflectionToString(loginSession));
		} catch (LoginFailedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		try {
			userManager.login(temporaryUsername, wrongPassword);
			fail("Ӧ���׳��˵�¼ʧ���쳣");
		} catch (LoginFailedException e) {
			assertTrue(true);
		}
		
	}

	public void testGetUserByUsername() {
		User user;
		try {
			user=userManager.getUserByUsername(temporaryUsername);
			assertNotNull(user);
			System.out.println(ToStringBuilder.reflectionToString(user));
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			fail("�û�Ӧ�ô���");
		}
		try {
			user=userManager.getUserByUsername(temporaryUsername+"notexist");
			fail("�û�Ӧ�ò�����");
		} catch (UserNotFoundException e) {
			assertTrue(true);
		}
	}

}
