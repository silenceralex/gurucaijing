package com.caijing.util;

import java.util.List;

import org.springframework.context.ApplicationContext;

import com.caijing.crawl.ReportExtractorImpl;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.ReportDao;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;

public class FetchRecommendStock {
	ApplicationContext context = ContextFactory.getApplicationContext();
	ReportDao reportDao = (ReportDao) context.getBean("reportDao");
	RecommendStockDao recommendStockDao = (RecommendStockDao) context.getBean("recommendStockDao");
	Config config = (Config) context.getBean("config");
	ReportExtractorImpl extractor = (ReportExtractorImpl) context.getBean("reportExtractor");

	public void extractAfter(String saname, String date) {
		long start = System.currentTimeMillis();
		//		 List<Report> reports = reportDao.getCompanyReportsBySaname("申银万国");
		//		 List<Report> reports = reportDao.getCompanyReportsBySaname("国泰君安");
		//		List<Report> reports = reportDao.getCompanyReportsBySaname("中金公司");
		//		List<Report> reports = reportDao.getCompanyReportsBySaname("海通证券");
		//		List<Report> reports = reportDao.getCompanyReportsBySaname("国金证券");
		//		List<Report> reports = reportDao.getCompanyReportsBySaname("安信证券");
		List<Report> reports = reportDao.getCompanyReportsBySaname(saname);
		System.out.println("Reports size: " + reports.size());
		long end = System.currentTimeMillis();
		System.out.println("Use time: " + (end - start) / 1000 + " seconds");
		int i = 0;
		for (Report report : reports) {
			if (config.getValue(report.getSaname()) != null) {
				System.out.println("Now process NO.: " + i);
				String txtpath = "http://guru.caijing.com" + report.getFilepath();
				System.out.println("Reports txt path: " + txtpath.replace(".pdf", ".txt"));
				if (recommendStockDao.getRecommendStockbyReportid(report.getRid()) == null) {
					RecommendStock rs = extractor.extractFromFile(report, txtpath.replace(".pdf", ".txt"));
					if (rs != null) {
						rs.setReportid(report.getRid());
						if (rs.getExtractnum() > 2) {
							recommendStockDao.insert(rs);
							System.out.println("Reports getAname: " + rs.getAname());
							System.out.println("Reports getObjectprice: " + rs.getObjectprice());
							System.out.println("Reports getCreatedate: " + rs.getCreatedate());
							System.out.println("Reports getGrade: " + rs.getGrade());
							System.out.println("Reports getEps: " + rs.getEps());
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
		//		 List<Report> reports = reportDao.getCompanyReportsBySaname("申银万国");
		//		 List<Report> reports = reportDao.getCompanyReportsBySaname("国泰君安");
		//		List<Report> reports = reportDao.getCompanyReportsBySaname("中金公司");
		//		List<Report> reports = reportDao.getCompanyReportsBySaname("海通证券");
		//		List<Report> reports = reportDao.getCompanyReportsBySaname("国金证券");
		//		List<Report> reports = reportDao.getCompanyReportsBySaname("安信证券");
//		List<Report> reports = reportDao.getCompanyReportsBySaname("国金证券");
		List<Report> reports = reportDao.getCompanyReportsBySaname("广发证券");
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
				String txtpath = "http://guru.caijing.com" + report.getFilepath();
				System.out.println("Reports txt path: " + txtpath.replace(".pdf", ".txt"));
				if (recommendStockDao.getRecommendStockbyReportid(report.getRid()) == null) {
					RecommendStock rs = extractor.extractFromFile(report, txtpath.replace(".pdf", ".txt"));
					if (rs != null) {
						rs.setReportid(report.getRid());
						if (rs.getExtractnum() > 2) {
							recommendStockDao.insert(rs);
							System.out.println("Reports getAname: " + rs.getAname());
							System.out.println("Reports getObjectprice: " + rs.getObjectprice());
							System.out.println("Reports getCreatedate: " + rs.getCreatedate());
							System.out.println("Reports getGrade: " + rs.getGrade());
							System.out.println("Reports getEps: " + rs.getEps());
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
