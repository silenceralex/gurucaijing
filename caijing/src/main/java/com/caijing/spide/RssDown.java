package com.caijing.spide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.ColumnArticleDao;
import com.caijing.domain.ColumnArticle;
import com.caijing.util.ContextFactory;

public class RssDown {
	private static Log logger = LogFactory.getLog(RssDown.class);
	ColumnArticle ar=null;
	List<ColumnArticle> arlist =null;
	
	@Autowired
	@Qualifier("columnArticleDao")
	private ColumnArticleDao dao = null;
//	ColumnArticleDao dao=(ColumnArticleDao)ContextFactory.getBean("columnArticleDao");
	HashMap<String ,String> db = new HashMap<String,String>();
	
	public int  initRssDown(){
		List<String> listlink = null;
		try{
			listlink = dao.getAllArticlelink();
		}catch(Exception e){
			return -1;
		}
		for(String tmp:listlink){
			if(null == db.get(tmp)){
				db.put(tmp, "ok");			
			}
		}
		return 0;
	}

	
	void getRssArList(String siteurl,String peoplename, RssItem site){
		String content = Downloads.downByHttpclient(siteurl, site.encoding);
		if(content == null) return;
		if(site != null){
			HtmlParser p = new HtmlParser(content);
			 int event = p.getEventType();
			 while (true) {
			 switch (event) {
			 case HtmlParser.START_DOCUMENT:
				 
				 arlist = new ArrayList<ColumnArticle>();
//				 logger.debug("Start Document.");
				 break;
			case HtmlParser.START_ELEMENT:
//				 logger.debug("Start Element: " + p.getName());
				 			 
				 if(site.item.equals(p.getName())){
					 ar = new ColumnArticle();				 
				 }else if(site.link.equals(p.getName())){
					 if(p.hasnext()==1){
						 int tmpev = p.next();
						 String ti="";
						 if(tmpev == HtmlParser.CHARACTERS){
							 ti = p.getText();
						 }else if(tmpev == HtmlParser.START_CDATA ){							 
							 ti = p.getCDATA();
						 }
						 if(ar != null){
							 ar.setLink(ti);
//							 logger.debug(ar.link);
						 }
					 }
				 }else if(site.title.equals(p.getName())){
					 if(p.hasnext()==1){
						 int tmpev = p.next();
						 String ti="";
						 if(tmpev == HtmlParser.CHARACTERS){
							 ti = p.getText();
						 }else if(tmpev == HtmlParser.START_CDATA ){							 
							 ti = p.getCDATA();
						 }
						 if(ar != null){
							 ar.setTitle(ti);
						 }
					 }
				 }else if(site.date.equals(p.getName())){
					 if(p.hasnext()==1){
						 int tmpev = p.next();
						 String ti="";
						 if(tmpev == HtmlParser.CHARACTERS){
							 ti = p.getText();
						 }else if(tmpev == HtmlParser.START_CDATA ){							 
							 ti = p.getCDATA();
						 }
						 if(ar != null){
							 Date date=ContentTool.strTime2Date(ti,site.tmformat);
							 ar.setPtime(date);
							 logger.debug("data="+date);
						 }
					 }
				 }
				 
				 break;
				 case HtmlParser.CHARACTERS:
				 if (p.isWhiteSpace()==1)
				 break;
				 
//				 logger.debug("Text: " + p.getText());
				 break;
				 case HtmlParser.END_ELEMENT:
//				 logger.debug("End Element:" + p.getName());
				 if("item".equals((p.getName()))){
					 if(arlist != null ) arlist.add(ar);
					 ar = null;
				 }
				 break;
				 case HtmlParser.END_DOCUMENT:
//				 logger.debug("End Document.");
				 break;
				 default :
//					 logger.debug("error");
				 }
				 
				 if (p.hasnext()==0)
				 break;
			
				 event = p.next();
			 }
		}
		
		if(arlist != null){
			String artext=null;
			for(ColumnArticle tmp:arlist){
				if(db.get(tmp.getLink())!=null){
					logger.debug("NOT down" +tmp.getLink()+tmp.getTitle());
					break;
				}else{
					logger.debug("down" +tmp.getLink()+tmp.getTitle());
					db.put(tmp.getLink(), "ok");
				}
				if(site.name.equals("sina")||site.name.equals("caijing")){
					artext=Downloads.downByHttpclient(tmp.getLink(), site.encoding);
					artext=ContentTool.getArticleText(artext, site);
					//logger.debug(artext);
					tmp.setContent(artext);
					tmp.setName(peoplename);						
					tmp.setSrc("blog");
					try{
						dao.insert(tmp);	
					}catch (Exception e){
						logger.debug("there is a insert error here");
						continue;
						
					}
				}
			}
		}	
	}
	
	public static void main(String[] args) throws IOException {
		int rlt=0;
//		ReadConfig r = new ReadConfig();
//		r.GetAllSite("D:\\opensource\\guru\\caijing\\src\\main\\java\\com\\caijing\\spide\\rsssite.xml","utf-8");
//		r.GetAllPeople("D:\\opensource\\guru\\caijing\\src\\main\\java\\com\\caijing\\spide\\focuspeople.xml","utf-8");
//		RssDown down = new RssDown();
//		ColumnArticleDao dao=(ColumnArticleDao)ContextFactory.getBean("columnArticleDao");
//		down.setDao(dao);
//		rlt = down.initRssDown();
//		if(rlt <0){
//			logger.debug("init rssdown failed");
//			return ;
//		}
//		for(Economistor people:r.allpeople){
//			RssItem site=r.getSiteByName(people.sitename);
//			logger.debug("start ...... "+people.name+".......");
//			down.getRssArList(people.siteurl,people.name,site);
//			logger.debug("end ...... "+people.name+".......");
//			logger.debug("");
//		}
		
		
		
		
		RssItem onesite = new RssItem();
		onesite.name="caijing";
		onesite.item="item";
		onesite.title="title";
		onesite.encoding = "UTF-8";
		onesite.link = "link";
		onesite.date = "pubDate";		
		onesite.tmformat="EEE, dd MMM yyyy HH:mm:ss Z";
		onesite.content = new ContentItem();
		String[] startstr = {"div","class","bltextcon"};
		onesite.content.setStart(startstr);
		
		Economistor people = new Economistor();
		people.siteurl="http://blog.caijing.com.cn/rss-u151333.xml";
		people.name="×óÐ¡ÀÙ";
		RssDown down = new RssDown();
		ColumnArticleDao dao=(ColumnArticleDao)ContextFactory.getBean("columnArticleDao");
		down.setDao(dao);
		down.getRssArList(people.siteurl,people.name,onesite);
		
		
		
//	  String   dateString   =   "Thu, 02 Sep 2010 08:54:37 +0000"; 
//	  String   pattern   =      "EEE, dd MMM yyyy HH:mm:ss Z";
//      
//      SimpleDateFormat   formatter   =   new   SimpleDateFormat(pattern,Locale.ENGLISH); 
//      try   { 
//              Date   date   =   formatter.parse(dateString); 
//              logger.debug( "date   =   "   +   date); 
//      } 
//      catch   (Exception   e)   { 
//              throw   new   RuntimeException(e.getMessage()); 
//      }
      
	
	}


	public ColumnArticleDao getDao() {
		return dao;
	}


	public void setDao(ColumnArticleDao dao) {
		this.dao = dao;
	}

}
