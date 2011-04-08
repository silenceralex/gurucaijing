package com.caijing.dao;

import java.util.List;

import com.caijing.domain.Post;
import com.caijing.util.CrudDao;

public interface PostDao extends CrudDao {
	public List<Post> getPostByGroupid(String groupid, int start, int length);

	public int getPostCountByGroupid(String groupid);

}
