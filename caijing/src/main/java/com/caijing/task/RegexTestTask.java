package com.caijing.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTestTask {
	
	String pattern = "(申银万国)_(.*?)_([0-9]{6})(.*?)\\.pdf";
	String input = "申银万国_深度报告_600267(海正.pdf";
	
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
