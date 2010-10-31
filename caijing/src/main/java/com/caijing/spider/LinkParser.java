package com.caijing.spider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.caijing.util.UrlDownload;

/**
 * ��������ҳ��ȡ���߳�,��Ҫ��ɵ�һ�׶ε����񣬻�ȡ������Ҫץȡ��pageUrls��
 * 
 * @author jun-chen
 * 
 */
public class LinkParser extends Thread {
	// �����м�����ҳURLs
	private Vector<String> linkUrls = new Vector<String>();
	// ��������ҳURLs
	private Vector<String> pageUrls = new Vector<String>();
	// URLs already searched
	private Vector<String> searchedUrls = new Vector<String>();

	protected boolean finished = false;

	// ��ȡ���ӵ�pattern
	private static Pattern HREF = Pattern.compile("<a.*?href=[\t\n\r\\s\"'#]?(.*?)[\t\n\r\\s\"'>#].*?>(.*?)</a>",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);

	private List<Pattern> onlys = new ArrayList<Pattern>();

	private Pattern rangePattern = null;

	private Pattern Pagexp = null;
	private UrlDownload urldown = new UrlDownload();
	private String curUrl = "";
	private long bytesDownloaded = 0;

	public void setDownEncode(String charset) {
		urldown.setCharset(charset);
	}

	private static Logger logger = Logger.getLogger(LinkParser.class);

	public LinkParser() {
	}

	private String normalizeOutlink(URL url, String outlink) throws MalformedURLException {
		if (outlink.startsWith("?")) {
			return url.toString().replaceAll("\\?.*$", outlink);
		}
		return new URL(url, outlink).toString();
	}

