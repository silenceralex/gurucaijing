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
		RecommendStockDao recommendStockDao = (RecommendStockDao) context
				.getBean("recommendStockDao");
		Config config = (Config) context.getBean("config");
		ReportExtractorImpl extractor = new ReportExtractorImpl();
		extractor.setConfig(config);
		long start = System.currentTimeMillis();
//		 List<Report> reports = reportDao.getCompanyReportsBySaname("�������");
//		 List<Report> reports = reportDao.getCompanyReportsBySaname("��̩����");
		List<Report> reports = reportDao.getCompanyReportsBySaname("�н�˾");
		System.out.println("Reports size: " + reports.size());
		long end = System.currentTimeMillis();
		System.out.println("Use time: " + (end - start) / 1000 + " seconds");
		int i = 0;
		for (Report report : reports) {
			if (config.getValue(report.getSaname()) != null) {
				System.out.println("Now process NO.: " + i);
				// System.out.println("Reports filepath: " +
				// report.getFilepath());
				// System.out.println("Reports type: " + report.getType());
				// System.out.println("Reports title: " + report.getTitle());
				// System.out.println("Reports Saname: " + report.getSaname());
				// System.out.println("Reports Stockcode: "
				// + report.getStockcode());
				// System.out.println("Reports Stockname: "
				// + report.getStockname());
				String txtpath = "http://guru.caijing.com"
						+ report.getFilepath();
				System.out.println("Reports txt path: "
						+ txtpath.replace(".pdf", ".txt"));
				if (recommendStockDao.getRecommendStockbyReportid(report
						.getRid()) == null) {
					RecommendStock rs = extractor.extractFromFile(report,
							txtpath.replace(".pdf", ".txt"));
					if (rs != null) {
						rs.setReportid(report.getRid());
						if (rs.getExtractnum() > 2) {
							recommendStockDao.insert(rs);
						}
					}
				} else {
					System.out.println("Already processed!");
				}
				i++;
			}
		}
		System.out.println("process size: " + i);

	}

}