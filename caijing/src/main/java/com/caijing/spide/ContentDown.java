package com.caijing.spide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

public class ContentDown {
	
	static String downContent(String url, String encoding){
		String s,content="";
		HttpClient client = new HttpClient();
		GetMethod authget = new GetMethod(url);
		try {
			client.executeMethod(authget);		
			InputStream input = authget.getResponseBodyAsStream();
			InputStreamReader re = new InputStreamReader(input, encoding);
			BufferedReader br = new BufferedReader(re);
	
			s=br.readLine();
			while(s != null){
				content+=s;
			    s=br.readLine();		    
			}
			br.close();
//			System.out.println(content);
			return content;
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
    public String getArticleText(String link, RssItem site){
		String text=null;
		String content=ContentDown.downContent(link,site.encoding);			

		HtmlParser p = new HtmlParser(content);
		int event = p.getEventType();
		while (true) {
			switch (event) {
				case HtmlParser.START_ELEMENT:					
					 if(site.content.start.startEle.equals(p.getName())){	
						 int i=0, n;
						 for(n = p.getAttributeCount(); i < n; ++i){
//						     System.out.println(i);
//						     System.out.println(n);
//							 System.out.println("name="+p.getAttributeName(i));
//							 System.out.println("value="+p.getAttributeValue(i));
							 String key =p.getAttributeName(i);
							 String value= p.getAttributeValue(i);
							 if(key!=null && value!=null &&site.content.start.name.equals(key) && site.content.start.value.equals(value)){
								System.out.println("okokokokokokok");
								text= p.parseRespValue(site);
								System.out.println(text);
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

	/**
	 * @param args
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public static void main(String[] args) throws HttpException, IOException {
		String url="http://blog.sina.com.cn/s/blog_6775ea670100l1tu.html";
		String encoding="UTF-8";
		String text="";
		text=ContentDown.downContent(url, encoding);	
		System.out.println(text);
	}
}
