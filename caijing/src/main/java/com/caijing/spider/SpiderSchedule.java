package com.caijing.spider;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.caijing.dao.ColumnArticleDao;

public class SpiderSchedule {
	private String paramXml = null;
	private ColumnArticleDao columnArticleDao = null;

	private String astockXml = null;
	private static Log logger = LogFactory.getLog(SpiderSchedule.class);

	public void run() {
		SAXReader sr = new SAXReader();
		Document xml = null;
		try {

			System.out.println("Input xml : " + paramXml);
			xml = sr.read(new File(paramXml));
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		RssJob rssjob = ConfigReader.getRssJobFromXML(xml);
		rssjob.setColumnArticleDao(columnArticleDao);
		rssjob.run();

		ConfigReader.getRssJobFromXML(xml);

		try {
			xml = sr.read(new File(astockXml));
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		CrawlJob job = ConfigReader.fromXML(xml);
		job.setColumnArticleDao(columnArticleDao);
		long startTime = System.currentTimeMillis();
		if (logger.isDebugEnabled()) {
			logger.debug("CrawlJob: \tThreads:" + job.getThreads() + "\tMax Connections Per Host:"
					+ job.getMaxConnections());
		}
		Thread t = new Thread(job);
		t.start();

		while (t.isAlive()) {
			long[] s = job.getStatus();
			long crawled = s[0];
			long queued = s[1];
			long bytes = s[2];
			long timeElpased = (System.currentTimeMillis() - startTime);
			double speed = timeElpased == 0 ? 0 : s[0] * 1.0 / timeElpased;
			double timeEst = queued / speed;
			double dlSpeed = timeElpased == 0 ? 0 : bytes * 1.0 / timeElpased;

			System.out.println("Crawled:" + crawled + "\tQueued:" + queued + "\tSpeed:" + (speed * 1000)
					+ " pps\tTime Used:" + (timeElpased / 1000) + "s\tTime Remain:" + (timeEst / 1000)
					+ "s\tDownload Speed: " + (dlSpeed) + "KBPS");

			for (String key : job.getWorkingOnPages().keySet()) {
				System.out.println("\t" + key + "\t" + job.getWorkingOnPages().get(key) + "\tErr:"
						+ job.getWorkingErrors().get(key));
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public String getParamXml() {
		return paramXml;
	}

	public void setParamXml(String paramXml) {
		this.paramXml = paramXml;
	}

	public ColumnArticleDao getColumnArticleDao() {
		return columnArticleDao;
	}

	public void setColumnArticleDao(ColumnArticleDao columnArticleDao) {
		this.columnArticleDao = columnArticleDao;
	}

	public String getAstockXml() {
		return astockXml;
	}

	public void setAstockXml(String astockXml) {
		this.astockXml = astockXml;
	}
}
