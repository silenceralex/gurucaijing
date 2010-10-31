package com.caijing.spider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.sleepycat.je.DatabaseException;

/**
 * ͨ�õİ�������ҳ�����Ŀץȡ���񣬿���ͨ��BuildProperty�Ľӿ������������ֶεĽ������磬video_url��
 * ��ˣ�set��ͬ��bpInterface���Դ���ͬ���ӵİ��ա�����ҳ�桱��ʽ��ץȡ������
 * 
 * @author jun-chen
 * 
 */
public class UniversalJob {
	// ����pattern����htmlȥ�룬ֻ��rangePattern�������ݽ���
	private Pattern rangePattern = null;
	// ���ھ������Խ�����InnerMatch
	private List<InnerMatch> innerMatches = new ArrayList<InnerMatch>();
	private String source = "";
	private String charset = "gb2312";
	private String urlDbPath = "/home/jun-chen/urldb/default";
	private Pattern pagexp = null;
	private int threads = 5;
	//	private List<Column> jobs = new ArrayList<Column>();

	Extractor bpInterface = null;
	private static Logger logger = Logger.getLogger(UniversalJob.class);

	public UniversalJob() {
	}

	/**
	 * �������ļ��ж�ȡץȡ���� ��ͬ��xml������������ͬ����ΪbpInterface�Ĳ�ͬ������video_url������ʽҲ��ͬ
	 * 
	 * @param xmlPath
	 *            �����ļ�·��
	 */
	public void readFromXML(String xmlPath) {

		SAXReader sr = new SAXReader();
		Document xml = null;
		try {
			xml = sr.read(new File(xmlPath));
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		Element root = (Element) xml.selectSingleNode("//jobs");
		charset = root.attributeValue("charset");
		bpInterface.setDownEncode(charset);
		urlDbPath = root.attributeValue("urldb");
		threads = Integer.parseInt(root.attributeValue("threads"));
		String range = root.attributeValue("rangePattern");
		if (range != null) {
			rangePattern = Pattern.compile(range, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		}
		source = root.attributeValue("source");
		System.out.println("source:" + source);
		Element patternNode = (Element) root.selectSingleNode("pattern");
		String reg = patternNode.attributeValue("regexp");
		if (reg != null) {
			pagexp = Pattern.compile(reg, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		}
		List<Element> linkIMNodes = patternNode.selectNodes("innermatch");
		for (Element imNode : linkIMNodes) {
			InnerMatch im = ConfigReader.createInnerMatch(imNode);
			innerMatches.add(im);
			System.out.println(im.getProperty() + " " + im.getRegexp().toString());
		}

		List<Element> jobNodes = root.selectNodes("job");
		//		for (Element jobNode : jobNodes) {
		////			Column col = new Column();
		//			col.setTag(jobNode.selectSingleNode("tag").getText().trim());
		//			col.setName(jobNode.selectSingleNode("column").getText().trim());
		//			col.setStartUrl(jobNode.selectSingleNode("start").getText().trim());
		//			col.setOnly(jobNode.selectSingleNode("only").getText().trim());
		//			col.setColumn_id(jobNode.selectSingleNode("column_id").getText().trim());
		////			int cid = db.getCidByColumnName(col.getName());
		////			System.out.println("Column name:" + col.getName());
		//			System.out.println("Column cid:" + cid);
		//			// �����Ŀ�����ڣ��򴴽���Ŀ
		//			if (cid == -1) {
		//				int code = db.insertColumn(col.getName(), source, col.getStartUrl(), col.getColumn_id());
		//				cid = db.getCidByColumnName(col.getName());
		//				System.out.println("insert column return code: " + code);
		//			}
		//			col.setCid(cid);
		//			jobs.add(col);
		//		}
	}

	/**
	 * ���ĵ�ץȡ����������Ϊ�����׶Σ� 1. �ɶ��̵߳�LinkParser����linkҳ��pageҳ����ȡ 2.
	 * �ɵ�һ����ȡ������pageUrl���б����pageParsers�Ķ��߳̽���
	 */
	public void crawl() {
		List<LinkParser> parsers = new ArrayList<LinkParser>();
		Map<Integer, Vector<String>> cidUrlMap = new HashMap<Integer, Vector<String>>();
		//		for (Column job : jobs) {
		// LinkJobThread t = new LinkJobThread();
		LinkParser parser = new LinkParser();
		String startUrl = "";
		parser.getLinkUrls().add(startUrl);
		Vector<String> pageUrls = new Vector<String>();
		parser.setPageUrls(pageUrls);
		//		cidUrlMap.put(job.getCid(), pageUrls);
		//		Pattern only = Pattern.compile(job.getOnly(), Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		//		t.getOnlys().add(only);
		parser.setPagexp(pagexp);
		parser.setRangePattern(rangePattern);
		parser.setDownEncode(charset);
		parsers.add(parser);
		parser.start();
		//		}

		long startTime = System.currentTimeMillis();
		while (true) {
			boolean allDie = true;
			for (LinkParser t : parsers) {
				if (!t.isFinished()) {
					allDie = false;
					long[] s = t.getStatus();
					long crawledPages = s[0];
					long crawledVideos = s[1];
					long bytes = s[2];
					long timeElpased = (System.currentTimeMillis() - startTime);
					double speed = timeElpased == 0 ? 0 : crawledVideos * 1.0 / timeElpased;
					double dlSpeed = timeElpased == 0 ? 0 : bytes * 1.0 / timeElpased;
					System.out.println("Thread name:" + t.getName() + "  OnWorking:" + t.getCurUrl());
					logger.debug("Thread name:" + t.getName() + "  OnWorking:" + t.getCurUrl());
					System.out.println("Crawled links:" + crawledPages + "\tCrawled pages:" + crawledVideos
							+ "\tSpeed:" + (speed * 1000) + " pps\tTime Used:" + (timeElpased / 1000)
							+ "s\tDownload Speed: " + (dlSpeed) + "KBPS");
					logger.debug("Crawled links:" + crawledPages + "\tCrawled pages:" + crawledVideos + "\tSpeed:"
							+ (speed * 1000) + " pps\tTime Used:" + (timeElpased / 1000) + "s\tDownload Speed: "
							+ (dlSpeed) + "KBPS");
				} else {
					logger.debug("Thread name:" + t.getName() + " is finished!\n");
					System.out.println("Thread name:" + t.getName() + " is finished!\n");
				}
			}
			if (allDie) {
				logger.debug("All links have already downloaded!");
				System.out.println("All links have already downloaded!");
				break;
			}
			try {
				System.out.println("#########################################");
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		List<PageParser> pageParsers = new ArrayList<PageParser>();
		//		for (Column job : jobs) {
		//		Vector<String> pages = cidUrlMap.get(job.getCid());
		//		System.out.println("CID: " + job.getCid() + "  download " + pages.size() + " pages!");
		PageParser p = new PageParser();
		//		p.setCol(job);
		p.setInnerMatches(innerMatches);
		p.setDownEncode(charset);
		p.setPageUrls(pageUrls);
		//		p.setBpInterface(bpInterface);
		try {
			p.getUrlDB().setup(urlDbPath, false);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		pageParsers.add(p);
		p.start();
		//		}
		startTime = System.currentTimeMillis();
		while (true) {
			boolean allDie = true;
			for (PageParser t : pageParsers) {
				if (!t.isFinished()) {
					allDie = false;
					long[] s = t.getStatus();
					long crawledPages = s[0];
					long crawledVideos = s[1];
					long bytes = s[2];
					long timeElpased = (System.currentTimeMillis() - startTime);
					double speed = timeElpased == 0 ? 0 : crawledVideos * 1.0 / timeElpased;
					double dlSpeed = timeElpased == 0 ? 0 : bytes * 1.0 / timeElpased;
					System.out.println("Thread name:" + t.getName() + "  OnWorking:" + t.getCurUrl());
					logger.debug("Thread name:" + t.getName() + "  OnWorking:" + t.getCurUrl());
					System.out.println("ToCrawl pages:" + crawledPages + "\tCrawled Videos:" + crawledVideos
							+ "\tSpeed:" + (speed * 1000) + " pps\tTime Used:" + (timeElpased / 1000)
							+ "s\tDownload Speed: " + (dlSpeed) + "KBPS");
					logger.debug("ToCrawl pages:" + crawledPages + "\tCrawled Videos:" + crawledVideos + "\tSpeed:"
							+ (speed * 1000) + " pps\tTime Used:" + (timeElpased / 1000) + "s\tDownload Speed: "
							+ (dlSpeed) + "KBPS");

				} else {
					logger.debug("Thread name:" + t.getName() + " is finished!\n");
					System.out.println("Thread name:" + t.getName() + " is finished!\n");
				}
			}
			if (allDie) {
				logger.debug("All feeds have already downloaded!");
				System.out.println("All feeds have already downloaded!");
				break;
			}
			try {
				System.out.println("#########################################");
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		UniversalJob uj = new UniversalJob();
		// uj.readFromXML("jobs\\chongqingTV.xml");
		// uj.readFromXML("jobs\\ningxiaTV.xml");
		// uj.readFromXML("jobs\\chongqingTV.xml");
		uj.readFromXML(args[0]);
		uj.crawl();
	}

	public Extractor getBpInterface() {
		return bpInterface;
	}

	public void setBpInterface(Extractor bpInterface) {
		this.bpInterface = bpInterface;
	}
}
