package com.caijing.crawl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.caijing.cache.BerkeleyDB;
import com.caijing.domain.Post;
import com.caijing.util.Config;
import com.caijing.util.FileUtil;

@Component("listExtractor")
public class ListExtractor implements Extractor {
	private static Pattern stockPattern = Pattern.compile(
			"(((002|000|300|600)[\\d]{3})|60[\\d]{4})",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
	private static Pattern rangePattern = Pattern
			.compile(
					"<table width=\"96%\".*?bgcolor=\"#D7E4EA\" style=\"margin:7px;\">(.*?)<div class=\"lt5\">",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL
							| Pattern.UNIX_LINES);

	private static Pattern titlePattern = Pattern.compile(
			"<div class=\"lt2\">(.*?)</div>", Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL | Pattern.UNIX_LINES);

	private static final String Path = "F:/caijing/";

	private static Pattern datePattern = Pattern.compile(
			"<div class=\"lt3\".*?>(.*?)</div>", Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL | Pattern.UNIX_LINES);

	private static Pattern contentPattern = Pattern.compile(
			"<div class=\"lt4\".*?>(.*?)</font>", Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL | Pattern.UNIX_LINES);

	private static Pattern nickPattern = Pattern.compile(
			"id=\"[0-9]+_nick\">(.*?)</div>", Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL | Pattern.UNIX_LINES);

	private Pattern totolnumberPattern = Pattern.compile(
			"总:<strong><font color='red'>(.*?)</font></strong>条",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);

	private Log logger = LogFactory.getLog(ListExtractor.class);

	private BerkeleyDB bdb = null;

	private Config config = null;

	public List<Post> extractFromHtml(String content, String groupID) {
		Matcher m = rangePattern.matcher(content);
		List<Post> posts = new ArrayList<Post>();
		if (m != null) {
			while (m.find()) {
				String inner = m.group(1);
				Post post = new Post();
				// System.out.println("inner: " + inner);

				Matcher m4 = nickPattern.matcher(inner);
				if (m4 != null && m4.find()) {
					String nick = m4.group(1).replaceAll("\\s", "").replaceAll(
							"<.*?>", "").trim();
					post.setNick(nick);
					if (!nick.equals(config.getValue("groupid").get(groupID))) {
						continue;
					}
					System.out.println("nickname: " + nick);
				} else
					System.out.println("##########");

				Matcher m1 = titlePattern.matcher(inner);
				if (m1 != null && m1.find()) {
					String title = m1.group(1);
					post.setTitle(title);
					System.out.println("title: " + title.trim());
				} else
					System.out.println("##########");

				String cont = "";
				Matcher m2 = contentPattern.matcher(inner);
				if (m2 != null && m2.find()) {
					cont = m2.group(1).replaceAll("<.*?>", "").trim();
					post.setContent(cont);
					// System.out.println("content: " + cont);
				} else
					System.out.println("##########");

				String date = "";
				Matcher m3 = datePattern.matcher(inner);
				if (m3 != null && m3.find()) {
					date = m3.group(1).replaceAll("<.*?>", "").trim();
					System.out.println("date: " + date);
					post.setDate(date);
				} else
					System.out.println("##########");

				String filePath = Path + groupID + "//" + date.substring(0, 10)
						+ ".txt";
				System.out.println("filePath:" + filePath);
				String key = groupID + "_" + date.substring(0, 10);
				if (bdb.get(key) == null) {
					bdb.put(key, cont);
				}
				// 提取股票
				Matcher stockm = stockPattern.matcher(content);
				// String stock="";
				StringBuffer stock = new StringBuffer();
				HashSet<String> st = new HashSet<String>();
				while (stockm != null && stockm.find()) {
					String tmp = stockm.group(1);
					if (!st.contains(tmp)) {
						stock.append(tmp + " ");
						// System.out.println("stock: " + tmp);
						st.add(tmp);
					}
				}
				key += "_stock";
				if (bdb.get(key) == null) {
					bdb.put(key, stock.toString());
				}
				FileUtil.write(filePath, cont);
				String stockPath = filePath.substring(0, filePath.indexOf('.'))
						+ "_stock" + ".txt";
				FileUtil.write(stockPath, stock.toString());
			}
		}
		return null;
	}

	public int getTotalPages(String content) {
		Matcher m = totolnumberPattern.matcher(content);
		if (m != null && m.find()) {
			return Integer.parseInt(m.group(1).trim());
		}
		return 0;
	}

	public BerkeleyDB getBdb() {
		return bdb;
	}

	public void setBdb(BerkeleyDB bdb) {
		this.bdb = bdb;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

}
