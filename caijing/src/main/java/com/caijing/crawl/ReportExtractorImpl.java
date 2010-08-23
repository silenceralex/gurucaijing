package com.caijing.crawl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationContext;

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
	Pattern numberPattern = Pattern.compile("[0-9\\.]+",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
	Pattern characterPattern = Pattern.compile(
			"^([\\u4e00-\\u9fa5\\u9d84\\s\\?]+)[0-9AS\\s]+\\n",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
	private Config config = null;

	public RecommendStock extractFromFile(Report report, String file) {

		Pattern publishDatePattern = Pattern.compile((String) config.getValue(
				report.getSaname()).get("publishdate"),
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		Pattern objectprice = Pattern.compile((String) config.getValue(
				report.getSaname()).get("objectprice"),
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		// System.out.println("eps:" + config.getValue(key).get("analyzer"));

		Pattern grade = Pattern.compile((String) config.getValue(
				report.getSaname()).get("grade"), Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.UNIX_LINES);
		int num = 0;
		RecommendStock rs = new RecommendStock();
		rs.setRecommendid(ServerUtil.getid());
		rs.setSaname(report.getSaname());
		rs.setStockcode(report.getStockcode());
		rs.setStockname(report.getStockname());
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
			num++;
		}

		String analyzer = fetchAnaylzer(report.getSaname(), content);
		if (analyzer != null) {
			rs.setAname(analyzer);
			num++;
		} else {
			rs.setAname("N/A");
		}
		m = objectprice.matcher(content);
		if (m != null && m.find()) {
			System.out.println("objectprice:" + m.group(1));
			rs.setObjectprice(Float.parseFloat(m.group(1)));
			num++;
		}
		m = grade.matcher(content);
		if (m != null && m.find()) {
			// System.out.println("grade:" + m.group(1).trim());
			if (m.group(1).trim().length() > 6) {
				String[] strs = m.group(1).trim().split("\\n");
				if (strs.length == 2 && strs[1].trim().length() < 5) {
					rs.setGrade(strs[1].trim());
					System.out.println("grade:" + strs[1]);
					num++;
				} else {
					rs.setGrade("");
				}
			} else {
				rs.setGrade(m.group(1).trim());
				System.out.println("grade:" + m.group(1).trim());
				num++;
			}

		}
		String eps = fetchEPS(report.getSaname(), content);
		if (eps == null) {
			System.out.println("Connot match the EPS!");
		} else {
			rs.setEps(eps);
			num++;
		}
		System.out.println("Extractnum: " + num);
		rs.setExtractnum(num);
		return rs;
	}

	private String fetchAnaylzer(String saname, String content) {
		Pattern analyzerPattern = null;
		Matcher m = null;
		String analyzer = null;
		if (config.getValue(saname).get("analyzerEps") != null) {
			String str = (String) config.getValue(saname).get("analyzerEps");
			// System.out.println("analyzerEps: " + str);
			analyzerPattern = Pattern.compile(str, Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL | Pattern.MULTILINE);
			m = analyzerPattern.matcher(content);
			if (m != null && m.find()) {
				String analyzers = m.group(1);
				analyzer = "";
				// System.out.println("analyzers: " + analyzers);
				m = characterPattern.matcher(analyzers);
				int threshold = 0;
				while (m != null && m.find() && threshold < 3) {
					// System.out.println("m.group(1).trim(): "
					// +m.group(1).trim());
					analyzer += m.group(1).trim() + " ";
					threshold++;
				}
				analyzer = analyzer.trim();
			}
			System.out.println("analyzer: " + analyzer);
		}
		if (analyzer == null) {
			String str = (String) config.getValue(saname).get("analyzer");
			// System.out.println("anaylzer:" + str);
			analyzerPattern = Pattern.compile(str, Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL | Pattern.MULTILINE);
			m = analyzerPattern.matcher(content);
			if (m != null && m.find()) {
				System.out.println("anaylzer:" + m.group(1).trim());
				// �����������ǰ׺
				if (m.group(1).trim().length() > 10) {
					String[] tests = m.group(1).trim().split("\\s");
					String aname = "";
					for (String tmp : tests) {
						if (tmp.trim().length() != 0 && tmp.trim().length() < 4) {
							aname += tmp.trim() + " ";
						}
					}
					analyzer = aname.trim();
					System.out.println("after anaylzer:" + aname.trim());
				} else {
					System.out.println("after anaylzer:" + m.group(1).trim());
					analyzer = m.group(1).trim();
				}
			} else {
				System.out.println("after anaylzer no match:");
			}
		}
		return analyzer;
	}

	private String fetchEPS(String saname, String content) {
		Pattern epsPattern = null;
		Matcher m = null;
		String eps = null;
		if (config.getValue(saname).get("specialEps") != null) {
			String str = (String) config.getValue(saname).get("specialEps");
			epsPattern = Pattern.compile(str, Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL | Pattern.UNIX_LINES);
			m = epsPattern.matcher(content);
			while (m != null && m.find()) {
				String epss = m.group(1);
				System.out.println("epss: " + epss);
				Matcher m2 = numberPattern.matcher(epss);
				ArrayList<String> epslist = new ArrayList<String>();
				while (m2 != null && m2.find()) {
					epslist.add(m2.group().trim());
				}
				System.out.println("epslist size:" + epslist.size());
				if (epslist.size() == 2) {
					eps = "{'2010':'" + epslist.get(0) + "','2011':'"
							+ epslist.get(1) + "','2012':'n/a'}";
					break;
				} else if (epslist.size() >= 3) {
					eps = "{'2010':'" + epslist.get(epslist.size() - 3)
							+ "','2011':'" + epslist.get(epslist.size() - 2)
							+ "','2012':'" + epslist.get(epslist.size() - 1)
							+ "'}";
					break;
				}
			}
			System.out.println("eps:" + eps);
		}
		if (eps == null) {
			List<String> strs = (List<String>) config.getValue(saname).get(
					"eps");
			// System.out.println("str size:" + strs.size());

			for (String str : strs) {
				// System.out.println("str:" + str);
				epsPattern = Pattern.compile(str, Pattern.CASE_INSENSITIVE
						| Pattern.DOTALL | Pattern.UNIX_LINES);
				m = epsPattern.matcher(content);
				if (m != null && m.find()) {
					System.out.println("Group count:" + m.groupCount());
					if (m.groupCount() == 1) {
						System.out.println("2010:" + m.group(1).trim());
						eps = "{'2010':'" + m.group(1)
								+ "','2011':'n/a','2012':'n/a'}";
					} else if (m.groupCount() == 2) {
						System.out.println("2010:" + m.group(1).trim());
						System.out.println("2011:" + m.group(2).trim());
						eps = "{'2010':'" + m.group(1) + "','2011':'"
								+ m.group(2) + "','2012':'n/a'}";
					} else {
						System.out.println("2010:" + m.group(1).trim());
						System.out.println("2011:" + m.group(2).trim());
						System.out.println("2012:" + m.group(3).trim());
						eps = "{'2010':'" + m.group(1) + "','2011':'"
								+ m.group(2) + "','2012':'" + m.group(3) + "'}";
					}
					break;
				}
			}
		}
		return eps;
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
			// ����
			if (strs[1].length() == 4) {
				report.setType(0);
			} else if (strs[1].length() > 4 && strs[1].contains("���")) {
				report.setType(3);
			} else if (strs[1].length() > 4 && strs[1].contains("ҵ")) {
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
		// extractor.extractFromFile("����֤ȯ",
		// "F:\\email\\�о�����7.07\\����֤ȯ--���ɷ�(600256)���������Ͻ��������������.txt",
		// ServerUtil.getid());
		// RecommendStockDao recommendStockDao = (RecommendStockDaoImpl)
		// ContextFactory
		// .getBean("recommendStockDao");
		// RecommendStock rs = extractor.extractFromFile("��̩����", "�й�ƽ��",
		// "http://guru.caijing.com/papers/20100818/6DFFKFR8.txt",
		// ServerUtil.getid());
		Report report = new Report();
		report.setSaname("�������");

		RecommendStock rs = extractor.extractFromFile(report,
		// "http://guru.caijing.com/papers/20100818/6DFFM141.txt",
				"http://guru.caijing.com/papers/20100805/6CLPSVML.txt");
		// "http://guru.caijing.com/papers/20100819/6DMNA4E8.txt");
		// "http://guru.caijing.com/papers/20100813/6DAKKVV0.txt");
		// "http://guru.caijing.com/papers/20100819/6DMNAMVB.txt");
		// "http://guru.caijing.com/papers/20100819/6DMNA20A.txt");

		// extractor.extractFromFile("�н�˾", "��ɽ����",
		// "http://guru.caijing.com/papers/20100818/6DFFK8O1.txt",
		// "http://guru.caijing.com/papers/20100818/6DFFK6F7.txt",
		// "http://guru.caijing.com/papers/20100818/6DFFKM04.txt",
		// "http://guru.caijing.com/papers/20100818/6DFFKP85.txt",
		// "http://guru.caijing.com/papers/20100818/6DFFKD65.txt",
		// ServerUtil.getid());

		// RecommendStock rs
		// =extractor.extractFromFile("��֤ͨȯ","000987","F:\\email\\�о�����7.07\\��֤ͨȯ--��������(000987)��Ӫ�������ã�����Ͷ�ʰ�ȫ�߼�.txt",ServerUtil.getid());

		// extractor.extractFromFile("�������","F:\\email\\�о�����7.07\\6CR4CDUO.txt",ServerUtil.getid());
		// extractor.extractFromFile("�н�˾","","F:\\email\\�о�����7.07\\6CR4CVCJ.txt",ServerUtil.getid());
		// recommendStockDao.insert(rs);
		// File file = new
		// File("F:\\email\\�о�����7.19\\��̩����--����ٻ�(600729)������ҵ������30�����ͷų�ַ���Ԥ��.txt");
		// extractor
		// .extractFromFile(
		// "F:\\email\\�о�����7.19\\����֤ȯ--���ϵ���(000939)2010���б����ش�����������б����ã���ɶ����ٴ������������й�˾Ͷ�������ʷ���.pdf",
		// "F:\\email\\papers\\�о�����7.19\\����֤ȯ--ʥũ��չ(002299)10�����ڲƱ����������ֹ�����������.txt");

		// char hi='��';
		// char hi = '��';

		// String str="����ʦ \n���� A0230208100457 \nluojing@swsresearch.com \n\nӦ����
		// \nyingzz@swsresearch.com \n \n��ϵ�� ";
		// ����ÿ������ 1.70 1.55 2.01 1.40 1.80
		// 2.20 ����ÿ������ 1.70 1.55 2.01 1.40 1.80 2.20
		// ȫ��̯��ÿ������ 0.71 0.74 1.00 1.40 1.80 2.20

		// String str = "�����ϵ���˾ 2010-2012��ÿ�������� 0.65Ԫ��0.80Ԫ��0.96Ԫ��ԭԤ\n��Ϊ 0.59
		// Ԫ��0.74 Ԫ��0.93 Ԫ��";
//		String str = FileUtil.read(
//				"http://guru.caijing.com/papers/20100805/6CLPSVML.txt", "GBK");
//		System.out.println("stri:!" + str);
//		Pattern publishDatePattern = Pattern
//				.compile(
//						"(?:ÿ��[ӯ��������Ϊ\\s]{2,4}|(?:EPS))(?:[\\s�ֱ�]{1,2}Ϊ)?��?(?:��[\u4e00-\u9fa5]+��)?([0-9\\.\\s/�͡�Ԫ]{5,})[,������\n��]",
//						Pattern.CASE_INSENSITIVE | Pattern.DOTALL
//								| Pattern.UNIX_LINES);
		// Pattern publishDatePattern = Pattern
		// .compile("2010E\\s+-?[0-9,\\.]+\\s+-?[0-9,\\.]+\\s+-?[0-9,\\.]+\\s+-?[0-9,\\.]+\\s+([0-9\\.]+)\\s.*?2011E\\s+-?[0-9,\\.]+\\s+-?[0-9,\\.]+\\s+-?[0-9,\\.]+\\s+-?[0-9,\\.]+\\s+([0-9\\.]+)\\s.*?2012E\\s+[0-9,\\.]+\\s+-?[0-9,\\.]+\\s+-?[0-9,\\.]+\\s+-?[0-9,\\.]+\\s+([0-9\\.]+)\\s",
		// Pattern.CASE_INSENSITIVE | Pattern.DOTALL
		// | Pattern.UNIX_LINES);
//		Matcher m = publishDatePattern.matcher("" + str);
//		// System.out.println("hi:!" + Integer.toHexString((int) hi));
//		while (m != null && m.find()) {
//			System.out.println("groupCount: !" + m.groupCount());
//			String ananlyzer = m.group(1);
//			// System.out.println("ananlyzer: !" + ananlyzer +
//			// m.group(2)+m.group(3));
//			System.out.println("ananlyzer: !" + ananlyzer);
//			System.out.println("Matcher!");
//		}
//		if (m == null || !m.find()) {
//			System.out.println("Not Matcher!");
//		}
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
}
