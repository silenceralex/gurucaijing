package com.caijing.mail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.crawl.ReportExtractorImpl;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.ReportDao;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;
import com.caijing.util.Config;
import com.caijing.util.FileUtil;

public class ExtractSchedule {

	@Autowired
	@Qualifier("reportExtractor")
	private ReportExtractorImpl extractor = null;

	@Autowired
	@Qualifier("reportDao")
	private ReportDao reportDao = null;

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	@Autowired
	@Qualifier("config")
	private Config config = null;
	
	private static final String timeStamp = "/home/app/extract_timeStamp";

	public void extractAll() {
		String judgetime = FileUtil.read(timeStamp, "GBK");
		Date date =new Date();
		extract("申银万国", judgetime);
		extract("国泰君安", judgetime);
		extract("中金公司", judgetime);
		extract("海通证券", judgetime);
		extract("国金证券", judgetime);
		extract("安信证券", judgetime);
		extract("招商证券", judgetime);
		extract("广发证券", judgetime);
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FileUtil.write(timeStamp, sdf.format(date));
	}

	public void extract(String saname, String date) {
		long start = System.currentTimeMillis();

		List<Report> reports = reportDao.getCompanyReportsBySanameAfter(saname, date);
		System.out.println("Reports size: " + reports.size());
		long end = System.currentTimeMillis();
		System.out.println("Use time: " + (end - start) / 1000 + " seconds");
		int i = 0;
		for (Report report : reports) {
			if (config.getValue(report.getSaname()) != null) {
				System.out.println("Now process NO.: " + i);
				String txtpath = "/home/html" + report.getFilepath();
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

	public ReportExtractorImpl getExtractor() {
		return extractor;
	}

	public void setExtractor(ReportExtractorImpl extractor) {
		this.extractor = extractor;
	}

	public ReportDao getReportDao() {
		return reportDao;
	}

	public void setReportDao(ReportDao reportDao) {
		this.reportDao = reportDao;
	}

	public RecommendStockDao getRecommendStockDao() {
		return recommendStockDao;
	}

	public void setRecommendStockDao(RecommendStockDao recommendStockDao) {
		this.recommendStockDao = recommendStockDao;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
}
