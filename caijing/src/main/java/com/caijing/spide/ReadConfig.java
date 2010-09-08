package com.caijing.spide;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caijing.spide.RssItem;

public class ReadConfig {	
	private static Log logger = LogFactory.getLog(ReadConfig.class);
	Map<String, RssItem> allsite=null;
	Vector<Economistor> allpeople=null;
	
	
	RssItem getSiteByName(String name){
		if(allsite != null){
			return allsite.get(name);
		}else return null;
	}
	
	

	void GetAllSite(String filename,String encoding) throws IOException{
	    RssItem site=null;
	    String content=Downloads.downAsStream(filename, encoding);
		
	    HtmlParser p = new HtmlParser(content);
	    int event=p.getEventType();
		while(true){
			switch(event){
				 case HtmlParser.START_DOCUMENT:
//					 logger.debug("Start Document: ");
					 allsite = new HashMap<String,RssItem>();
					 break;
			     case HtmlParser.START_ELEMENT:
//				     logger.debug("Start Element: " + p.getName());
				     if("onesite".equals(p.getName())){
				    	 site=new RssItem();
				     }else if("name".equals(p.getName())){
						 if(p.hasnext()==1 && p.next()==HtmlParser.CHARACTERS){
						     if(site != null) site.name= p.getText();
						 }
				     }else if("item".equals(p.getName())){
				    	 if(p.hasnext()==1 && p.next()==HtmlParser.CHARACTERS){
				    		 if(site != null) site.item= p.getText();
				    	 }
				     }else if("title".equals(p.getName())){
				    	 if(p.hasnext()==1 && p.next()==HtmlParser.CHARACTERS){
				    		 if(site != null) site.title= p.getText();
				    	 }
				     }else if("link".equals(p.getName())){
				    	 if(p.hasnext()==1 && p.next()==HtmlParser.CHARACTERS){
				    		 if(site != null) site.link= p.getText();
				    	 }
				     }else if("encoding".equals(p.getName())){
				    	 if(p.hasnext()==1 && p.next()==HtmlParser.CHARACTERS){
				    		 if(site != null) site.encoding= p.getText();
				    	 }
				     }else if("date".equals(p.getName())){
					    if(p.hasnext()==1 && p.next()==HtmlParser.CHARACTERS){
					    	if(site != null) site.date = p.getText();
					    }
				     }else if("des".equals(p.getName())){
						    if(p.hasnext()==1 && p.next()==HtmlParser.CHARACTERS){
						    	logger.debug(p.getText());
						    	if(site != null) site.des = p.getText();
						    }
					 }else if("tmformat".equals(p.getName())){
						    if(p.hasnext()==1 && p.next()==HtmlParser.CHARACTERS){
						    	if(site != null) site.tmformat = p.getText();
						    }
					 }else if("content".equals(p.getName())){
				    	 String con = p.getRespValue();
				    	 site.content=GetText(con);
				    	 logger.debug("content="+site.content);				    	
				     }
				     break;
			     case HtmlParser.END_ELEMENT:
//				     logger.debug("End Element:" + p.getName());
				     if("onesite".equals(p.getName())){
				    	 if(!site.name.equals("")) allsite.put(site.name,site);
						site=null;
				     }
					 break;
				 case HtmlParser.END_DOCUMENT:
//			         logger.debug("End Document.");
					 break;				
				} 
			if (p.hasnext()==0)
				break;
				
			event = p.next();
		    }
	}
	
	
	
	void GetAllPeople(String filename, String encoding) throws IOException{
	    Economistor people=null;
	    String content=Downloads.downAsStream(filename, encoding);
		
	    HtmlParser p = new HtmlParser(content);
	    int event=p.getEventType();
		while(true){
			switch(event){
				 case HtmlParser.START_DOCUMENT:
//					 logger.debug("Start Document: ");
					 allpeople = new Vector<Economistor>();
					 break;
			     case HtmlParser.START_ELEMENT:
//				     logger.debug("Start Element: " + p.getName());
				     if("people".equals(p.getName())){
				    	 people=new Economistor();
				     }else if("name".equals(p.getName())){
						 if(p.hasnext()==1 && p.next()==HtmlParser.CHARACTERS){
						     if(people != null) people.name= p.getText();
						 }
				     }else if("rss".equals(p.getName())){
				    	 if(p.hasnext()==1 && p.next()==HtmlParser.CHARACTERS){
				    		 if(people != null) people.siteurl= p.getText().trim();
				    	 }
				     }else if("sitename".equals(p.getName())){
				    	 if(p.hasnext()==1 && p.next()==HtmlParser.CHARACTERS){
				    		 if(people != null) people.sitename= p.getText();
				    	 }
				     }
				     break;
			     case HtmlParser.END_ELEMENT:
//				     logger.debug("End Element:" + p.getName());
				     if("people".equals(p.getName())){
				    	 if(!people.name.equals("")) allpeople.add(people);
						people=null;
				     }
					 break;
				 case HtmlParser.END_DOCUMENT:
//			         logger.debug("End Document.");
					 break;				
				} 
			if (p.hasnext()==0)
				break;
				
			event = p.next();
		    }
	}

	ContentItem GetText(String content){
		ContentItem curCon=null;
		String val=null;
		HtmlParser p = new HtmlParser(content);
	    int event=p.getEventType();
	    while(true){
			switch(event){
				 case HtmlParser.START_DOCUMENT:
					 break;
			     case HtmlParser.START_ELEMENT:
//				     logger.debug("Start Element: " + p.getName());
				     if("start".equals(p.getName())){
				    	 curCon=new ContentItem();
				    	 if(p.hasnext()==1 && p.next()==HtmlParser.CHARACTERS){
						     val= p.getText();
						     String[] tripleString =val.split(":");
						     curCon.setStart(tripleString);
						 }
				     }else if("del".equals(p.getName()) && curCon!=null){
						 if(p.hasnext()==1 && p.next()==HtmlParser.CHARACTERS){
							 val= p.getText();
						     String[] tripleString =val.split(":");
						     curCon.addDelItem(tripleString);
						 }
				     }
				     break;
			     case HtmlParser.END_ELEMENT:
//				     logger.debug("End Element:" + p.getName());
				     if("content".equals(p.getName())){
				    	 return curCon;
				     }
					 break;
				 case HtmlParser.END_DOCUMENT:
//			         logger.debug("End Document.");
					 break;				
				} 
			if (p.hasnext()==0)
				break;
				
			event = p.next();
		    }
	    return null;	
	}
}