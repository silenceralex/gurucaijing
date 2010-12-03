package com.caijing.spider;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.ColumnArticleDao;
import com.caijing.domain.ColumnArticle;
import com.caijing.remote.CmsWebservice;
import com.caijing.util.DateTools;
import com.caijing.util.MD5Utils;
import com.caijing.util.UrlDownload;

/**
 * Main entrance for crawl a video website, it could control several thread to
 * parse the pages which fit for the rule.
 * 该类下载的流程是由入口进入后沿着url深度遍历去下载所有符合url规则的页面，并且解析相关页面 优势：给定入口，遍历所有，多栏目的爬取，一次性爬取所有栏目
 * 
 * @author chenjun <jun-chen@corp.netease.com>
 */
public class CrawlJob implements Runnable {
	private static Log logger = LogFactory.getLog(CrawlJob.class);
	public static final String DISALLOW = "Disallow:";
	// 获取链接的pattern
	private static Pattern HREF = Pattern.compile("<a.*?href=[\t\n\r\\s\"'#]?(.*?)[\t\n\r\\s\"'>#].*?>(.*?)</a>",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
	private URL startUrl;
	private List<String> aliases = new ArrayList<String>();
	private List<SpecialPattern> specials = new ArrayList<SpecialPattern>();
	private String charset = "GBK";
	private int maxConnections = 5;
	private int threads = 5;
	//对应分类，0专栏，1大势研判，2宏观动态，3草根
	private int type = 0;
	//发布栏目的id
	private long columnid = 0;
	// 为了限制页面html内容部分的pattern,链接页和最终页的区分。
	//	private List<Pattern> rangePattern = new ArrayList<Pattern>();
	private List<String> rangePatterns = new ArrayList<String>();
	private List<Pattern> excludes = new ArrayList<Pattern>();
	private List<Pattern> onlys = new ArrayList<Pattern>();
	private List<String> starturls = new ArrayList<String>();
	// URLs to be searched
	private Vector<String> urlsToSearch = new Vector<String>();
	// URLs already searched
	private Vector<String> urlsSearched = new Vector<String>();

	private long bytesDownloaded = 0;
	// 当前工作的页面，以及出错的页面
	private Map<String, String> workingOnPages = new HashMap<String, String>();
	private Map<String, String> workingErrors = new HashMap<String, String>();
	private boolean closing = false;
	private int downMethod = 0;
	private String defaultHost;
	private UrlDownload urldown = new UrlDownload();

	private BerkeleyDB urlDB = null;

	@Autowired
	@Qualifier("columnArticleDao")
	private ColumnArticleDao columnArticleDao = null;

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public CrawlJob(String startPage) {
		try {
			this.startUrl = new URL(startPage);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		defaultHost = this.startUrl.getHost();
		System.out.println(this.startUrl.getHost());
	}

	/**
	 * 处理页面遍历过程中的链接页，是否符合enclude规则加入遍历urlsToSearch
	 * 
	 * @param url
	 *            当前的context url
	 * @param content
	 *            当前页的内容
	 */
	private void processLink(URL url, String content) {
		if (content == null || content.length() == 0)
			return;
		// match anchors
		Matcher m = HREF.matcher(content);
		if (m != null) {
			while (m.find()) {
				String outlink = m.group(1);
				try {
					URL ou = normalizeUrl(normalizeOutlink(url, outlink));
					//					System.out.println("after normalizeUrl:" + ou.toString());
					//System.out.println(ou.toString());
					if (ou != null) {
						if (notExcluded(ou)) {
							boolean prior = false;
							if (!isVisistedUrl(ou)) {
								addWithoutDuplication(urlsToSearch, ou.toString(), prior);
							} else {
								showMsg("Visited URL " + outlink);
							}
						} else {
							// System.out.println("Excluded:" + ou.toString());
							showMsg("Excluded:" + ou.toString());
						}
					}
				} catch (MalformedURLException e) {
					showMsg("bad URL " + outlink);
					continue;
				}
			}
		}
	}

	/**
	 * 处理一整个页面遍历过程，包括获取遍历中的链接页，并且否符合SpecialPattern规则的内容进行最终页的parse 从而获取到视频信息
	 * 
	 * @param url
	 *            当前页面 url
	 */
	private void processPage(URL url) {
		if (this.closing) {
			this.workingErrors.put(Thread.currentThread().getName(), "Closing...");
			return;
		}
		String strUrl = url.toString();
		this.workingOnPages.put(Thread.currentThread().getName(), strUrl);
		this.workingErrors.put(Thread.currentThread().getName(), null);
		// mark the URL as searched (we want this one way or the other)
		addWithoutDuplication(urlsSearched, strUrl);
		String content = "";
		// 两种不同的下载方式
		//		if (downMethod == 0) {
		try {
			content = urldown.load(strUrl);
		} catch (ClientProtocolException e) {
			showMsg("bad URL " + strUrl);
			e.printStackTrace();
			return;
		} catch (IOException e) {
			showMsg("IOException when downloading:" + strUrl);
			e.printStackTrace();
			return;
		}
		//		} else {
		//			content = urldown.download(strUrl);
		//		}
		if (content == null)
			return;
		bytesDownloaded += content.getBytes().length;

		if (rangePatterns != null) {
			// 有内容rangePattern的限制，将只取中间部分进行parse
			for (String pattern : rangePatterns) {
				Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				Matcher rangeM = p.matcher(content);
				if (rangeM != null && rangeM.find()) {
					String rangeStr = rangeM.group();
					processLink(url, rangeStr); // 整个页面进行链接爬取
				}
			}
			//			logger.warn("Content:" + content);
		}

		// 只在最终页面进行page解析
		SpecialPattern special = getSpecialPattern(url, content);
		if (special == null) {
			logger.warn("special is null");
		}
		if (special != null) {
			try {
				ColumnArticle article = special.processPage(url, content, urldown);
				if (article == null)
					return;
				System.out.println(" article:" + article.getTitle() + "   author:" + article.getAuthor());
				//改用标题+作者进行去重
				String md5 = MD5Utils.hash(article.getTitle() + article.getAuthor());
				if (urlDB.contains(md5)) {
					return;
				} else {
					//					logger.warn("Content:" + content);
					urlDB.putUrl(md5);
					article.setType(type);
					columnArticleDao.insert(article);
					long articleid = CmsWebservice.getInstance().addArticle(
							columnid != 0 ? columnid : CmsWebservice.catelogID, article.getTitle(),
							article.getAuthor(), article.getSrc(), article.getAbs(), article.getContent(),
							DateTools.transformDateDetail(article.getPtime()));
					article.setCmsid(articleid);
					columnArticleDao.update(article);
					if (CmsWebservice.getInstance().publishArticle(articleid)) {
						System.out.println("publish article:" + article.getTitle() + " success!");
					} else {
						System.out.println("publish article:" + article.getTitle() + " failed!");
					}
				}
			} catch (Exception e) {
				logger.warn(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据url和页面内容获取匹配的SpecialPattern
	 * 
	 * @param url
	 *            页面链接
	 * @param content
	 *            页面内容
	 * @return SpecialPattern
	 */
	private SpecialPattern getSpecialPattern(URL url, String content) {
		for (SpecialPattern sp : this.specials) {
			if (sp.match(url, content)) {
				return sp;
			}
		}
		return null;
	}

	/**
	 * @param string
	 * @return URL
	 * 
	 */
	private URL normalizeUrl(String uStr) {
		try {
			// uStr = StringEscapeUtils.unescapeHtml(uStr);
			URL url = new URL(uStr);
			String normalizedStr;
			if (url.getProtocol().equalsIgnoreCase("http")) {
				normalizedStr = url.getProtocol().toLowerCase() + "://";
				String host = url.getHost().toLowerCase();
				if (this.aliases.contains(host)) {
					host = this.defaultHost;
				}
				normalizedStr += host + (url.getPort() == 80 || url.getPort() == -1 ? "" : ":" + url.getPort());
				normalizedStr += url.getFile().startsWith("/..") ? url.getFile().substring(3) : url.getFile();
				url = new URL(normalizedStr);
				return url;
			} else {
				// System.out.println("Normalize url is null! url : " + uStr);
				//				logger.warn("Normalize url is null!  url : " + uStr);
				return null;
			}
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * @param url
	 * @param outlink
	 * @return
	 * @throws MalformedURLException
	 */
	private String normalizeOutlink(URL url, String outlink) throws MalformedURLException {
		if (outlink.startsWith("?")) {
			return url.toString().replaceAll("\\?.*$", outlink);
		}
		return new URL(url, outlink).toString();
	}

	/**
	 * @param ou
	 * @return
	 */
	private boolean notExcluded(URL ou) {
		String u = ou.toString();
		if (this.onlys.size() > 0) {
			// first, the url should match at least one pattern in onlys
			// list
			boolean inOnly = false;
			for (Pattern p : this.onlys) {
				Matcher m = p.matcher(u);
				if (m != null && m.find()) {
					inOnly = true;
					break;
				}
			}
			if (!inOnly) {
				return false;
			}
		}
		// next, it should not match any pattern in excludes list
		for (Pattern p : this.excludes) {
			Matcher m = p.matcher(u);
			if (m != null && m.find()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param urlsSearched2
	 * @param url
	 */
	private synchronized void addWithoutDuplication(Vector<String> vector, String url) {
		addWithoutDuplication(vector, url, false);
	}

	/**
	 * @param urlsToSearch
	 * @param string
	 * @param prior
	 */
	private synchronized void addWithoutDuplication(Vector<String> vector, String url, boolean prior) {
		if (!alreadyInVector(vector, url)) {
			if (prior) {
				vector.add(0, url);
			} else {
				vector.add(url);
			}
		}
	}

	/**
	 * @param errMsg
	 */
	private void showMsg(String errMsg) {
		// System.err.println(string);
		this.workingErrors.put(Thread.currentThread().getName(), errMsg);
	}

	/**
	 * @param url
	 * @return
	 */
	private synchronized boolean isVisistedUrl(URL url) {
		// System.out.println("urlsSearched:" + urlsSearched.size());
		return alreadyInVector(this.urlsSearched, url.toString());
	}

	private synchronized boolean alreadyInVector(Vector<String> vector, String url) {
		if (url == null) {
			return false;
		}
		return vector.contains(url);
	}

	public List<Pattern> getExcludes() {
		return excludes;
	}

	public void addSepcialPattern(SpecialPattern sp) {
		this.specials.add(sp);
	}

	/**
	 * @param startUrl
	 */
	private void startJob() {
		// addWithoutDuplication(urlsToSearch, startUrl.toString());
		while (this.urlsToSearch.size() > 0) {
			long time = System.currentTimeMillis();
			String urlto = urlsToSearch.remove(0);
			URL url = normalizeUrl(urlto);
			if (url != null)
				processPage(url);
			System.out.println("Time used on page #" + url.toString() + ":" + (System.currentTimeMillis() - time)
					+ "ms");
		}
	}

	/**
	 * 该类的抓取任务线程在页面爬取url链接中间页的同时，判断是否为最终页，并进行视频信息的解析， 爬行策略相对松散，是一种发散式的爬取
	 */
	public void run() {
		this.urlsSearched.removeAllElements();
		this.urlsToSearch.removeAllElements();

		processPage(startUrl);

		for (String starturl : starturls) {
			addWithoutDuplication(urlsToSearch, starturl);
		}

		// System.out.println("urlsSearched size:" + urlsSearched.size());
		// System.out.println("urlsToSearch size:" + urlsToSearch.size());
		if (logger.isDebugEnabled()) {
			logger.debug("urlsSearched size:" + urlsSearched.size());
			logger.debug("urlsToSearch size:" + urlsToSearch.size());
		}
		//		for (int i = 0; i < this.urlsToSearch.size(); i++) {
		//			System.out.println("urlsToSearch :" + urlsToSearch.get(i));
		//		}

		class JobThread extends Thread {
			protected boolean finished = false;

			public void run() {
				startJob();
				finished = true;
			}
		}

		List<JobThread> ts = new ArrayList<JobThread>();
		for (int i = 0; i < threads && i < this.urlsToSearch.size(); i++) {
			JobThread t = new JobThread();
			t.start();
			ts.add(t);
		}
		this.workingOnPages.remove(Thread.currentThread().getName());
		this.workingErrors.remove(Thread.currentThread().getName());
		while (true) {
			boolean allDie = true;
			for (JobThread t : ts) {
				if (!t.finished) {
					allDie = false;
					break;
				} else {
					this.workingOnPages.remove(t.getName());
					this.workingErrors.remove(t.getName());
				}
			}
			if (allDie) {
				if (logger.isDebugEnabled()) {
					logger.debug("CrawlJob finished Crawling from Start url: " + this.startUrl);
				}
				break;
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// No need to call close here
		// as the shutdown hook will call this when the app exits
		// this.close();
	}

	// 获取当前抓取的状态
	public synchronized long[] getStatus() {
		return new long[] { this.urlsSearched.size(), this.urlsToSearch.size(), this.bytesDownloaded };
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public List<Pattern> getOnlys() {
		return onlys;
	}

	public void setOnlys(List<Pattern> onlys) {
		this.onlys = onlys;
	}

	public Map<String, String> getWorkingOnPages() {
		return workingOnPages;
	}

	public void setWorkingOnPages(Map<String, String> workingOnPages) {
		this.workingOnPages = workingOnPages;
	}

	public Map<String, String> getWorkingErrors() {
		return workingErrors;
	}

	public void setWorkingErrors(Map<String, String> workingErrors) {
		this.workingErrors = workingErrors;
	}

	private static void printUsage() {
		System.out.println("USAGE:\t#>./CrawlJob JOBXML");
		System.out.println("\t\t-JOBXML\t\t\tJob Descriptive File in XML format");
	}

	// 主抓取程序的入口
	public static void main(String[] args) {
		//		if (args.length == 0) {
		//			printUsage();
		//			return;
		//		}

		SAXReader sr = new SAXReader();
		Document xml = null;

		try {
			xml = sr.read(new File("jobs\\wsj_fuchifeng.xml"));
			// xml = sr.read(new File(args[0]));
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		CrawlJob job = ConfigReader.fromXML(xml);

		//		ColumnArticleDao columnArticleDao = (ColumnArticleDao) ContextFactory.getBean("columnArticleDao");
		//		job.setColumnArticleDao(columnArticleDao);
		long startTime = System.currentTimeMillis();

		// System.out.println("CrawlJob: \tThreads:" + job.getThreads()
		// + "\tMax Connections Per Host:" + job.getMaxConnections());
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
			// if (logger.isDebugEnabled()) {
			// logger.debug("Crawled:" + crawled + "\tQueued:" + queued
			// + "\tSpeed:" + (speed * 1000) + " pps\tTime Used:"
			// + (timeElpased / 1000) + "s\tTime Remain:"
			// + (timeEst / 1000) + "s\tDownload Speed: " + (dlSpeed)
			// + "KBPS");
			// }
			for (String key : job.getWorkingOnPages().keySet()) {
				System.out.println("\t" + key + "\t" + job.getWorkingOnPages().get(key) + "\tErr:"
						+ job.getWorkingErrors().get(key));
				// if (logger.isDebugEnabled()) {
				// logger.debug("\t" + key + "\t"
				// + job.getWorkingOnPages().get(key) + "\tErr:"
				// + job.getWorkingErrors().get(key));
				// }
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public int getDownMethod() {
		return downMethod;
	}

	public void setDownMethod(int downMethod) {
		this.downMethod = downMethod;
	}

	public UrlDownload getUrldown() {
		return urldown;
	}

	public boolean isClosing() {
		return closing;
	}

	public void setClosing(boolean closing) {
		this.closing = closing;
	}

	public BerkeleyDB getUrlDB() {
		return urlDB;
	}

	public void setUrlDB(BerkeleyDB urlDB) {
		this.urlDB = urlDB;
	}

	public List<SpecialPattern> getSpecials() {
		return specials;
	}

	public void setSpecials(List<SpecialPattern> specials) {
		this.specials = specials;
	}

	public void setUrldown(UrlDownload urldown) {
		this.urldown = urldown;
	}

	public ColumnArticleDao getColumnArticleDao() {
		return columnArticleDao;
	}

	public void setColumnArticleDao(ColumnArticleDao columnArticleDao) {
		this.columnArticleDao = columnArticleDao;
	}

	public List<String> getStarturls() {
		return starturls;
	}

	public void setStarturls(List<String> starturls) {
		this.starturls = starturls;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getColumnid() {
		return columnid;
	}

	public void setColumnid(long columnid) {
		this.columnid = columnid;
	}

	public List<String> getRangePatterns() {
		return rangePatterns;
	}

	public void setRangePatterns(List<String> rangePatterns) {
		this.rangePatterns = rangePatterns;
	}

}
