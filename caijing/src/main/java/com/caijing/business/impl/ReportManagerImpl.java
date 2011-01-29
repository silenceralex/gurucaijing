package com.caijing.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import com.caijing.business.ReportManager;
import com.caijing.crawl.ReportExtractor;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.ReportDao;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;
import com.caijing.mail.PDFReader;
import com.caijing.util.ContextFactory;

public class ReportManagerImpl implements ReportManager {
	private static int pageSize = 20;
	@Autowired
	@Qualifier("reportDao")
	private ReportDao reportDao = null;

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	@Autowired
	@Qualifier("reportExtractor")
	private ReportExtractor reportExtractor = null;

	public ReportDao getReportDao() {
		return reportDao;
	}

	public void setReportDao(ReportDao reportDao) {
		this.reportDao = reportDao;
	}

	public ReportExtractor getReportExtractor() {
		return reportExtractor;
	}

	public void setReportExtractor(ReportExtractor reportExtractor) {
		this.reportExtractor = reportExtractor;
	}

	public RecommendStockDao getRecommendStockDao() {
		return recommendStockDao;
	}

	public void setRecommendStockDao(RecommendStockDao recommendStockDao) {
		this.recommendStockDao = recommendStockDao;
	}

	private PDFReader reader = null;

	public RecommendStock extractFromPDF(String pdfilepath) {
		// TODO Auto-generated method stub
		return null;
	}

	public PDFReader getReader() {
		return reader;
	}

	public void setReader(PDFReader reader) {
		this.reader = reader;
	}

	public List<Report> getAllReports(int currpage) {
		int start = pageSize * currpage;
		int offset = pageSize;
		return reportDao.getAllReports(start, offset);
	}

	public int getAllReportsCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void updateObjectPriceBySaname(String saname) {
		int count = recommendStockDao.getAllRecommendCountBySaname(saname);
		System.out.println("count:" + count);
		int start = 0;
		while (start < count) {
			System.out.println("start:" + start);
			List<RecommendStock> stocks = recommendStockDao.getRecommendStocksBySaname(saname, start, 50);
			for (RecommendStock stock : stocks) {
				if (stock.getObjectprice() != 0) {
					continue;
				}
				String url = "http://51gurus.com" + stock.getFilepath().replace("pdf", "txt");
				if (url.contains("/oldhtml")) {
					url = url.replace("/home", "");
				}
				float objectprice = reportExtractor.extractObjectpriceFromURL(saname, url);

				if (objectprice != 0 && stock.getObjectprice() != objectprice) {
					//					recommendStockDao.update(newObject);
					System.out.println("url:" + url);
					recommendStockDao.updateObjectPriceByReportid(stock.getRecommendid(), objectprice);
					System.out.println("insert:" + stock.getRecommendid());
				} else {
					System.out.println("url:" + url + "  objectprice: " + objectprice);
				}
			}
			start += 50;
		}
	}

	public static void main(String[] args) {
		ApplicationContext context = ContextFactory.getApplicationContext();
		ReportManager reportManager = (ReportManager) context.getBean("reportManager");
		reportManager.updateObjectPriceBySaname("ÉêÒøÍò¹ú");
	}
}
