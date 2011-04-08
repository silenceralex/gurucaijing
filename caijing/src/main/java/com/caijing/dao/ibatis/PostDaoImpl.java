package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.PostDao;
import com.caijing.domain.Post;
import com.caijing.util.CrudDaoDefault;

public class PostDaoImpl extends CrudDaoDefault implements PostDao {

	@Override
	public List<Post> getPostByGroupid(String groupid, int start, int length) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("groupid", groupid);
		params.put("start", start);
		params.put("length", length);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getPostByGroupid", params);
	}

	@Override
	public int getPostCountByGroupid(String groupid) {
		return (Integer) getSqlMapClientTemplate().queryForObject(this.getNameSpace() + ".getPostCountByGroupid",
				groupid);
	}

}
