package com.caijing.dao;

import java.util.List;

import com.caijing.domain.Analyzer;
import com.caijing.util.CrudDao;

public interface AnalyzerDao extends CrudDao {
	public List<Analyzer> getAnalyzersByAgency(String agency);

	public List<Analyzer> getAllAnalyzers();

	public List<Analyzer> getStarAnalyzers();

	public Analyzer getAnalyzerByName(String name);

	public List<Analyzer> getAnalyzerRankList(String date, int start, int length);
}
