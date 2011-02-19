package com.caijing.task;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.caijing.util.Command;
import com.caijing.util.ContextFactory;

/**
 * 删除历史研报的数据
 */
public class DeleteOldPaperTask {

	public void delAllOldReports() {
		String queryreportsql = "select rid from report where filepath like \"/oldhtml/papers/%\"";
		//String queryreportsql = "select rid from report where filepath like \"/oldhtml/papers/%\" and ptime>\"2011-01-28 9:30:00\"";
		String deletereportsql = "delete from report where rid=?";
		String deleterecommendstocksql = "delete from recommendstock where reportid=?";
		String deleteoldhtml = "sh /data/shells/rm.sh";
		
		System.out.println("==delAllOldReports task start==");
		JdbcTemplate jdbcTemplate = (JdbcTemplate) ContextFactory.getBean("jdbcTemplate");
		List<Map<String, String>> rows = jdbcTemplate.queryForList(queryreportsql);
		if (rows != null && rows.size() > 0) {
			for (Map<String, String> row : rows) {
				String rid = (String) row.get("rid");
				System.out.print(rid);
				System.out.print(" " + jdbcTemplate.update(deleterecommendstocksql, new Object[] { rid }));
				System.out.print(" " + jdbcTemplate.update(deletereportsql, new Object[] { rid }));
				StringWriter sw = new StringWriter();
				Command.run(deleteoldhtml, sw);
				System.out.println(" "+sw.toString());
			}
		}
		System.out.println("==delAllOldReports task exit==");
	}

	public void delSpecialSanameOldReports(String saname){
		String queryreportsql = "select rid from report where filepath like \"/oldhtml/papers/%\" and saname=?";
		String deletereportsql = "delete from report where rid=?";
		String deleterecommendstocksql = "delete from recommendstock where reportid=?";
		String deleteoldhtml = "rm /home/oldhtml/";
		
		System.out.println("==delSpecialSaname task start==");
		JdbcTemplate jdbcTemplate = (JdbcTemplate) ContextFactory.getBean("jdbcTemplate");
		List<Map<String, String>> rows = jdbcTemplate.queryForList(queryreportsql, new Object[] { saname });
		if (rows != null && rows.size() > 0) {
			for (Map<String, String> row : rows) {
				String rid = (String) row.get("rid");
				System.out.print(rid);
				System.out.print(" " + jdbcTemplate.update(deleterecommendstocksql, new Object[] { rid }));
				System.out.print(" " + jdbcTemplate.update(deletereportsql, new Object[] { rid }));
				StringWriter sw = new StringWriter();
				deleteoldhtml = deleteoldhtml+rid+".pdf";
				Command.run(deleteoldhtml, sw);
				System.out.println(" "+sw.toString());
			}
		}
		System.out.println("==delSpecialSaname task exit==");
	}
	
	public static void main(String[] args) {
		DeleteOldPaperTask task = new DeleteOldPaperTask();
		if(args[0].equals("-all")){
			task.delAllOldReports();
		}
		if(args[0].equals("-saname")){
			String saname = args[1];
			task.delSpecialSanameOldReports(saname);
		}
		System.exit(0);
	}
}
