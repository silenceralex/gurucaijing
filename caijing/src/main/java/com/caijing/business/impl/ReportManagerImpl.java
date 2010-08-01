package com.caijing.business.impl;

import com.caijing.business.ReportManager;
import com.caijing.domain.RecommendStock;
import com.caijing.mail.PDFReader;

public class ReportManagerImpl implements ReportManager {

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

}
