package com.caijing.crawl;

import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;

public interface ReportExtractor {
	public Report extractFromTitle(String file,String rid);
	public RecommendStock extractFromFile(String key,String file,String rid);
}