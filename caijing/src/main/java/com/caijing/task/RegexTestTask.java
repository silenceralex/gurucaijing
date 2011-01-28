package com.caijing.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTestTask {
	
	String pattern = "(申银万国)_(.*?)_([0-9]{6})[\\(（](.*?)[\\)）]_(.*?)_(?:＊*_)*(.*?)\\.pdf";
	String input = "申银万国_综合分析_600276(恒瑞医药)_应振洲，罗鶄_＊＊＊_由仿制走向仿创，由国内走向国际，维持买入评级。.pdf";
	
	public void run(){
		Pattern titlePattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		Matcher m = titlePattern.matcher(input);
		if (m != null && m.find()) {
			for (int i = 0; i < m.groupCount(); i++) {
				System.out.println("group "+i+": "+m.group(i));
			}
		}
	}
	
	public static void main(String[] args) {
		RegexTestTask task = new RegexTestTask();
		task.run();
	}

}
