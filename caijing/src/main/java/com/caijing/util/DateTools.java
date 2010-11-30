package com.caijing.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTools {
	public String transformDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(date);
	}

	public static String transformDateDetail(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static String transformYYYYMMDDDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	public static String transformTomm(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(date);
	}

	public static Date parseYYYYMMDDDate(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.parse(date);
	}

	public static Date parseShortDate(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.parse(date);
	}

	//	public static Date parseYYYYMMDDDate(String date) throws ParseException {
	//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	//		return sdf.parse(date);
	//	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String transformYYYYMMDDDate(String date) {
		String tmp = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
		return tmp;
	}

	public static String getYesterday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, -1);
		return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
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

	public String transformMMDDDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		return sdf.format(date);
	}

	public String transformDate(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date dateObj = sdf.parse(date);
		return transformDate(dateObj);
	}

	public static Date getToday() {
		Calendar cld = Calendar.getInstance();
		cld.set(Calendar.HOUR, 0);
		cld.set(Calendar.MINUTE, 0);
		cld.set(Calendar.HOUR, 0);
		cld.set(Calendar.SECOND, 0);
		return cld.getTime();
	}

	public static void main(String[] args) {
		DateTools tools = new DateTools();
		System.out.println(tools.transformYYYYMMDDDate("20100830"));
		System.out.println(DateTools.getToday());
		//tools.getAfterDay("20100830");
	}
}
