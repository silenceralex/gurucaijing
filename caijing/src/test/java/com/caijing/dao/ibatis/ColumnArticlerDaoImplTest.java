package com.caijing.dao.ibatis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
		dao=(ColumnArticleDao)ContextFactory.getBean("columnArticleDao");

	}
	
	public void testInsert() {
//		ColumnArticle ca=new ColumnArticle();
//		ca.setContent("haha");
//		ca.setLink("http://www.baidu.com");
//		ca.setSrc("blog");
//		ca.setName("liujintao");
//		ca.setTitle("www");
//		Integer obj=(Integer) dao.insert(ca);
		
		
		
//		assertNotNull(obj);
//		assertEquals(1,obj.intValue());
//		System.out.println(ToStringBuilder.reflectionToString(obj));
		
		
//		String   dateString   =   "Wed, 01 Sep 2010 08:22:37 GMT+8"; 
//        String   pattern   =      "EEE, dd MMM yyyy HH:mm:ss z";
//		
//		
//        String   dateString   =   "Tue, 31 Aug 2010 15:47:37 GMT+8"; 
//        String   pattern   =      "EEE, dd MMM yyyy HH:mm:ss 'GMT+8'";
//        
//        SimpleDateFormat   formatter   =   new   SimpleDateFormat(pattern,Locale.ENGLISH); 
//        try   { 
//                Date   date   =   formatter.parse(dateString); 
//                System.out.println( "date   =   "   +   date); 
//        } 
//        catch   (Exception   e)   { 
//                throw   new   RuntimeException(e.getMessage()); 
//        }
//        
//        
//        String str = "Wed, 01 Sep 2010 08:22:37 GMT+8";  
//        SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'Z",Locale.ENGLISH);
}
	
	public void testlink(){
		List<String> linklist = dao.getAllArticlelink();
		for(String tmp:linklist){
			System.out.println(tmp);
		}
	}
	
	
	public void testGetAnayzerByAgency() {
//		List list=(List) dao.getAnalyzersByAgency("中金公司");
//		Analyzer analyzer=(Analyzer)list.get(0);
//		assertNotNull(list);
//		assertEquals("应该拿到数据白宏炜才对", "白宏炜",analyzer.getName());
//		assertEquals("应该拿到数据中金公司才对", "中金公司",analyzer.getAgency());
//		System.out.println(ToStringBuilder.reflectionToString(analyzer));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
