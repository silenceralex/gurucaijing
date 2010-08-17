package com.caijing.dao;

import java.util.List;
import com.caijing.domain.ColumnArticle;
import com.caijing.util.CrudDao;

public  interface ColumnArticleDao extends CrudDao{
	public List<ColumnArticle> getColumnAritcleByname(String name);
}
