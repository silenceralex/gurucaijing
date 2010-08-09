package com.caijing.spide;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.caijing.spide.RssItem;

public class ReadConfig {	

	Map<String, RssItem> allsite=null;
	Vector<FocusPeople> allpeople=null;
	String content = "";
	
	
	RssItem getArticle(String name){
		if(allsite != null){
			return allsite.get(name);
		}else return null;
	}
	
	void getContent(String filename) throws IOException{
		InputStream f = new FileInputStream(filename);   
		InputStreamReader re = new InputStreamReader(f, "UTF-8");
	    BufferedReader reader = new BufferedReader(re);
	    String line;
	    while ((line = reader.readLine()) != null) {
            content += line;
        }
	    reader.close();	    
	}

	void GetAllSite(String filename) throws IOException{
	    RssItem site=null;
		getContent(filename);
		
	    HtmlParser p = new HtmlParser(content);
	    int event=p.getEventType();
		while(true){
			switch(event){
				 case HtmlParser.START_DOCUMENT:
//					 System.out.println("Start Document: ");
					 allsite = new HashMap<String,RssItem>();
					 break;
			     case HtmlParser.START_ELEMENT:
//				     System.out.println("Start Element: " + p.getName());
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
				     }else if("content".equals(p.getName())){
				    	 if(p.hasnext()==1 && p.next()==HtmlParser.CHARACTERS){
				    		 if(site != null) site.contents= p.getText();
				    	 }
				     }
				     break;
			     case HtmlParser.END_ELEMENT:
//				     System.out.println("End Element:" + p.getName());
				     if("onesite".equals(p.getName())){
				    	 if(!site.name.isEmpty()) allsite.put(site.name,site);
						site=null;
				     }
					 break;
				 case HtmlParser.END_DOCUMENT:
//			         System.out.println("End Document.");
					 break;				
				} 
			if (p.hasnext()==0)
				break;
				
			event = p.next();
		    }
	}
	
	void GetAllPeople(String filename) throws IOException{
	    FocusPeople people=null;
		getContent(filename);
		
	    HtmlParser p = new HtmlParser(content);
	    int event=p.getEventType();
		while(true){
			switch(event){
				 case HtmlParser.START_DOCUMENT:
//					 System.out.println("Start Document: ");
					 allpeople = new Vector<FocusPeople>();
					 break;
			     case HtmlParser.START_ELEMENT:
//				     System.out.println("Start Element: " + p.getName());
				     if("people".equals(p.getName())){
				    	 people=new FocusPeople();
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
//				     System.out.println("End Element:" + p.getName());
				     if("people".equals(p.getName())){
				    	 if(!people.name.isEmpty()) allpeople.add(people);
						people=null;
				     }
					 break;
				 case HtmlParser.END_DOCUMENT:
//			         System.out.println("End Document.");
					 break;				
				} 
			if (p.hasnext()==0)
				break;
				
			event = p.next();
		    }
	}
}