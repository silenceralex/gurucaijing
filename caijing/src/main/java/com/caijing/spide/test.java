package com.caijing.spide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

public class test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public static void main(String[] args) throws HttpException, IOException {
		// TODO Auto-generated method stub
//		String ying="Ӣabc";
//		char tmp = ying.charAt(1);
//		System.out.println(tmp);		
//		String   yingHex   =   Integer.toHexString(tmp); 
//		System.out.println(yingHex);
		
//		String ying="Ӣ";
//		byte[] byteGB2312 = ying.getBytes("GB2312");
//		byte[] byteDefault = ying.getBytes();
//		String newStrGB = new String(byteGB2312, "GB2312");
//		String newStrDefault = new String(byteDefault);
//		
//		System.out.println(newStrGB);
//		System.out.println(newStrDefault);
		
		HttpClient client = new HttpClient();
		GetMethod authget = new GetMethod("http://blog.sina.com.cn/s/blog_49a3971d0100kyan.html");
		client.executeMethod(authget);
		InputStream input = authget.getResponseBodyAsStream();
		InputStreamReader re = new InputStreamReader(input, "UTF-8");
		BufferedReader br = new BufferedReader(re);
		String s,content="";
		s=br.readLine();
		while(s != null){
			content+=s;
		    s=br.readLine();		    
		}
		br.close();
		System.out.println(content);

	}

}
