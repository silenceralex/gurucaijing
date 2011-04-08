package com.caijing.spider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.caijing.crawl.Extractor;
import com.caijing.util.UrlDownload;
import com.sleepycat.je.DatabaseException;

/**
 * 完成第二阶段的任务，最终页面的解析，并且写入数据库 根据bpInterface的不同来进行不同来源的video_url的解析需求。
 * 
 * @author jun-chen
 * 
 */
public class PageParser extends Thread {
	private List<InnerMatch> innerMatches = new ArrayList<InnerMatch>();
	private UrlDownload urldown = new UrlDownload();
	private Vector<String> pageUrls = new Vector<String>();
	private BerkeleyDB urlDB = null;
	private String THRESHOLD = "2009-10-01 00:00:00";
	//	private Column col = null;
	private long bytesDownloaded = 0;
	private int videos = 0;
	private String curUrl = "";
	private boolean finished = false;

	private Extractor bpInterface = null;

	private static Logger logger = Logger.getLogger(PageParser.class);
	private HashSet<String> fieldSet = new HashSet<String>();
	String[] fields = { "title", "video_url", "url", "description", "last_time", "publish_time", "tag" };

	public PageParser(String urlDbPath, boolean readOnly) {
		for (String str : fields) {
			fieldSet.add(str);
		}
		urlDB = new BerkeleyDB(urlDbPath, readOnly);
	}

	/**
	 * 获取具体视频属性
	 * 
	 * @param inner
	 *            中间内容块
	 * @return 属性的map
	 */
	public Map<String, String> processPageInnerMatch(String inner) {
		Map<String, String> properties = new HashMap<String, String>();
		for (InnerMatch property : innerMatches) {
			String value = property.getMatchResult(null, inner, urldown);
			String key = property.getProperty();
			if (value != null) {
				properties.put(key, value);
			}
			//			System.out.println("KEY: "+key+"   Value: "+value);
		}
		return properties;
	}

	// 获取当前抓取的状态
	public long[] getStatus() {
		return new long[] { this.pageUrls.size(), videos, this.bytesDownloaded };
	}

	public void setDownEncode(String charset) {
		urldown.setCharset(charset);
	}

	public void run() {
		while (this.pageUrls.size() > 0) {
			curUrl = pageUrls.remove(0);
			String content = "";
			// 已经存在就不用下载了
			try {
				if (urlDB.contains(curUrl))
					continue;
				content = urldown.load(curUrl);
				// for test
				//				System.out.println("Thread name: "
				//						+ Thread.currentThread().getName() + " process : "
				//						+ curUrl);
				if (content == null)
					continue;
				bytesDownloaded += content.getBytes().length;
				//解析页面视频属性
				Map<String, String> properties = processPageInnerMatch(content);
				//根据栏目不同的需求，建立视频完整属性，核心为抽取video_url
				//				properties = buildProperties(properties, col);
				//判断是否需要插入数据库
				//				if (!needInsertDB(properties))
				//					continue;
				//				String sql = buildSql(properties, col, curUrl);
				//				//				System.out.println("sql: "+sql);
				//				int retCode = db.insertSQL(sql);
				//				if (retCode != 0) {
				//					System.out.println("insert return code is :" + retCode);
				//					logger.error("insert DB error  return code:" + retCode + "     insert sql:" + sql);
				//				} else {
				//					urlDB.putUrl(curUrl);
				//				}
				videos++;
			} catch (DatabaseException e) {
				logger.error("when downloading:" + curUrl + "  Exception Message: " + e.getMessage());
				continue;
			} catch (ClientProtocolException e) {
				logger.error("ClientProtocolException when downloading:" + curUrl + "  Exception Message: "
						+ e.getMessage());
				continue;
			} catch (IOException e) {
				logger.error("IOException when downloading:" + curUrl + "  Exception Message: " + e.getMessage());
				continue;
			}
		}
		finished = true;
	}

