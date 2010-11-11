package com.caijing.dao;

import java.util.Date;
import java.util.List;

import com.caijing.domain.GroupStock;
import com.caijing.util.CrudDao;

public interface GroupStockDao extends CrudDao {
	List<GroupStock> getCurrentStockByGroupid(String groupid);

	Date getEarliestIntimeByAid(String aid);

	GroupStock getCurrentStockByGroupidAndStockcode(String groupid, String stockcode);
}
