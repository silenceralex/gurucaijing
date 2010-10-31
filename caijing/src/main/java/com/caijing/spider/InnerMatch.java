package com.caijing.spider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;

import com.caijing.util.HtmlUtils;
import com.caijing.util.UrlDownload;

/**
 * InnerMatch is the concrete class to match real pattern and extract properties
 * for SpecialPattern,it can deal with different types of property
 * ��ҳ��һ�����ԵĽ�����ȡ����������Ĳ�������Բ����������õ���Ƶ��Ϣ �ĸ�������
 * 
 * @author chenjun <jun-chen@corp.netease.com>
 */
public class InnerMatch {
	private Pattern regexp;

	private String property;

	private boolean stripTags = false;

	private String sperator = null;

	private String dateformat = null;

	private boolean isUrl = false;

	private boolean isDownload = false;

	private boolean extracURL = false;

	private String baseurl = null;

	private int maxDesLenth = 250;

	public InnerMatch(String regexp, String property) {
		this.regexp = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		this.property = property.trim();
	}

	public String getProperty() {
		return property;
	}

	/**
	 * �ؼ���ȡ������
	 * 
	 * @param contextUrl
	 *            ��ǰҳ�������ĵ�url
	 * @param inner
	 *            ���жϵ�����string
	 * @param loader
	 *            ����download���ݵ�������
	 * @return
	 */
	public String getMatchResult(URL contextUrl, String inner, UrlDownload loader) {

		// extracURLΪtrue���õ�ʱ�򣬴�url�н�������
		if (this.extracURL) {
			Matcher urlM = regexp.matcher(contextUrl.toString());
			if (urlM != null && urlM.find()) {
				String r = urlM.group(1).trim();
				if (this.dateformat != null && !dateformat.equals("yyyy-MM-dd HH:mm:ss")) {
					SimpleDateFormat DATE_OUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					SimpleDateFormat fmt = new SimpleDateFormat(this.dateformat);
					try {
						Date d = fmt.parse(r);
						r = DATE_OUT_FORMAT.format(d);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				return r.trim();
			}
			return null;
		}

		Matcher m = regexp.matcher(inner);
		if (m != null && m.find()) {
			// stripTags������ȥ��html��ǩ����
			String r = this.stripTags ? HtmlUtils.stripTags(m.group(1)) : m.group(1);
			r = r.trim();
			if (this.sperator != null) {
				r = r.replaceAll("\\s+", " ");
				Vector<String> parts = new Vector<String>();
				for (String p : r.split(this.sperator)) {
					if (!parts.contains(p)) {
						parts.add(p);
					}
				}
				StringBuffer sb = new StringBuffer();
				for (String p : parts) {
					sb.append(p.trim());
					sb.append("|");
				}
				sb.deleteCharAt(sb.length() - 1);
				r = sb.toString();
			}
			// ���ڵĸ�ʽ����
			if (this.dateformat != null && !dateformat.equals("yyyy-MM-dd HH:mm:ss")) {
				SimpleDateFormat DATE_OUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat fmt = new SimpleDateFormat(this.dateformat, Locale.US);
				try {
					if (r.length() == 0)
						return null;
					Date d = fmt.parse(r);
					r = DATE_OUT_FORMAT.format(d);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			// �Ƿ�url�������⴦��
			if (this.isUrl) {
				try {
					URL u = new URL(contextUrl, r);
					r = u.toString();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			if (this.baseurl != null) {
				r = baseurl + r;
			}

			// �Ƿ���Ҫͨ�����زŻ�ȡ����
			if (this.isDownload) {
				URL u;
				try {
					if (r.startsWith("mms")) {
						// System.out.println("contextUrl:"+contextUrl);
						// System.out.println("mmsUrl:"+r);
						return r;
					}
					u = new URL(contextUrl, r);
					r = loader.load(u.toString());
					// r = HtmlUtils.stripTags(r);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			r = r.trim();
			// �޶�description�ĳ���
			if (this.property.equals("description") && r.length() > maxDesLenth) {
				return r.substring(0, maxDesLenth);
			}

			return r.trim();
		}
		return null;
	}

	public void setStripTags(boolean stripTags) {
		this.stripTags = stripTags;
	}

	public void setSperator(String sperator) {
		this.sperator = sperator;
	}

	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}

	public String getDateformat() {
		return this.dateformat;
	}

	public void setUrl(boolean isUrl) {
		this.isUrl = isUrl;
	}

	public boolean isUrl() {
		return isUrl;
	}

	public Pattern getRegexp() {
		return regexp;
	}

	public void setRegexp(Pattern regexp) {
		this.regexp = regexp;
	}

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}

	public String getBaseurl() {
		return baseurl;
	}

	public void setBaseurl(String baseurl) {
		this.baseurl = baseurl;
	}

	public boolean isExtracURL() {
		return extracURL;
	}

	public void setExtracURL(boolean extracURL) {
		this.extracURL = extracURL;
	}

}
