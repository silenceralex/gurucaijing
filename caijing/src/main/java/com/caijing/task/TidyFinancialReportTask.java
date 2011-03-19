package com.caijing.task;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.caijing.domain.FinancialReport;
import com.caijing.util.ContextFactory;
import com.caijing.util.ServerUtil;

/**
 * 整理财报，功能如下：
 * 
 * 1. 归类，放于各自年份类型的目录下 
 * $from: /data/report/`report_original_name`.pdf $to: /data/reports/`year`/`type`/`stockcode`.pdf
 * 2. 数据库记录
 */
public class TidyFinancialReportTask {
	
//	@Autowired
//	@Qualifier("stockDao")
//	private StockDao stockDao = null;
//	
//	@Autowired
//	@Qualifier("financialReportDao")
//	private FinancialReportDao financialReportDao = null;
//	
//	@Autowired
//	@Qualifier("jdbcTemplate")
//	private JdbcTemplate jdbcTemplate = null;
	
	static String fromRootDir = "/data/report/";
	static String toDir = "/data/reports/";
	static Pattern stockcodePattern = Pattern.compile("^(((002|000|300|600)[\\d]{3})|60[\\d]{4})$", Pattern.CASE_INSENSITIVE
			| Pattern.DOTALL | Pattern.UNIX_LINES);
	static Pattern titlePattern = Pattern.compile("([0-9-]{4,9})(jb|nd|zq)_?(\\d{1})?", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
			| Pattern.UNIX_LINES); //nj 年鉴
	static Pattern chinesePattern=Pattern.compile("[\u4e00-\u9fa5]+", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
	static Pattern yeartypePattern=Pattern.compile("([\\d]{4}|[零一二三四五六七八九]{4}|[０１２３４５６７８９]{4})年?(第一季度|中期|第三季度|年度)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);

	final SimpleDateFormat timeFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	String stocknamequery = "select stockname from stock where stockcode=?";
//	String isreportexist = "select reportid from financialreport where filepath=?";
	String financialReportInsert = "insert into financialreport (reportid, title, type, year, stockcode, stockname, filepath, lmodify, status) " +
			"values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	String financialReportUpdate = "update financialreport set type=?, year=?, filepath=?, lmodify=? where filepath=?";
	
	JdbcTemplate jdbcTemplate = (JdbcTemplate) ContextFactory.getBean("jdbcTemplate");

	static String txt = ".txt";
	static String pdf = ".pdf";
	String[] FileSuffix = {pdf, txt};
	
	public void run() {
		
		File[] yearDir = new File(fromRootDir).listFiles();
		for (File dir : yearDir) {
			if (dir.isDirectory()) {
				String dirname = dir.getName();
				System.out.println("> yearDir: " + dirname);
				
				Object[] data = dirnameParser(dirname);
				String year = (String) data[0];
				byte type = (Byte) data[1];
				
				File reportDir = new File(dir, "reports");
				File[] reportFiles = TidyFinancialReportTask.listFileBySuffix(reportDir, FileSuffix);
				if (reportFiles != null) {
					for (File reportfile : reportFiles) {
						System.out.println("==> report: " + reportfile.getPath());
						String report_title = reportfile.getName();
						String stockcode = report_title.split("\\.")[0].split("_")[0];
						String stockname = null; 
						byte status = 1;
						Matcher m = stockcodePattern.matcher(stockcode);
						if (m != null && m.find()){//例外 异常数据
							status = 0;
							try {
								stockname = (String) jdbcTemplate.queryForObject(stocknamequery, new Object[]{stockcode}, String.class);
							} catch (DataAccessException e) {
								stockname = null;
								status = 1;
								System.err.println("[EmptyResultDataAccessException] stockcode: "+stockcode+" not exist");
							}
						}
						String filepath = "/" + year + "/" + type + "/" + stockcode + "."+getExtension(reportfile.getName(),"").toLowerCase();
						
						Date lmodify = new Date();
						System.out.println("[" + filepath + ", " + stockname +", "+ timeFORMAT.format(lmodify) + "]");
						
						//cp report
						try {
							//英文,txt文件大小比较
							File targetfile = new File(toDir, filepath);
							if(targetfile.exists()){
								if(isChinese(reportfile, "gbk")){
									if(reportfile.getName().toLowerCase().endsWith(txt)&&!islarger(reportfile, targetfile)){
										System.out.println("pass txt "+ reportfile.getPath());
										continue;
									} else {
										FileUtils.copyFile(reportfile, targetfile);
										System.out.println("rewrite "+ reportfile.getPath());
										continue;
									}
								}
								System.out.println("pass "+ reportfile.getPath());
								continue;
							}
							FileUtils.copyFile(reportfile, targetfile);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						
						//to database
						FinancialReport report = new FinancialReport();
						report.setReportid(ServerUtil.getid());
						report.setStockcode(stockcode);
						report.setStockname(stockname);
						report.setTitle(report_title);
						report.setType(type);
						report.setYear(year);
						report.setLmodify(lmodify);
						report.setFilepath(filepath);
						report.setStatus(status);
						//reportid, title, type, year, stockcode, stockname, filepath, lmodify, status
						jdbcTemplate.update(financialReportInsert, new Object[]{report.getReportid(),report.getTitle(),report.getType(),report.getYear(),
								report.getStockcode(),report.getStockname(),report.getFilepath(),report.getLmodify(),report.getStatus()});
					}
				} 
			}
		}
	}

	public static File[] listFileBySuffix(File dir, String[] suffix) {
		IOFileFilter fileFilter1 = new SuffixFileFilter(suffix, IOCase.INSENSITIVE);
		IOFileFilter fileFilter2 = new NotFileFilter(DirectoryFileFilter.INSTANCE);
		FilenameFilter filenameFilter = new AndFileFilter(fileFilter1, fileFilter2);
		return dir.listFiles(filenameFilter);
	}
	
	public static String getExtension(String filename, String defExt) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');
			if ((i > -1) && (i < (filename.length() - 1))) {
				return filename.substring(i + 1).trim();
			}
		}
		return defExt;
	}
	
	public static boolean islarger(File file1, File file2){
		return (file1.length()>file2.length());
	}
	
	//TODO 
	public void cat(String yearDir) {

		File[] typeDir = new File(yearDir).listFiles();
		for (File filedir : typeDir) {
			if (filedir.isDirectory()) {
				File[] files = filedir.listFiles();
				for (File reportfile : files) {
					System.out.println(reportfile.getPath());
					String txtfile = getString(reportfile, "GBK");
					Object[] data = txtTilteParser(txtfile);
					String year = (String) data[0];
					byte type = (Byte) data[1];

					String path = "/" + year + "/" + type + "/"	+ reportfile.getName();
					File targetfile = new File(toDir, path);

					FinancialReport report = new FinancialReport();
					report.setReportid(ServerUtil.getid());
					report.setType(type);
					report.setYear(year);
					report.setLmodify(new Date());
					report.setFilepath(path);

					jdbcTemplate.update(financialReportUpdate, new Object[] { report.getType(), report.getYear(),
									report.getFilepath(), report.getLmodify(), reportfile.getPath().replace(toDir, "/") });
					try {
						FileUtils.copyFile(reportfile, targetfile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
	
	/**
	 * 从目录的名称解析year和type
	 * @param dirname
	 * @return data[0]:year data[1]:type
	 */
	public static Object[] dirnameParser(String dirname){
		Object[] data = new Object[2];
		Matcher m = titlePattern.matcher(dirname);
		if (m != null && m.find()) {
			/*for (int i = 0; i <= m.groupCount(); i++) {
				System.out.println("group "+i+": "+m.group(i));
			}*/
			String year = m.group(1);
			String type = m.group(2);
			byte quarter_type = -1; // error type
			if (type.equalsIgnoreCase("zq")) { //半年
				quarter_type = 2;
			} else if (type.equalsIgnoreCase("nd")) {//全年
				quarter_type = 4;
			} else if (type.equalsIgnoreCase("jb")) {//季度
				quarter_type = Byte.parseByte(m.group(3).trim()); //1,3
			}
			data[0] = year;
			data[1] = quarter_type;
			//System.out.println(year + " " + quarter_type);
		}
		return data;
	}
	
	/**
	 * 从研报的正文解析year和type
	 * @param dirname
	 * @return data[0]:year data[1]:type
	 */
	public static Object[] txtTilteParser(String txtfile){
		Object[] data = new Object[2];
		Matcher m = yeartypePattern.matcher(txtfile);
		if (m != null && m.find()) {
			String year = yearParser(m.group(1));
			String typestr = m.group(2);
			byte quarter_type = -1; // error type
			if(typestr.equals("第一季度")){
				quarter_type = 1;
			} else if (typestr.equals("中期")){
				quarter_type = 2;
			} else if (typestr.equals("第三季度")){
				quarter_type = 3;
			} else if (typestr.equals("年度")){
				quarter_type = 4;
			} 
			data[0] = year;
			data[1] = quarter_type;
			//System.out.println(year + " " + quarter_type);
		}
		return data;
	}
	
	/**
	 * 非阿拉伯数字转换为阿拉伯数字
	 * @param str 
	 * @return
	 */
	public static String yearParser(String str){
		if(str==null){
			return "";
		}
		String LatinNum="0123456789";
		String SBCNum="０１２３４５６７８９";
		String bigNum="零一二三四五六七八九";
		StringBuffer number = new StringBuffer();
		
		char[] cArray = str.toCharArray();
		for (char chr : cArray) {
			int cc = -1;
			if((cc = LatinNum.indexOf(chr))!=-1 || (cc = bigNum.indexOf(chr))!=-1 || (cc = SBCNum.indexOf(chr))!=-1){
				number.append(cc);
			} else {
				throw new IllegalArgumentException("非法数字: "+str);
			}
		}
		return number.toString();
	}
	
	/**
	 * 获取研报的内容
	 * @param file
	 * @param encoding
	 * @return
	 */
	public static String getString(File file, String encoding) {
		String txtfile = null;
		if(file.getName().toLowerCase().endsWith(txt)){
			try {
				txtfile = FileUtils.readFileToString(file, encoding);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(file.getName().toLowerCase().endsWith(pdf)){
			boolean sort = false;
			int startPage = 1;
			int endPage = Integer.MAX_VALUE;
			PDDocument document = null;
			try {
				document = PDDocument.load(file);
				PDFTextStripper stripper = new PDFTextStripper();
				stripper.setSortByPosition(sort);
				stripper.setStartPage(startPage);
				stripper.setEndPage(endPage);
				txtfile = stripper.getText(document);
			} catch (Exception e) {
				System.out.print(e.getMessage());
				e.printStackTrace();
			} finally {
				try {
					document.close();
				} catch (Exception e) {
					System.out.print(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return txtfile;
	}
	
	/**
	 * 是否包含中文
	 * @param file
	 * @param encoding e.g.gbk
	 * @return 
	 */
	public static boolean isChinese(File file, String encoding){
		String txtFile = getString(file, encoding);
		if(txtFile==null){
			return false;
		}
		Matcher result = chinesePattern.matcher(txtFile);
		return result.find();
	}
	
	public static void main(String[] args) {
		TidyFinancialReportTask task = new TidyFinancialReportTask();
		task.cat("/data/reports/1990-1995");
//		task.cat("/data/reports/1995");
//		System.out.println(task.numberParser(null));
		//task.run();
		System.exit(0);
	}

}
