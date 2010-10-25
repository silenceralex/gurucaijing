package com.caijing.dao;

import java.util.List;

import com.caijing.domain.GroupStock;
import com.caijing.util.CrudDao;

public interface GroupStockDao extends CrudDao {
	List<GroupStock> getCurrentStockByGroupid(String groupid);
}
