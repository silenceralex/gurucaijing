package com.caijing.spider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.caijing.util.UrlDownload;
import com.sleepycat.je.DatabaseException;

/**
 * crawljob的配置读取类，同过一个任务的xml文件（jobs里的xml）来初始化一个crawljob
 * 提供两种类型任务的读取，CrawlJob，CrawlVideoJob
 * 
 * @author jun-chen
 * 
 */
public class ConfigReader {
	/**
	 * @param xml
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static CrawlJob fromXML(Document xml) {
		Element root = (Element) xml.selectSingleNode("//job");
		try {
			String startPage = root.attributeValue("start");
			String charset = root.attributeValue("charset");
			String strMaxConnections = root.attributeValue("maxconnections");
			int downMethod = Integer.parseInt(root.attributeValue("downMethod"));
			String source = root.attributeValue("source");
			String urlDB_path = root.attributeValue("urldb");
			Element rangeNode = (Element) root.selectSingleNode("rangePattern");
			String rangePattern = rangeNode.getTextTrim();
			Pattern rangeP = null;
			if (rangePattern != null) {
				rangeP = Pattern.compile(rangePattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
			}

			int maxConnections = 5;
			if (strMaxConnections != null) {
				try {
					maxConnections = Integer.parseInt(strMaxConnections);
				} catch (NumberFormatException e) {
				}
			}
			String strThreads = root.attributeValue("threads");
			int threads = 5;
			if (strThreads != null) {
				try {
					threads = Integer.parseInt(strThreads);
				} catch (NumberFormatException e) {
				}
			}

			CrawlJob job = new CrawlJob(startPage);
			if (charset != null) {
				job.setCharset(charset);
				job.getUrldown().setCharset(charset);
			}
			job.setMaxConnections(maxConnections);
			job.setThreads(threads);
			job.setDownMethod(downMethod);
			job.setRangePattern(rangeP);

			BerkeleyDB urlDB = new BerkeleyDB();
			urlDB.setup(urlDB_path, false);
			job.setUrlDB(urlDB);

			// excludes
			List excludeNodes = root.selectNodes("exclude");
			for (int i = 0; i < excludeNodes.size(); i++) {
				Element elm = (Element) excludeNodes.get(i);
				Pattern p = Pattern.compile(elm.getTextTrim(), Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				job.getExcludes().add(p);
			}

			// starturl  多个起始的url
			List starturlNodes = root.selectNodes("starturl");
			for (int i = 0; i < starturlNodes.size(); i++) {
				Element elm = (Element) starturlNodes.get(i);
				job.getStarturls().add(elm.getTextTrim());
			}

			// onlys
			List onlyNodes = root.selectNodes("only");
			for (int i = 0; i < onlyNodes.size(); i++) {
				Element elm = (Element) onlyNodes.get(i);
				Pattern p = Pattern.compile(elm.getTextTrim(), Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				job.getOnlys().add(p);
			}

			// init special patterns
			List<Element> patternNodes = root.selectNodes("specials/pattern");
			for (Element patternNode : patternNodes) {

				// Element patternNode = patternNodes.get(0);
				String regexp = patternNode.attributeValue("regexp");

				Element contentpatternNode = (Element) patternNode.selectSingleNode("contentpattern");
				String contentpattern = contentpatternNode.getTextTrim();
				String revisit = patternNode.attributeValue("revisit");
				boolean isJoint = Boolean.parseBoolean(patternNode.attributeValue("isjoint"));
				// String docset = patternNode.attributeValue("docset");
				// String picset = patternNode.attributeValue("picset");

				Element videoUrl = (Element) patternNode.selectSingleNode("videourl");
				SpecialPattern pattern = new SpecialPattern(regexp, contentpattern);
				if (isJoint) {
					pattern.setBaseUrl(videoUrl.attributeValue("baseurl"));
					pattern.setPostFix(videoUrl.attributeValue("postfix"));
					List<Element> paramNodes = patternNode.selectNodes("videourl/param");
					for (Element pNode : paramNodes) {
						String param = pNode.attributeValue("property");
						Boolean paramFlag = Boolean.parseBoolean(pNode.attributeValue("display"));
						pattern.addParam(param, paramFlag);
						System.out.println(param + " " + paramFlag.toString());
					}
				}
				if (revisit != null && revisit.equalsIgnoreCase("no")) {
					pattern.setRevisit(false);
				}
				pattern.setJoint(isJoint);
				List<Element> linkIMNodes = patternNode.selectNodes("link/innermatch");
				List<Element> pageIMNodes = patternNode.selectNodes("page/innermatch");

				for (Element imNode : linkIMNodes) {
					InnerMatch im = createInnerMatch(imNode);
					pattern.addLinkInnerMatch(im);
					System.out.println(im.getProperty() + " " + im.getRegexp().toString());
				}
				for (Element imNode : pageIMNodes) {
					InnerMatch im = createInnerMatch(imNode);
					pattern.addPageInnerMatch(im);
					// System.out.println(im.getProperty() + " "
					// + im.getRegexp().toString()+" "+im.getDateformat());
				}
				job.addSepcialPattern(pattern);
			}
			// job.setSpecial(pattern);
			return job;

		} catch (DatabaseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static RssJob getRssJobFromXML(Document xml) {
		RssJob rssjob = new RssJob();
		UrlDownload urldown = new UrlDownload();

		Element root = (Element) xml.selectSingleNode("//job");

		String startPage = root.attributeValue("start");
		String charset = root.attributeValue("charset");
		String urlDB_path = root.attributeValue("urldb");
		urldown.setCharset(charset);
		rssjob.setUrldown(urldown);
		rssjob.setStartPage(startPage);
		BerkeleyDB urlDB = new BerkeleyDB();
		urlDB.setup(urlDB_path, false);
		rssjob.setUrlDB(urlDB);
		Element node = (Element) root.selectSingleNode("root");
		rssjob.setRoot(node.attributeValue("property"));
		node = (Element) root.selectSingleNode("list");
		rssjob.setList(node.attributeValue("property"));
		node = (Element) root.selectSingleNode("src");
		rssjob.setSrc(node.attributeValue("property"));
		List<Element> patternNodes = root.selectNodes("page/innermatch");
		List<RssInnerMatch> innerMatches = new ArrayList<RssInnerMatch>();
		for (Element patternNode : patternNodes) {
			RssInnerMatch im = new RssInnerMatch();
			im.setProperty(patternNode.attributeValue("property"));
			im.setMapping(patternNode.attributeValue("mapping"));
			if (patternNode.attributeValue("dateformat") != null) {
				im.setDateformat(patternNode.attributeValue("dateformat"));
			}
			if (patternNode.attributeValue("isdown") != null) {
				im.setDownload(true);
			}

			Element repexp = (Element) patternNode.selectSingleNode("repexp");
			if (repexp != null) {
				im.setRepexp(repexp.getText().trim());
			}
			List<Element> filterNodes = patternNode.selectNodes("filter");
			List<Filter> filters = new ArrayList<Filter>();
			for (Element filterNode : filterNodes) {
				Filter filter = new Filter();
				if (filterNode.attribute("userPattern") != null) {
					filter.setUsePatter(true);
				}
				filter.setFilterStr(filterNode.getText().trim());
				filters.add(filter);
			}
			im.setFilters(filters);
			innerMatches.add(im);
		}
		rssjob.setInnerMatches(innerMatches);
		return rssjob;
	}

	public static InnerMatch createInnerMatch(Element imNode) {
		InnerMatch im = new InnerMatch(imNode.selectSingleNode("regexp").getText().trim(), imNode
				.attributeValue("property"));
		String attvStripTags = imNode.attributeValue("striptags");
		if (attvStripTags != null && attvStripTags.equalsIgnoreCase("true")) {
			im.setStripTags(true);
		}
		String attvSeperator = imNode.attributeValue("seperator");
		if (attvSeperator != null) {
			im.setSperator(attvSeperator);
		}
		String attvBaseUrl = imNode.attributeValue("baseurl");
		if (attvBaseUrl != null) {
			im.setBaseurl(attvBaseUrl);
		}
		String attvExtractUrl = imNode.attributeValue("extracturl");
		if (attvExtractUrl != null && attvExtractUrl.equals("true")) {
			im.setExtracURL(true);
		}

		String attvDateformat = imNode.attributeValue("dateformat");
		if (attvDateformat != null) {
			im.setDateformat(attvDateformat);
		}
		String attvIsUrl = imNode.attributeValue("isurl");
		if (attvIsUrl != null && attvIsUrl.equalsIgnoreCase("true")) {
			im.setUrl(true);
		}
		String attvIsDownload = imNode.attributeValue("isdownload");
		if (attvIsDownload != null && attvIsDownload.equalsIgnoreCase("true")) {
			im.setDownload(true);
		}
		return im;
	}

	public static PageParser getPageParserFromXML(String xmlPath) {
		Extractor bpInterface = null;

		SAXReader sr = new SAXReader();
		Document xml = null;
		try {
			xml = sr.read(new File(xmlPath));
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		Element root = (Element) xml.selectSingleNode("//jobs");
		String charset = root.attributeValue("charset");
		bpInterface.setDownEncode(charset);
		String urlDbPath = root.attributeValue("urldb");

		PageParser pp = new PageParser();
		pp.setDownEncode(charset);
		try {
			pp.getUrlDB().setup(urlDbPath, false);
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Element patternNode = (Element) root.selectSingleNode("pattern");
		List<Element> linkIMNodes = patternNode.selectNodes("innermatch");
		for (Element imNode : linkIMNodes) {
			InnerMatch im = ConfigReader.createInnerMatch(imNode);
			pp.getInnerMatches().add(im);
			System.out.println(im.getProperty() + " " + im.getRegexp().toString());
		}
		Element jobNode = (Element) root.selectNodes("job").get(0);
		//		Column col = new Column();
		//		col.setTag(jobNode.selectSingleNode("tag").getText().trim());
		//		col.setName(jobNode.selectSingleNode("column").getText().trim());
		//		col.setStartUrl(jobNode.selectSingleNode("start").getText().trim());
		//		col.setOnly(jobNode.selectSingleNode("only").getText().trim());
		//		col.setColumn_id(jobNode.selectSingleNode("column_id").getText().trim());
		//		pp.setCol(col);
		//		pp.setBpInterface(bpInterface);
		return pp;
	}

}
