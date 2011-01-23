package com.caijing.task;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.caijing.util.Command;
import com.caijing.util.ContextFactory;

/**
 * 删除历史研报的数据
 * @author liliren
 *
 */
public class DeleteOldPaperTask {

	String queryreportsql = "select rid from report where filepath like \"/home/oldhtml/papers/%\"";
	String deletereportsql = "delete from report where rid=?";
	String deleterecommendstocksql = "delete from recommendstock where reportid=?";
	String deleteoldhtml = "sh /data/shells/rm.sh";

	public void run() {
		System.out.println("==task start==");
		JdbcTemplate jdbcTemplate = (JdbcTemplate) ContextFactory.getBean("jdbcTemplate");
		List<Map<String, String>> rows = jdbcTemplate.queryForList(queryreportsql);
		if (rows != null && rows.size() > 0) {
			for (Map<String, String> row : rows) {
				String rid = (String) row.get("rid");
				System.out.print(rid);
				System.out.print(" " + jdbcTemplate.update(deletereportsql, new Object[] { rid }));
				System.out.println(" " + jdbcTemplate.update(deleterecommendstocksql, new Object[] { rid }));
				StringWriter sw = new StringWriter();
				Command.run(deleteoldhtml, sw);
				System.out.println(sw.toString());
			}
		}
		System.out.println("==task exit==");
	}

	public static void main(String[] args) {
		DeleteOldPaperTask task = new DeleteOldPaperTask();
		task.run();
	}
}
