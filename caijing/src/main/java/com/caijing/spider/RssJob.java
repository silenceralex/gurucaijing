package com.caijing.spider;

import java.io.File;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.ColumnArticleDao;
import com.caijing.domain.ColumnArticle;
import com.caijing.util.ContextFactory;
import com.caijing.util.UrlDownload;

public class RssJob {

	private BerkeleyDB urlDB = null;

	private SimpleDateFormat sdf = null;

	private String startPage = null;
	private String root = null;
	private String list = null;
	private String src = null;
	private String charset = "GBK";
	private int maxConnections = 5;
	private int threads = 5;
	private UrlDownload urldown = null;
	private List<RssInnerMatch> innerMatches = null;

	@Autowired
	@Qualifier("columnArticleDao")
	private ColumnArticleDao columnArticleDao = null;

	public void run() {
		SAXReader sr = new SAXReader();
		Document xml = null;
		try {
			String rss = urldown.load(startPage);
			xml = sr.read(new StringReader(rss));
			Element root = (Element) xml.selectSingleNode("//" + this.root);
			Element srcNode = (Element) root.selectSingleNode(src);
			String src = srcNode.getTextTrim();
			List<Element> patternNodes = root.selectNodes(list);
			for (Element patternNode : patternNodes) {
				ColumnArticle article = new ColumnArticle();
				article.setSrc(src);
				for (RssInnerMatch innerMatch : innerMatches) {
					if (innerMatch.getDateformat() != null) {
						Date ptime = (Date) innerMatch.getMatchResult(patternNode, urldown);
						article.setPtime(ptime);
					} else {
						if ("author".equals(innerMatch.getProperty())) {
							article.setAuthor((String) innerMatch.getMatchResult(patternNode, urldown));
						} else if ("title".equals(innerMatch.getProperty())) {
							article.setTitle((String) innerMatch.getMatchResult(patternNode, urldown));
						} else if ("link".equals(innerMatch.getProperty())) {
							article.setLink((String) innerMatch.getMatchResult(patternNode, urldown));
						} else if ("abs".equals(innerMatch.getProperty())) {
							article.setAbs((String) innerMatch.getMatchResult(patternNode, urldown));
						} else if ("content".equals(innerMatch.getProperty())) {
							article.setContent((String) innerMatch.getMatchResult(patternNode, urldown));
						}
					}
				}
				if (!urlDB.contains(article.getLink())) {
					urlDB.putUrl(article.getLink());
					columnArticleDao.insert(article);
				}
				System.out.println("author: " + article.getAuthor());
				System.out.println("title: " + article.getTitle());
				System.out.println("pubDate: " + article.getPtime());
				System.out.println("abs: " + article.getAbs());
				System.out.println("link: " + article.getLink());
				System.out.println("content: " + article.getContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SAXReader sr = new SAXReader();
		Document xml = null;
		try {
			xml = sr.read(new File("jobs\\caijing.xml"));
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		ColumnArticleDao columnArticleDao = (ColumnArticleDao) ContextFactory.getBean("columnArticleDao");
		RssJob rssjob = ConfigReader.getRssJobFromXML(xml);
		rssjob.setColumnArticleDao(columnArticleDao);
		rssjob.run();
	}

	public BerkeleyDB getUrlDB() {
		return urlDB;
	}

	public void setUrlDB(BerkeleyDB urlDB) {
		this.urlDB = urlDB;
	}

	public SimpleDateFormat getSdf() {
		return sdf;
	}

	public void setSdf(SimpleDateFormat sdf) {
		this.sdf = sdf;
	}

	public String getStartPage() {
		return startPage;
	}

	public void setStartPage(String startPage) {
		this.startPage = startPage;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public UrlDownload getUrldown() {
		return urldown;
	}

	public void setUrldown(UrlDownload urldown) {
		this.urldown = urldown;
	}

	public List<RssInnerMatch> getInnerMatches() {
		return innerMatches;
	}

	public void setInnerMatches(List<RssInnerMatch> innerMatches) {
		this.innerMatches = innerMatches;
	}

	public ColumnArticleDao getColumnArticleDao() {
		return columnArticleDao;
	}

	public void setColumnArticleDao(ColumnArticleDao columnArticleDao) {
		this.columnArticleDao = columnArticleDao;
	}
}
