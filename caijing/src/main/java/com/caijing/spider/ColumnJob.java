package com.caijing.spider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.caijing.util.UrlDownload;

/**
 * 针对一个栏目的抓取任务，主要作为非最终页的抓取 核心思想：通过猜测中间链接页url，进行抓取，并且以rangePattern来判断抓取的完结。
 * 
 * @author jun-chen
 * 
 */
public class ColumnJob extends Thread {
	protected boolean finished = false;

	//	protected Column col = null;
	private static Logger logger = Logger.getLogger(ColumnJob.class);
	private UrlDownload urldown = new UrlDownload();
	private List<InnerMatch> innerMatches = new ArrayList<InnerMatch>();
	//	private VideoBase db = new VideoBase();
	// 判断是否到最后一页
	private Pattern rangePattern = null;
	// 解析每个视频
	private Pattern regexp = null;
	private BerkeleyDB urlDB = new BerkeleyDB();
	private final String DefaultTime = "2000-01-01 00:00:00";
	private final String THRESHOLD = "2009-05-01 00:00:00";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private long bytesDownloaded = 0;
	private int pages = 0;
	private int videos = 0;

	private String curUrl = "";

	public ColumnJob() {
	}

	public void setUrlDownCharset(String charset) {
		urldown.setCharset(charset);
	}

	public void run() {
		//		startJob(col);
		finished = true;
	}

	// 获取当前抓取的状态
	public long[] getStatus() {
		return new long[] { this.pages, this.videos, this.bytesDownloaded };
	}

	//	public void startJob(Column col) {
	//		String url = col.getStartUrl();
	//		int cid = col.getCid();
	//		int i = 1;
	//		boolean flag = true;
	//		String html = "";
	//		while (flag) {
	//			// String curUrl = url + i;
	//			curUrl = url + i;
	//			pages++;
	//			i++;
	//			try {
	//				html = urldown.load(curUrl);
	//				bytesDownloaded += html.getBytes().length;
	//				// System.out.println("curUrl: " + curUrl);
	//			} catch (ClientProtocolException e) {
	//				logger.error("bad URL " + curUrl);
	//				e.printStackTrace();
	//				continue;
	//			} catch (IOException e) {
	//				logger.error("IOException when downloading:" + curUrl);
	//				e.printStackTrace();
	//				continue;
	//			}
	//
	//			// 仅在rangePattern不匹配时，算是抓取到尽头，跳出
	//			Matcher rangem = rangePattern.matcher(html);
	//			if (rangem == null || !rangem.find()) {
	//				flag = false;
	//				break;
	//			}
	//
	//			Matcher m = regexp.matcher(html);
	//			while (m.find()) {
	//				// 添加解析的代码
	//				videos++;
	//				String content = m.group();
	//				Map<String, String> properties = processPageInnerMatch(content);
	//				if (!properties.containsKey("tag")) {
	//					properties.put("tag", col.getTag());
	//				}
	//				// 无视频文件地址不入库
	//				if (!properties.containsKey("video_url")) {
	//					continue;
	//				}
	//				try {
	//					// 由于此处并无最终页，因此需要以video_url来判断是否重复
	//					if (urlDB.contains(properties.get("video_url"))) {
	//						continue;
	//					}
	//					// 将首先尝试以video_url 的lastModified字段的值来填充，仍旧没有则以"2000-01-01
	//					// 00:00:00"来填充；
	//					if (!properties.containsKey("publish_time")) {
	//
	//						// String time = urldown.getLastModified(properties
	//						// .get("video_url"));
	//						// if (time != null && time.length() > 0) {
	//						// properties.put("publish_time", time);
	//						// } else {
	//
	//						properties.put("publish_time", sdf.format(new Date()));
	//						// }
	//					}
	//					String ptime = properties.get("publish_time");
	//					if (ptime == null || ptime.length() != 19) {
	//						System.out.println("publish_time is not right format!!!");
	//						continue;
	//					}
	//					try {
	//						if (beforeThreshold(ptime)) {
	//							// System.out.println("ptime:" + ptime);
	//							continue;
	//						}
	//					} catch (ParseException e) {
	//						logger.error(e.getMessage());
	//					}
	//
	//					//					int code = db.insertByInnerMatch(properties, cid, curUrl);
	//					//					if (code != 0) {
	//					//						logger.error("insert return code is :" + code);
	//					//					} else {
	//					//						// 插入成功，加入去重库
	//					//						urlDB.putUrl(properties.get("video_url"));
	//					//					}
	//				} catch (DatabaseException e) {
	//					System.out.println("DatabaseException");
	//					logger.error(e);
	//					continue;
	//				}
	//			}
	//		}
	//	}

	// 给定默认时间后进行入数据库库，太老则不入
	private boolean beforeThreshold(String time) throws ParseException {
		SimpleDateFormat DATE_OUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dt1 = DATE_OUT_FORMAT.parse(time);
		Date dt2 = DATE_OUT_FORMAT.parse(THRESHOLD);
		if (dt1.getTime() > dt2.getTime()) {
			// System.out.println("在"+THRESHOLD+"之后");
			return false;
		} else {
			// System.out.println("在"+THRESHOLD+"之前");
			return true;
		}
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
		}
		return properties;
	}

	public List<InnerMatch> getInnerMatches() {
		return innerMatches;
	}

	public void setInnerMatches(List<InnerMatch> innerMatches) {
		this.innerMatches = innerMatches;
	}

	public Pattern getRangePattern() {
		return rangePattern;
	}

	public void setRangePattern(Pattern rangePattern) {
		this.rangePattern = rangePattern;
	}

	public BerkeleyDB getUrlDB() {
		return urlDB;
	}

	public void setUrlDB(BerkeleyDB urlDB) {
		this.urlDB = urlDB;
	}

	public Pattern getRegexp() {
		return regexp;
	}

	public void setRegexp(Pattern regexp) {
		this.regexp = regexp;
	}

	public String getCurUrl() {
		return curUrl;
	}
}
