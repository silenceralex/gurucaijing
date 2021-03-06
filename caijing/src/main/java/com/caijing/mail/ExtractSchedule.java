package com.caijing.mail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.crawl.OnlineCrawler;
import com.caijing.crawl.ReportExtractorImpl;
import com.caijing.crawl.ThreadCrawler;
import com.caijing.dao.MasterMessageDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.ReportDao;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;
import com.caijing.flush.MasterFlusher;
import com.caijing.util.Config;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.caijing.util.FileUtil;

public class ExtractSchedule {
	private static final Log logger = LogFactory.getLog(ExtractSchedule.class);

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
	@Qualifier("threadCrawler")
	private ThreadCrawler threadCrawler = null;

	public ThreadCrawler getThreadCrawler() {
		return threadCrawler;
	}

	public void setThreadCrawler(ThreadCrawler threadCrawler) {
		this.threadCrawler = threadCrawler;
	}

	private MasterFlusher masterFlush = null;

	public MasterFlusher getMasterFlush() {
		return masterFlush;
	}

	public void setMasterFlush(MasterFlusher masterFlush) {
		this.masterFlush = masterFlush;
	}

	@Autowired
	@Qualifier("config")
	private Config config = null;

	@Autowired
	@Qualifier("masterMessageDao")
	private MasterMessageDao masterMessageDao = null;

	public MasterMessageDao getMasterMessageDao() {
		return masterMessageDao;
	}

	public void setMasterMessageDao(MasterMessageDao masterMessageDao) {
		this.masterMessageDao = masterMessageDao;
	}

	private static final String timeStamp = "/home/app/extract_timeStamp";

	public void crawlOnline() {
		OnlineCrawler crawler = new OnlineCrawler();
		crawler.init();
		crawler.setMasterMessageDao(masterMessageDao);
		Map map = config.getValue("groupid");
		for (Object key : map.keySet()) {
			Map propertys = (Map) map.get(key);
			String k = (String) propertys.get("key");
			String d_str = (String) propertys.get("dstr");
			String refer = (String) propertys.get("refer");
			String uid = (String) propertys.get("uid");
			String cookie = (String) propertys.get("cookie");

			int masterid = Integer.parseInt((String) key);
			int num = masterMessageDao.getCurrentNumByMasterid(masterid, DateTools.transformYYYYMMDDDate(new Date()));
			logger.debug("masterid:" + key + "  mastername:" + propertys.get("name") + "  key:" + k + "  d_str:"
					+ d_str + "  startNum:" + num);
			crawler.crawler(masterid, num, uid, cookie, d_str, k, refer);

			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//				crawler.getZhibo(masterid, num, k, d_str);

		}
	}

	public void crawlThread() {
		Map map = config.getValue("groupid");
		for (Object key : map.keySet()) {
			Map propertys = (Map) map.get(key);
			String cookie = (String) propertys.get("cookie");
			//有新thread入库,刷新thread页
			if (threadCrawler.crawl((String) key, cookie)) {
				masterFlush.flushMasterInfo();
			}
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void extractAll() {
		String judgetime = FileUtil.read(timeStamp, "GBK");
		Date date = new Date();
		extract("申银万国", judgetime);
		extract("国泰君安", judgetime);
		extract("中金公司", judgetime);
		extract("海通证券", judgetime);
		extract("国金证券", judgetime);
		extract("安信证券", judgetime);
		extract("招商证券", judgetime);
		extract("广发证券", judgetime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FileUtil.write(timeStamp, sdf.format(date));
	}

	public void extract(String saname, String date) {
		long start = System.currentTimeMillis();
		System.out.println("Reports size: " + date);
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

	public static void main(String[] args) {
		ExtractSchedule extractScheduler = (ExtractSchedule) ContextFactory.getBean("extractScheduler");
		extractScheduler.crawlThread();
		System.exit(0);
	}
}
