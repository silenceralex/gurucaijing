package com.caijing.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
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

		// Create and initialize scheme registry
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));

		// Create an HttpClient with the ThreadSafeClientConnManager.
		// This connection manager must be used if more than one thread will
		// be using the HttpClient.
		cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		httpClient = new DefaultHttpClient(cm, params);
	}

	// // 原始下载用
	public int testURL(String url) throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(url);
		// get.setHeader("User-Agent", agent);
		HttpContext localContext = new BasicHttpContext();
		// get.setHeader("refer", "http://www.autohome.com.cn/145/");
		HttpResponse response = httpClient.execute(get);

		// 判断页面返回状态判断是否进行转向抓取新链接
		int statusCode = response.getStatusLine().getStatusCode();
		get.abort();
		return statusCode;
		// System.out.println("statusCode: " + statusCode);
		// if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
		// || (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
		// || (statusCode == HttpStatus.SC_SEE_OTHER)
		// || (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
		// // 此处重定向处理 此处还未验证
		// HttpHost target = (HttpHost) localContext
		// .getAttribute(ExecutionContext.HTTP_TARGET_HOST);
		//
		// String newUri = response.getLastHeader("Location").getValue();
		// System.out.println("Redirect url: " + newUri);
		// System.out.println("HttpHost: " + target);
		// response = httpClient.execute(target, new HttpGet(newUri));
		// System.out.println(response.getStatusLine());
		// System.out.println("Redirect url html: " +
		// EntityUtils.toString(response.getEntity(), "gb2312"));
		// }
		//
		// String status = response.getStatusLine().toString();
		//
		// get.abort();
		// return status;
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
	public String getLastModified(String video_url)
			throws ClientProtocolException, IOException, ParseException {
		HttpGet get = new HttpGet(video_url);
		HttpResponse response = httpClient.execute(get);
		String time = response.getFirstHeader("Last-Modified").getValue();
		// 释放get
		get.abort();
		time = time.substring(0, time.indexOf(" GMT"));
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss", Locale.US);
		SimpleDateFormat standard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(time);
		return standard.format(date);
	}

	// httpclient的下载方式
	// public String load(String url) throws ClientProtocolException,
	// IOException {
	// HttpGet get = new HttpGet(url);
	// // System.out.println("response:" + get.getRequestLine());
	// HttpResponse response = httpClient.execute(get);
	//
	// HttpEntity entity = response.getEntity();
	// BufferedReader br = new BufferedReader(new InputStreamReader(entity
	// .getContent(), charset));
	// String line;
	// StringBuffer sb = new StringBuffer();
	// while ((line = br.readLine()) != null) {
	// sb.append(line);
	// sb.append("\n");
	// }
	// return sb.toString();
	// }

	public String post(String url) {
		HttpPost post = new HttpPost(url);
		// post.
		org.apache.http.client.HttpClient client = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(url);
		// NameValuePair[] data = { new NameValuePair("zhcxModel_ent_name",
		// "北京中关村科技担保有限公司 "),
		// new NameValuePair("zhcxModel_lic_reg_no", ""),
		// new NameValuePair("zhcxModel_corp_rpt", ""),
		// new NameValuePair("zhcxModel_cer_no", ""),
		// new NameValuePair("zhcxModel_dom", "")};
		List<BasicNameValuePair> data4 = new ArrayList<BasicNameValuePair>();

		data4.add(new BasicNameValuePair("zhcxModel.lic_reg_no",
				"110000001141090"));
		data4.add(new BasicNameValuePair("zhcxModel.ent_name", ""));
		data4.add(new BasicNameValuePair("zhcxModel.corp_rpt", ""));
		data4.add(new BasicNameValuePair("zhcxModel.dom", ""));
		data4.add(new BasicNameValuePair("zhcxModel.cer_no", ""));
		try {
			httpost.setEntity(new UrlEncodedFormEntity(data4, HTTP.UTF_8));
			httpost.addHeader("Host", "qyxy.baic.gov.cn");
			httpost
					.addHeader(
							"User-Agent",
							"Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			httpost.addHeader("Accept-Encoding", "GB2312,utf-8;q=0.7,*;q=0.7");
			httpost.addHeader("Keep-Alive", "115");
			httpost.addHeader("Accept-Encoding", "gzip,deflate");
			httpost
					.addHeader("Accept",
							"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			httpost.addHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
			httpost.addHeader("Content-Type",
					"application/x-www-form-urlencoded ");
			httpost.addHeader("Referer",
					"http://qyxy.baic.gov.cn/zhcx/zhcxAction!query.dhtml");
			httpost
					.addHeader(
							"Cookie",
							"JSESSIONID=MtpVL0hQnsk3dMyJxY7YQL8f8BV1BBT0wqtr5f9vv5pZ7v3xSv3z!529400894; BIGipServerPool_xy=202746048.22811.0000; wdcid=139beaaecc801ecb; wdlast=1270130881");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			System.out.println("<< httpost: " + httpost.getRequestLine());
			HttpResponse response = client.execute(httpost);
			HttpEntity entity = response.getEntity();
			System.out.println("<< Response: " + response.getStatusLine());
			if (entity != null) {
				return EntityUtils.toString(entity);
			}
			return null;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public String load(String url) throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(url);
		get.setHeader("Accept-Encoding", "gzip");
		return load(get);
	}

	public String load(String url, boolean isGzip)
			throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(url);
		if (isGzip) {
			get.setHeader("Accept-Encoding", "gzip");
			HttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			if (entity.getContentEncoding() != null
					&& entity.getContentEncoding().getValue().equalsIgnoreCase(
							"gzip")) {
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

	public String loadSince(String url, String lastModified)
			throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(url);
		get.setHeader("Accept-Encoding", "gzip");
		get.setHeader("If-Modified-Since", lastModified);
		return load(get);
	}

	public String getRelocation(HttpGet get) throws ClientProtocolException,
			IOException {
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
		if (entity.getContentEncoding() != null
				&& entity.getContentEncoding().getValue().equalsIgnoreCase(
						"gzip")) {
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

	public void downAttach(HttpGet get, String saveFile) {

		HttpResponse response = null;
		try {
			response = httpClient.execute(get);

			System.out.println("status:"
					+ response.getStatusLine().getStatusCode());
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
			System.out.println("header name :" + header.getName() + " value:"
					+ header.getValue());
		}
		System.out.print("saveFile:"+saveFile);
		HttpEntity entity = response.getEntity();
		InputStream input = null;
		try {
			input = entity.getContent();
			FileOutputStream out = new FileOutputStream(new File(saveFile));
			byte[] b = new byte[1024];
			int tmp = 0;
			while (true) {
				tmp = input.read(b, 0, 1024);
				if (tmp == -1) {
					break;
				} else {
					out.write(b, 0, tmp);
//					out.write(b);
				}
			}
			out.close();
			input.close();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (entity.getContentEncoding() != null
				&& entity.getContentEncoding().getValue().equalsIgnoreCase(
						"gzip")) {
			// System.out.println("compressed size: "
			// + entity.toString().getBytes().length);
			GzipEntity gentity = new GzipEntity(entity);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str = "http://sports.yahoo.com/nba/teams/bos";
		// String str =
		// "http://tech.163.com/09/1118/05/5OCJS5FQ000sadf915BE.html";
		String content = "";
		UrlDownload down = new UrlDownload();
		down.setCharset("utf-8");
		long start = System.currentTimeMillis();
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
		System.out.println("Download Used: time:" + (end - start)
				+ "  miliseconds!");
		// System.out.println("content:"+content);
		// str = down
		// .load("http://218.22.14.84:8080/soms4/web/jwzt/player/vod_player.jsp?fileId=22951");
		// str =
		// down.download("http://218.22.14.84:8080/soms4/web/jwzt/player/vod_player.jsp?fileId=22951");
		// System.out.println(str);
		// str = down.download("http://www.997788.com");
		// System.out.println(str);
		HttpGet get = new HttpGet(
				"http://220.112.42.246/newsoft.asp?type=%D0%D0%D2%B5%D1%D0%BE%BF");
		String cookie = "Newasp%5Fnet=onlineid=22122398114; ASPSESSIONIDCSQDTRDS=ELPNDMPCFMCKMIFKKCHMLEKF; virtualwall=vsid=73bad331ca1c4f5c1f6b3f3069edb063; test=logo=221%2E223%2E98%2E114&Grade=3&isencrypt=1&dby=90&uid=110&dbyDayHits=0&point=0&password=28ad49f7c66e5707&card=0&userid=131445&username=silenceralex; zq=softname=%B5%C8%B4%FD%D6%FE%B5%D7%2D%2D2010%C4%EA5%2D6%D4%C2A%B9%C9%B2%DF%C2%D4%B1%A8%B8%E6%A1%AA%A1%AA%D4%AC%D2%CB+%B3%C2%BD%DC+; date=authori=&typei=%CA%D0%B3%A1%D1%D0%BE%BF";
		get.setHeader("Cookie", cookie);
		HttpResponse response = null;
		try {
			response = down.getHttpClient().execute(get);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Header[] headers = response.getAllHeaders();
		for (Header header : headers) {
			System.out.println("response:" + header.getName() + " : "
					+ header.getValue());
		}
		HttpEntity entity = response.getEntity();
		try {
			System.out.println(EntityUtils.toString(entity, "gbk"));
		} catch (org.apache.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
