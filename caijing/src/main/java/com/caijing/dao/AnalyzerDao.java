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

	public void updateSuccessRatio(Analyzer analyzer);

	public List<Analyzer> getSuccessRankedAnalyzersByAgency(String agency);

	public List<Analyzer> getSuccessRankedAnalyzers();

	public List<Analyzer> getAnalyzersAfter(String date);

	//获取非明星分析师，level=0
	public List<Analyzer> getUnStarAnalyzers();
}
