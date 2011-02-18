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
	
	String querystocksql = "select stockcode,stockname from stock";
	//String queryreportsql = "select rid,stockcode,stockname,title from report where filepath like \"/home/oldhtml/papers/%\" limit 10";
	String updatereportsql = "update report set stockcode=?,stockname=? where rid=?";

	String deletereportsql = "delete from report where rid=?";
	String deleterecommendstocksql = "delete from recommendstock where reportid=?";
	
	String queryreportsql = "select rid,stockcode,stockname,title,filepath from report where filepath like \"/oldhtml/papers/%\" and (stockcode=? or stockname=?) limit 10";

	
	@SuppressWarnings("unchecked")
	public void run() {
		System.out.println("==task start==");
		JdbcTemplate jdbcTemplate = (JdbcTemplate) ContextFactory.getBean("jdbcTemplate");

		List<Map<String, String>> stocks = jdbcTemplate.queryForList(querystocksql);
		//List<Map<String, String>> reports = jdbcTemplate.queryForList(queryreportsql);

		if (stocks != null && stocks.size() > 0) {
			for (Map<String, String> stock : stocks) {
				String stockcode = stock.get("stockcode");
				String stockname = stock.get("stockname");
				System.out.println("stock: " + stockcode + " " + stockname);
				List<Map<String, String>> reports = jdbcTemplate.queryForList(queryreportsql, new Object[]{stockcode, stockname});
				
				if (reports != null && reports.size() > 0) {
					for (Map<String, String> report : reports) {
						String rid = report.get("rid");
						String code = report.get("stockcode");
						String name = report.get("stockname");
						String title = report.get("title");
						//System.out.println(rid + " " + code + " " + name);
						
						boolean isnotcode = (code == null || code.trim().length() == 0);
						if (!isnotcode){
							Matcher m = stockcodePattern.matcher(code);
							if (m != null && m.find()) {
								isnotcode = false;
							}
						}

						boolean isnotname = (name == null || name.trim().length() == 0);
						boolean isnottitle = (title == null || title.trim().length() == 0);

						if (isnotcode && !isnotname) {
							//stockcode为空，stockname不为空
							System.out.println("update " + rid + "(" + "!" + stockcode + "," + name + ")" + title);
							//System.out.println("update " + rid +jdbcTemplate.update(updatereportsql, new Object[] { stockcode, stockname, rid }));
						} else if (!isnotcode && isnotname) {
							//stockcode不为空，stockname为空
							System.out.println("update " + rid + "(" + code + "," + "!" + stockname + ")" + title);
							//System.out.println("update " + rid +jdbcTemplate.update(updatereportsql, new Object[] { stockcode, stockname, rid }));
						} else if (isnotcode && isnotname && isnottitle) {
							System.out.println("delete " + rid + "(" + code + "," + name + ")" + title);
							//System.out.print(" " + jdbcTemplate.update(deleterecommendstocksql, new Object[] { rid }));
							//System.out.print(" " + jdbcTemplate.update(deletereportsql, new Object[] { rid }));
						}

					}
				}
			}
		}

		System.out.println("==task exit==");
	}

	public static void main(String[] args) {
		Stockcode2StocknameTask task = new Stockcode2StocknameTask();
		task.run();
		System.exit(0);
	}

}
