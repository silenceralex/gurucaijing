package com.caijing.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTools {
	public String transformDate(Date date){
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(date);
	}
	
	public static String transformYYYYMMDDDate(Date date){
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
}
