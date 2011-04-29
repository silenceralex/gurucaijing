package com.caijing.dao;

import java.util.List;

import com.caijing.domain.Userright;
import com.caijing.util.CrudDao;

public interface UserrightDAO extends CrudDao {
	List<String> getIndustriesByUserid(String userid, String path);
	public boolean updateSelective(Userright userright);
	List<Userright> getUserrightByUserid(String userid);
}