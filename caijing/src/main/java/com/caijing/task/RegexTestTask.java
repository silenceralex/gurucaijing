package com.caijing.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTestTask {
	
	String pattern = "(�������)_(.*?)_([0-9]{6})[\\(��](.*?)[\\)��]_(.*?)_(?:��*_)*(.*?)\\.pdf";
	String input = "�������_�ۺϷ���_600276(����ҽҩ)_Ӧ���ޣ�����_������_�ɷ�������´����ɹ���������ʣ�ά������������.pdf";
	
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