	public void run() {
		while (this.linkUrls.size() > 0) {
			curUrl = linkUrls.remove(0);
			URL url = null;
			try {
				url = new URL(curUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				logger.error("Exception Occurred! :" + e.getMessage() + "  when downloading :" + curUrl);
				continue;
			}

			// mark the URL as searched (we want this one way or the other)
			addWithoutDuplication(searchedUrls, curUrl);
			String content = "";
			try {
				content = urldown.load(curUrl);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				logger.error("Exception Occurred! :" + e.getMessage() + "  when downloading :" + curUrl);
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Exception Occurred! :" + e.getMessage() + "  when downloading :" + curUrl);
				continue;
			}

			bytesDownloaded += content.getBytes().length;

			if (rangePattern != null) {
				Matcher rangeM = rangePattern.matcher(content);
				if (rangeM != null && rangeM.find())
					content = rangeM.group();
				// System.out.println("content: " + content);
			}

			if (url != null)
				processLink(url, content);
			// System.out.println("Time used on page #" + url.toString() + ":"
			// + (System.currentTimeMillis() - time) + "ms");
		}
		finished = true;
	}

	/**
	 * ����url��ҳ����������������ȡ�м�����ҳ
	 * 
	 * @param url
	 * @param content
	 */
	private void processLink(URL url, String content) {
		if (content == null || content.length() == 0)
			return;
		// match anchors
		Matcher m = HREF.matcher(content);
		if (m != null) {
			while (m.find()) {
				String outlink = m.group(1);
				try {
					String ou = normalizeOutlink(url, outlink);
					if (ou != null) {
						// �ж��Ƿ�Ϊ����ҳ
						if (isPage(ou)) {
							addWithoutDuplication(pageUrls, ou);
						} else {
							if (notExcluded(ou)) {
								// Special URL Pattern for link
								if (!searchedUrls.contains(ou)) {
									addWithoutDuplication(linkUrls, ou.toString());
								}
							}
						}
					}
				} catch (MalformedURLException e) {
					System.out.println("bad URL " + outlink);
					logger.error("Exception Occurred! :" + e.getMessage() + "  when downloading :" + outlink);
					continue;
				}
			}
		}
	}

	private boolean notExcluded(String ou) {
		if (this.onlys.size() > 0) {
			// the url should match at least one pattern in onlys
			boolean inOnly = false;
			for (Pattern p : this.onlys) {
				Matcher m = p.matcher(ou);
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

	// �ж�һ��url�Ƿ�Ϊ������Ƶ��Ϣҳ
	private boolean isPage(String url) {

		if (url == null) {
			return false;
		}
		Matcher m = Pagexp.matcher(url.toString());
		return (m != null && m.find());
	}

	/**
	 * @param urlsSearched2
	 * @param url
	 */
	private synchronized void addWithoutDuplication(Vector<String> vector, String url) {
		if (!vector.contains(url)) {
			vector.add(url);
		}
	}

	public Vector<String> getPageUrls() {
		return pageUrls;
	}

	public void setPageUrls(Vector<String> pageUrls) {
		this.pageUrls = pageUrls;
	}

	public List<Pattern> getOnlys() {
		return onlys;
	}

	public void setOnlys(List<Pattern> onlys) {
		this.onlys = onlys;
	}

	public Pattern getRangePattern() {
		return rangePattern;
	}

	public void setRangePattern(Pattern rangePattern) {
		this.rangePattern = rangePattern;
	}

	public Pattern getPagexp() {
		return Pagexp;
	}

	public void setPagexp(Pattern pagexp) {
		Pagexp = pagexp;
	}

	public Vector<String> getLinkUrls() {
		return linkUrls;
	}

	public void setLinkUrls(Vector<String> linkUrls) {
		this.linkUrls = linkUrls;
	}

	// ��ȡ��ǰץȡ��״̬
	public long[] getStatus() {
		return new long[] { this.linkUrls.size(), this.pageUrls.size(), this.bytesDownloaded };
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//		LinkParser lp = new LinkParser();
		//		Pattern only = Pattern.compile(
		//				"http://video\\.nxtv\\.cn/video\\.php\\?pType=72&page=\\d+",
		//				Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		//		Pattern pagexp = Pattern.compile(
		//				"http://video\\.nxtv\\.cn/player\\.php\\?id=\\d+",
		//				Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		//		Pattern rangePattern = Pattern.compile("��Ƶ�б�(.*?)video_list_right",
		//				Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		//		lp.getOnlys().add(only);
		//		lp.setPagexp(pagexp);
		//		lp.setRangePattern(rangePattern);
		//		lp.setDownEncode("UTF-8");
		//		Vector<String> pageUrls = new Vector<String>();
		//		lp.setPageUrls(pageUrls);
		//		Vector<String> linkUrls = new Vector<String>();
		//		linkUrls.add("http://video.nxtv.cn/video.php?view=&pType=72");
		//		lp.setLinkUrls(linkUrls);
		//		lp.run();
		//		System.out.println("Totally download pages : " + pageUrls.size());
		//		for (String url : pageUrls) {
		//			System.out.println("Page: " + url);
		//		}

		LinkParser t = new LinkParser();
		t.getLinkUrls().add("http://v.cbg.cn/node_4600.htm");
		Vector<String> pageUrls = new Vector<String>();
		t.setPageUrls(pageUrls);
		Pattern only = Pattern.compile("http://v\\.cbg\\.cn/node_4600_\\d+\\.htm", Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.UNIX_LINES);
		Pattern pagexp = Pattern.compile("http://v\\.cbg\\.cn/first/\\d{4}-\\d{2}/\\d{2}/content_\\d+?\\.htm",
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		Pattern rangePattern = Pattern.compile("div_w1_l_list(.*?)�󲿽���", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
				| Pattern.UNIX_LINES);
		t.getOnlys().add(only);
		t.setPagexp(pagexp);
		t.setRangePattern(rangePattern);
		t.setDownEncode("UTF-8");
		t.run();
		System.out.println("Totally download pages : " + pageUrls.size());
		for (String url : pageUrls) {
			System.out.println("Page: " + url);
		}

	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public String getCurUrl() {
		return curUrl;
	}

	public void setCurUrl(String curUrl) {
		this.curUrl = curUrl;
	}
}
