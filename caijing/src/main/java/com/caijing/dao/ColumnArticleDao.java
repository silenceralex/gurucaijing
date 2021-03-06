package com.caijing.dao;

import java.util.List;

import com.caijing.domain.ColumnArticle;
import com.caijing.util.CrudDao;

public interface ColumnArticleDao extends CrudDao {
	public List<ColumnArticle> getColumnArticleByAuthor(String name, int start, int length);

	public int getColumnArticleCountByAuthor(String name);

	public List<ColumnArticle> getColumnArticleByaid(int aid);

	public List<String> getAllArticlelink();

	public List<ColumnArticle> getColumnArticleByname(String saname, int start, int offset);

	public int getAllColumnArticleCount();

	public List<ColumnArticle> getAllColumnArticle(int start, int offset);

	public List<ColumnArticle> getColumnArticleBySource(String source);

	public List<ColumnArticle> getColumnArticleByDomain();

	//	/**
	//	 * type 1 大师研判，2 宏观动态，3 草根博客 ， 0 财经专栏
	//	 * @param type
	//	 * @param length  条目数
	//	 * @return
	//	 */
	//	public List<ColumnArticle> getColumnArticleByType(int type, int length);

	public List<ColumnArticle> getUnpublishArticles();

	/**
	 * type 1 大师研判，2 宏观动态，3 草根博客 ， 0 财经专栏
	 * @param type
	 * @param length  条目数
	 * @return
	 */
	public List<ColumnArticle> getColumnArticleByType(int type, int start, int length);

	/**
	 * 方便首页调用，必须有摘要并且abs要长于5
	 * @param type  1 大师研判，2 宏观动态，3 草根博客 ， 0 财经专栏
	 * @param start  起始位置
	 * @param length 条目数
	 * @return
	 */
	public List<ColumnArticle> getABSArticlesByType(int type, int start, int length);

	public int getAllArticleCountByType(int type);
}
