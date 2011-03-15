package com.caijing.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caijing.dao.ColumnArticleDao;
import com.caijing.domain.ColumnArticle;
import com.caijing.util.CrudDaoDefault;

public class ColumnArticleDaoImpl extends CrudDaoDefault implements ColumnArticleDao {

	@SuppressWarnings("unchecked")
	public List<ColumnArticle> getColumnArticleByAuthor(String author, int start, int length) {
		Map<String, Object> params = new HashMap<String, Object>(1);
		params.put("author", author);
		params.put("start", start);
		params.put("length", length);
		return (List<ColumnArticle>) getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".getColumnArticleByAuthor", params);
	}

	public int getColumnArticleCountByAuthor(String author) {
		return (Integer) getSqlMapClientTemplate().queryForObject(getNameSpace() + ".getColumnArticleCountByAuthor",
				author);
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

	@Override
	public List<ColumnArticle> getColumnArticleBySource(String source) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("source", source);
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getColumnArticleBySource", params);
	}

	@Override
	public List<ColumnArticle> getColumnArticleByDomain() {
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getColumnArticleByDomain");
	}

	@Override
	public List<ColumnArticle> getUnpublishArticles() {
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getUnpublishArticles");
	}

	@Override
	public List<ColumnArticle> getColumnArticleByType(int type, int start, int length) {
		Map<String, Object> params = new HashMap<String, Object>(4);
		params.put("type", type);
		params.put("length", length);
		params.put("start", start);
		//宏观动态栏目没有作者
		if (type != 2) {
			params.put("author", "111");
		}
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getColumnArticleByType", params);
	}

	@Override
	public int getAllArticleCountByType(int type) {
		return (Integer) getSqlMapClientTemplate().queryForObject(this.getNameSpace() + ".getAllArticleCountByType",
				type);
	}

	@Override
	public List<ColumnArticle> getABSArticlesByType(int type, int start, int length) {
		Map<String, Object> params = new HashMap<String, Object>(4);
		params.put("type", type);
		params.put("length", length);
		params.put("start", start);
		//宏观动态栏目没有作者
		if (type != 2) {
			params.put("author", "111");
		}
		return getSqlMapClientTemplate().queryForList(this.getNameSpace() + ".getABSArticlesByType", params);
	}
}
