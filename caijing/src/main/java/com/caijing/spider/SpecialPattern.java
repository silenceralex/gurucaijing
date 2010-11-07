package com.caijing.spider;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.caijing.domain.ColumnArticle;
import com.caijing.util.MD5Utils;
import com.caijing.util.UrlDownload;

/**
 * SpecialPattern is a InnerMatch container to extract content from html
 * 该类主要功能为从一个视频页面解析出相关视频信息，并且更新数据库记录。
 * 
 * @author chenjun <jun-chen@corp.netease.com>
 */
public class SpecialPattern {
	private Pattern regexp = null;
	private Pattern contentRegexp = null; // 加入内容过滤，也是为了同url规则，而内容匹配板式不同情况的处理
	private boolean revisit = true;
	private List<InnerMatch> link = new ArrayList<InnerMatch>();
	private List<InnerMatch> page = new ArrayList<InnerMatch>();
	private List<String> params = new ArrayList<String>();
	private List<Boolean> paramFlags = new ArrayList<Boolean>();
	private String source = "";
	private boolean joint = false; // video source是否需要拼接
	private String baseUrl = "";
	private String postFix = "";
	private String THRESHOLD = "2009-10-20 00:00:00";
	private static Log logger = LogFactory.getLog(SpecialPattern.class);

	public boolean isRevisit() {
		return revisit;
	}

	public void setRevisit(boolean revisit) {
		this.revisit = revisit;
	}

	public SpecialPattern(String regexp, String contentRegexp) {
		this.regexp = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		if (contentRegexp != null)
			this.contentRegexp = Pattern.compile(contentRegexp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL
					| Pattern.UNIX_LINES);
	}

	private boolean beforeThreshold(String time) throws ParseException {
		SimpleDateFormat DATE_OUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dt1 = DATE_OUT_FORMAT.parse(time);
		Date dt2 = DATE_OUT_FORMAT.parse(THRESHOLD);
		if (dt1.getTime() > dt2.getTime()) {
			// System.out.println("在"+THRESHOLD+"之后");
			return false;
		} else {
			// System.out.println("在"+THRESHOLD+"之前");
			return true;
		}
	}

	public void addLinkInnerMatch(InnerMatch im) {
		link.add(im);
	}

	public void addParam(String param, Boolean paramFlag) {
		params.add(param);
		paramFlags.add(paramFlag);
	}

	public void addPageInnerMatch(InnerMatch im) {
		page.add(im);
	}

	/**
	 * Function: parse the real video_url from the iframe link
	 * 
	 * @param targetUrl
	 * @param contextUrl
	 * @param inner
	 * @return video_url
	 */
	public String processLinkURL(URL contextUrl, String inner, UrlDownload loader) {
		// System.out.println("url:"+inner);
		if (inner.startsWith("mms:")) {
			return inner;
		}
		String tmp = "";
		try {
			tmp = new URL(contextUrl, inner).toString();
			tmp = tmp.replaceAll("&amp;", "&");
			// System.out.println("tmp:" + tmp);
		} catch (MalformedURLException e) {
			System.out.println("url:" + inner);
			e.printStackTrace();
		}
		for (InnerMatch im : link) {
			tmp = im.getMatchResult(contextUrl, tmp, loader);
			// System.out.println("url:" + tmp);
			if (tmp == null || tmp.startsWith("mms://"))
				break;
		}
		if (tmp == null)
			return tmp;
		// 河北Tv 猥琐的用rtsp来掩饰其mms的本质
		if (tmp.startsWith("rtsp://")) {
			tmp = tmp.replaceAll("rtsp://", "mms://");
		}
		return tmp;
	}

	public boolean match(URL url, String content) {
		if (url == null) {
			return false;
		}
		Matcher m = regexp.matcher(url.toString());
		if (contentRegexp == null) {
			return (m != null && m.find());
		} else {
			Matcher contentM = contentRegexp.matcher(content);
			return (m != null && m.find() && contentM != null && contentM.find());
		}
	}

	public boolean matchUrl(String url) {
		if (url == null) {
			return false;
		}
		Matcher m = regexp.matcher(url.toString());
		return (m != null && m.find());
	}

