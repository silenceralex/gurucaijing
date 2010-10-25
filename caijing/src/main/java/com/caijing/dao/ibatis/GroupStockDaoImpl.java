package com.caijing.dao.ibatis;

import java.util.List;

import com.caijing.dao.GroupStockDao;
import com.caijing.domain.GroupStock;
import com.caijing.util.CrudDaoDefault;

public class GroupStockDaoImpl extends CrudDaoDefault implements GroupStockDao {

	@Override
	public List<GroupStock> getCurrentStockByGroupid(String groupid) {
		return (List<GroupStock>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".getCurrentStockByGroupid",
				groupid);
	}

}
