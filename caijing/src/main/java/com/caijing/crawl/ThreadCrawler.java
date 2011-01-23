package com.caijing.crawl;

import java.io.IOException;

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

import com.caijing.util.ContextFactory;

public class ThreadCrawler {

	private static final String COOKIE = "	__gads=ID=579bb78f0ec5705b:T=1295015028:S=ALNI_MadvIXE6VJLvQ5cweicul8cCF7w5w; SUV=1295015144090647; IPLOC=CN1101; cookie[passport][userId]=3929853; cookie[passport][username]=issn517; cookie[passport][nickname]=surrogate; cookie[passport][money]=2637; cookie[passport][keys]=7784D13AA43F27CD6A8B9D407CC6960B; cookie[passport][logtime]=1297951270; cookie[passport][keystr]=3F2F7AD6501967C235923E324600DC97; cookie[passport][cache]=97AD78F6FB1A883684391560CD13A803; cookie[passport][auto]=0; g7F_cookietime=86400; g7F_sid=8H8yhY; g7F_visitedfid=27; smile=1D1; JSESSIONID=0QlCoiXh4ScZAJfK2s";
	private HttpParams params = new BasicHttpParams();
	private ClientConnectionManager cm = null;
	private Extractor listExtractor = null;

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

	private void assemble(HttpGet get) {
		get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		get.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
		get.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
		get.setHeader("Keep-Alive", "115");
		get.setHeader("Connection", "keep-alive");
		get.setHeader("User-Agent",
				"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		get.setHeader("Accept-Encoding", "gzip,deflate");
		get.setHeader("Content-type", "application/x-www-form-urlencoded");
		get.setHeader("Cookie", COOKIE);
	}

	private String download(String url) {
		HttpClient httpClient = new DefaultHttpClient(cm, params);
		HttpGet get = new HttpGet(url);
		get.setHeader("Host", "vip.g.cnfol.com");
		assemble(get);
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

	public void crawl(String startUrl) {
		HttpClient httpClient = new DefaultHttpClient(cm, params);
		HttpGet get = new HttpGet(startUrl);
		String groupID = startUrl.substring(startUrl.lastIndexOf("/") + 1, startUrl.indexOf(','));
		System.out.println("groupID: " + groupID);
		get.setHeader("Host", "vip.g.cnfol.com");
		assemble(get);
		try {
			HttpResponse response = httpClient.execute(get);
			//			String cookie = response.getFirstHeader("Set-Cookie").getValue();
			GzipEntity gentity = new GzipEntity(response.getEntity());
			String content = EntityUtils.toString(gentity, "utf-8");
			//			String content = EntityUtils.toString(response.getEntity(), "utf-8");
			int page = listExtractor.getTotalPages(content);
			System.out.println("page: " + page);
			page = (page / 15 + 1);
			System.out.println("page num: " + page);
			for (int i = 1; i < page + 1; i++) {
				String url = startUrl.substring(0, startUrl.lastIndexOf(".html")) + ",p" + i + ".html";
				System.out.println("page url: " + url);
				String pageContent = download(url);
				//				System.out.println("pageContent: " + pageContent);
				listExtractor.extractFromHtml(pageContent, groupID);
				Thread.sleep(5000);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
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
		//		BerkeleyDB bdb = new BerkeleyDB();
		//		bdb.setup("D:\\bdb", false);
		//		extractor.setBdb(bdb);
		//		crawler.setListExtractor(extractor);
		//		crawler.init();
		//		crawler.crawl("http://vip.g.cnfol.com/thread/2074,316182.html");
		//		crawler.crawl("http://vip.g.cnfol.com/thread/103,144394.html");
		ApplicationContext context = ContextFactory.getApplicationContext();
		ThreadCrawler crawler = (ThreadCrawler) context.getBean("threadCrawler");
		crawler.crawl("http://vip.g.cnfol.com/thread/2074,316182.html");

	}

	public Extractor getListExtractor() {
		return listExtractor;
	}

	public void setListExtractor(Extractor listExtractor) {
		this.listExtractor = listExtractor;
	}

}
