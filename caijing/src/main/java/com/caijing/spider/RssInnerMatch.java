package com.caijing.spider;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.dom4j.Element;

import com.caijing.util.UrlDownload;

public class RssInnerMatch {
	private String property;

	private boolean stripTags = false;

	private String sperator = null;

	private String dateformat = null;

	private boolean isUrl = false;

	private boolean isDownload = false;

	private String mapping = null;

	private List<Filter> filters = null;

	private String repexp = null;
	
	private String extractexp = null;

	public String getExtractexp() {
		return extractexp;
	}

	public void setExtractexp(String extractexp) {
		this.extractexp = extractexp;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public boolean isStripTags() {
		return stripTags;
	}

	public void setStripTags(boolean stripTags) {
		this.stripTags = stripTags;
	}

	public String getSperator() {
		return sperator;
	}

	public void setSperator(String sperator) {
		this.sperator = sperator;
	}

	public String getDateformat() {
		return dateformat;
	}

	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}

	public boolean isUrl() {
		return isUrl;
	}

	public void setUrl(boolean isUrl) {
		this.isUrl = isUrl;
	}

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public Object getMatchResult(Element patternNode, UrlDownload loader) throws ClientProtocolException, IOException,
			ParseException {
		String property = patternNode.selectSingleNode(mapping).getText().trim();
		if (!isDownload) {
			if (dateformat != null) {
				return new SimpleDateFormat(dateformat, Locale.US).parse(property);
			} else {
				for (Filter filter : filters) {
					property = filter.filter(property);
				}
				return property;
			}
		} else {
			if(extractexp != null){
				Pattern linkPattern = Pattern.compile(extractexp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL
						| Pattern.UNIX_LINES);
//				System.out.println("extractexp:"+extractexp);
				
				Matcher m = linkPattern.matcher(property);
				if (m != null && m.find()) {
					property = m.group(1);
					System.out.println("link:"+property);
				}
			}
			String content = loader.load(property);
			if (repexp != null) {
				Pattern contentPattern = Pattern.compile(repexp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL
						| Pattern.UNIX_LINES);
				Matcher m = contentPattern.matcher(content);
				if (m != null && m.find()) {
					content = m.group(1);
				}
//				System.out.println("content:"+content);
			}
			for (Filter filter : filters) {
				content = filter.filter(content);
			}
			return content;
		}
	}

	public String getRepexp() {
		return repexp;
	}

	public void setRepexp(String repexp) {
		this.repexp = repexp;
	}
}
