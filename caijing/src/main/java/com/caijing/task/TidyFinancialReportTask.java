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
	
	String fromRootDir = "/data/report/";
	String toDir = "/data/reports/";
	Pattern stockcodePattern = Pattern.compile("^(((002|000|300|600)[\\d]{3})|60[\\d]{4})$", Pattern.CASE_INSENSITIVE
			| Pattern.DOTALL | Pattern.UNIX_LINES);
	Pattern titlePattern = Pattern.compile("(\\d{4})(jb|nd|zq)_?(\\d{1})?", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
			| Pattern.UNIX_LINES);

	final SimpleDateFormat timeFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	String stocknamequery = "select stockname from stock where stockcode=?";
	String financialReportInsert = "insert into financialreport (reportid, title, type, year, stockcode, stockname, filepath, lmodify, status) " +
			"values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	public void run() {
		JdbcTemplate jdbcTemplate = (JdbcTemplate) ContextFactory.getBean("jdbcTemplate");
		
		File[] yearDir = new File(fromRootDir).listFiles();
		for (File dir : yearDir) {
			if (dir.isDirectory()) {
				String dirname = dir.getName();
				System.out.println("> yearDir: " + dirname);

				String year = null;
				String type = null;
				byte quarter_type = 0;

				Matcher m = titlePattern.matcher(dirname);
				if (m != null && m.find()) {
					/* //test
					for (int i = 0; i <= m.groupCount(); i++) {
						System.out.println("group "+i+": "+m.group(i));
					}*/
					year = m.group(1);
					type = m.group(2);
					if (type.equalsIgnoreCase("zq")) { //半年
						quarter_type = 2;
					} else if (type.equalsIgnoreCase("nd")) {//全年
						quarter_type = 4;
					} else if (type.equalsIgnoreCase("jb")) {//季度
						quarter_type = Byte.parseByte(m.group(3).trim()); //1,3
					}
				}
				File reportDir = new File(dir, "reports");
				File[] reportFiles = TidyFinancialReportTask.listFileBySuffix(reportDir, ".pdf");
				if (reportFiles != null) {
					for (File reportfile : reportFiles) {
						System.out.println("==> report: " + reportfile.getPath());
						String report_title = reportfile.getName();
						String stockcode = report_title.split("_")[0];
						String stockname = null; 
						byte status = 1;
						m = stockcodePattern.matcher(stockcode);
						if (m != null && m.find()){//例外 异常数据
							status = 0;
							stockname = (String) jdbcTemplate.queryForObject(stocknamequery, new Object[]{stockcode}, String.class);
						}
						String filepath = "/" + year + "/" + quarter_type + "/" + stockcode + ".pdf";
						Date lmodify = new Date();
						System.out.println("[" + filepath + ", " + stockname +", "+ timeFORMAT.format(lmodify) + "]");
						
						//cp report
						try {
							//TODO 英文
							if(reportfile.exists()){
								continue;
							}
							FileUtils.copyFile(reportfile, new File(toDir, filepath));
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						//to database
						FinancialReport report = new FinancialReport();
						report.setReportid(ServerUtil.getid());
						report.setStockcode(stockcode);
						report.setStockname(stockname);
						report.setTitle(report_title);
						report.setType(quarter_type);
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

	public static File[] listFileBySuffix(File dir, String suffix) {
		IOFileFilter fileFilter1 = new SuffixFileFilter(suffix, IOCase.INSENSITIVE);
		IOFileFilter fileFilter2 = new NotFileFilter(DirectoryFileFilter.INSTANCE);
		FilenameFilter filenameFilter = new AndFileFilter(fileFilter1, fileFilter2);
		return dir.listFiles(filenameFilter);
	}

	public static void main(String[] args) {
		TidyFinancialReportTask task = new TidyFinancialReportTask();
		task.run();
		System.exit(0);
	}

}
