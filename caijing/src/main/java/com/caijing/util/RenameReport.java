package com.caijing.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;

public class RenameReport {
	public static void main(String[] args) {
		String prefix = "/data/oldpapers2/hanjianping/";
		String destPath = "/data/";
		//		String file = FileUtil.read("E:\\ÍøÕ¾\\test.txt", "gbk");
		String file = FileUtil.read("/data/test.txt", "gbk");
		String[] lines = file.split("\n");
		for (String line : lines) {
			System.out.println("line : " + line);
			String[] fields = line.split(",");
			if (fields.length == 8) {
				System.out.println("title : " + fields[0]);
				File report = new File(prefix + fields[0]);
				if (report.exists() && report.isFile()) {
					System.out.println("report:" + report.getAbsolutePath() + " " + report.getName());
					try {
						FileUtils.copyFile(report, new File(destPath + fields[0]));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						String utfName = new String(fields[0].getBytes("utf-8"));
						System.out.println("title:" + utfName);
						report = new File(prefix + utfName);
						if (report.exists() && report.isFile()) {
							System.out.println("report:" + report.getAbsolutePath() + " " + report.getName());
							FileUtils.copyFile(report, new File(destPath + utfName));
						} else {
							System.out.println("transform title Error!");
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}
		System.out.println("file : " + file);
	}
}
