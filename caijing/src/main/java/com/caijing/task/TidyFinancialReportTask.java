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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import com.caijing.dao.FinancialReportDao;
import com.caijing.util.ContextFactory;

/**
 * ����Ʊ����������£�
 * 
 * 1. ���࣬���ڸ���������͵�Ŀ¼��
 * 2. ���ݿ��¼
 */
public class TidyFinancialReportTask {

	@Autowired
	@Qualifier("financialReportDao")
	private FinancialReportDao financialReportDao = null;
	
	String fromRootDir = "/data/report/";
	Pattern stockcodePattern = Pattern.compile("(((002|000|300|600)[\\d]{3})|60[\\d]{4})", Pattern.CASE_INSENSITIVE
			| Pattern.DOTALL | Pattern.UNIX_LINES);
	Pattern titlePattern = Pattern.compile("(\\d{4})(jb|nd|zq)_?(\\d{1})?", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
			| Pattern.UNIX_LINES);

	final SimpleDateFormat timeFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	String stocknamequery = "select stockname from stock where stockcode=?";

	//$from: `/report_original_name`.pdf $to: report /year/`type`/`stockcode`.pdf
	//String from = "/data/report/";
	String toDir = "/data/reports/";

	public void run() {
		JdbcTemplate jdbcTemplate = (JdbcTemplate) ContextFactory.getBean("jdbcTemplate");
		
		File[] yearDir = new File(fromRootDir).listFiles();
		for (File dir : yearDir) {
			if (dir.isDirectory()) {
				String dirname = dir.getName();
				System.out.println("> yearDir: " + dirname);

				String year = null;
				String type = null;
				int quarter_type = 0;

				Matcher m = titlePattern.matcher(dirname);
				if (m != null && m.find()) {
					/* //test
					for (int i = 0; i <= m.groupCount(); i++) {
						System.out.println("group "+i+": "+m.group(i));
					}*/
					year = m.group(1);
					type = m.group(2);
					if (type.equalsIgnoreCase("nd")) { //����
						quarter_type = 2;
					} else if (type.equalsIgnoreCase("zq")) {//ȫ��
						quarter_type = 4;
					} else if (type.equalsIgnoreCase("jb")) {//����
						quarter_type = Integer.parseInt(m.group(3).trim()); //1,3
					}
				}
				File reportDir = new File(dir, "reports");
				File[] reportFiles = TidyFinancialReportTask.listFileBySuffix(reportDir, ".pdf");
				if (reportFiles != null) {
					for (File report : reportFiles) {
						System.out.println("==> report: " + report.getPath());
						String report_title = report.getName();
						String stockcode = report_title.split("_")[0];
						String stockname = (String) jdbcTemplate.queryForObject(stocknamequery, new Object[]{stockcode}, String.class);
						String filepath = "/" + year + "/" + quarter_type + "/" + stockcode + ".pdf";
						Date lmodify = new Date();
						System.out.println("[" + filepath + ", " + stockname + timeFORMAT.format(lmodify) + "]");
						
						//cp report
						try {
							FileUtils.copyFile(report, new File(toDir, filepath));
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						//to database
						
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
	}

}
