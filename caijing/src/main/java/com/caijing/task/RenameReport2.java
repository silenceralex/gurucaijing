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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.caijing.crawl.ReportExtractorImpl;
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

public class RenameReport2 {
	
	private Pattern stockcodePattern = Pattern.compile("(((002|000|300|600)[\\d]{3})|60[\\d]{4})",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);	
	
	private Pattern datePattern = Pattern.compile("([\\d]{8})",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
	
	final static String excelDirPath = "/data/excel/"; //TODO rename to same name with report dirname
	final static String reportDirPath = "/data/oldpapers2/";
	final static String desthtmlPath = "/home/rnhtml2/papers/"; //TODO 

	private ReportDao reportDao = (ReportDao) ContextFactory.getBean("reportDao");
	private StockDao stockDao = (StockDao) ContextFactory.getBean("stockDao");
	private RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
	private ReportExtractorImpl extractor = (ReportExtractorImpl) ContextFactory.getBean("reportExtractor");
	
	public void readExcels(String filepath){
		File excelDir = new File(filepath);
		File[] excels = excelDir.listFiles();
		for (File excel : excels) {
			String prefix = reportDirPath+excel.getName().split("\\.")[0].split("-")[0]+"/";
			readExcel(excel, new File(prefix));
		}
	}
	
	public void readExcels2(){
		Map<String, String> map = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
//				put("/data/oldpapers/201008/201008.xls", "/data/oldpapers/201008/");
//				put("/data/oldpapers/201009/201009.xlsx", "/data/oldpapers/201009/");
//				put("/data/oldpapers/201010/201010.xlsx", "/data/oldpapers/201010/");
//				put("/data/oldpapers/201011/201011.xls", "/data/oldpapers/201011/");
//				put("/data/oldpapers/201012/201012.xls", "/data/oldpapers/201012/");
//				put("/data/oldpapers/201101temp/201101temp.xls", "/data/oldpapers/201101temp/");
//				put("/data/excel/hanjianping-2.xls", "/data/oldpapers/hanjianping/");
//				put("/data/excel/yanshiyou-1.xlsx", "/data/oldpapers/yanshiyou/");
//				put("/data/excel/wanghan-3.xlsx", "/data/oldpapers/wanghan/");
				put("/data/excel/test.xlsx", "/data/oldpapers/test/");
			}
		};
		
