package com.caijing.crawl;

import java.io.File;
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

	// private PDFReader pdfreader = new PDFReader();

	private Pattern stockPattern = Pattern.compile(
			"(.*?)--(.*?)\\((((002|000|300|600)[\\d]{3})|60[\\d]{4})\\)",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
	private Config config = null;

	public RecommendStock extractFromFile(String key, String file, String rid) {

		// System.out.println("publishdate:"
		// + config.getValue(key).get("publishdate"));
		Pattern publishDatePattern = Pattern.compile((String) config.getValue(
				key).get("publishdate"), Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.UNIX_LINES);
		// System.out.println("eps:" + config.getValue(key).get("eps"));
		Pattern epsPattern = Pattern.compile((String) config.getValue(key).get(
				"eps"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL
				| Pattern.UNIX_LINES);
		Pattern objectprice = Pattern.compile((String) config.getValue(key)
				.get("objectprice"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL
				| Pattern.UNIX_LINES);
		// System.out.println("eps:" + config.getValue(key).get("analyzer"));
		Pattern anaylzerPattern = Pattern.compile((String) config.getValue(key)
				.get("analyzer"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL
				| Pattern.UNIX_LINES);
		// System.out.println("grade:" + config.getValue(key).get("grade"));
		Pattern grade = Pattern.compile((String) config.getValue(key).get(
				"grade"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL
				| Pattern.UNIX_LINES);
		RecommendStock rs = new RecommendStock();
		String content = FileUtil.read(file);
		// System.out.println("content:" + content);
		Matcher m = publishDatePattern.matcher(content);
		if (m != null && m.find()) {
			System.out.println("publishDate:" + m.group(1));
			rs.setCreatedate(m.group(1));
		}
		m = anaylzerPattern.matcher(content);
		if (m != null && m.find()) {
			System.out.println("anaylzer:" + m.group(1).trim());
			rs.setAid(m.group(1));
		}
		m = objectprice.matcher(content);
		if (m != null && m.find()) {
			System.out.println("objectprice:" + m.group(1));
			rs.setObjectprice(Float.parseFloat(m.group(1)));
		}
		m = grade.matcher(content);
		if (m != null && m.find()) {
			System.out.println("grade:" + m.group(1));
			rs.setGrade(m.group(1));
		}
		m = epsPattern.matcher(content);
		if (m != null && m.find()) {
			System.out.println("2010:" + m.group(1));
			System.out.println("2011:" + m.group(2));
			System.out.println("2012:" + m.group(3));
			rs.setEps(Float.parseFloat(m.group(1)));
		} else {
			System.out.println("out!");
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
			String[] strs=title.split("--");
			String sanam=strs[0];
			report.setSaname(sanam);
			report.setTitle(title);
			//����
			if(strs[1].length()==4){
				report.setType(0);
			}else if(strs[1].length()>4 && strs[1].contains("���")){
				report.setType(3);
			}else if(strs[1].length()>4 && strs[1].contains("ҵ")){
				report.setType(2);
			}else{
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
//		extractor.extractFromFile("����֤ȯ",
//				"F:\\email\\�о�����7.07\\����֤ȯ--���ɷ�(600256)���������Ͻ��������������.txt",
//				ServerUtil.getid());
//		extractor.extractFromFile("��̩����","F:\\email\\�о�����7.07\\��̩����--ɽ���ƽ�(600547)����������ۣ��ڴ���������ʽ������.txt",ServerUtil.getid());
		extractor.extractFromFile("��֤ͨȯ","F:\\email\\�о�����7.07\\��֤ͨȯ--��������(000987)��Ӫ�������ã�����Ͷ�ʰ�ȫ�߼�.txt",ServerUtil.getid());
		// File file = new
		// File("F:\\email\\�о�����7.19\\��̩����--����ٻ�(600729)������ҵ������30�����ͷų�ַ���Ԥ��.txt");
		// extractor
		// .extractFromFile(
		// "F:\\email\\�о�����7.19\\����֤ȯ--���ϵ���(000939)2010���б����ش�����������б����ã���ɶ����ٴ������������й�˾Ͷ�������ʷ���.pdf",
		// "F:\\email\\papers\\�о�����7.19\\����֤ȯ--ʥũ��չ(002299)10�����ڲƱ����������ֹ�����������.txt");
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
}
