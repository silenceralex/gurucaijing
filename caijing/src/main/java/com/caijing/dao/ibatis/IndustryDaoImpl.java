package com.caijing.dao.ibatis;

import java.util.List;

import com.caijing.dao.IndustryDao;
import com.caijing.domain.Industry;
import com.caijing.util.CrudDaoDefault;

public class IndustryDaoImpl extends CrudDaoDefault implements IndustryDao {
	
	@Override
	public List<Industry> selectlv1() {
		return (List<Industry>) getSqlMapClientTemplate().queryForList(getNameSpace() + ".selectlv1");
	}
	
	@Override
	public Industry selectByIndustryname(String industry) {
		return (Industry) getSqlMapClientTemplate().queryForList(getNameSpace() + ".selectByIndustryname");
	}
	
}
