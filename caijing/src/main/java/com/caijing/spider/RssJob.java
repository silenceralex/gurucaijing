package com.caijing.spider;

import java.io.File;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.caijing.remote.CmsWebservice;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.caijing.util.MD5Utils;
import com.caijing.util.UrlDownload;

public class RssJob {

	private BerkeleyDB urlDB = null;

	private SimpleDateFormat sdf = null;

	private String startPage = null;
	private String root = null;
	private String list = null;
	private String src = null;
	private long columnid = 0;
	private String charset = "GBK";
	private int maxConnections = 5;
	private int threads = 5;
	private UrlDownload urldown = null;
	private List<RssInnerMatch> innerMatches = null;

	private List<String> startUrls = new ArrayList<String>();
	private int type = 0;

	@Autowired
	@Qualifier("columnArticleDao")
	private ColumnArticleDao columnArticleDao = null;

	public void run() {
		SAXReader sr = new SAXReader();
		Document xml = null;
		try {
			for (String startUrl : startUrls) {
				String rss = urldown.load(startUrl);
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
					//改用标题+作者进行去重
					String md5 = MD5Utils.hash(article.getTitle() + article.getAuthor());
					if (!urlDB.contains(md5)) {
						article.setAid(MD5Utils.hash(article.getLink()));
						article.setType(type);
						columnArticleDao.insert(article);
						urlDB.putUrl(md5);
						long articleid = CmsWebservice.getInstance().addArticle(
								columnid != 0 ? columnid : CmsWebservice.catelogID, article.getTitle(),
								article.getAuthor(), article.getSrc(), article.getAbs(), article.getContent(),
								DateTools.transformDateDetail(article.getPtime()));
						article.setCmsid(articleid);
						columnArticleDao.update(article);
						if (CmsWebservice.getInstance().publishArticle(articleid)) {
							System.out.println("publish article:" + article.getTitle() + "  articleid:" + articleid
									+ " success!");
						} else {
							System.out.println("publish article:" + article.getTitle() + "  articleid:" + articleid
									+ " failed!");
						}
						System.out.println("author: " + article.getAuthor());
						System.out.println("title: " + article.getTitle());
						System.out.println("pubDate: " + article.getPtime());
					}

					//					System.out.println("abs: " + article.getAbs());
					//					System.out.println("link: " + article.getLink());
					//					System.out.println("content: " + article.getContent());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printUsage() {
		System.out.println("USAGE:\t#>./RssJob JOBXML");
		System.out.println("\t\t-JOBXML\t\t\tJob Descriptive File in XML format");
	}

	public static void main(String[] args) {

		//		if (args.length == 0) {
		//			printUsage();
		//			return;
		//		}
		SAXReader sr = new SAXReader();
		Document xml = null;
		try {
			//			xml = sr.read(new File("jobs\\163_huigensi.xml"));
			System.out.println("Input xml : " + args[0]);
			xml = sr.read(new File(args[0]));
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		ColumnArticleDao columnArticleDao = (ColumnArticleDao) ContextFactory.getBean("columnArticleDao");
		RssJob rssjob = ConfigReader.getRssJobFromXML(xml);
		rssjob.setColumnArticleDao(columnArticleDao);
		rssjob.run();
		//		List<ColumnArticle> articles = columnArticleDao.getColumnArticleBySource("《财经网》-专栏作家");
		//		for (ColumnArticle article : articles) {
		//			long articleid = CmsWebservice.getInstance().addArticle(CmsWebservice.catelogID, article.getTitle(),
		//					article.getAuthor(), article.getSrc(), article.getAbs(), article.getContent(),
		//					DateTools.transformDateDetail(article.getPtime()));
		//			article.setCmsid(articleid);
		//			columnArticleDao.update(article);
		//			if (CmsWebservice.getInstance().publishArticle(articleid)) {
		//				System.out.println("publish article:" + article.getTitle() + " success!");
		//			} else {
		//				System.out.println("publish article:" + article.getTitle() + " failed!");
		//			}
		//		}
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

	public List<String> getStartUrls() {
		return startUrls;
	}

	public void setStartUrls(List<String> startUrls) {
		this.startUrls = startUrls;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getColumnid() {
		return columnid;
	}

	public void setColumnid(long columnid) {
		this.columnid = columnid;
	}
}
