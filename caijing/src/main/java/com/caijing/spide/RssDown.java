package com.caijing.spide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.caijing.dao.ColumnArticleDao;
import com.caijing.domain.ColumnArticle;
import com.caijing.util.ContextFactory;

public class RssDown {
	Article ar=null;
	List<Article> arlist =null;
	ColumnArticleDao dao=(ColumnArticleDao)ContextFactory.getBean("columnArticleDao");
	
	void getRssArList(String siteurl,String peoplename, RssItem site) throws IOException{
		
		URL url = new URL(siteurl);
		InputStream input = url.openStream();
		String content="";
		String s=null;
		InputStreamReader re = new InputStreamReader(input, site.encoding);
		BufferedReader br = new BufferedReader(re);
		s=br.readLine();
		while(s != null){
		    s=br.readLine();
		    content+=s;
		}
		br.close();
//		System.out.println(content);

		if(site != null){
			HtmlParser p = new HtmlParser(content);
			 int event = p.getEventType();
			 while (true) {
			 switch (event) {
			 case HtmlParser.START_DOCUMENT:
				 
				 arlist = new ArrayList<Article>();
//				 System.out.println("Start Document.");
				 break;
			case HtmlParser.START_ELEMENT:
//				 System.out.println("Start Element: " + p.getName());
				 			 
				 if(site.item.equals(p.getName())){
					 ar = new Article();				 
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
							 ar.link= ti;
//							 System.out.println(ar.link);
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
							 ar.title= ti;
//							 System.out.println(ar.title);
						 }
					 }
				 }
				 
				 break;
				 case HtmlParser.CHARACTERS:
				 if (p.isWhiteSpace()==1)
				 break;
				 
//				 System.out.println("Text: " + p.getText());
				 break;
				 case HtmlParser.END_ELEMENT:
//				 System.out.println("End Element:" + p.getName());
				 if("item".equals((p.getName()))){
					 if(arlist != null ) arlist.add(ar);
					 ar = null;
				 }
				 break;
				 case HtmlParser.END_DOCUMENT:
//				 System.out.println("End Document.");
				 break;
				 default :
//					 System.out.println("error");
				 }
				 
				 if (p.hasnext()==0)
				 break;
			
				 event = p.next();
			 }
		}
		
		if(arlist != null){
			for(Article tmp:arlist){
				System.out.println(tmp.link+tmp.title);
				if(site.name.equals("sina")){
					ContentDown sinadown = new ContentDown();
					tmp.contents=sinadown.getArticleText(tmp.link, site);
//					System.out.println(tmp.contents);
					if(tmp.contents != null){
						ColumnArticle ca=new ColumnArticle();
						ca.setContent(tmp.contents);
						ca.setLink(tmp.link);
						ca.setSrc("blog");
						ca.setName(peoplename);
						ca.setTitle(tmp.title);
						dao.insert(ca);
					}
				}
			}
		}
		
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		ReadConfig r = new ReadConfig();
		r.GetAllSite("D:\\opensource\\guru\\caijing\\src\\main\\java\\com\\caijing\\spide\\rsssite.xml");
		r.GetAllPeople("D:\\opensource\\guru\\caijing\\src\\main\\java\\com\\caijing\\spide\\focuspeople.xml");
		RssDown down = new RssDown();
		for(FocusPeople people:r.allpeople){
			RssItem site=r.getArticle(people.sitename);
			System.out.println("start ...... "+people.name+".......");
			down.getRssArList(people.siteurl,people.name,site);
			System.out.println("end ...... "+people.name+".......");
			System.out.println("");
		}
		
	}

}
