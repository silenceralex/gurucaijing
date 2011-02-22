package com.caijing.task;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.JdbcTemplate;

import com.caijing.util.ContextFactory;

/**
 * 0. 纠正和双向替换Stockcode和Stockname
 * 1. title，saname，stockcode均为null的report，删除对应的report和recommendstock的数据
 * 2. /home/oldhtml/换成/oldhtml/ eg./home/oldhtml/papers/
 */
public class Stockcode2StocknameTask {

	private Pattern stockcodePattern = Pattern.compile("(((002|000|300|600)[\\d]{3})|60[\\d]{4})",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);	
	private static String Prifix = "/oldhtml/papers/";
	
	String queryallstocksql = "select stockcode,stockname from stock";
	String queryallrecommendstocksql = "select stockcode,stockname from recommendstock";
	String queryallreportsql = "select stockcode,stockname,title from report where filepath like \""+Prifix+"%\"";
	
	
	//String queryreportsql = "select rid,stockcode,stockname,title from report where filepath like \""+Prifix+"%\" limit 20000";
	String updatereportsql = "update report set stockcode=?,stockname=? where rid=?";

	String deletereportsql = "delete from report where rid=?";
	String deleterecommendstocksql = "delete from recommendstock where reportid=?";
	
	String queryreportsql = "select rid,stockcode,stockname,title,filepath from report where filepath like \""+Prifix+"%\" and (stockcode=? or stockname=?)";

	@SuppressWarnings("unchecked")
	public void run() {
		System.out.println("==task start==");
		JdbcTemplate jdbcTemplate = (JdbcTemplate) ContextFactory.getBean("jdbcTemplate");

		List<Map<String, String>> stocks = jdbcTemplate.queryForList(queryallstocksql);
		//List<Map<String, String>> reports = jdbcTemplate.queryForList(queryreportsql);

		if (stocks != null && stocks.size() > 0) {
			for (Map<String, String> stock : stocks) {
				String stockcode = stock.get("stockcode").trim();
				String stockname = stock.get("stockname").trim();
				System.out.println("==>stock: " + stockcode + " " + stockname);
				
				List<Map<String, String>> reports = jdbcTemplate.queryForList(queryreportsql, new Object[]{stockcode, stockname});
				
				if (reports != null && reports.size() > 0) {
					for (Map<String, String> report : reports) {
						String rid = report.get("rid");
						String code = report.get("stockcode");
						if(code!=null){
							code = code.trim();
						}
						String name = report.get("stockname");
						if(name!=null){
							name = name.trim();
						}
						String title = report.get("title");
						if(title!=null){
							title = title.trim();
						}
						System.out.println("==>report: "+  rid + " " + code + " " + name);
						
						boolean equalcode = stockcode.equalsIgnoreCase(code);
						boolean equalname = stockname.equalsIgnoreCase(name);
						boolean isvalidcode = !(code == null || code.trim().length() == 0);
						if (isvalidcode){
							Matcher m = stockcodePattern.matcher(code);
							if (m != null && m.find()) {
								isvalidcode = true;
							}
						}
						boolean isvalidname = !(name == null || name.length() == 0);
						boolean isvalidtitle = !(title == null || title.length() == 0);
						
						if(equalcode && equalname){
							reports.remove(report);
							continue;
						} else if(!equalcode && equalname){
							//stockcode不合法，stockname合法
							System.out.println("update " + rid + "(" + "!" + stockcode + "," + name + ")" + title);
							//System.out.println("update " + rid +jdbcTemplate.update(updatereportsql, new Object[] { stockcode, stockname, rid }));
							reports.remove(report);
							continue;
						} else if(equalcode && !equalname){
							//stockcode合法，stockname不合法
							System.out.println("update " + rid + "(" + code + "," + "!" + stockname + ")" + title);
							//System.out.println("update " + rid +jdbcTemplate.update(updatereportsql, new Object[] { stockcode, stockname, rid }));
							reports.remove(report);
							continue;
						}
						
						/*
						if (!isvalidcode && isvalidname) {
							//stockcode为空，stockname不为空
							System.out.println("update " + rid + "(" + "!" + stockcode + "," + name + ")" + title);
							//System.out.println("update " + rid +jdbcTemplate.update(updatereportsql, new Object[] { stockcode, stockname, rid }));
						} else if (isvalidcode && !isvalidname) {
							//stockcode不为空，stockname为空
							System.out.println("update " + rid + "(" + code + "," + "!" + stockname + ")" + title);
							//System.out.println("update " + rid +jdbcTemplate.update(updatereportsql, new Object[] { stockcode, stockname, rid }));
						} else if (!isvalidcode && !isvalidname && !isvalidtitle) {
							System.out.println("delete " + rid + "(" + code + "," + name + ")" + title);
							//System.out.print(" " + jdbcTemplate.update(deleterecommendstocksql, new Object[] { rid }));
							//System.out.print(" " + jdbcTemplate.update(deletereportsql, new Object[] { rid }));
						}*/

					}
				}
				
				/*
				System.out.println("无法处理的历史研报: ");
				for (Map<String, String> report : reports) {
					String rid = report.get("rid");
					String code = report.get("stockcode")==null?null:report.get("stockcode").trim();
					String name = report.get("stockname")==null?null:report.get("stockname").trim();
					String title = report.get("title")==null?null:report.get("title").trim();
					System.out.println(rid + " " + code + " " + name+" "+ title);
				}*/
			}
		}

		System.out.println("==task exit==");
	}

	public void cleanReportAndRecommendstock(){
		String queryallreportsql = "select stockcode,stockname,title from report where filepath like \""+Prifix+"%\"";
		String updatereportsql = "update report set stockcode=?,stockname=?,title=? where rid=?";
		String updaterecommendstocksql = "update recommendstock set stockcode=?,stockname=?,title=? where reportid=?";
		
		JdbcTemplate jdbcTemplate = (JdbcTemplate) ContextFactory.getBean("jdbcTemplate");
		List<Map<String, String>> reports = jdbcTemplate.queryForList(queryallreportsql);
		for (Map<String, String> report : reports) {
			//report
		}
	}
	
	public static void main(String[] args) {
		Stockcode2StocknameTask task = new Stockcode2StocknameTask();
		task.run();
		//System.out.println("aa".equals(null));
		System.exit(0);
	}

}
