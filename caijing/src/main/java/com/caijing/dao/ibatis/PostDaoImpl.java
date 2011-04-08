package com.caijing.dao.ibatis;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.caijing.dao.PostDao;
import com.caijing.domain.Post;
import com.caijing.util.CrudDaoDefault;

public class PostDaoImpl extends CrudDaoDefault implements PostDao {

	@Override
	public int delete(Object obj) throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object insert(Object obj) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object select(Object primaryKey) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Object newObject) throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setNamespace(String nameSpace) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Post> getPostByGroupid(String groupid, int start, int length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPostCountByGroupid(String groupid) {
		// TODO Auto-generated method stub
		return 0;
	}

}
