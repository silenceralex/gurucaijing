package com.caijing.dao;

import java.util.Date;
import java.util.List;

import com.caijing.domain.GroupStock;
import com.caijing.util.CrudDao;

public interface GroupStockDao extends CrudDao {
	List<GroupStock> getCurrentStockByGroupid(String groupid);

	Date getEarliestIntimeByAid(String aid);

	GroupStock getCurrentStockByGroupidAndStockcode(String groupid, String stockcode);

	List<GroupStock> getNameAndCodeByAid(String aid);

	List<GroupStock> getAllGroupStock();

	int updateStockGain(GroupStock groupStock);

	List<GroupStock> getGroupStockList(int length);
}
