package com.caijing.util;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

/**
 * 通过iBatis支持数据库基本CRUD操作的接口。Object代表域对象
 * @author Raymond
 *
 */
public interface CrudDao {
	/**
	 * 删除记录操作
	 * @param obj 代表要删除的对象，默认是按照主键来删除。
	 * @return 受影响的记录条数
	 * @throws DataAccessException
	 */
	public int delete(Object obj) throws DataAccessException ;

	/**
	 * 插入记录操作
	 * @param obj 代表要插入的记录的域对象
	 * @return 受影响的记录条数
	 * @throws DataAccessException
	 */
	public Object insert(Object obj) throws DataAccessException ;

	/**
	 * 加载一条记录
	 * @param obj 代表要加载的对象，默认是取其主键来匹配
	 * @return
	 * @throws DataAccessException
	 */
	public Object select(Object primaryKey) throws DataAccessException ;

	/**
	 * 更新记录
	 * @param obj 要更新的记录类型，默认更新除主键以外的所有字段，匹配条件是主键。
	 * @return
	 * @throws DataAccessException
	 */
	public int update(Object newObject) throws DataAccessException ;
	
	/**
	 * 设置对象的iBatis配置里的命名空间
	 * @param nameSpace
	 */
	public void setNamespace(String nameSpace) ;
	/**
	 * 设置操作db的sqlmapclient
	 * @param sqlMapClientTemplate
	 */
	public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate);
}
