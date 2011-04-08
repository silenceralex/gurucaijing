package com.caijing.crawl;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.springframework.context.ApplicationContext;

import com.caijing.dao.PostDao;
import com.caijing.domain.Post;
import com.caijing.spider.BerkeleyDB;
import com.caijing.util.Config;
import com.caijing.util.ContextFactory;
import com.caijing.util.ServerUtil;

public class ThreadCrawler {

	private static final String start = "http://vip.g.cnfol.com/fourm/master_lastpost_";
	private HttpParams params = new BasicHttpParams();
	private ClientConnectionManager cm = null;
	private Extractor listExtractor = null;

	private PostDao postDao = null;

	public PostDao getPostDao() {
		return postDao;
	}

	public void setPostDao(PostDao postDao) {
		this.postDao = postDao;
	}

	private static Pattern rangePattern = Pattern.compile("<tr\\s+bgcolor=\"#[f8]{6}\">(.*?)</tr>",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);

	private static Pattern urlPattern = Pattern.compile(
			"<a\\s+href='(http://vip\\.g\\.cnfol\\.com/thread/[0-9]+,[0-9]+\\.html)'>", Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL | Pattern.UNIX_LINES);

	private BerkeleyDB bdb = null;

	public BerkeleyDB getBdb() {
		return bdb;
	}

	public void setBdb(BerkeleyDB bdb) {
		this.bdb = bdb;
	}

	public void init() {
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "GB2312");
		HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
		HttpProtocolParams.setUseExpectContinue(params, true);
		// 设置不自动跳转
		params.setBooleanParameter("http.protocol.handle-redirects", false);

		ConnManagerParams.setMaxTotalConnections(params, 100);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

		cm = new ThreadSafeClientConnManager(params, schemeRegistry);
	}

	private void assemble(HttpGet get, String cookie) {

		get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		get.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
		get.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
		get.setHeader("Keep-Alive", "115");
		get.setHeader("Connection", "keep-alive");
		get.setHeader("User-Agent",
				"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		get.setHeader("Accept-Encoding", "gzip,deflate");
		get.setHeader("Content-type", "application/x-www-form-urlencoded");
		get.setHeader("Cookie", cookie);
	}

	private String download(String url, String cookie) {
		HttpClient httpClient = new DefaultHttpClient(cm, params);
		HttpGet get = new HttpGet(url);
		get.setHeader("Host", "vip.g.cnfol.com");
		assemble(get, cookie);
		try {
			HttpResponse response = httpClient.execute(get);
			//			String cookie = response.getFirstHeader("Set-Cookie").getValue();
			GzipEntity gentity = new GzipEntity(response.getEntity());
			//			String content = EntityUtils.toString(response.getEntity(), "GBK");
			String content = EntityUtils.toString(gentity, "GB2312");
			return content;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public void crawl(String masterid, String cookie) {
		HttpClient httpClient = new DefaultHttpClient(cm, params);
		String startUrl = start + masterid + ".html";
		HttpGet get = new HttpGet(startUrl);
		System.out.println("groupID: " + masterid);
		get.setHeader("Host", "vip.g.cnfol.com");
		assemble(get, cookie);
		try {
			HttpResponse response = httpClient.execute(get);
			//			String cookie = response.getFirstHeader("Set-Cookie").getValue();
			GzipEntity gentity = new GzipEntity(response.getEntity());
			String content = EntityUtils.toString(gentity, "GBK");
			//			String content = EntityUtils.toString(response.getEntity(), "utf-8");

			Matcher m = rangePattern.matcher(content);
			while (m != null && m.find()) {
				String rangecontent = m.group(1).trim();
				Matcher urlm = urlPattern.matcher(rangecontent);
				if (urlm != null && urlm.find()) {
					String threadurl = urlm.group(1).trim();
					System.out.println("threadurl:" + threadurl);
					if (!bdb.contains(threadurl)) {
						String threadcontent = download(threadurl, cookie);
						Post post = listExtractor.extractFromHtml(threadcontent, "" + masterid);
						post.setPid(ServerUtil.getid());
						post.setGroupid(masterid);
						try {
							postDao.insert(post);
							bdb.putUrl(threadurl);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}

			}

			//			int page = listExtractor.getTotalPages(content);
			//			System.out.println("page: " + page);
			//			page = (page / 15 + 1);
			//			System.out.println("page num: " + page);
			//			for (int i = 1; i < page + 1; i++) {
			//				String url = startUrl.substring(0, startUrl.lastIndexOf(".html")) + ",p" + i + ".html";
			//				System.out.println("page url: " + url);
			//				String pageContent = download(url);
			//				System.out.println("pageContent: " + pageContent);
			//				listExtractor.extractFromHtml(pageContent, groupID);
			//				Thread.sleep(5000);
			//			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//		ThreadCrawler crawler = new ThreadCrawler();
		//		ListExtractor extractor = new ListExtractor();
		BerkeleyDB bdb = new BerkeleyDB("D:\\bdb", false);
		//		bdb.setup("D:\\bdb", false);
		//		extractor.setBdb(bdb);
		//		crawler.setListExtractor(extractor);
		//		crawler.init();
		//		crawler.crawl("http://vip.g.cnfol.com/thread/2074,316182.html");
		//		crawler.crawl("http://vip.g.cnfol.com/thread/103,144394.html");
		//"http://vip.g.cnfol.com/fourm/master_lastpost_1752.html"
		ApplicationContext context = ContextFactory.getApplicationContext();
		ThreadCrawler crawler = (ThreadCrawler) context.getBean("threadCrawler");
		PostDao postDao = (PostDao) context.getBean("postDao");
		crawler.setBdb(bdb);
		crawler.setPostDao(postDao);
		Config config = (Config) context.getBean("config");
		Map map = (Map) config.getObject("groupid");
		//		for (Object key : map.keySet()) {
		Map propertys = (Map) map.get("575");
		String cookie = (String) propertys.get("cookie");
		crawler.crawl("575", cookie);
		//		}
		System.exit(0);
	}

	public Extractor getListExtractor() {
		return listExtractor;
	}

	public void setListExtractor(Extractor listExtractor) {
		this.listExtractor = listExtractor;
	}

}
