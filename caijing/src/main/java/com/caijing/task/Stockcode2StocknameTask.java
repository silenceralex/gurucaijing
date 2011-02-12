package com.caijing.task;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.caijing.util.ContextFactory;

/**
 * 0. ������˫���滻Stockcode��Stockname
 * 1. title��saname��stockcode��Ϊnull��report��ɾ����Ӧ��report��recommendstock������
 * 2. /home/oldhtml/����/oldhtml/ eg./home/oldhtml/papers/
 */
public class Stockcode2StocknameTask {

	String querystocksql = "select stockcode,stockname from stock";
	String queryreportsql = "select rid,stockcode,stockname,title from report where filepath like \"/home/oldhtml/papers/%\" limit 10";
	String updatereportsql = "update report set stockcode=?,stockname=? where rid=?";

	String deletereportsql = "delete from report where rid=?";
	String deleterecommendstocksql = "delete from recommendstock where reportid=?";

	public void run() {
		System.out.println("==task start==");
		JdbcTemplate jdbcTemplate = (JdbcTemplate) ContextFactory.getBean("jdbcTemplate");

		List<Map<String, String>> stocks = jdbcTemplate.queryForList(querystocksql);
		List<Map<String, String>> reports = jdbcTemplate.queryForList(queryreportsql);

		if (stocks != null && stocks.size() > 0) {
			for (Map<String, String> stock : stocks) {
				String stockcode = stock.get("stockcode");
				String stockname = stock.get("stockname");
				System.out.println("stock: " + stockcode + " " + stockname);

				if (reports != null && reports.size() > 0) {
					for (Map<String, String> report : reports) {
						String rid = report.get("rid");
						String code = report.get("stockcode");
						String name = report.get("stockname");
						String title = report.get("title");
						//System.out.println(rid + " " + code + " " + name);

						boolean iscode = (code == null || code.trim().length() == 0);
						boolean isname = (name == null || name.trim().length() == 0);
						boolean istitle = (title == null || title.trim().length() == 0);

						if (iscode && !isname) {
							//stockcodeΪ�գ�stockname��Ϊ��
							System.out.println("update " + rid + "(" + "!" + stockcode + "," + name + ")" + title);
							//System.out.println("update " + rid +jdbcTemplate.update(updatereportsql, new Object[] { stockcode, stockname, rid }));
						} else if (!iscode && isname) {
							//stockcode��Ϊ�գ�stocknameΪ��
							System.out.println("update " + rid + "(" + code + "," + "!" + stockname + ")" + title);
							//System.out.println("update " + rid +jdbcTemplate.update(updatereportsql, new Object[] { stockcode, stockname, rid }));
						} else if (iscode && isname && istitle) {
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
