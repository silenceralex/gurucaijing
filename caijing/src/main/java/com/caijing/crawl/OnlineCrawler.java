package com.caijing.crawl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.caijing.dao.MasterMessageDao;
import com.caijing.domain.MasterMessage;
import com.caijing.util.DateTools;
import com.caijing.util.FileUtil;
import com.caijing.util.ServerUtil;

public class OnlineCrawler {

	private MasterMessageDao masterMessageDao = null;

	public MasterMessageDao getMasterMessageDao() {
		return masterMessageDao;
	}

	public void setMasterMessageDao(MasterMessageDao masterMessageDao) {
		this.masterMessageDao = masterMessageDao;
	}

	HttpParams params = new BasicHttpParams();
	ClientConnectionManager cm = null;
	HttpClient httpClient = null;
	private static Pattern stockPattern = Pattern.compile("(((002|000|300|600)[\\d]{3})|60[\\d]{4})",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);

	private static Pattern keyPattern = Pattern.compile("callGetInfoService\\('575','3929853','(.*?)','(.*?)'\\)",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);

	// TODO 获取直播时的解析内容   <div class="qzL2"> 09:38:19  今日的压力带依旧是在2740--2760之间</div>
	private static Pattern contentPattern = Pattern.compile(
			"<div class=\"qzL2\">\\s+([0-9]{2}:[0-9]{2}:[0-9]{2})\\s+(.*?)</div>", Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL | Pattern.UNIX_LINES);

	private static String LOGPATH = "/home/app/crawlog/";
	private static final String COOKIE = "__gads=ID=579bb78f0ec5705b:T=1295015028:S=ALNI_MadvIXE6VJLvQ5cweicul8cCF7w5w; SUV=1295015144090647; IPLOC=CN1100; g7F_cookietime=86400; g7F_sid=8H8yhY; g7F_visitedfid=27; smile=1D1; __utma=105107665.1699322037.1295749815.1295749815.1295749815.1; __utmz=105107665.1295749816.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); Hm_lvt_c378c4854ec370c1c8438f72e19b7170=1295749815844; cookie[passport][userId]=3929853; cookie[passport][username]=issn517; cookie[passport][nickname]=surrogate; cookie[passport][money]=2637; cookie[passport][keys]=1967D734C104ABF31D2D3D884862D06A; cookie[passport][logtime]=1298341899; cookie[passport][keystr]=EC249A80CDE48A2224CA9CD150AB1A74; cookie[passport][cache]=97AD78F6FB1A883684391560CD13A803; cookie[passport][auto]=1; cookie[passport][mailnum]=2; JSESSIONID=VgtiiOXFUK6XLER12s";

	public void init() {
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "GB2312");
		HttpProtocolParams.setUseExpectContinue(params, true);
		// 设置不自动跳转
		//		params.setBooleanParameter("http.protocol.handle-redirects", false);

		ConnManagerParams.setMaxTotalConnections(params, 100);
		ConnManagerParams.setTimeout(params, 30000);
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		// Create and initialize scheme registry
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

		cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		httpClient = new DefaultHttpClient(cm, params);
	}

	public void getZhibo(int masterid, int startnum, String key, String d_str) throws UnsupportedEncodingException {
		HttpPost post = new HttpPost("http://online.g.cnfol.com/getinfo.html");
		List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
		data.add(new BasicNameValuePair("clubid", "" + masterid));
		data.add(new BasicNameValuePair("displayNum", "" + startnum));
		data.add(new BasicNameValuePair("uid", "3929853"));
		data.add(new BasicNameValuePair("key", key));
		data.add(new BasicNameValuePair("d_str", d_str));

		post.setEntity(new UrlEncodedFormEntity(data, HTTP.UTF_8));
		post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
		post.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
		post.setHeader("Keep-Alive", "115");
		post.setHeader("Connection", "keep-alive");
		post.setHeader("User-Agent",
				"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		post.setHeader("Accept-Encoding", "gzip,deflate");
		post.setHeader("Content-type", "application/x-www-form-urlencoded");
		post.setHeader("Cookie", COOKIE);
		try {
			HttpResponse response = httpClient.execute(post);
			GzipEntity gentity = new GzipEntity(response.getEntity());

			String content = EntityUtils.toString(gentity, "GB2312");
			System.out.println("HTML: " + content);
			String log = DateTools.transformDateDetail(new Date()) + "  Html: " + content + "\r\n";
			FileUtil.appendWrite(LOGPATH + masterid + ".html", log, "GB2312");
			String curnum = content.substring(0, content.indexOf(","));
			System.out.println("curnum: " + curnum);
			Matcher m = contentPattern.matcher(content);
			while (m != null && m.find()) {
				String ptime = m.group(1).trim();
				String mcontent = m.group(2).trim();
				System.out.println("ptime: " + ptime + "  mcontent:" + mcontent);
				MasterMessage mm = new MasterMessage();
				mm.setContent(mcontent);
				mm.setCurrdate(new Date());
				mm.setMessageid(ServerUtil.getid());
				mm.setPtime(ptime);
				mm.setMasterid(masterid);
				if (masterMessageDao == null) {
					System.out.println("masterMessageDao is null! ");
				}
				masterMessageDao.insert(mm);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void crawler(int masterid, int startnum) {
		HttpGet get = new HttpGet("http://online.g.cnfol.com/575,display");
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
		get.setHeader("Cookie", COOKIE);
		try {
			HttpResponse response = httpClient.execute(get);
			GzipEntity gentity = new GzipEntity(response.getEntity());
			// String content = EntityUtils.toString(response.getEntity(),
			// "utf-8");
			String content = EntityUtils.toString(gentity, "GB2312");
			Matcher m = keyPattern.matcher(content);
			if (m != null && m.find()) {
				String dstr = m.group(1);
				String key = m.group(2);
				System.out.println("dstr:" + dstr + "   key:" + key);
				getZhibo(masterid, startnum, key, dstr);
			}

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
				String content = FileUtil.read(filePath, "GBK");
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
		//		ExtractSchedule scheduledExtract = (ExtractSchedule) ContextFactory.getBean("extractScheduler");
		//		scheduledExtract.crawlOnline();
		OnlineCrawler crawler = new OnlineCrawler();
		crawler.init();
		crawler.crawler(575, 0);
	}
}
