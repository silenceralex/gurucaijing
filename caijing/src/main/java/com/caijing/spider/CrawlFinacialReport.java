package com.caijing.spider;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.FinancialReportDao;
import com.caijing.domain.FinancialReport;
import com.caijing.util.ContextFactory;
import com.caijing.util.ServerUtil;
import com.caijing.util.UrlDownload;

//"http://www.cninfo.com.cn/disclosure/sh/mb/shmbq1.html",
//"http://www.cninfo.com.cn/disclosure/sz/cn/szcnq1.html",
//"http://www.cninfo.com.cn/disclosure/sz/sme/szsmeq1.html",
//"http://www.cninfo.com.cn/disclosure/sz/mb/szmbq1.html" };

public class CrawlFinacialReport {
	private static Log logger = LogFactory.getLog(CrawlFinacialReport.class);
	String[] starturls = {
			"http://www.cninfo.com.cn/disclosure/sz/mb/szmbsar.html",
			"http://www.cninfo.com.cn/disclosure/sh/mb/shmbsar.html",
			"http://www.cninfo.com.cn/disclosure/sz/cn/szcnsar.html",
			"http://www.cninfo.com.cn/disclosure/sz/sme/szsmesar.html"
			};

	private static String PREFIX = "/data/reports/";

	UrlDownload downloader = new UrlDownload();
	private static Pattern HREF = Pattern.compile(
			"<a.*?href=[\t\n\r\\s\"'#]?(.*?)[\t\n\r\\s\"'>#].*?>(.*?)</a>",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
	Pattern range = Pattern.compile("", Pattern.CASE_INSENSITIVE
			| Pattern.DOTALL);
	private List<Pattern> onlys = new ArrayList<Pattern>();
	Pattern contentpattern = Pattern
			.compile(
					"<div class=\"add[0-9]+\">.*?<span>([0-9]{6})</span></a>\\)(.*?)：(.*?)<br />(.*?)</div>",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	Pattern content2pattern = Pattern
			.compile(
					"<li><span class='zz'>([0-9]{6})</span><span class='dm2'><a href='(.*?)' target='_blank'>(.*?)：(.*?)</a>"
							+ ".*?<span class='time2'>(201[0-9]-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2})</span></li>",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	Pattern desctpattern = Pattern.compile("<pre>(.*?)</pre>",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	Pattern endpattern = Pattern.compile("页次：1/([0-9]+) 转到：",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	// URLs to be searched
	private Vector<String> urlsToSearch = new Vector<String>();

	@Autowired
	@Qualifier("financialReportDao")
	private FinancialReportDao financialReportDao = null;

	public FinancialReportDao getFinancialReportDao() {
		return financialReportDao;
	}

	public void setFinancialReportDao(FinancialReportDao financialReportDao) {
		this.financialReportDao = financialReportDao;
	}

	public Vector<String> getUrlsToSearch() {
		return urlsToSearch;
	}

	public void setUrlsToSearch(Vector<String> urlsToSearch) {
		this.urlsToSearch = urlsToSearch;
	}

	// URLs already searched
	private Vector<String> urlsSearched = new Vector<String>();

	protected boolean finished = false;

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

	private synchronized void addWithoutDuplication(Vector<String> vector,
			String url) {
		if (!vector.contains(url)) {
			vector.add(url);
		}
	}

	private void processLink(URL url, String content)
			throws MalformedURLException {
		if (content == null || content.length() == 0)
			return;
		// match anchors
		Matcher m = HREF.matcher(content);
		if (m != null) {
			while (m.find()) {
				String outlink = m.group(1);
				URL ou = new URL(url, outlink);
				// System.out.println("urlsToSearch:" + ou.toString());
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

	public void runEastMoney() {
		String content = "";
		HashMap<String, FinancialReport> codeMap = new HashMap<String, FinancialReport>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		for (String starturl : starturls) {
			try {
				content = downloader.load(starturl);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			int end = 0;
			Matcher endm = endpattern.matcher(content);
			if (endm != null && endm.find()) {
				System.out.println("endm.group(1):" + endm.group(1));
				end = Integer.parseInt(endm.group(1).trim());
				System.out.println("end:" + end);
			}
			// Matcher m = rangePattern.matcher(content);
			// if (m != null && m.find()) {
			// content = m.group();
			// }
			Matcher m = null;
			if (content2pattern != null) {
				m = content2pattern.matcher(content);
				int i = 0;
				while (m != null && m.find()) {
					String code = m.group(1);
					String url = "";
					try {
						url = new URL(new URL(starturl), m.group(2)).toString();
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					}
					String name = m.group(3);
					String title = m.group(4);
					if(title.contains("摘要")){
						continue;
					}

//					if (!codeMap.containsKey(code) || title.contains("全文")
//							|| title.contains("修订版")) {
					if (!codeMap.containsKey(code) || title.contains("更正")
								|| title.contains("补充") || title.contains("修订")) {
						FinancialReport report = new FinancialReport();
						report.setStockcode(code);
						report.setStockname(name);
						report.setTitle(title);
						report.setYear(title.substring(0, 4));
						report.setType((byte) 2);
						String time = m.group(5).trim();
						try {
							report.setLmodify(sdf.parse(time));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						codeMap.put(code, report);

						System.out.println("name:" + name + " code :" + code);
						System.out.println("url:" + url);

						System.out
								.println("title:" + title + "   time:" + time);
						report.setFilepath(url);
					}
				}
			}
			String prefix = starturl.substring(0, starturl.indexOf(".html"));
			System.out.println("prefix:" + prefix);
			int page = 2;
			while (page < end) {
				String currpagge = prefix + page + ".html";
				System.out.println("currpagge:" + currpagge);
				processpage(currpagge, codeMap);
				page++;
			}
		}

		for (String key : codeMap.keySet()) {
			FinancialReport report = codeMap.get(key);
			report.setReportid(ServerUtil.getid());
			// filepath store url
			String url = report.getFilepath();
			download(report);
			String filepath = "/" + report.getYear() + "/" + report.getType()
					+ "/" + report.getStockcode() + ".pdf";
			report.setFilepath(filepath);
			report.setStatus((byte) 0);
			if (report.getStockname().length() > 8) {
				logger.error("ERROR  name out of range:"
						+ report.getStockname());
			}
			try {
				financialReportDao.insert(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void processpage(String page, Map<String, FinancialReport> codeMap) {
		String content = "";
		try {
			content = downloader.load(page);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Matcher m = null;
		if (content2pattern != null) {
			m = content2pattern.matcher(content);
			int i = 0;
			while (m != null && m.find()) {
				String code = m.group(1);

				String url = "";
				try {
					url = new URL(new URL(page), m.group(2)).toString();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
				String name = m.group(3);

				String title = m.group(4);
				if(title.contains("摘要")){
					continue;
				}
//				if (!codeMap.containsKey(code) || title.contains("全文")
//						|| title.contains("修订版")) {
					if (!codeMap.containsKey(code) || title.contains("更正")
							|| title.contains("补充") || title.contains("修订")) {
					FinancialReport report = new FinancialReport();
					report.setStockcode(code);
					report.setStockname(name);
					report.setTitle(title);
					report.setYear(title.substring(0, 4));
					report.setType((byte) 2);
//					report.setType((byte) 1);
					String time = m.group(5).trim();
					try {
						report.setLmodify(sdf.parse(time));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					codeMap.put(code, report);
					System.out.println("name:" + name + " code :" + code);
					System.out.println("url:" + url);
					System.out.println("title:" + title + "   time:" + time);
					report.setFilepath(url);
				}
			}
		}

	}

	private void download(FinancialReport report) {
		String path = PREFIX + report.getYear() + "/" + report.getType() + "/";
		if (!(new File(path).exists())) {
			new File(path).mkdirs();
		}
		String saveFile = path + "/" + report.getStockcode() + ".pdf";
		downloader.downAttach(report.getFilepath(), saveFile);
	}

	public static void main(String[] args) {
		CrawlFinacialReport finacialreport = new CrawlFinacialReport();
		FinancialReportDao financialReportDao = (FinancialReportDao) ContextFactory
				.getBean("financialReportDao");
		finacialreport.setFinancialReportDao(financialReportDao);
		finacialreport.runEastMoney();
		System.exit(0);
	}
}
