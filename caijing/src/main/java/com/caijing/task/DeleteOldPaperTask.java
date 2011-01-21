package com.caijing.task;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.caijing.util.Command;
import com.caijing.util.ContextFactory;

/**
 * ɾ����ʷ�б�������
 * @author liliren
 *
 */
public class DeleteOldPaperTask {

	String queryreportsql = "select rid from report where filepath like \"/home/oldhtml/papers/%\" limit 1";
	String deletereportsql = "delete from report where rid=?";
	String deleterecommendstocksql = "delete from recommendstock where reportid=?";
	String deleteoldhtml = "sh /data/shells/rm.sh";
	
	public void run() {
		JdbcTemplate jdbcTemplate = (JdbcTemplate) ContextFactory.getBean("jdbcTemplate");
		List<Map<String, String>> rows = jdbcTemplate.queryForList(queryreportsql);
		for (Map<String, String> row : rows) {
			String rid = (String) row.get("rid");
			System.out.println(rid);
			jdbcTemplate.update(deletereportsql,new Object[]{rid});
			jdbcTemplate.update(deleterecommendstocksql,new Object[]{rid});
			StringWriter sw = new StringWriter();
			Command.run(deleteoldhtml, sw);
			System.out.println(sw.toString());
		}
	}
	
	public static void main(String[] args) {
		DeleteOldPaperTask task = new DeleteOldPaperTask();
		task.run();
	}
}
