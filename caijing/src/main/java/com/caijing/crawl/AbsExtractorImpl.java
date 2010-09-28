package com.caijing.crawl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.caijing.util.FileUtil;

public class AbsExtractorImpl implements AbsExtractor {
	private static Pattern absPattern = Pattern.compile("本报告导读：(.*?)摘要：", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
			| Pattern.UNIX_LINES);;

	public String extractAbs(String content, int type, String saname) {
		if(type==2&&"国泰君安".equals(saname)){
			Matcher m=absPattern.matcher(content);
			if(m!=null&&m.find()){
				return m.group(1).trim();
			}
		}
		return null;
	}
	 
	public static void main(String[] args) {
		String url ="http://guru.caijing.com/papers/20100916/6FQ6M46O.txt";
		String content=FileUtil.read(url,"gb2312");
		AbsExtractorImpl extractor=new AbsExtractorImpl();
		String abs=extractor.extractAbs(content, 2, "国泰君安");
		System.out.println("abs:"+abs);
		
	}
}
