package com.caijing.dao;

import java.util.List;
import com.caijing.domain.ColumnArticle;
import com.caijing.util.CrudDao;

public  interface ColumnArticleDao extends CrudDao{
	public List<ColumnArticle> getColumnArticleByname(String name);
	public List<ColumnArticle> getColumnArticleByaid(int aid);
	public List<String> getAllArticlelink();
	public List<ColumnArticle> getColumnArticleByname(String saname,int start,int offset);
	public int getAllColumnArticleCount();
	public List<ColumnArticle> getAllColumnArticle(int start,int offset);
}
