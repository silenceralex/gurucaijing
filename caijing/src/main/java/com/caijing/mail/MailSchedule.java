package com.caijing.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.crawl.ReportExtractorImpl;
import com.caijing.dao.ReportDao;

public class MailSchedule {
	@Autowired
	@Qualifier("reportExtractor")
	private ReportExtractorImpl extractor = null;
	
	@Autowired
	@Qualifier("reportDao")
	private ReportDao reportDao = null;

	public void processMail() {
		MailReceiver receiver = new MailReceiver();
		PDFReader reader = new PDFReader();
		reader.setExtractor(extractor);
		reader.setReportDao(reportDao);
		reader.init();
		receiver.setReader(reader);
		receiver.setUsername("bg20052008");// 您的邮箱账号
		receiver.setPassword("336699");// 您的邮箱密码
		receiver.setAttachPath("/home/app/email");// 您要存放附件在什么位置？绝对路径
		try {
			receiver.reveiveMail();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("process over!");
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
}
