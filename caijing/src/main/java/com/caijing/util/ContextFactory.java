package com.caijing.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import com.caijing.business.UserManager;

public class ContextFactory {
	private static String APPLICATION_CONFIGS=
		 " classpath:config.xml" +
	    "  classpath:applicationContext-resources.xml" +
		"  classpath:applicationContext-dao.xml" +
		"  classpath:applicationContext-business.xml" +
		"  classpath:applicationContext-services.xml";
	private static ApplicationContext _applicationContext= new ClassPathXmlApplicationContext(StringUtils.tokenizeToStringArray(APPLICATION_CONFIGS, ConfigurableWebApplicationContext.CONFIG_LOCATION_DELIMITERS));;
	public static ApplicationContext getApplicationContext() {
		return _applicationContext;
	}
	public static void setApplicationContext(ApplicationContext applicationContext) {
		_applicationContext = applicationContext;
	}
	
	public static Object getBean(String beanName) {
		return _applicationContext.getBean(beanName);
	}
	
	public static SqlMapClientTemplate getSqlMapClientTemplate() {
		return (SqlMapClientTemplate)_applicationContext.getBean("sqlMapClientTemplate");
	}
	
	public static JdbcTemplate getJdbcTemplate() {
		return (JdbcTemplate)_applicationContext.getBean("jdbcTemplate");
	}
	
	// 下面的是业务相关的获取business bean的方法
	public static UserManager getUserManager() {
		return (UserManager)_applicationContext.getBean("userManager");
	}
}