		for (Map.Entry<String,String> data : map.entrySet()) {
			String excel = data.getKey();
			System.out.println("=== "+excel+" ===");
			String reportdir = data.getValue();
			readExcel(new File(excel), new File(reportdir));
		}
	}
	
	public void readExcel(File excel, File reportDir){
		POIExcelUtil poi = new POIExcelUtil();
		String prefix = reportDir.getPath()+"/";
		List<ArrayList<String>> data = poi.read(excel.getPath());
		for (ArrayList<String> row : data) {
			System.out.println("row : " + row);
			
			String rid = ServerUtil.getid();
			String filename = row.get(0);
			String saname = row.get(1).trim();
			String stockcode = getcode(row.get(2));
			String createdate = row.get(7).trim();
			String title = row.get(3).trim();
			String aname = row.get(4).trim();
			String grade = row.get(5).trim();
			String objectpricestr = row.get(6).trim();
//			String eps = fetchEps(saname, destPdffilepath, destTxtfilepath);
			String eps = null;
			
			String destPdffilepath = desthtmlPath +createdate+"/"+rid + ".pdf";
			String destTxtfilepath = desthtmlPath +createdate+"/"+rid + ".txt";

			if(stockcode==null||createdate.length()==0||createdate.equals("��")||row.get(1).equals("ȯ������")||row.get(1).length()==0||row.get(1).length()>8){
				continue;
			}

			String rid2 = isReportExist(saname, stockcode, createdate);
			if(rid2!=null){ //�ظ��Ļ�����������
				rid = rid2;
				updateReport(rid, saname, stockcode, title, aname, grade, objectpricestr, createdate, eps);
				continue;
			}
			
			if(!newReportFile(prefix, filename, destPdffilepath)){
				continue;
			}

			insertReport(rid, saname, stockcode, title, aname, grade, objectpricestr, createdate, eps);
		}
	}
	
	public String isReportExist(String saname,String stockcode,String createdate){
		Report report = reportDao.selectByMultiKey(saname, stockcode, createdate);
		if(report==null){
			return null;
		}
		return report.getRid();
	}
	
	private boolean newReportFile(String prefix, String filename, String destPdffilepath) {
		String[] suffixs = {".pdf",".PDF",".PDf", ".Pdf",""};
		
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

	public void updateReport(String rid, String saname, String stockcode, String title, String aname, String grade, 
			String objectpricestr, String createdate, String eps) {
		//Report
		Map<String, Object> report = newReportMap(rid, saname, stockcode, title, aname, createdate);
		reportDao.updateByPrimaryKeySelective(report);
		System.out.println("update report " + rid);
		
		//RecommendStock
		if(recommendStockDao.selectByReportid((String)report.get("reportid"))==null){
			RecommendStock recommendStock = newRecommendStock(rid, stockcode, (String)report.get("stockname"), saname, aname, grade, eps, createdate, objectpricestr);
			recommendStockDao.insert(recommendStock);
			System.out.println("insert missing recommendStock " + rid);
		} else {
			Map<String, Object> recommendStock = newRecommendStockMap(rid, stockcode, (String)report.get("stockname"), saname, aname, grade, eps, createdate, objectpricestr);
			recommendStockDao.updateByPrimaryKeySelective(recommendStock);
			System.out.println("update recommendStock " + rid);
		}
	}
	
	public void insertReport(String rid, String saname, String stockcode, String title, String aname, String grade, 
			String objectpricestr, String createdate, String eps) {
		//Report
		Report report = newReport(rid, saname, stockcode, title, aname, createdate);
		reportDao.insert(report);
		System.out.println("insert report " + rid);
		
		//RecommendStock
		RecommendStock recommendStock = newRecommendStock(rid, stockcode, report.getStockname(), saname, aname, grade, eps, createdate, objectpricestr);
		recommendStockDao.insert(recommendStock);
		System.out.println("insert recommendStock " + rid);
	}
	
	public Map<String, Object> newReportMap(String rid, String saname, String stockcode, String title, String aname, String createdate){
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("rid", rid);
		params.put("saname",saname);
		params.put("stockcode",stockcode);
		Stock stock = (Stock)stockDao.select(stockcode);
		String stockname = null;
		if(stock!=null){
			stockname = stock.getStockname();
		}
		params.put("stockname",stockname);
		params.put("type",1);
		params.put("title",title);
		params.put("aname",aname);
		params.put("ptime",new Date());
		
		String filepath = desthtmlPath +createdate+"/"+rid + ".pdf";
		params.put("filepath",filepath.replace("/home", ""));
		return params;
	}
	
	public Map<String, Object> newRecommendStockMap(String rid, String stockcode, String stockname, String saname, 
			String aname, String grade, String eps, String createdate, String objectpricestr){
		Map<String, Object> params = new HashMap<String, Object>();
		
//		params.put("recommendid",ServerUtil.getid());
		params.put("reportid",rid);
		params.put("stockcode",stockcode);
		params.put("stockname",stockname);
		params.put("saname",saname);
		params.put("aname",aname);
		params.put("grade",grade);
		params.put("status",judgeStaus(grade));
		params.put("eps",eps);
		params.put("createdate",createdate);
		float objectprice = 0;
		if(isNumeric(objectpricestr)){
			objectprice = Float.parseFloat(objectpricestr);
		}
		params.put("objectprice",objectprice);
		params.put("extractnum",4);
		return params;
	}
	
	public Report newReport(String rid, String saname, String stockcode, String title, String aname, String createdate){
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
		return report;
	}
	
	public RecommendStock newRecommendStock(String rid, String stockcode, String stockname, String saname, 
			String aname, String grade, String eps, String createdate, String objectpricestr){
		RecommendStock recommendStock = new RecommendStock();
		recommendStock.setRecommendid(ServerUtil.getid());
		recommendStock.setReportid(rid);
		recommendStock.setStockcode(stockcode);
		recommendStock.setStockname(stockname);
		recommendStock.setSaname(saname);
		recommendStock.setAname(aname);
		recommendStock.setGrade(grade);
		recommendStock.setStatus(judgeStaus(grade));
		recommendStock.setEps(eps);
		recommendStock.setCreatedate(createdate);
		float objectprice = 0;
		if(isNumeric(objectpricestr)){
			objectprice = Float.parseFloat(objectpricestr);
		}
		recommendStock.setObjectprice(objectprice);
		recommendStock.setExtractnum(4);
		return recommendStock;
	}
	
	public String getcode(String codestr){
		if(codestr.trim().length()==0){
			return null;
		}
		if(isNumeric(codestr)){
			codestr = addZeroForNum(codestr, 6, true); 
		}
		Matcher m = stockcodePattern.matcher(codestr);
		if (m != null && m.find()) {
			return m.group(1);
		}
		return null;
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
	
	/**
	 * ����������Ϣ�ж�ȯ���б���������(statusΪ2) ����������(statusΪ1)
	 */
	public int judgeStaus(String grade) {
		String judgeStr = grade.replaceAll("\\s", "");
		if (!judgeStr.contains("����") && !judgeStr.contains("����")) {
			if (judgeStr.contains("����") || judgeStr.contains("����") || judgeStr.contains("�Ƽ�")) {
				return 2;
			} else if (judgeStr.contains("����")) {
				return 1;
			}
		} else {
			return 1;
		}
		return 0;
	}
	
	/**
	 * ��ȡpdf������������txt
	 * @param file
	 * @param outPath
	 * @return
	 */
	public static File readFdf(String file, String outPath) {
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
	
	/**
	 * unrar
	 * @param rarname
	 * @param targetdir
	 */
	public static void unrar(String rarname, String targetdir){
		StringWriter sw = new StringWriter();
		Command.run("unrar "+rarname+" "+targetdir, sw);
		System.out.println(" "+sw.toString());
	}
	
	/**
	 * unzip
	 * @param zipname
	 * @param targetdir
	 */
	public static void unzip(String zipname, String targetdir){
		StringWriter sw = new StringWriter();
		Command.run("unzip "+zipname+" -d "+targetdir, sw);
		System.out.println(" "+sw.toString());
	}
	
	/**
	 * �Ƿ�������
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]+");
	    return pattern.matcher(str).matches();   
	} 
	
	/**
	 * �������0
	 * @param str
	 * @param strLength
	 * @param isleft
	 * @return
	 */
	public static String addZeroForNum(String str, int strLength, boolean isleft) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				if(isleft){
					sb.append("0").append(str);// ��0
				} else {
					sb.append(str).append("0");//�Ҳ�0
				}
				str = sb.toString();
				strLen = str.length();
			}
		}
		return str;
	}
	
	public static void main(String[] args) {
		RenameReport2 rr = new RenameReport2();
		//rr.readExcels(excelDirPath);
		rr.readExcels2();
//		rr.readTxt(excelDirPath);
		System.exit(0);
	}
}