package com.caijing.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.business.ReportManager;
import com.caijing.dao.ReportDao;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;
import com.caijing.mail.PDFReader;

public class ReportManagerImpl implements ReportManager {
	private static int pageSize=20;
	@Autowired
	@Qualifier("reportDao")
	private ReportDao reportDao = null;
	private PDFReader reader=null;
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
		int start=pageSize*currpage;
		int offset=pageSize;
		return reportDao.getAllReports(start, offset);
	}
	public int getAllReportsCount() {
		// TODO Auto-generated method stub
		return 0;
	}

}