	private boolean beforeThreshold(String time) throws ParseException {
		SimpleDateFormat DATE_OUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dt1 = DATE_OUT_FORMAT.parse(time);
		Date dt2 = DATE_OUT_FORMAT.parse(THRESHOLD);
		if (dt1.getTime() > dt2.getTime()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断是否需要进行入库操作
	 * @param properties
	 * @return
	 */
	private boolean needInsertDB(Map<String, String> properties) {
		String title = properties.get("title");
		// 无标题，无下载地址则不插入数据库
		if (title == null || title.trim().length() == 0 || properties.get("video_url") == null)
			return false;

		if (properties.containsKey("publish_time")) {
			String ptime = properties.get("publish_time");
			// System.out.println("ptime: " + ptime);
			if (ptime == null || ptime.length() != 19) {
				System.out.println("publish_time is not right format!!!");
				logger.warn("publish_time is not right format!!!");
				return false;
			}
			try {
				if (beforeThreshold(ptime)) {
					return false;
				}
			} catch (ParseException e) {
				logger.error(e.getMessage());
				return false;
			}
		} else {
			logger.warn("Do not contain publish_time! :: current url: " + curUrl);
		}
		return true;
	}

	/**
	 * 通过页面获取的视频属性来建立完整的属性
	 * 
	 * @param properties
	 *            页面获取的属性key-value对
	 * @param Column
	 *            当前处理的栏目
	 * @return 完整的属性key-value对
	 */
	//	public Map<String, String> buildProperties(Map<String, String> properties, Column col) {
	//		if (!properties.containsKey("tag") || properties.get("tag").trim().length() == 0) {
	//			properties.put("tag", col.getTag());
	//		} else {
	//			if (!properties.get("tag").contains(col.getTag())) {
	//				properties.put("tag", col.getTag() + " " + properties.get("tag").trim());
	//			}
	//		}
	//		if (!properties.containsKey("description")) {
	//			properties.put("description", properties.get("title"));
	//		}
	//		if (!properties.containsKey("video_url")) {
	//			String video_url = bpInterface.extractVideoUrl(properties);
	//			if (video_url != null) {
	//				properties.put("video_url", video_url);
	//			}
	//		}
	//		return properties;
	//	}
	//
	//	public String buildSql(Map<String, String> properties, Column col, String url) {
	//		String sql = "insert into tbl_videoInfo (";
	//		for (String k : properties.keySet()) {
	//			if (fieldSet.contains(k))
	//				sql += k + ",";
	//		}
	//		sql += "cid,crawl_time,url) values(";
	//		for (String k : properties.keySet()) {
	//			if (fieldSet.contains(k)) {
	//				// System.out
	//				// .println("KEY: " + k + " Value:" + properties.get(k));
	//				sql += "'" + properties.get(k) + "',";
	//			}
	//		}
	//		sql += col.getCid() + ",now(),'" + url.toString() + "');";
	//		return sql;
	//	}
	public String normalizeOutlink(URL url, String outlink) throws MalformedURLException {
		if (outlink.startsWith("?")) {
			return url.toString().replaceAll("\\?.*$", outlink);
		}
		return new URL(url, outlink).toString();
	}

	public List<InnerMatch> getInnerMatches() {
		return innerMatches;
	}

	public void setInnerMatches(List<InnerMatch> innerMatches) {
		this.innerMatches = innerMatches;
	}

	public UrlDownload getUrldown() {
		return urldown;
	}

	public void setUrldown(UrlDownload urldown) {
		this.urldown = urldown;
	}

	public Vector<String> getPageUrls() {
		return pageUrls;
	}

	public void setPageUrls(Vector<String> pageUrls) {
		this.pageUrls = pageUrls;
	}

	public BerkeleyDB getUrlDB() {
		return urlDB;
	}

	public void setUrlDB(BerkeleyDB urlDB) {
		this.urlDB = urlDB;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PageParser p = new PageParser("d:\\test", false);

		p.getPageUrls().add("http://video.nxtv.cn/player.php?id=19548");
		InnerMatch im = new InnerMatch("现在播放的视频: (.*?)</", "title");
		p.getInnerMatches().add(im);
		im = new InnerMatch("发布于:(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})&nbsp;", "publish_time");
		p.getInnerMatches().add(im);
		im = new InnerMatch("视频简介：(.*?)</div>", "description");
		p.getInnerMatches().add(im);
		im = new InnerMatch(">标签：(.*?)<br />", "tag");
		im.setStripTags(true);
		p.getInnerMatches().add(im);
		im = new InnerMatch("objds\\.addVariable\\('p', \"(.*?)\"\\);", "p");
		p.getInnerMatches().add(im);
		p.getUrldown().setCharset("UTF-8");
		p.run();

	}

	public String getCurUrl() {
		return curUrl;
	}

	public void setCurUrl(String curUrl) {
		this.curUrl = curUrl;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public Extractor getBpInterface() {
		return bpInterface;
	}

	public void setBpInterface(Extractor bpInterface) {
		this.bpInterface = bpInterface;
	}
}
