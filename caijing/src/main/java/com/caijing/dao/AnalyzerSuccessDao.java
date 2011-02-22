package com.caijing.dao;

import java.util.List;

import com.caijing.domain.AnalyzerSuccess;
import com.caijing.util.CrudDao;

public interface AnalyzerSuccessDao extends CrudDao {
	public List<AnalyzerSuccess> getAnalyzerRankByYear(String year, int start, int length);

	public List<AnalyzerSuccess> getAnalyzerRankBySanameYear(String saname, String year, int start, int length);

	public AnalyzerSuccess getOneAnalyzerSuccess(String aid, String year);

	public List<String> getYearList(String aid);
}
