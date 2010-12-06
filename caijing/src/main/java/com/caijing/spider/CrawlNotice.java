package com.caijing.spider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import com.caijing.dao.NoticeDao;
import com.caijing.domain.Notice;
import com.caijing.flush.HtmlFlusher;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.caijing.util.MD5Utils;
import com.caijing.util.ServerUtil;
import com.caijing.util.UrlDownload;

public class CrawlNotice {
	String starturl = "http://data.eastmoney.com/notice/Notice_List.aspx";
	private static Log logger = LogFactory.getLog(CrawlNotice.class);
	private static String PREFIX = "http://51gurus.com/notice/";

	Map keyMap = new HashMap<String, Integer>();
	UrlDownload downloader = new UrlDownload();
	private static Pattern HREF = Pattern.compile("<a.*?href=[\t\n\r\\s\"'#]?(.*?)[\t\n\r\\s\"'>#].*?>(.*?)</a>",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
	Pattern range = Pattern.compile("", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private List<Pattern> onlys = new ArrayList<Pattern>();
	Pattern contentpattern = Pattern.compile(
			"<div class=\"add[0-9]+\">.*?<span>([0-9]{6})</span></a>\\)(.*?)：(.*?)<br />(.*?)</div>",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	Pattern hexunDatePattern = Pattern.compile("公告日期：(.*?)</strong></div>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	private Pattern rangePattern = Pattern.compile("<ul class=\"article\">(.*?)<div id=\"footer\">",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	Pattern content2pattern = Pattern
			.compile(
					"<span class=\"caption\"><a href=\"(.*?)\">(.*?)\\(([0-9]{6})\\)(.*?)</a></span>\\s+<span class=\"time\">(201\\d-[0-1][0-9]-[0-3][0-9])</span>",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	Pattern desctpattern = Pattern.compile("<pre>(.*?)</pre>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	// URLs to be searched
	private Vector<String> urlsToSearch = new Vector<String>();

	private HtmlFlusher htmlFlush = new HtmlFlusher();

	public Vector<String> getUrlsToSearch() {
		return urlsToSearch;
	}

	public void setUrlsToSearch(Vector<String> urlsToSearch) {
		this.urlsToSearch = urlsToSearch;
	}

	// URLs already searched
	private Vector<String> urlsSearched = new Vector<String>();

	protected boolean finished = false;
	private Trie trie = new Trie();

	private BerkeleyDB urlDB = new BerkeleyDB();

	@Autowired
	@Qualifier("noticeDao")
	private NoticeDao noticeDao = null;

	private String currentdate = "";

	public NoticeDao getNoticeDao() {
		return noticeDao;
	}

	public void setNoticeDao(NoticeDao noticeDao) {
		this.noticeDao = noticeDao;
	}

	public void init() {
		Pattern p1 = Pattern.compile("http://stockdata\\.stock.hexun\\.com/2008/jrgg\\.aspx\\?page=\\d&type=\\d",
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		onlys.add(p1);

		trie.add("增持", 2);
		trie.add("减持", 3);
		trie.add("资产注入", 1);
		trie.add("资产重组", 1);
		trie.add("并购", 1);
		trie.add("股权激励", 0);
		trie.add("期权激励", 0);
		trie.add("激励计划", 0);
		urlsToSearch.add("http://stockdata.stock.hexun.com/2008/jrgg.aspx?type=3");
		urlsToSearch.add("http://stockdata.stock.hexun.com/2008/jrgg.aspx?type=2");
		urlDB.setup("/home/app/urldb/notice", false);
	}

	private boolean withIn(URL ou) {
		String u = ou.toString();
		if (this.onlys.size() > 0) {
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
		return true;
	}

	private synchronized void addWithoutDuplication(Vector<String> vector, String url) {
		if (!vector.contains(url)) {
			vector.add(url);
		}
	}

	private void processLink(URL url, String content) throws MalformedURLException {
		if (content == null || content.length() == 0)
			return;
		// match anchors
		Matcher m = HREF.matcher(content);
		if (m != null) {
			while (m.find()) {
				String outlink = m.group(1);
				URL ou = new URL(url, outlink);
				//				System.out.println("urlsToSearch:" + ou.toString());
				if (withIn(ou) && !isVisistedUrl(ou)) {
					addWithoutDuplication(urlsToSearch, ou.toString());
					System.out.println("add urlsToSearch:" + ou.toString());
				}
			}
		}
	}

	private synchronized boolean isVisistedUrl(URL url) {
		return this.urlsSearched.contains(url.toString());
	}

	private Date parseHexunDate(String content) throws ParseException {
		Matcher m = hexunDatePattern.matcher(content);
		while (m != null && m.find()) {
			return DateTools.parseChineseDate(m.group(1).trim());
		}
		return new Date();
	}

	public void crawlHexun(URL url) {
		try {
			addWithoutDuplication(urlsSearched, url.toString());
			String content = downloader.load(url.toString());
			Date date = parseHexunDate(content);
			processLink(url, content);
			if (contentpattern != null) {
				Matcher m = contentpattern.matcher(content);
				while (m != null && m.find()) {
					//					System.out.println(m.groupCount());
					Notice notice = new Notice();
					notice.setId(ServerUtil.getid());

					notice.setContent(m.group(4).trim());
					notice.setTitle(m.group(3).trim());
					notice.setStockcode(m.group(1).trim());
					notice.setStockname(m.group(2).trim());
					notice.setDate(date);
					TrieNode titlenode = trie.searchNode(notice.getTitle());
					TrieNode contentnode = trie.searchNode(notice.getContent());
					if (titlenode != null || contentnode != null) {
						if (titlenode != null) {
							notice.setType(titlenode.getType());
						} else if (contentnode != null) {
							notice.setType(contentnode.getType());
						}
						String pageurl = PREFIX + DateTools.getYear(date) + "/" + DateTools.getMonth(date) + "/"
								+ notice.getId() + ".html";
						notice.setUrl(pageurl);

						String md5 = MD5Utils.hash(notice.getTitle() + notice.getStockcode()
								+ DateTools.transformYYYYMMDDDate(date));
						if (urlDB.contains(md5)) {
							System.out.println("Duplicate ！！！notice ID:" + notice.getId() + "  md5:" + md5);
							continue;
						} else {
							System.out.println("notice ID:" + notice.getId() + "  md5:" + md5);
							Object obj = noticeDao.insert(notice);
							urlDB.putUrl(md5);
							if (obj != null) {
								htmlFlush.flushOneNotice(notice);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runHexun() {
		try {
			while (this.urlsToSearch.size() > 0) {
				long time = System.currentTimeMillis();
				String urlto = urlsToSearch.remove(0);
				URL url = new URL(urlto);
				if (url != null) {
					crawlHexun(url);
					System.out.println("Time used on page #" + url.toString() + ":"
							+ (System.currentTimeMillis() - time) + "ms");
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		finished = true;
	}

	public void runEastMoney() {
		String content = "";
		boolean terminate = false;
		try {
			content = downloader.load(starturl);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Matcher m = rangePattern.matcher(content);
		if (m != null && m.find()) {
			content = m.group();
		}
		if (content2pattern != null) {
			m = content2pattern.matcher(content);
			int i = 0;
			while (m != null && m.find()) {
				String url = "";
				try {
					url = new URL(new URL(starturl), m.group(1)).toString();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
				String title = m.group(4);
				if (i == 0) {
					currentdate = m.group(5).trim();
				}
				i++;
				if (!currentdate.equals(m.group(5).trim())) {
					terminate = true;
					break;
				}

				TrieNode titlenode = trie.searchNode(title);
				if (titlenode != null) {
					Notice notice = new Notice();
					notice.setId(ServerUtil.getid());
					if (m.group(4).trim().startsWith("刊登")) {
						notice.setTitle(m.group(4).trim().substring(2));
					} else {
						notice.setTitle(m.group(4).trim());
					}
					notice.setStockcode(m.group(3).trim());
					notice.setStockname(m.group(2).trim());
					notice.setType(titlenode.getType());
					try {
						notice.setDate(DateTools.parseYYYYMMDDDate(m.group(5).trim()));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					String desc = parseContent(url);
					notice.setContent(desc);
					String pageurl = PREFIX + DateTools.getYear(notice.getDate()) + "/"
							+ DateTools.getMonth(notice.getDate()) + "/" + notice.getId() + ".html";
					notice.setUrl(pageurl);
					String md5 = MD5Utils.hash(notice.getTitle() + notice.getStockcode() + currentdate);
					if (urlDB.contains(md5)) {
						System.out.println("Duplicate ！！！notice ID:" + notice.getId() + "  md5:" + md5);
						continue;
					} else {
						//						System.out.println("url:" + url);
						//						System.out.println("stockcode:" + m.group(3));
						//						System.out.println("stockname:" + m.group(2));
						//						System.out.println("title:" + m.group(4));
						//						System.out.println("time:" + m.group(5).trim());
						//						System.out.println("desc:" + desc);
						System.out.println("notice ID:" + notice.getId() + "  md5:" + md5);
						Object obj = noticeDao.insert(notice);
						urlDB.putUrl(md5);
						if (obj != null) {
							htmlFlush.flushOneNotice(notice);
						}
					}
				}
			}
		}

		int page = 2;
		while (!terminate) {
			String currpagge = starturl + "?p=" + page;
			System.out.println("currpagge:" + currpagge);
			if (processpage(currpagge)) {
				terminate = true;
			}
			page++;
		}
	}

	public boolean processpage(String page) {
		String content = "";
		try {
			content = downloader.load(page);

			Matcher m = rangePattern.matcher(content);
			if (m != null && m.find()) {
				content = m.group();
			}
			if (content2pattern != null) {
				m = content2pattern.matcher(content);
				while (m != null && m.find()) {
					String url = new URL(new URL(starturl), m.group(1)).toString();
					if (!currentdate.equals(m.group(5).trim())) {
						return true;
					}
					TrieNode titlenode = trie.searchNode(m.group(4).trim());
					if (titlenode != null) {
						Notice notice = new Notice();
						notice.setId(ServerUtil.getid());
						if (m.group(4).trim().startsWith("刊登")) {
							notice.setTitle(m.group(4).trim().substring(2));
						} else {
							notice.setTitle(m.group(4).trim());
						}
						notice.setStockcode(m.group(3).trim());
						notice.setStockname(m.group(2).trim());
						notice.setType(titlenode.getType());
						String desc = parseContent(url);
						//						System.out.println("url:" + url);
						//						System.out.println("stockcode:" + m.group(3));
						//						System.out.println("stockname:" + m.group(2));
						//						System.out.println("title:" + m.group(4));
						//						System.out.println("time:" + m.group(5).trim());
						//						System.out.println("desc:" + desc);
						notice.setContent(desc);
						try {
							notice.setDate(DateTools.parseYYYYMMDDDate(m.group(5).trim()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						String pageurl = PREFIX + DateTools.getYear(notice.getDate()) + "/"
								+ DateTools.getMonth(notice.getDate()) + "/" + notice.getId() + ".html";
						notice.setUrl(pageurl);
						String md5 = MD5Utils.hash(notice.getTitle() + notice.getStockcode() + currentdate);
						if (urlDB.contains(md5)) {
							System.out.println("Duplicate ！！！notice ID:" + notice.getId() + "  md5:" + md5);
							continue;
						} else {
							System.out.println("notice ID:" + notice.getId() + "  md5:" + md5);
							Object obj = noticeDao.insert(notice);
							urlDB.putUrl(md5);
							if (obj != null) {
								htmlFlush.flushOneNotice(notice);
							}

						}
					}
				}
			}
			return false;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return true;
		}
	}

	private String parseContent(String url) {
		String content = "";
		try {
			content = downloader.load(url);
			Matcher m = desctpattern.matcher(content);
			if (m != null && m.find()) {
				return m.group(1).trim();
			}
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void main(String[] args) {
		CrawlNotice notice = new CrawlNotice();
		ApplicationContext context = ContextFactory.getApplicationContext();
		NoticeDao noticeDao = (NoticeDao) context.getBean("noticeDao");
		notice.setNoticeDao(noticeDao);
		notice.init();
		notice.runHexun();
		notice.runEastMoney();

	}
}
