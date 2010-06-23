package com.caijing.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;



/**
 * 实现CRUD功能的类，DAO可以继承此类来获得基本的四个操作，另外的DAO操作须自行定义。
 * @author Raymond
 * 
 */
public class CrudDaoDefault implements CrudDao {
	
	public CrudDaoDefault() {
		
	}
	
	
	public CrudDaoDefault(String nameSpace, SqlMapClientTemplate sqlMapClientTemplate) {
		this.namespace=nameSpace;
		this.sqlMapClientTemplate=sqlMapClientTemplate;
	}

	private static String INSERT = ".insert";

	private static String SELECT = ".select";

	private static String UPDATE = ".update";

	private static String DELETE = ".delete";


	protected Log logger = LogFactory.getLog(getClass());
	
	private String namespace;
	
	private SqlMapClientTemplate sqlMapClientTemplate;

	public int delete(Object obj) throws DataAccessException {
		logger.debug("Ruuning statement: "+namespace+DELETE);
		validateNameSpace();
		return sqlMapClientTemplate.delete(namespace+DELETE,obj);
	}

	public Object insert(Object obj) throws DataAccessException {
		logger.debug("Ruuning statement: "+namespace+INSERT);
		validateNameSpace();
		return sqlMapClientTemplate.insert(namespace+INSERT,obj);
	}

	public Object select(Object primaryKey) throws DataAccessException {
		logger.debug("Ruuning statement: "+namespace+SELECT);
		validateNameSpace();
		return sqlMapClientTemplate.queryForObject(namespace+SELECT,primaryKey);
	}

	public int update(Object newObject) throws DataAccessException {
		logger.debug("Ruuning statement: "+namespace+UPDATE);
		validateNameSpace();
		return sqlMapClientTemplate.update(namespace+UPDATE,newObject);
	}

	public void setNamespace(String namespace) {
		this.namespace=namespace;
		
	}

	public SqlMapClientTemplate getSqlMapClientTemplate() {
		return sqlMapClientTemplate;
	}

	public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
		this.sqlMapClientTemplate = sqlMapClientTemplate;
	}

	public String getNameSpace() {
		return namespace;
	}
	
	private void validateNameSpace() {
		if (this.namespace==null) {
			throw new RuntimeException("NameSpace has not been specified for the CrudDaoDefault class or subclass. It should be initialized explicitly or configured in the spring config file.");
		}
	}
}
