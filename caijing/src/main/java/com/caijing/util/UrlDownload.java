package com.caijing.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
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
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * html页面的下载器，提供last-modified since 的请求方式，支持gzip的下载。
 * 
 * @author chenjun <jun-chen@corp.netease.com>
 * 
 */
public class UrlDownload {
	HttpParams params = new BasicHttpParams();

	ClientConnectionManager cm = null;
	HttpClient httpClient = null;

	private String charset = "GB2312";
	private static Logger logger = Logger.getLogger(UrlDownload.class);

	public UrlDownload() {
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "GB2312");
		HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
		HttpProtocolParams.setUseExpectContinue(params, true);
		// 设置不自动跳转
		params.setBooleanParameter("http.protocol.handle-redirects", false);

		ConnManagerParams.setMaxTotalConnections(params, 100);
		ConnManagerParams.setTimeout(params, 30000);
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		// Create and initialize scheme registry
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

		// Create an HttpClient with the ThreadSafeClientConnManager.
		// This connection manager must be used if more than one thread will
		// be using the HttpClient.
		cm = new ThreadSafeClientConnManager(params, schemeRegistry);

		httpClient = new DefaultHttpClient(cm, params);
	}

	/**
	 * 获取视频文件的最后修改时间，以便在页面获取不到publish_time的时候代替之
	 * 
	 * @param video_url
	 * @return yyyy-MM-dd HH:mm:ss格式的时间值
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws ParseException
	 */
	public String getLastModified(String video_url) throws ClientProtocolException, IOException, ParseException {
		HttpGet get = new HttpGet(video_url);
		HttpResponse response = httpClient.execute(get);
		String time = response.getFirstHeader("Last-Modified").getValue();
		// 释放get
		get.abort();
		time = time.substring(0, time.indexOf(" GMT"));
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
		SimpleDateFormat standard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(time);
		return standard.format(date);
	}

	public String load(String url) throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(url);
		get.setHeader("User-Agent",
				"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.12) Gecko/20101026 Firefox/3.6.12");
		get.setHeader("Accept-Encoding", "gzip,deflate");
		get.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
		get.setHeader(
				"Cookie",
				"aastocks_stocksHistory=000001.HK%2c000002.HK%2c000003.HK%2c000004.HK%2c000005.HK%2c000006.HK%2c000007.HK; aastocks_astocksHistory=000001.SZ%2c000002.SZ%2c000004.SZ%2c000005.SZ%2c000006.SZ%2c000007.SZ%2c000008.SZ; BIGipServerCNWEB2.0=17563914.20480.0000");
		get.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
		return load(get);
	}

	public String load(String url, boolean isGzip) throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(url);
		if (isGzip) {
			get.setHeader("Accept-Encoding", "gzip");
			HttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			if (entity.getContentEncoding() != null && entity.getContentEncoding().getValue().equalsIgnoreCase("gzip")) {
				GzipEntity gentity = new GzipEntity(entity);
				String content = EntityUtils.toString(gentity, charset);
				return content;
			} else {
				return EntityUtils.toString(entity, charset);
			}
		} else {
			HttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity, charset);
		}
		// return load(get);
	}

	public String loadSince(String url, String lastModified) throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(url);
		get.setHeader("Accept-Encoding", "gzip");
		get.setHeader("If-Modified-Since", lastModified);
		return load(get);
	}

	public String getRelocation(HttpGet get) throws ClientProtocolException, IOException {
		HttpResponse response = httpClient.execute(get);
		return response.getFirstHeader("location").getValue();
	}

	public String load(HttpGet get) throws ClientProtocolException, IOException {
		HttpResponse response = httpClient.execute(get);
		// System.out.println("----------------------------------------");
		// System.out.println(response.getStatusLine());
		// System.out.println(response.getLastHeader("Content-Encoding"));
		// System.out.println(response.getLastHeader("Content-Length"));
		// System.out.println("----------------------------------------");

		if (response.getStatusLine().getStatusCode() == 304) {
			System.out.println("Not Modified");
			return "";
		}
		Header[] headers = response.getAllHeaders();
		// for (Header header : headers) {
		// System.out.println("header name :" + header.getName() + " value:"
		// + header.getValue());
		// }
		HttpEntity entity = response.getEntity();
		if (entity.getContentEncoding() != null && entity.getContentEncoding().getValue().equalsIgnoreCase("gzip")) {
			// System.out.println("compressed size: "
			// + entity.toString().getBytes().length);
			GzipEntity gentity = new GzipEntity(entity);
			String content = EntityUtils.toString(gentity, charset);
			// System.out.println("----------------------------------------");
			// System.out.println("Uncompressed size: " + content.length());
			return content;
		} else {
			return EntityUtils.toString(entity, charset);
		}
	}

	public int downAttach(HttpGet get, String saveFile) {

		HttpResponse response = null;
		try {
			response = httpClient.execute(get);

			System.out.println("status:" + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 302) {
				System.out.println("Not Modified");
				String move = response.getFirstHeader("Location").getValue();
				HttpGet get2 = new HttpGet(move);
				response = httpClient.execute(get2);
			}
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Header[] headers = response.getAllHeaders();
		for (Header header : headers) {
			System.out.println("header name :" + header.getName() + " value:" + header.getValue());
		}
		System.out.print("saveFile:" + saveFile);
		HttpEntity entity = response.getEntity();
		InputStream input = null;
		try {
			input = entity.getContent();
			FileOutputStream out = new FileOutputStream(new File(saveFile));
			byte[] b = new byte[1024];
			int total = 0;
			int tmp = 0;
			while (true) {
				tmp = input.read(b, 0, 1024);
				total += tmp;
				if (tmp == -1) {
					break;
				} else {
					out.write(b, 0, tmp);
				}
			}
			System.out.print("total Length:" + total);

			out.close();
			input.close();
			return total;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {
		String str = "http://sports.yahoo.com/nba/teams/bos";
		// String str =
		// "http://tech.163.com/09/1118/05/5OCJS5FQ000sadf915BE.html";
		String content = "";
		UrlDownload down = new UrlDownload();
		down.setCharset("utf-8");
		long start = System.currentTimeMillis();
		content=down.load("http://magazine.caijing.com.cn/2011-06-07/110739368.html");
		
		// down.testURL(str);
		// content=down.load(str,false);
		// try {
		// //
		// content=down.load("http://qyxy.baic.gov.cn/zhcx/zhcxAction!list.dhtml?op=cx&zhcxModel.ent_name=&zhcxModel.lic_reg_no=110000001141090&zhcxModel.corp_rpt=&zhcxModel.cer_no=&zhcxModel.dom=");
		// } catch (ClientProtocolException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// content = down
		// .post("http://qyxy.baic.gov.cn/zhcx/zhcxAction!list.dhtml?op=cx");
		// System.out.println("content:" + content);

		long end = System.currentTimeMillis();
		System.out.println("content:"+content);
		System.out.println("Download Used: time:" + (end - start) + "  miliseconds!");
		// System.out.println("content:"+content);
		// str = down
		// .load("http://218.22.14.84:8080/soms4/web/jwzt/player/vod_player.jsp?fileId=22951");
		// str =
		// down.download("http://218.22.14.84:8080/soms4/web/jwzt/player/vod_player.jsp?fileId=22951");
		// System.out.println(str);
		// str = down.download("http://www.997788.com");
		// System.out.println(str);
//		HttpGet get = new HttpGet("http://210.52.215.72/ycpj/GetUpdateConfig.aspx?type=init&version=2.48.csv");
//		//		String cookie = "Newasp%5Fnet=onlineid=22122398114; ASPSESSIONIDCSQDTRDS=ELPNDMPCFMCKMIFKKCHMLEKF; virtualwall=vsid=73bad331ca1c4f5c1f6b3f3069edb063; test=logo=221%2E223%2E98%2E114&Grade=3&isencrypt=1&dby=90&uid=110&dbyDayHits=0&point=0&password=28ad49f7c66e5707&card=0&userid=131445&username=silenceralex; zq=softname=%B5%C8%B4%FD%D6%FE%B5%D7%2D%2D2010%C4%EA5%2D6%D4%C2A%B9%C9%B2%DF%C2%D4%B1%A8%B8%E6%A1%AA%A1%AA%D4%AC%D2%CB+%B3%C2%BD%DC+; date=authori=&typei=%CA%D0%B3%A1%D1%D0%BE%BF";
//		//		get.setHeader("Cookie", cookie);
//		HttpResponse response = null;
//		try {
//			response = down.getHttpClient().execute(get);
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Header[] headers = response.getAllHeaders();
//		String cookie = "";
//		for (Header header : headers) {
//			System.out.println("response:" + header.getName() + " : " + header.getValue());
//			if ("Set-Cookie".equals(header.getName())) {
//				cookie = header.getValue();
//			}
//		}
//		cookie = cookie.substring(0, cookie.indexOf(';'));
//		System.out.println("cookie:" + cookie);
//
//		get = new HttpGet("http://210.52.215.72//YCPJ/Logon.aspx?type=logon");
//		get.setHeader("user", "johnnychenjun");
//		get.setHeader("pwd", "4a91b5691286687a5d8eccb47f153ed2");
//		get.setHeader("sn", "5CAC-4C53-FA52-BFEBFBFF00020655-WD-WXR1A60H9613");
//		get.setHeader("ver", "2.48");
//		get.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
//		get.setHeader("Cookie", cookie);
//		String authority = "";
//		try {
//			response = down.getHttpClient().execute(get);
//			HttpEntity entity = response.getEntity();
//			authority = EntityUtils.toString(entity, "gbk");
//			System.out.println("retStr:" + authority);
//			authority = authority.substring(authority.lastIndexOf('=') + 1);
//			System.out.println("authority:" + authority);
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		//		get = new HttpGet("http://210.52.215.72/ycpj/GetBinaryStream.aspx?id=10001");
		//		get.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
		//		get.setHeader("Cookie", cookie);
		//
		//		try {
		//			response = down.getHttpClient().execute(get);
		//			System.out.print("StatusCode:" + response.getStatusLine().getStatusCode());
		//			headers = response.getAllHeaders();
		//			for (Header header : headers) {
		//				System.out.println("response:" + header.getName() + " : " + header.getValue());
		//			}
		//			HttpEntity entity = response.getEntity();
		//			GzipEntity gentity = new GzipEntity(entity);
		//			content = EntityUtils.toString(gentity, "gbk");
		//			FileUtil.write("D://test", content);
		//			FileOutputStream out = new FileOutputStream(new File("D://test"));
		//			byte[] b = new byte[1024];
		//			int total = 0;
		//			int tmp = 0;
		//			while (true) {
		//				tmp = input.read(b, 0, 1024);
		//				total += tmp;
		//				if (tmp == -1) {
		//					break;
		//				} else {
		//					out.write(b, 0, tmp);
		//					//					out.write(b);
		//				}
		//			}
		//			System.out.print("total Length:" + total);

		//			out.close();
		//			input.close();

		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

//		String url = "http://210.52.215.72/YCPJ/GetStockJS.aspx?type=TitleList&user=johnnychenjun&guid=" + authority
//				+ "&articleBm=wyxs_new_Cache_TOP_syzy";
//		System.out.println("url:" + url);
//		get = new HttpGet(url);
//		get.setHeader("User-Agent",
//				"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; InfoPath.2; .NET4.0C; .NET4.0E)");
//		get.setHeader("Cookie", cookie);
//		get.setHeader("Accept", "*/*");
//		get.setHeader("Accept-Language", "zh-cn");
//		get.setHeader("Accept-Encoding", "gzip, deflate");
//		get.setHeader("Connection", "Keep-Alive");
//
//		String recentArticleList = "";
//		try {
//			response = down.getHttpClient().execute(get);
//			HttpEntity entity = response.getEntity();
//			System.out.print("StatusCode:" + response.getStatusLine().getStatusCode());
//			headers = response.getAllHeaders();
//			for (Header header : headers) {
//				System.out.println("response:" + header.getName() + " : " + header.getValue());
//			}
//			recentArticleList = EntityUtils.toString(entity, "gbk");
//			System.out.println("recentArticleList:" + recentArticleList);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

}
