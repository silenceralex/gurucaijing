package com.caijing.flush;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.NoticeDao;
import com.caijing.spider.CrawlNotice;

public class FlushSchedule {
	private NoticeDao noticeDao = null;
	private static Log logger = LogFactory.getLog(FlushSchedule.class);

	@Autowired
	@Qualifier("htmlFlush")
	private HtmlFlusher htmlFlush = null;

	public HtmlFlusher getHtmlFlush() {
		return htmlFlush;
	}

	public void setHtmlFlush(HtmlFlusher htmlFlush) {
		this.htmlFlush = htmlFlush;
	}

	public void run() {
		try {
			crawlNotice();
			htmlFlush.flushNotice();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//华尔街大师研判
	}

	private void crawlNotice() {
		CrawlNotice notice = new CrawlNotice();
		notice.init();
		notice.setNoticeDao(noticeDao);
		notice.setHtmlFlush(htmlFlush);
		logger.debug("crawl hexun notice!");
		notice.runHexun();

		logger.debug("crawl EastMoney notice!");
		notice.runEastMoney();

	}

	public NoticeDao getNoticeDao() {
		return noticeDao;
	}

	public void setNoticeDao(NoticeDao noticeDao) {
		this.noticeDao = noticeDao;
	}
}
