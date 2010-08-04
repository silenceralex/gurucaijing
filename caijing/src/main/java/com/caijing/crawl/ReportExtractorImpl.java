package com.caijing.crawl;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.caijing.domain.Report;
import com.caijing.util.ServerUtil;

public class ReportExtractorImpl implements ReportExtractor {
	private static Pattern publishDatePattern = Pattern.compile(
			"(2010�� \\d{1,2}�� [0-3]\\d��)", Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL | Pattern.UNIX_LINES);
	
	// 2010��\\s-2012�� EPS�ֱ�Ϊ 0.71Ԫ/0.68Ԫ/1Ԫ��0.71Ԫ/0.68Ԫ/1Ԫ��
	private static Pattern epsPattern = Pattern.compile(
			"EPS�ֱ�Ϊ ([0-9\\.]+)[��Ԫ/\\s]+([0-9\\.]+)[��Ԫ/\\s]+([0-9\\.]+)Ԫ��",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
	private static Pattern anaylzerPattern = Pattern.compile("����ʦ��(.*?)\\s",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
	private static Pattern stockPattern = Pattern.compile(
			"(.*?)--(.*?)\\((((002|000|300|600)[\\d]{3})|60[\\d]{4})\\)",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);

	// private PDFReader pdfreader = new PDFReader();

	public Report extractFromFile(String file, String rid,String outpath) {
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
			report.setTitle(file.substring(file.lastIndexOf('/')+1,file.lastIndexOf('.')));
		} else {
			return null;
		}
		// File pdffile = new File(file);
		// String outpath = pdffile.getParentFile().getAbsolutePath() + "/" +
		// ServerUtil.getid() + ".txt";
		// System.out.println("outpath:" + outpath);
		// pdfreader.readFdf(file, outpath);
		// String content = FileUtil.read(outpath);
		// System.out.println("content:" + content);
		// m = publishDatePattern.matcher(content);
		// if (m != null && m.find()) {
		// System.out.println("publishDate:" + m.group(1));
		// }
		// m = anaylzerPattern.matcher(content);
		// if (m != null && m.find()) {
		// System.out.println("anaylzer:" + m.group(1));
		// }
		// m = epsPattern.matcher(content);
		// if (m != null && m.find()) {
		// System.out.println("2010:" + m.group(1));
		// System.out.println("2011:" + m.group(2));
		// System.out.println("2012:" + m.group(3));
		// } else {
		// System.out.println("out!");
		// }
		// }
		return report;
	}

	public static void main(String[] args) {
		ReportExtractor extractor = new ReportExtractorImpl();
		// File file = new
		// File("F:\\email\\�о�����7.19\\��̩����--����ٻ�(600729)������ҵ������30�����ͷų�ַ���Ԥ��.txt");
//		extractor
//				.extractFromFile(
//						"F:\\email\\�о�����7.19\\����֤ȯ--���ϵ���(000939)2010���б����ش�����������б����ã���ɶ����ٴ������������й�˾Ͷ�������ʷ���.pdf",
//						"F:\\email\\papers\\�о�����7.19\\����֤ȯ--ʥũ��չ(002299)10�����ڲƱ����������ֹ�����������.txt");
	}
}
