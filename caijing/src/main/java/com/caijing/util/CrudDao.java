package com.caijing.util;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

/**
 * ͨ��iBatis֧�����ݿ����CRUD�����Ľӿڡ�Object���������
 * @author Raymond
 *
 */
public interface CrudDao {
	/**
	 * ɾ����¼����
	 * @param obj ����Ҫɾ���Ķ���Ĭ���ǰ���������ɾ����
	 * @return ��Ӱ��ļ�¼����
	 * @throws DataAccessException
	 */
	public int delete(Object obj) throws DataAccessException ;

	/**
	 * �����¼����
	 * @param obj ����Ҫ����ļ�¼�������
	 * @return ��Ӱ��ļ�¼����
	 * @throws DataAccessException
	 */
	public Object insert(Object obj) throws DataAccessException ;

	/**
	 * ����һ����¼
	 * @param obj ����Ҫ���صĶ���Ĭ����ȡ��������ƥ��
	 * @return
	 * @throws DataAccessException
	 */
	public Object select(Object primaryKey) throws DataAccessException ;

	/**
	 * ���¼�¼
	 * @param obj Ҫ���µļ�¼���ͣ�Ĭ�ϸ��³���������������ֶΣ�ƥ��������������
	 * @return
	 * @throws DataAccessException
	 */
	public int update(Object newObject) throws DataAccessException ;
	
	/**
	 * ���ö����iBatis������������ռ�
	 * @param nameSpace
	 */
	public void setNamespace(String nameSpace) ;
	/**
	 * ���ò���db��sqlmapclient
	 * @param sqlMapClientTemplate
	 */
	public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate);
}
