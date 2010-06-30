package com.caijing.crawl;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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

import com.caijing.util.FileUtil;

public class TestSoquan {

	HttpParams params = new BasicHttpParams();
	ClientConnectionManager cm = null;
	HttpClient httpClient = null;
	private static Pattern viewStatePattern = Pattern.compile("gentity", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
			| Pattern.UNIX_LINES);
	private static Pattern stockPattern = Pattern.compile("(((002|000|300|600)[\\d]{3})|60[\\d]{4})",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);

	private static final String COOKIE = "  cookie[passport][userId]=3929853; cookie[passport][username]=issn517; cookie[passport][nickname]=surrogate; cookie[passport][password]=b41003f9cc166e8237916aac24a4e614; cookie[passport][keys]=88C14D50FCF09959DADCB6F387D06BB6; cookie[passport][logtime]=1277057407; skillId=4193; operatorId=undefined; cookie[upload][url]=http%3A%2F%2Fpassport.cnfol.com%2Fblogmodule%2FPostUpload%2Curls%3DaHR0cDovL3Bvc3QuY25mb2wuY29tL2luc2VydGZja2ltZy8%3D";

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
		httpClient = new DefaultHttpClient(cm, params);
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

	public void getOnline(String clubid) {
		HttpGet get = new HttpGet("http://online.g.cnfol.com//getinfo.html?displayNum=0&sortid=0&clubid=" + clubid);
		get.setHeader("Host", "online.g.cnfol.com");
		assemble(get);
		try {
			HttpResponse response = httpClient.execute(get);
			// String cookie = response.getFirstHeader("Set-Cookie").getValue();
			GzipEntity gentity = new GzipEntity(response.getEntity());
			// String content = EntityUtils.toString(response.getEntity(),
			// "utf-8");
			String content = EntityUtils.toString(gentity, "GB2312");
			System.out.println("HTML: " + content);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getCircle() {
		HttpGet get = new HttpGet("http://vip.g.cnfol.com/shengweitouji");
		get.setHeader("Host", "vip.g.cnfol.com");
		assemble(get);
		try {
			HttpResponse response = httpClient.execute(get);
			GzipEntity gentity = new GzipEntity(response.getEntity());
			String content = EntityUtils.toString(gentity, "GB2312");
			System.out.println("HTML: " + content);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void login(String name, String password) {
		HttpGet get = new HttpGet("http://online.g.cnfol.com//getinfo.html?clubid=103&displayNum=0&sortid=0");
		// HttpGet get = new HttpGet(str3);
		get.setHeader("Host", "online.g.cnfol.com");
		get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		get.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
		get.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
		get.setHeader("Keep-Alive", "115");
		get.setHeader("Connection", "keep-alive");
		get.setHeader("User-Agent",
				"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		get.setHeader("Accept-Encoding", "gzip,deflate");
		get.setHeader("Content-type", "application/x-www-form-urlencoded");
		get
				.setHeader(
						"Cookie",
						"  cookie[passport][userId]=3929853; cookie[passport][username]=issn517; cookie[passport][nickname]=surrogate; cookie[passport][password]=b41003f9cc166e8237916aac24a4e614; cookie[passport][keys]=88C14D50FCF09959DADCB6F387D06BB6; cookie[passport][logtime]=1277057407; skillId=4193; operatorId=undefined; cookie[upload][url]=http%3A%2F%2Fpassport.cnfol.com%2Fblogmodule%2FPostUpload%2Curls%3DaHR0cDovL3Bvc3QuY25mb2wuY29tL2luc2VydGZja2ltZy8%3D");

		try {
			HttpResponse response = httpClient.execute(get);
			// String cookie = response.getFirstHeader("Set-Cookie").getValue();
			GzipEntity gentity = new GzipEntity(response.getEntity());
			// String content = EntityUtils.toString(response.getEntity(),
			// "utf-8");
			String content = EntityUtils.toString(gentity, "GB2312");
			System.out.println("HTML: " + content);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void process(String folder) {
		File file = new File(folder);
		if (file.isDirectory()) {
			String[] files = file.list();
			for (String f : files) {
				String filePath = folder + "/" + f;
				System.out.println("File: " + filePath);
				String content = FileUtil.read(filePath);
				Matcher m = stockPattern.matcher(content);
				// String stock="";
				StringBuffer stock = new StringBuffer();
				HashSet<String> st = new HashSet<String>();
				while (m != null && m.find()) {
					String tmp = m.group(1);
					if (!st.contains(tmp)) {
						stock.append(tmp + "\r\n");
						System.out.println("stock: " + tmp);
						st.add(tmp);
					}
				}
				String stockPath = folder + "/" + f.substring(0, f.indexOf('.')) + "_stock" + ".txt";
				FileUtil.write(stockPath, stock.toString());
				System.out.println("stockFile: " + stockPath);

			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String postURL = "http://www.sinofin.net/sr/registe.aspx";
		TestSoquan crawler = new TestSoquan();
		// crawler.init();
		// crawler.getCircle();
		crawler.process("f:/caijing/960");
		// crawler.login("zhoukan001", "20091228");

	}

}
