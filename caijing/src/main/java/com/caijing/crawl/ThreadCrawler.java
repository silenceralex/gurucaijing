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
	
	private static final String COOKIE = "  cookie[passport][userId]=3929853; cookie[passport][username]=issn517; cookie[passport][nickname]=surrogate; cookie[passport][password]=b41003f9cc166e8237916aac24a4e614; cookie[passport][keys]=88C14D50FCF09959DADCB6F387D06BB6; cookie[passport][logtime]=1277057407; skillId=4193; operatorId=undefined; cookie[upload][url]=http%3A%2F%2Fpassport.cnfol.com%2Fblogmodule%2FPostUpload%2Curls%3DaHR0cDovL3Bvc3QuY25mb2wuY29tL2luc2VydGZja2ltZy8%3D";
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
	
	private String download(String url){
		HttpClient httpClient =new DefaultHttpClient(cm, params);
		HttpGet get = new HttpGet(url);
		get.setHeader("Host", "vip.g.cnfol.com");
		assemble(get);
		try {
			HttpResponse response = httpClient.execute(get);
			//			String cookie = response.getFirstHeader("Set-Cookie").getValue();
			GzipEntity gentity = new GzipEntity(response.getEntity());
			//			String content = EntityUtils.toString(response.getEntity(), "utf-8");
			String content = EntityUtils.toString(gentity, "GB2312");
			return content;
		}catch (ClientProtocolException e) {
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
		HttpClient httpClient =new DefaultHttpClient(cm, params);
		HttpGet get = new HttpGet(startUrl);
		String groupID=startUrl.substring(startUrl.lastIndexOf("/")+1,startUrl.indexOf(','));
		System.out.println("groupID: "+ groupID);
		get.setHeader("Host", "vip.g.cnfol.com");
		assemble(get);
		try {
			HttpResponse response = httpClient.execute(get);
			//			String cookie = response.getFirstHeader("Set-Cookie").getValue();
			GzipEntity gentity = new GzipEntity(response.getEntity());
			//			String content = EntityUtils.toString(response.getEntity(), "utf-8");
			String content = EntityUtils.toString(gentity, "GB2312");
			int page=listExtractor.getTotalPages(content);
			System.out.println("page: "+page);
			page= (page/15+1);
			System.out.println("page num: " +page);
			for(int i=1;i<page+1;i++){
				String url=startUrl.substring(0,startUrl.lastIndexOf(".html"))+",p"+i+".html";
				System.out.println("page url: "+url);
				String pageContent=download(url);
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
//		Extractor extractor=new ListExtractor();
//		crawler.setListExtractor(extractor);
//		crawler.init();
////		crawler.crawl("http://vip.g.cnfol.com/thread/960,186467.html");
//		crawler.crawl("http://vip.g.cnfol.com/thread/103,144394.html");
		ApplicationContext context = ContextFactory.getApplicationContext();
		ThreadCrawler crawler = (ThreadCrawler) context.getBean("threadCrawler");
		crawler.crawl("http://vip.g.cnfol.com/thread/103,144394.html");


	}

	public Extractor getListExtractor() {
		return listExtractor;
	}

	public void setListExtractor(Extractor listExtractor) {
		this.listExtractor = listExtractor;
	}

}
