package com.caijing.flush;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caijing.dao.NoticeDao;
import com.caijing.spider.CrawlNotice;

public class FlushSchedule {
	private NoticeDao noticeDao = null;
	private static Log logger = LogFactory.getLog(FlushSchedule.class);
	private HtmlFlusher htmlFlush = new HtmlFlusher();

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
		notice.runHexun();
		notice.runEastMoney();
	}

	public NoticeDao getNoticeDao() {
		return noticeDao;
	}

	public void setNoticeDao(NoticeDao noticeDao) {
		this.noticeDao = noticeDao;
	}
}
