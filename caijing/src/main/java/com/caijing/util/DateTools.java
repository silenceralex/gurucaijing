package com.caijing.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTools {
	public String transformDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(date);
	}

	public static String transformYYYYMMDDDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String transformYYYYMMDDDate(String date) {
		String tmp = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
		return tmp;
	}

	/**
	 * 对当前日期增加一天
	 * @param creatdate 输入类型“YYYYMMDD”
	 * @return 输出类型“yyyy-MM-dd HH:mm”
	 */
	public static String getAfterDay(String creatdate) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(creatdate.substring(0, 4)));
		if (creatdate.charAt(4) == '0') {
			cal.set(Calendar.MONTH, Integer.parseInt(creatdate.substring(5, 6)) - 1);
		} else {
			cal.set(Calendar.MONTH, Integer.parseInt(creatdate.substring(4, 6)) - 1);
		}
		if (creatdate.charAt(6) == '0') {
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(creatdate.substring(7, 8)));
		} else {
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(creatdate.substring(6, 8)));
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println(df.format(cal.getTime()));
		cal.add(Calendar.DATE, 1);
		Date date = cal.getTime();

		System.out.println(df.format(date));
		return df.format(date);
	}

	public static void main(String[] args) {
		DateTools tools = new DateTools();
		System.out.println(tools.transformYYYYMMDDDate("20100830"));

		tools.getAfterDay("20100830");
	}
}
