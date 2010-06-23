package com.caijing.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.caijing.util.ContextFactory;


import junit.framework.TestCase;

public class ContextFactoryTest extends TestCase {
	static Log log=LogFactory.getLog(ContextFactoryTest.class);

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetApplicationContext() {
		log.debug("≤‚ ‘ªÒ»°ApplicationContext");
		ApplicationContext context;
		try {
			context = ContextFactory.getApplicationContext();
			assertNotNull(context);
		} catch (RuntimeException e) {
			e.printStackTrace();
			fail("Exception occured:"+e.getMessage());
		}
	}

	public void testGetBean() {
		assertNotNull(ContextFactory.getBean("dataSource"));
	}
	
	public void testGetSqlMapClientTemplate() throws Exception {
		assertNotNull(ContextFactory.getSqlMapClientTemplate());
	}
	
	public void testGetJdbcTemplate() throws Exception {
		assertNotNull(ContextFactory.getJdbcTemplate());
		assertNotNull(ContextFactory.getJdbcTemplate().getDataSource());
	}

}
