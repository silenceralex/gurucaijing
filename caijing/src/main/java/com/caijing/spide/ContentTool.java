package com.caijing.spide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.caijing.cache.BerkeleyDB;
import com.caijing.mail.MailReceiver;
import com.caijing.util.ContextFactory;

public class ContentTool {
	private static Log logger = LogFactory.getLog(ContentTool.class);
	static String delLable(String content){
		if(content == null) return null;
		String rlt="";
		HtmlParser p = new HtmlParser(content);
		int event = p.getEventType();
	    event = p.next();
	    while(true){
			switch (event) {
				 case HtmlParser.START_ELEMENT:
					 if("script".equals(p.getName()) || "style".equals(p.getName())|| "INS".equals(p.getName())){
						 p.mv2RespEnd();
					 }
				 break;
				 case HtmlParser.END_ELEMENT:
				 break;
				 case HtmlParser.END_DOCUMENT:
				 break;
				 case HtmlParser.CHARACTERS:
					 rlt+=p.getText();
				 break;
				 default : 
				 break;
			}
			 if (p.hasnext()==0)
			     break;
			 event = p.next();
		 }
	    return rlt;
	}
	
	
    static public String getArticleText(String content, RssItem site){
    	if(content == null) return null;
    	String text=null;
		HtmlParser p = new HtmlParser(content);
		int event = p.getEventType();
		while (true) {
			switch (event) {
				case HtmlParser.START_ELEMENT:					
					 if(site.content.start.startEle.equals(p.getName())){	
						 int i=0, n;
						 for(n = p.getAttributeCount(); i < n; ++i){
//						     logger.debug(i);
//						     logger.debug(n);
//							 logger.debug("name="+p.getAttributeName(i));
//							 logger.debug("value="+p.getAttributeValue(i));
							 String key =p.getAttributeName(i);
							 String value= p.getAttributeValue(i);
							 if(key!=null && value!=null &&site.content.start.name.equals(key) && site.content.start.value.equals(value)){
								text=p.getRespValue();
								//logger.debug("11111"+text);

								text=delLable(text);
								return text;
							 }
						 }
					 }
			}
			if(p.hasnext()!=1)break;
			event = p.next();
		}		
		return text;
    }
    
    static public Date strTime2Date(String dateString, String pattern){
    	logger.debug("sys time...."+dateString+"   "+pattern);
    	 SimpleDateFormat   formatter   =   new   SimpleDateFormat(pattern,Locale.ENGLISH); 
    	 Date   date=null;
         try   { 
                    date =   formatter.parse(dateString); 
                 logger.debug( "date   =   "   +   date); 
         } 
         catch   (Exception   e)   { 
                 throw   new   RuntimeException(e.getMessage()); 
         }
         return date;
  }
   

}