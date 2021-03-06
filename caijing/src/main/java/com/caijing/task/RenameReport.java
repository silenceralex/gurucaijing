package com.caijing.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.ReportDao;
import com.caijing.dao.StockDao;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;
import com.caijing.domain.Stock;
import com.caijing.util.Command;
import com.caijing.util.ContextFactory;
import com.caijing.util.POIExcelUtil;
import com.caijing.util.ServerUtil;
import com.caijing.crawl.ReportExtractorImpl;

public class RenameReport {
	
	private Pattern stockcodePattern = Pattern.compile("(((002|000|300|600)[\\d]{3})|60[\\d]{4})",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);	
	
	private Pattern datePattern = Pattern.compile("([\\d]{8})",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
	
	final static String excelDirPath = "/data/excel/"; //TODO rename to same name with report dirname
	final static String reportDirPath = "/data/oldpapers2/";
	final static String desthtmlPath = "/home/rnhtml/papers/"; //TODO 

	private ReportDao reportDao = (ReportDao) ContextFactory.getBean("reportDao");
	private StockDao stockDao = (StockDao) ContextFactory.getBean("stockDao");
	private RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
	private ReportExtractorImpl extractor = (ReportExtractorImpl) ContextFactory.getBean("reportExtractor");
	
	public void readExcel(String filepath){
		File excelDir = new File(filepath);
		File[] excels = excelDir.listFiles();
		HashSet<String> set = new HashSet<String>();
		POIExcelUtil poi = new POIExcelUtil();
		for (File excel : excels) {
			String prefix = reportDirPath+excel.getName().split("\\.")[0].split("-")[0]+"/";
			List<ArrayList<String>> data = poi.read(excel.getPath());
			for (ArrayList<String> row : data) {
				System.out.println("row : " + row);
				
				String rid = ServerUtil.getid();
				String filename = row.get(0);
				String saname = row.get(1);
				String stockcode = getcode(row.get(2));
				String createdate = row.get(7).trim();
				String destPdffilepath = desthtmlPath +createdate+"/"+rid + ".pdf";
				String destTxtfilepath = desthtmlPath +createdate+"/"+rid + ".txt";
				String uid = saname+stockcode+createdate;

				if(stockcode==null||createdate.length()==0||row.get(1).equals("券商名称")||row.get(1).length()==0||row.get(1).length()>8){
					continue;
				}

				if(set.contains(uid)){//去重
					continue;
				}
				
				if(!newReportFile(prefix, filename, destPdffilepath)){
					continue;
				}
				set.add(uid);
//				String eps = fetchEps(saname, destPdffilepath, destTxtfilepath);
				String eps = null;
				newReport(rid, saname, stockcode, row.get(3), row.get(4), row.get(5), row.get(6), createdate, eps);
			}
		}
	}
	
	public void readTxt(String filepath){
		File excelDir = new File(filepath);
		File[] txts = excelDir.listFiles();
		
		for (File txt : txts) {
			String prefix = reportDirPath+txt.getName().split("\\.")[0]+"/";
			try {
				List<String> rows = FileUtils.readLines(txt);
				for (String row : rows) {
					String[] data = row.split("\t");
					if(data.length<7){
						System.out.println(data.length);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean newReportFile(String prefix, String filename, String destPdffilepath) {
		String[] suffixs = {".pdf",".PDF",".PDf", ".Pdf"};
		
		for (String suffix : suffixs) {
			File report = new File(prefix + filename + suffix);
			try {
				if (report.exists() && report.isFile()) {
					System.out.println("report:" + report.getAbsolutePath() + " " + report.getName());
					FileUtils.copyFile(report, new File(destPdffilepath)); 
					return true;
				} else {
					String utfName = new String(filename.getBytes("gbk"));
					report = new File(prefix + utfName + suffix);
					if (report.exists() && report.isFile()) {
						System.out.println("report:" + report.getAbsolutePath() + " " + report.getName());
						FileUtils.copyFile(report, new File(destPdffilepath)); 
						return true;
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("transform title Error!");
		return false;
	}

	public void newReport(String rid, String saname, String stockcode, String title, String aname, String grade, 
			String objectpricestr, String createdate, String eps) {
		//Report
		Report report = new Report();
		report.setRid(rid);
		report.setSaname(saname);
		report.setStockcode(stockcode);
		Stock stock = (Stock)stockDao.select(stockcode);
		String stockname = null;
		if(stock!=null){
			stockname = stock.getStockname();
		}
		report.setStockname(stockname);
		report.setType(1);
		report.setTitle(title);
		report.setAname(aname);
		report.setPtime(new Date());
		
		String filepath = desthtmlPath +createdate+"/"+rid + ".pdf";
		report.setFilepath(filepath.replace("/home", ""));
		System.out.println("filepath:" + report.getFilepath());
		System.out.println("saname:" + report.getSaname());
		System.out.println("stockname(code):" + report.getStockname() + "(" + report.getStockcode() + ")");
		System.out.println("eps:" + eps);
		reportDao.insert(report);
		
		//RecommendStock
		RecommendStock recommendStock = new RecommendStock();
		recommendStock.setRecommendid(ServerUtil.getid());
		recommendStock.setReportid(rid);
		recommendStock.setStockcode(stockcode);
		recommendStock.setStockname(stockname);
		recommendStock.setSaname(saname);
		recommendStock.setAname(aname);
		recommendStock.setGrade(grade);
		recommendStock.setStatus(judgeStaus(grade)); //TODO 原insert语句有问题
		recommendStock.setEps(eps);
		recommendStock.setCreatedate(createdate);
		float objectprice = 0;
		if(isNumeric(objectpricestr)){
			objectprice = Float.parseFloat(objectpricestr);
		}
		recommendStock.setObjectprice(objectprice);
		recommendStock.setExtractnum(4);
		recommendStockDao.insert(recommendStock);
	}
	
	public static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]*");
	    return pattern.matcher(str).matches();   
	} 
	
	public static String addZeroForNum(String str, int strLength, boolean isleft) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				if(isleft){
					sb.append("0").append(str);// 左补0
				} else {
					sb.append(str).append("0");//右补0
				}
				str = sb.toString();
				strLen = str.length();
			}
		}
		return str;
	}
	
	public String getcode(String codestr){
		if(isNumeric(codestr)){
			codestr = addZeroForNum(codestr, 6, true); 
		}
		Matcher m = stockcodePattern.matcher(codestr);
		if (m != null && m.find()) {
			return m.group(1);
		}
		return null;
	}
	
	public void cpReport(File reportfile, File targetfile){
		
	}
	
	public String fetchEps(String saname, String destPdffilepath, String destTxtfilepath){
		String eps = null;
		try {
			File destTxtfile = readFdf(destPdffilepath, destTxtfilepath);
			eps = extractor.fetchEPS(saname, FileUtils.readFileToString(destTxtfile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return eps;
	}
	
	public File readFdf(String file, String outPath) {
		boolean sort = false;
		String pdfFile = file;
		String encoding = "gbk";
		int startPage = 1;
		int endPage = Integer.MAX_VALUE;
		Writer output = null;
		PDDocument document = null;
		try {
			document = PDDocument.load(pdfFile);
			// String fileName = url.getFile();
			System.out.println("textFile:" + outPath);
			output = new OutputStreamWriter(new FileOutputStream(outPath), encoding);
			PDFTextStripper stripper = null;
			stripper = new PDFTextStripper();
			stripper.setSortByPosition(sort);
			stripper.setStartPage(startPage);
			stripper.setEndPage(endPage);
			stripper.writeText(document, output);
		} catch (Exception e) {
			System.out.print(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				output.close();
				document.close();
			} catch (Exception e) {
				System.out.print(e.getMessage());
				e.printStackTrace();
			}
		}
		return new File(outPath);
	}
	
	public void unrar(String rarname, String targetdir){
		StringWriter sw = new StringWriter();
		Command.run("unrar "+rarname+" "+targetdir, sw);
		System.out.println(" "+sw.toString());
	}
	
	public void unzip(String zipname, String targetdir){
		StringWriter sw = new StringWriter();
		Command.run("unzip "+zipname+" -d "+targetdir, sw);
		System.out.println(" "+sw.toString());
	}
	
	/**
	 * 根据评级信息判断券商研报是买入类(status为2) 还是卖出类(status为1)
	 */
	public int judgeStaus(String grade) {
		String judgeStr = grade.replaceAll("\\s", "");
		if (!judgeStr.contains("谨慎") && !judgeStr.contains("审慎")) {
			if (judgeStr.contains("买入") || judgeStr.contains("增持") || judgeStr.contains("推荐")) {
				return 2;
			} else if (judgeStr.contains("中性")) {
				return 1;
			}
		} else {
			return 1;
		}
		return 0;
	}
	
	public static void main(String[] args) {
		RenameReport rr = new RenameReport();
		rr.readExcel(excelDirPath);
//		rr.readTxt(excelDirPath);
		System.exit(0);
	}
}