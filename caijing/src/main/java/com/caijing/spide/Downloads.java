package com.caijing.spide;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Downloads {
	private static Log logger = LogFactory.getLog(Downloads.class);
	static String downByHttpclient(String url, String encoding){
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
//			logger.debug(content);
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
	
	static String downByUrlopen(String url, String encoding) throws IOException{
		String content="";
		String s=null;
		URL siteurl;
		try {
			siteurl = new URL(url);		
			InputStream input = siteurl.openStream();		
			InputStreamReader re = new InputStreamReader(input, encoding);
			BufferedReader br = new BufferedReader(re);
			s=br.readLine();
			while(s != null){
			    s=br.readLine();
			    content+=s;
			}
			br.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		
		return content;
	}
	
	static String downByReadFile(String filename, String encoding) throws IOException{
		String content=null;
		InputStream f = new FileInputStream(filename);   
		InputStreamReader re = new InputStreamReader(f, encoding);
	    BufferedReader reader = new BufferedReader(re);
	    String line;
	    while ((line = reader.readLine()) != null) {
            content += line;
        }
	    reader.close();	
	    return content;
	}
	
	static String downAsStream(String filename, String encoding) throws IOException{
		String content=null;
		InputStream   f   =   RssDown.class.getResourceAsStream(filename);  
		InputStreamReader re = new InputStreamReader(f, encoding);
	    BufferedReader reader = new BufferedReader(re);
	    String line;
	    while ((line = reader.readLine()) != null) {
            content += line;
        }
	    reader.close();	
	    return content;
	}

}
