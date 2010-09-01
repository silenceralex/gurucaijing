package com.caijing.dao.ibatis;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caijing.dao.AnalyzerDao;
import com.caijing.domain.Analyzer;
import com.caijing.util.ContextFactory;
import com.caijing.util.FileUtil;
import com.caijing.util.ServerUtil;

public class AnalyzerDaoImplTest extends TestCase {
	AnalyzerDao dao;
	
	private Log log=LogFactory.getLog(getClass());
	protected void setUp() throws Exception {
		super.setUp();
		dao=(AnalyzerDao)ContextFactory.getBean("analyzerDao");

	}
	
	public void testInsert() {
		String content = FileUtil.read("G:\\workspace\\caijing\\src\\main\\resources\\analyzer.txt", "GBK");
		System.out.println("content: "+content);
		String[] lines=content.split("\n");
		for(String line:lines){
			String[] strs=line.split(" ");
			Analyzer analyzer=new Analyzer();
			analyzer.setAid(ServerUtil.getid());
			analyzer.setAgency(strs[1]);
			analyzer.setName(strs[0]);
			analyzer.setPtime(new Date());
			analyzer.setLmodify(new Date());
			analyzer.setIndustry(strs[2]);
			analyzer.setLevel(3);
			Integer obj=(Integer) dao.insert(analyzer);
//			assertNotNull(obj);
//			assertEquals(1,obj.intValue());
		}
		System.out.println("lines: "+lines.length);
}
	
	
//	public void testGetAnayzerByAgency() {
//		List list=(List) dao.getAnalyzersByAgency("中金公司");
//		Analyzer analyzer=(Analyzer)list.get(0);
//		assertNotNull(list);
//		assertEquals("应该拿到数据白宏炜才对", "白宏炜",analyzer.getName());
//		assertEquals("应该拿到数据中金公司才对", "中金公司",analyzer.getAgency());
//		System.out.println(ToStringBuilder.reflectionToString(analyzer));
//	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
