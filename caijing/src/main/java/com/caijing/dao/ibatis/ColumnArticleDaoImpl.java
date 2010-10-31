package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.ColumnArticleDao;
import com.caijing.domain.ColumnArticle;
import com.caijing.util.CrudDaoDefault;

public class ColumnArticleDaoImpl extends CrudDaoDefault implements ColumnArticleDao {

	@SuppressWarnings("unchecked")
	public List<ColumnArticle> getColumnArticleByname(String author) {
		Map<String, Object> params = new HashMap<String, Object>(1);
		params.put("author", author);
		return (List<ColumnArticle>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getColumnArticleByname",
				params);
	}

	public List<ColumnArticle> getColumnArticleByaid(int aid) {
		Map<String, Object> params = new HashMap<String, Object>(1);
		params.put("aid", aid);
		return (List<ColumnArticle>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getColumnArticleByaid",
				params);
	}

	public List<String> getAllArticlelink() {
		return (List<String>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getAllArticlelink");
	}

	@SuppressWarnings("unchecked")
	public List<ColumnArticle> getColumnArticleByname(String name, int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("saname", name);
		params.put("start", start);
		params.put("offset", offset);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getColumnArticleByname", params);
	}

	@SuppressWarnings("unchecked")
	public int getAllColumnArticleCount() {
		return (Integer) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getAllColumnArticleCount");

	}

	@SuppressWarnings("unchecked")
	public List<ColumnArticle> getAllColumnArticle(int start, int offset) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("start", start);
		params.put("offset", offset);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getAllColumnArticle", params);
	}

}
