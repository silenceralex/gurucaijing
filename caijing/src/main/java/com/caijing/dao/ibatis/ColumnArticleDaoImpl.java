package com.caijing.dao.ibatis;

import java.util.List;

import com.caijing.dao.ColumnArticleDao;
import com.caijing.domain.ColumnArticle;
import com.caijing.util.CrudDaoDefault;

public class ColumnArticleDaoImpl extends CrudDaoDefault implements ColumnArticleDao {

	@SuppressWarnings("unchecked")
	public List<ColumnArticle> getColumnAritcleByname(String name){
		return (List<ColumnArticle>)getSqlMapClientTemplate().queryForList(getNameSpace()+".getColumnAritcleByname",name);
	}

}
