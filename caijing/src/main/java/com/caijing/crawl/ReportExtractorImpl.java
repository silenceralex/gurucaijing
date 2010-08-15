package com.caijing.crawl;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationContext;

import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.ibatis.RecommendStockDaoImpl;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;
import com.caijing.util.Config;
import com.caijing.util.ContextFactory;
import com.caijing.util.FileUtil;
import com.caijing.util.ServerUtil;

public class ReportExtractorImpl implements ReportExtractor {

	private Pattern stockPattern = Pattern.compile(
			"(.*?)--(.*?)\\((((002|000|300|600)[\\d]{3})|60[\\d]{4})\\)",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
	private Config config = null;

	public RecommendStock extractFromFile(String saname, String stockcode,
			String file, String rid) {

		// System.out.println("publishdate:"
		// + config.getValue(key).get("publishdate"));
		Pattern publishDatePattern = Pattern.compile((String) config.getValue(
				saname).get("publishdate"), Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.UNIX_LINES);
		// System.out.println("eps:" + config.getValue(key).get("eps"));

		Pattern objectprice = Pattern.compile((String) config.getValue(saname)
				.get("objectprice"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL
				| Pattern.UNIX_LINES);
		// System.out.println("eps:" + config.getValue(key).get("analyzer"));
		Pattern anaylzerPattern = Pattern.compile((String) config.getValue(
				saname).get("analyzer"), Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.UNIX_LINES);
		// System.out.println("grade:" + config.getValue(key).get("grade"));
		Pattern grade = Pattern.compile((String) config.getValue(saname).get(
				"grade"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL
				| Pattern.UNIX_LINES);
		RecommendStock rs = new RecommendStock();
		rs.setRecommendid(rid);
		rs.setSaname(saname);
		rs.setStockcode(stockcode);
		String content = FileUtil.read(file, "GBK");
		// System.out.println("content:" + content);
		Matcher m = publishDatePattern.matcher(content);
		if (m != null && m.find()) {
			String month = m.group(2);
			String day = m.group(3);
			month = month.trim().length() > 1 ? month.trim() : "0"
					+ month.trim();
			day = day.trim().length() > 1 ? day.trim() : "0" + day.trim();
			System.out
					.println("publishdate:" + m.group(1).trim() + month + day);
			rs.setCreatedate(m.group(1).trim() + month + day);
		}
		m = anaylzerPattern.matcher(content);
		if (m != null && m.find()) {
			System.out.println("anaylzer:" + m.group(1).trim());
			//处理非正常的前缀
			if (m.group(1).trim().length() > 10) {
				String[] tests=m.group(1).trim().split("\\s");
				String aname="";
				for(String tmp : tests){
					if(tmp.trim().length()!=0&&tmp.trim().length()<4){
						aname+=tmp.trim()+" ";
					}
				}
				rs.setAname(aname.trim());
				System.out.println("after anaylzer:" + aname.trim());
			} else {
				rs.setAname(m.group(1).trim());
			}
		}
		m = objectprice.matcher(content);
		if (m != null && m.find()) {
			System.out.println("objectprice:" + m.group(1));
			rs.setObjectprice(Float.parseFloat(m.group(1)));
		}
		m = grade.matcher(content);
		if (m != null && m.find()) {
			System.out.println("grade:" + m.group(1));
			if (m.group(1).trim().length() > 6) {
				rs.setGrade("");
			} else {
				rs.setGrade(m.group(1).trim());
			}
		}

		List<String> strs = (List<String>) config.getValue(saname).get("eps");
//		System.out.println("str size:" + strs.size());
		Pattern epsPattern = null;
		String eps = null;
		for (String str : strs) {
//			System.out.println("str:" + str);
			epsPattern = Pattern.compile(str, Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL | Pattern.MULTILINE);
			m = epsPattern.matcher(content);
			if (m != null && m.find()) {
				System.out.println("2010:" + m.group(1).trim());
				System.out.println("2011:" + m.group(2).trim());
				System.out.println("2012:" + m.group(3).trim());
				eps = "{'2010':'" + m.group(1) + "','2011':'" + m.group(2)
						+ "','2012':'" + m.group(3) + "'}";
				break;
			}
		}
		if (eps == null) {
			System.out.println("Connot match the EPS!");
		} else {
			rs.setEps(eps);
		}
		return rs;
	}

	public Report extractFromTitle(String file, String rid) {

		Matcher m = stockPattern.matcher(new File(file).getName());
		Report report = new Report();
		report.setRid(rid);
		if (m != null && m.find()) {
			String sanam = m.group(1);
			String stockname = m.group(2);
			String stockcode = m.group(3);
			System.out.println("sanam:" + sanam);
			System.out.println("stockname:" + stockname);
			System.out.println("stockcode:" + stockcode);
			report.setSaname(sanam);
			report.setStockcode(stockcode);
			report.setStockname(stockname);
			report.setType(1);
			report.setTitle(file.substring(file.lastIndexOf('/') + 1, file
					.lastIndexOf('.')));
			return report;
		} else {
			String title = file.substring(file.lastIndexOf('/') + 1, file
					.lastIndexOf('.'));
			String[] strs = title.split("--");
			String sanam = strs[0];
			report.setSaname(sanam);
			report.setTitle(title);
			// 晨会
			if (strs[1].length() == 4) {
				report.setType(0);
			} else if (strs[1].length() > 4 && strs[1].contains("宏观")) {
				report.setType(3);
			} else if (strs[1].length() > 4 && strs[1].contains("业")) {
				report.setType(2);
			} else {
				report.setType(4);
			}
			return report;
		}

	}

	public static void main(String[] args) {
		ReportExtractorImpl extractor = new ReportExtractorImpl();
		ApplicationContext context = ContextFactory.getApplicationContext();
		Config config = (Config) context.getBean("config");
		extractor.setConfig(config);
		// extractor.extractFromFile("安信证券",
		// "F:\\email\\研究报告7.07\\安信证券--广汇股份(600256)参与气化南疆，履行社会责任.txt",
		// ServerUtil.getid());
		RecommendStockDao recommendStockDao = (RecommendStockDaoImpl) ContextFactory
				.getBean("recommendStockDao");
		RecommendStock rs = extractor.extractFromFile("国泰君安", "民生银行",
				"http://guru.caijing.com/papers/20100812/6D0SD1NH.txt",
				ServerUtil.getid());

		// RecommendStock rs
		// =extractor.extractFromFile("海通证券","000987","F:\\email\\研究报告7.07\\海通证券--广州友谊(000987)经营趋势良好，重现投资安全边际.txt",ServerUtil.getid());

		// extractor.extractFromFile("申银万国","F:\\email\\研究报告7.07\\6CR4CDUO.txt",ServerUtil.getid());
		// extractor.extractFromFile("中金公司","","F:\\email\\研究报告7.07\\6CR4CVCJ.txt",ServerUtil.getid());
		// recommendStockDao.insert(rs);
		// File file = new
		// File("F:\\email\\研究报告7.19\\国泰君安--重庆百货(600729)二季度业绩增长30％，释放充分符合预期.txt");
		// extractor
		// .extractFromFile(
		// "F:\\email\\研究报告7.19\\国信证券--凯迪电力(000939)2010年中报及重大事项点评：中报良好，大股东拟再次倾力扶持上市公司投身生物质发电.pdf",
		// "F:\\email\\papers\\研究报告7.19\\国信证券--圣农发展(002299)10年中期财报点评：天灾过后或现真机会.txt");
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
}
