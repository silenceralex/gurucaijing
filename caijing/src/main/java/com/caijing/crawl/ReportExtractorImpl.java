package com.caijing.crawl;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.caijing.domain.Report;
import com.caijing.util.FileUtil;

public class ReportExtractorImpl implements ReportExtractor {
	private static Pattern publishDatePattern = Pattern.compile("(2010年 \\d{1,2}月 [0-3]\\d日)", Pattern.CASE_INSENSITIVE
			| Pattern.DOTALL | Pattern.UNIX_LINES);
	//	2010年\\s-2012年 EPS分别为 0.71元/0.68元/1元。0.71元/0.68元/1元。
	private static Pattern epsPattern = Pattern.compile("EPS分别为 ([0-9\\.]+)[、元/\\s]+([0-9\\.]+)[、元/\\s]+([0-9\\.]+)元。",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
	private static Pattern anaylzerPattern = Pattern.compile("分析师：(.*?)\\s", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
			| Pattern.UNIX_LINES);
	private static Pattern stockPattern = Pattern.compile("(.*?)--(.*?)\\((((002|000|300|600)[\\d]{3})|60[\\d]{4})\\)",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);

	//	private PDFReader pdfreader = new PDFReader();

	public Report extractFromFile(String file, String outpath) {
		//		String[] strs=file.split(" ");
		//		if(strs.length==2){
		//			String agency=strs[0];
		//			String title=strs[1].replace(".txt", "");
		//			System.out.println("agency:"+agency);
		//			System.out.println("title:"+title);

		Matcher m = stockPattern.matcher(new File(file).getName());
		if (m != null && m.find()) {
			String sanam = m.group(1);
			String stockname = m.group(2);
			String stockcode = m.group(3);
			System.out.println("sanam:" + sanam);
			System.out.println("stockname:" + stockname);
			System.out.println("stockcode:" + stockcode);
		}
		//		File pdffile = new File(file);
		//		String outpath = pdffile.getParentFile().getAbsolutePath() + "/" + ServerUtil.getid() + ".txt";
		//		System.out.println("outpath:" + outpath);
		//		pdfreader.readFdf(file, outpath);
		String content = FileUtil.read(outpath);
		System.out.println("content:" + content);
		m = publishDatePattern.matcher(content);
		if (m != null && m.find()) {
			System.out.println("publishDate:" + m.group(1));
		}
		m = anaylzerPattern.matcher(content);
		if (m != null && m.find()) {
			System.out.println("anaylzer:" + m.group(1));
		}
		m = epsPattern.matcher(content);
		if (m != null && m.find()) {
			System.out.println("2010:" + m.group(1));
			System.out.println("2011:" + m.group(2));
			System.out.println("2012:" + m.group(3));
		} else {
			System.out.println("out!");
		}
		//		}
		return null;
	}

	public static void main(String[] args) {
		ReportExtractor extractor = new ReportExtractorImpl();
		//		File file = new File("F:\\email\\研究报告7.19\\国泰君安--重庆百货(600729)二季度业绩增长30％，释放充分符合预期.txt");
		extractor.extractFromFile(
				"F:\\email\\研究报告7.19\\国信证券--凯迪电力(000939)2010年中报及重大事项点评：中报良好，大股东拟再次倾力扶持上市公司投身生物质发电.pdf",
				"F:\\email\\papers\\研究报告7.19\\国信证券--圣农发展(002299)10年中期财报点评：天灾过后或现真机会.txt");
	}
}
