package com.caijing.dao;

import java.util.List;

import com.caijing.domain.Master;
import com.caijing.util.CrudDao;

public interface MasterDao extends CrudDao {
	List<Master> getAllMasters(Integer start, Integer length);

	Master getMasterByName(String name);
}
