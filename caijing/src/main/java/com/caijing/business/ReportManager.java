package com.caijing.business;

import java.util.List;

import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;

public interface ReportManager {
	public RecommendStock extractFromPDF(String pdfilepath);

	public List<Report> getAllReports(int currpage);

	public int getAllReportsCount();

	//对某一券商重提取objectprice
	public void updateObjectPriceBySaname(String saname);

}
