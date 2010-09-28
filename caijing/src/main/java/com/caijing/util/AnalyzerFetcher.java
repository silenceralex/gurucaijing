package com.caijing.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.context.ApplicationContext;

import com.caijing.dao.ReportDao;
import com.caijing.domain.Report;

public class AnalyzerFetcher {

	public static void main(String[] args) {
		//		AnalyzerDao analyzerDao = (AnalyzerDaoImpl) ContextFactory
		//				.getBean("analyzerDao");
		//		RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory
		//				.getBean("recommendStockDao");
		//		List<Analyzer> analyzers = analyzerDao.getAllAnalyzers();
		//		int i=0;
		//		for (Analyzer analyzer : analyzers) {
		//			recommendStockDao.updateAnalyzer(analyzer.getName(), analyzer
		//					.getAid());
		//			i++;
		//		}

		ApplicationContext context = ContextFactory.getApplicationContext();
		ReportDao reportDao = (ReportDao) context.getBean("reportDao");
		AbsConfig absConfig = (AbsConfig) context.getBean("absConfig");
		String saname = "国泰君安";
		Map map = absConfig.getValue(saname);
		Pattern yaobao_absPattern = Pattern.compile((String) map.get("1_abs"), Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.UNIX_LINES);
		Pattern yaobao_desPattern = Pattern.compile((String) map.get("1_des"), Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.UNIX_LINES);
		System.out.println("yaobao: " + map.get("1"));
		Pattern cenhui_absPattern = Pattern.compile((String) map.get("0_abs"), Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.UNIX_LINES);
		Pattern cenhui_desPattern= Pattern.compile((String) map.get("0_des"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL
				| Pattern.UNIX_LINES);
		Pattern hangye_absPattern = Pattern.compile((String) map.get("2_abs"), Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.UNIX_LINES);
		Pattern hangye_desPattern = Pattern.compile((String) map.get("2_des"), Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.UNIX_LINES);
		Pattern hongguan_absPattern = Pattern.compile((String) map.get("3_abs"), Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.UNIX_LINES);
		Pattern hongguan_desPattern = Pattern.compile((String) map.get("3_des"), Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.UNIX_LINES);
		int count = reportDao.getAllReportsCountBySaname(saname);

		System.out.println("count: " + count);
		List<Report> reports = reportDao.getReportsBySaname(saname, 0, 20);
		for (Report report : reports) {
			String txtpath = "/home/html" + report.getFilepath();
			System.out.println("txtpath: " + txtpath.replace("pdf", "txt"));
			String content = FileUtil.read(txtpath.replace("pdf", "txt"), "gb2312");
			//			report.getFilepath()
			try {
				PDDocument document = PDDocument.load(txtpath);
				PDFTextStripper stripper = new PDFTextStripper();
				stripper.setSortByPosition(false);
				stripper.setStartPage(1);
				stripper.setEndPage(1);
				StringWriter sw=new StringWriter();
				stripper.writeText(document, sw);
				System.out.println("first page: " + sw.toString());
				content = sw.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if ("1".equals("" + report.getType())) {
				System.out.println("公司: ");
				Matcher m = yaobao_absPattern.matcher(content);
				if (m != null && m.find()) {
					String abs = m.group(1).trim();
					System.out.println("ABS: " + abs);
				}
				m = yaobao_desPattern.matcher(content);
				if (m != null && m.find()) {
					String abs = m.group(1).trim();
					System.out.println("des: " + abs);
				}
			} else if ("2".equals("" + report.getType())) {
				System.out.println("行业: ");
				Matcher m = hangye_absPattern.matcher(content);
				if (m != null && m.find()) {
					String abs = m.group(1).trim();
					System.out.println("ABS: " + abs);
				}
				m = hangye_desPattern.matcher(content);
				if (m != null && m.find()) {
					String abs = m.group(1).trim();
					System.out.println("des: " + abs);
				}
			} else if ("3".equals("" + report.getType())) {
				System.out.println("宏观: ");
				Matcher m = hongguan_absPattern.matcher(content);
				if (m != null && m.find()) {
					String abs = m.group(1).trim();
					System.out.println("ABS: " + abs);
				}
				m = hongguan_desPattern.matcher(content);
				if (m != null && m.find()) {
					String abs = m.group(1).trim();
					System.out.println("des: " + abs);
				}
			} else if ("0".equals("" + report.getType())) {
				System.out.println("晨报: ");
				Matcher m = cenhui_absPattern.matcher(content);
				if (m != null && m.find()) {
					String abs = m.group(1).trim();
					System.out.println("ABS: " + abs);
				}
				m = cenhui_desPattern.matcher(content);
				if (m != null && m.find()) {
					String abs = m.group(1).trim();
					System.out.println("des: " + abs);
				}
			}
		}
	}
}
