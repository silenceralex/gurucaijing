package com.caijing.business;

import com.caijing.domain.RecommendStock;

public interface ReportManager {
	public RecommendStock extractFromPDF(String pdfilepath);
}
