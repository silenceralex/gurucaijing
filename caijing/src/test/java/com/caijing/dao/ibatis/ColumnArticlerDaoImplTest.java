package com.caijing.dao.ibatis;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caijing.dao.ColumnArticleDao;
import com.caijing.domain.ColumnArticle;
import com.caijing.util.ContextFactory;

public class ColumnArticlerDaoImplTest extends TestCase {
	ColumnArticleDao dao;
	
	private Log log=LogFactory.getLog(getClass());
	protected void setUp() throws Exception {
		super.setUp();
		dao=(ColumnArticleDao)ContextFactory.getBean("columnarticleDao");

	}
	
	public void testInsert() {
		ColumnArticle ca=new ColumnArticle();
		ca.setContent("haha");
		ca.setLink("http://www.baidu.com");
		ca.setSrc("blog");
		ca.setName("liujintao");
		ca.setTitle("www");
		Integer obj=(Integer) dao.insert(ca);
//		assertNotNull(obj);
//		assertEquals(1,obj.intValue());
//		System.out.println(ToStringBuilder.reflectionToString(obj));
}
	
	
	public void testGetAnayzerByAgency() {
//		List list=(List) dao.getAnalyzersByAgency("�н�˾");
//		Analyzer analyzer=(Analyzer)list.get(0);
//		assertNotNull(list);
//		assertEquals("Ӧ���õ����ݰ׺�쿲Ŷ�", "�׺��",analyzer.getName());
//		assertEquals("Ӧ���õ������н�˾�Ŷ�", "�н�˾",analyzer.getAgency());
//		System.out.println(ToStringBuilder.reflectionToString(analyzer));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
