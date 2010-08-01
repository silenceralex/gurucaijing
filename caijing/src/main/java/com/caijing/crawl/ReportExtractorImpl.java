package com.caijing.crawl;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.caijing.domain.Report;

public class ReportExtractorImpl implements ReportExtractor {
	private static Pattern stockPattern = Pattern.compile(
			"(.*?)--(.*?)\\((((002|000|300|600)[\\d]{3})|60[\\d]{4})\\)",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
	
	public Report extractFromFile(String file) {
//		String[] strs=file.split(" ");
//		if(strs.length==2){
//			String agency=strs[0];
//			String title=strs[1].replace(".txt", "");
//			System.out.println("agency:"+agency);
//			System.out.println("title:"+title);
			Matcher m=stockPattern.matcher(file);
			if(m!=null&&m.find()){
				String sanam=m.group(1);
				String stockname=m.group(2);
				String stockcode=m.group(3);
				System.out.println("sanam:"+sanam);
				System.out.println("stockname:"+stockname);
				System.out.println("stockcode:"+stockcode);
			}
//		}
		return null;
	}

	
	public static void main(String[] args) {
		ReportExtractor extractor=new ReportExtractorImpl();
		File file=new File("F:\\email\\研究报告7.07\\安信证券--广汇股份(600256)参与气化南疆，履行社会责任.txt");
		extractor.extractFromFile(file.getName());
	}
}
