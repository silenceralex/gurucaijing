package com.caijing.util;

import java.util.List;

import org.springframework.context.ApplicationContext;

import com.caijing.crawl.ReportExtractorImpl;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.ReportDao;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;

public class FetchRecommendStock {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext context = ContextFactory.getApplicationContext();
		ReportDao reportDao = (ReportDao) context.getBean("reportDao");
		RecommendStockDao recommendStockDao = (RecommendStockDao) context.getBean("recommendStockDao");
		Config config = (Config) context.getBean("config");
		ReportExtractorImpl extractor = new ReportExtractorImpl();
		extractor.setConfig(config);
		long start = System.currentTimeMillis();
		List<Report> reports = reportDao.getAllCompanyReports();
		System.out.println("Reports size: " + reports.size());
		long end = System.currentTimeMillis();
		System.out.println("Use time: " + (end - start) / 1000 + " seconds");
		int i = 0;
		for (Report report : reports) {
			if (report.getType() == 1
					&& config.getValue(report.getSaname()) != null) {
//				System.out.println("Reports filepath: " + report.getFilepath());
//				System.out.println("Reports type: " + report.getType());
//				System.out.println("Reports title: " + report.getTitle());
//				System.out.println("Reports Saname: " + report.getSaname());
//				System.out.println("Reports Stockcode: "
//						+ report.getStockcode());
//				System.out.println("Reports Stockname: "
//						+ report.getStockname());
				String txtpath="/home/html"+report.getFilepath();
				System.out.println("Reports txt path: "
						+ txtpath.replace(".pdf", ".txt"));
				RecommendStock rs=extractor.extractFromFile(report.getSaname(), report.getStockname(),txtpath.replace(".pdf", ".txt") , report.getRid());
				recommendStockDao.insert(rs);		
				i++;
			}
		}
		System.out.println("process size: " + i);

	}

}
