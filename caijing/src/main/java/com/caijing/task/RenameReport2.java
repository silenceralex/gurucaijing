package com.caijing.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.dao.DataAccessException;

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
	
	private Pattern stockcodePattern = Pattern.compile("^(((002|000|300|600)[\\d]{3})|60[\\d]{4})$",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);	
	
	private Pattern datePattern = Pattern.compile("^(19|20)\\d\\d(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
	
	final static String excelDirPath = "/data/excel/"; //TODO rename to same name with report dirname
	final static String reportDirPath = "/data/oldpapers2/";
	final static String desthtmlPath = "/home/rnhtml/papers/"; //TODO 

	private ReportDao reportDao = (ReportDao) ContextFactory.getBean("reportDao");
	private StockDao stockDao = (StockDao) ContextFactory.getBean("stockDao");
	private RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
	private ReportExtractorImpl extractor = (ReportExtractorImpl) ContextFactory.getBean("reportExtractor");
	
	private static OutputStream os = null;
	static{
		try {
			os = new FileOutputStream("/data/excel/oldpapers_error.log", true);
			IOUtils.write("==== start RenameReport2 ===="+"\r\n", os, "UTF-8");
			IOUtils.write(new Date()+"\r\n", os, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
//				put("/data/oldpapers/201008/yw201008_0411.xls", "/data/oldpapers/201008/");
//				put("/data/oldpapers/201008/yw201008_0418.xls", "/data/oldpapers/201008/");
				put("/data/oldpapers/201008/201008_0425.xls", "/data/oldpapers/201008/");
//				put("/data/oldpapers/201009/201009.xlsx", "/data/oldpapers/201009/");
//				put("/data/oldpapers/201009/201009_0418.xlsx", "/data/oldpapers/201009/");
//				put("/data/oldpapers/201010/201010.xlsx", "/data/oldpapers/201010/");
//				put("/data/oldpapers/201010/201010_0411.xlsx", "/data/oldpapers/201010/");
				put("/data/oldpapers/201010/201010_0425.xlsx", "/data/oldpapers/201010/");
//				put("/data/oldpapers/201011/201011.xls", "/data/oldpapers/201011/");
//				put("/data/oldpapers/201011/201011_0411.xls", "/data/oldpapers/201011/");
				put("/data/oldpapers/201011/201011_0425.xls", "/data/oldpapers/201011/");
//				put("/data/oldpapers/201012/201012.xls", "/data/oldpapers/201012/");
//				put("/data/oldpapers/201012/201012_0411.xls", "/data/oldpapers/201012/");
//				put("/data/oldpapers/201012/201012_0418.xls", "/data/oldpapers/201012/");
//				put("/data/oldpapers/201101temp/201101temp.xls", "/data/oldpapers/201101temp/");
//				put("/data/oldpapers/201102/201102_0418.xlsx", "/data/oldpapers/201102/");
//				put("/data/excel/hanjianping-1.xls", "/data/oldpapers2/hanjianping/");
//				put("/data/excel/hanjianping-2.xls", "/data/oldpapers2/hanjianping/");
//				put("/data/excel/hanjianping-3.xls", "/data/oldpapers2/hanjianping/");
//				put("/data/excel/hanjianping-4.xls", "/data/oldpapers2/hanjianping/");
				put("/data/excel/hanjianping-5.xls", "/data/oldpapers2/hanjianping/"); //0425
//				put("/data/excel/yanshiyou-1.xlsx", "/data/oldpapers2/yanshiyou/");
				put("/data/excel/yanshiyou-2.xlsx", "/data/oldpapers2/yanshiyou/"); //0425
				put("/data/excel/yanshiyou-3.xlsx", "/data/oldpapers2/yanshiyou/"); //0425
				put("/data/excel/yanshiyou-4.xlsx", "/data/oldpapers2/yanshiyou/"); //0425
//				put("/data/excel/wanghan-1.xlsx", "/data/oldpapers2/wanghan/");
//				put("/data/excel/wanghan-2.xlsx", "/data/oldpapers2/wanghan/");
//				put("/data/excel/wanghan-3.xlsx", "/data/oldpapers2/wanghan/");
				put("/data/excel/wanghan-4.xlsx", "/data/oldpapers2/wanghan/"); //0425
//				put("/data/excel/test.xlsx", "/data/oldpapers/test/");
			}
		};
		
		for (Map.Entry<String,String> data : map.entrySet()) {
			String excel = data.getKey();
			System.out.println("=== "+excel+" ===");
			String reportdir = data.getValue();
			readExcel(new File(excel), new File(reportdir));
		}
		
		try {
			IOUtils.write("==== end RenameReport2 ====\r\n", os, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
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
			String saname = getsaname(row.get(1).trim());
			String stockcode = getcode(row.get(2).trim());
			String createdate = getcreatedate(row.get(7).trim());
			String title = row.get(3).trim();
			String aname = row.get(4).trim();
			String grade = row.get(5).trim();
			String objectpricestr = row.get(6).trim();
//			String eps = fetchEps(saname, destPdffilepath, destTxtfilepath);
			String eps = null;
			
			String destPdffilepath = desthtmlPath +createdate+"/"+rid + ".pdf";
			String destTxtfilepath = desthtmlPath +createdate+"/"+rid + ".txt";

			if(stockcode==null||createdate==null||createdate.length()==0||createdate.equals("无")||saname.equals("券商名称")||saname.length()==0||saname.length()>8){
				try {
					IOUtils.write(excel.getName()+" >>> "+filename+"\r\n", os, "UTF-8");
				} catch (IOException e) {
					e.printStackTrace();
				}
				continue;
			}

			try {
				String rid2 = isReportExist(saname, stockcode, createdate, title);
				if(rid2!=null){ //重复的话，更新数据
					rid = rid2;
					updateReport(rid, saname, stockcode, title, aname, grade, objectpricestr, createdate, eps);
					continue;
				} else {
					if(!newReportFile(prefix, filename, destPdffilepath)){
						continue;
					}
					insertReport(rid, saname, stockcode, title, aname, grade, objectpricestr, createdate, eps);
				}
			} catch (DataAccessException e) {
				try {
					IOUtils.write(excel.getName()+" >>> "+filename+"\r\n", os, "UTF-8");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}
	
	public String isReportExist(String saname,String stockcode,String createdate, String title){
		String rid = null;
		List<Report> reports = reportDao.selectByMultiKey(saname, stockcode, createdate);
		if(reports.size()==1){
			rid = reports.get(0).getRid();
		} else {
			for (Report report : reports) {
				if(report.getTitle().equalsIgnoreCase(title)){
					rid = report.getRid();
					break;
				}
			}
		}
		return rid;
	}
	
	/*
	public String isReportExist(String saname,String stockcode,String createdate){
		Report report = reportDao.selectByMultiKey(saname, stockcode, createdate);
		if(report==null){
			return null;
		}
		return report.getRid();
	}*/
	
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
			String objectpricestr, String createdate, String eps) throws DataAccessException {
		//Report
		Map<String, Object> report = newReportMap(rid, saname, stockcode, title, aname, createdate);
		reportDao.updateByPrimaryKeySelective(report);
		System.out.println("update report " + rid);
		
		//RecommendStock
		if(recommendStockDao.selectByReportid((String)report.get("rid"))==null){
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
			String objectpricestr, String createdate, String eps) throws DataAccessException {
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
		params.put("publishdate",createdate);
		
		String filepath = desthtmlPath +createdate+"/"+rid + ".pdf";
		params.put("filepath",filepath.replace("/home", ""));
		return params;
	}
	
	public Map<String, Object> newRecommendStockMap(String rid, String stockcode, String stockname, String saname, 
			String aname, String grade, String eps, String createdate, String objectpricestr){
		Map<String, Object> params = new HashMap<String, Object>();
		
		//params.put("recommendid",ServerUtil.getid());
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
		report.setPublishdate(createdate);
		
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
	
	public String getsaname(String saname){
		int len = saname.length();
		if(len==6 && saname.endsWith("证券")){
			saname = saname.substring(0, 4);
		}
		if(len==2){
			if(saname.endsWith("中金")){
				saname="中金证券";
			}
		}
		return saname;
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

	public String getcreatedate(String createdate){
		Matcher m = datePattern.matcher(createdate);
		if (m != null && m.find()) {
			return m.group(0);
		} else {
			createdate = null;
		}
		return createdate;
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
	
	/**
	 * 读取pdf，并解析返回txt
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
	 * 是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]+");
	    return pattern.matcher(str).matches();   
	} 
	
	/**
	 * 填充数字0
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
	
	public static void main(String[] args) {
		RenameReport2 rr = new RenameReport2();
		rr.readExcels2();
//		System.out.println(rr.getcreatedate("20080102"));
//		System.out.println(rr.getcreatedate("200811207"));
		System.exit(0);
	}
}