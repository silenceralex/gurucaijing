package com.caijing.spider;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.ColumnArticleDao;
import com.caijing.dao.NoticeDao;
import com.caijing.dao.PostDao;
import com.caijing.flush.HtmlFlusher;
import com.caijing.util.ContextFactory;

public class SpiderSchedule {
	private String paramXml = null;
	private ColumnArticleDao columnArticleDao = null;
	private NoticeDao noticeDao = null;
	private String astockXml = null;
	private String wsjdashiXml = null;
	private String wsjhongguanXml = null;
	private String caogenXml = null;

	public String getTyjXml() {
		return tyjXml;
	}

	public void setTyjXml(String tyjXml) {
		this.tyjXml = tyjXml;
	}

	public String getHgsXml() {
		return hgsXml;
	}

	public void setHgsXml(String hgsXml) {
		this.hgsXml = hgsXml;
	}

	private String tyjXml = null;
	private String hgsXml = null;
	private static Log logger = LogFactory.getLog(SpiderSchedule.class);
	@Autowired
	@Qualifier("htmlFlush")
	private HtmlFlusher htmlFlush = null;

	public HtmlFlusher getHtmlFlush() {
		return htmlFlush;
	}

	public void setHtmlFlush(HtmlFlusher htmlFlush) {
		this.htmlFlush = htmlFlush;
	}

	@Autowired
	@Qualifier("postDao")
	private PostDao postDao = null;

	public PostDao getPostDao() {
		return postDao;
	}

	public void setPostDao(PostDao postDao) {
		this.postDao = postDao;
	}

	private BerkeleyDB titleDB = null;

	public BerkeleyDB getTitleDB() {
		return titleDB;
	}

	public void setTitleDB(BerkeleyDB titleDB) {
		this.titleDB = titleDB;
	}

	public void run() {
		try {
			crawlRss(paramXml);
			crawlRss(caogenXml);
			crawlRss(hgsXml);
			crawlRss(tyjXml);
			crawlHtml(astockXml);
			crawlHtml(wsjdashiXml);
			crawlHtml(wsjhongguanXml);
			htmlFlush.flushIndex();
			htmlFlush.flushArticleList(0);
			htmlFlush.flushArticleList(1);
			htmlFlush.flushArticleList(2);
			htmlFlush.flushArticleList(3);
			//			crawlNotice();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//华尔街大师研判
	}

	//	private void crawlNotice() {
	//		CrawlNotice notice = new CrawlNotice();
	//		notice.init();
	//		notice.setNoticeDao(noticeDao);
	//		notice.runHexun();
	//		notice.runEastMoney();
	//	}

	private void crawlRss(String rss) {
		SAXReader sr = new SAXReader();
		Document xml = null;
		try {
			System.out.println("Input xml : " + rss);
			xml = sr.read(new File(rss));
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		RssJob rssjob = ConfigReader.getRssJobFromXML(xml);
		rssjob.setColumnArticleDao(columnArticleDao);
		rssjob.setPostDao(postDao);
		rssjob.setTitleDB(titleDB);
		rssjob.run();
	}

	private void crawlHtml(String confxml) {
		SAXReader sr = new SAXReader();
		Document xml = null;
		try {
			xml = sr.read(new File(confxml));
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

	public String getCaogenXml() {
		return caogenXml;
	}

	public void setCaogenXml(String caogenXml) {
		this.caogenXml = caogenXml;
	}

	public String getWsjdashiXml() {
		return wsjdashiXml;
	}

	public void setWsjdashiXml(String wsjdashiXml) {
		this.wsjdashiXml = wsjdashiXml;
	}

	public String getWsjhongguanXml() {
		return wsjhongguanXml;
	}

	public void setWsjhongguanXml(String wsjhongguanXml) {
		this.wsjhongguanXml = wsjhongguanXml;
	}

	public NoticeDao getNoticeDao() {
		return noticeDao;
	}

	public void setNoticeDao(NoticeDao noticeDao) {
		this.noticeDao = noticeDao;
	}

	public static void main(String[] args) {
		SpiderSchedule schedule = (SpiderSchedule) ContextFactory.getBean("timeSpiderSchedule");
		schedule.run();
		System.exit(0);
	}
}
