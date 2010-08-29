package com.caijing.dao.ibatis;

import java.util.List;

import com.caijing.dao.EconomistDao;
import com.caijing.domain.Economist;
import com.caijing.util.CrudDaoDefault;

public class EconomistDaoImpl extends CrudDaoDefault implements EconomistDao {
	
	public int getAllEconomistCount(){
		return (Integer) getSqlMapClientTemplate().queryForObject(getNameSpace()+".getAllEconomistCount");		
	}
	
	@SuppressWarnings("unchecked")
	public List<Economist> getAllEconomist() {
		return getSqlMapClientTemplate().queryForList(this.getNameSpace()+".getAllEconomist");
	}
	
	public Economist getEcomonistByname(String name) {
		return (Economist) getSqlMapClientTemplate().queryForList(getNameSpace()+".getEconomistByname",name);
	}

}
