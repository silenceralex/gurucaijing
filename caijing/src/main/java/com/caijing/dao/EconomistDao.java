package com.caijing.dao;

import java.util.List;

import com.caijing.domain.Economist;
import com.caijing.util.CrudDao;

public  interface EconomistDao extends CrudDao{
	public Economist getEcomonistByname(String name);
	public int getAllEconomistCount();
	public List<Economist> getAllEconomist();
}