	/**
	 * Function: parse the real video_url from the iframe link
	 * 
	 * @param targetUrl
	 * @param contextUrl
	 * @param inner
	 * @return video_url
	 */
	public String getVideoUrl(URL contextUrl, String inner, UrlDownload loader) {
		String tmp = "";
		try {
			tmp = new URL(contextUrl, inner).toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		for (InnerMatch im : link) {
			tmp = im.getMatchResult(contextUrl, tmp, loader);
			if (tmp == null || tmp.startsWith("mms://"))
				break;
		}
		return tmp;
	}

	/**
	 * Function: extract property content from the page's html
	 * 
	 * @param pageUrl
	 * @param inner
	 * @return Map<String, String> property's key-value pair
	 */
	public Map<String, String> processPageInnerMatch(URL pageUrl, String inner, UrlDownload loader) {
		Map<String, String> properties = new HashMap<String, String>();
		for (InnerMatch im : page) {
			String v = im.getMatchResult(pageUrl, inner, loader);
			if (v != null) {
				properties.put(im.getProperty(), v);
			}
		}
		return properties;
	}

	public ColumnArticle processPage(URL pageUrl, String inner, UrlDownload loader) {
		Map<String, String> properties = new HashMap<String, String>();
		if (contentRegexp != null) {
			inner = contentRegexp.matcher(inner).replaceAll("");
		}

		properties = processPageInnerMatch(pageUrl, inner, loader);
		if (properties.size() == 0) {
			return null;
		}

		if (properties.containsKey("publish_time")) {
			String ptime = properties.get("publish_time");
			// System.out.println("ptime: " + ptime);
			if (ptime == null || ptime.length() != 19) {
				System.out.println("publish_time is not right format!!!");
				return null;
			}
			try {
				if (beforeThreshold(ptime)) {
					return null;
				}
			} catch (ParseException e) {
				logger.error(e.getMessage());
				// return;
				// e.printStackTrace();
			}
		}
		ColumnArticle article = new ColumnArticle();
		article.setLink(pageUrl.toString());
		article.setAid(MD5Utils.hash(article.getLink()));
		if (properties.get("abs") != null && properties.get("abs").length() > 255) {
			article.setAbs(properties.get("abs").substring(0, 255));
		} else {
			article.setAbs(properties.get("abs"));
		}

		article.setAuthor(properties.get("author"));
		article.setTitle(properties.get("title"));
		try {
			article.setPtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(properties.get("ptime")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//		article.setSrc(properties.get("source"));
		if (properties.get("source") != null && properties.get("source").length() > 20) {
			article.setSrc(properties.get("source").substring(0, 20));
		} else {
			article.setSrc(properties.get("source"));
		}
		article.setContent(properties.get("content"));

		//		for (String k : properties.keySet()) {
		//			System.out.println("k is :" + k + " \nvalue:" + properties.get(k));
		//		}
		return article;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public boolean isJoint() {
		return joint;
	}

	public void setJoint(boolean joint) {
		this.joint = joint;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getPostFix() {
		return postFix;
	}

	public void setPostFix(String postFix) {
		this.postFix = postFix;
	}

	public String getTHRESHOLD() {
		return THRESHOLD;
	}

	public void setTHRESHOLD(String threshold) {
		THRESHOLD = threshold;
	}

	public static void main(String[] args) {
		SAXReader sr = new SAXReader();
		Document xml = null;

		try {
			xml = sr.read(new File("jobs\\aastocks.xml"));
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		CrawlJob job = ConfigReader.fromXML(xml);
		SpecialPattern special = job.getSpecials().get(0);
		UrlDownload urldown = job.getUrldown();
		urldown.setCharset("GB2312");
		String content = null;
		try {
			content = urldown
					.load("http://www.aastocks.com.cn/News/2010/10/28/30287cec-db7b-4a72-b634-919033d061c3.shtml");
			URL url = new URL("http://www.aastocks.com.cn/News/2010/10/28/30287cec-db7b-4a72-b634-919033d061c3.shtml");
			special.processPage(url, content, urldown);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
