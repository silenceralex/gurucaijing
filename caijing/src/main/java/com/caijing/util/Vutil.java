package com.caijing.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component("vutil")
public class Vutil {

	private Log logger = LogFactory.getLog(Vutil.class);

	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
	public Date stringtodate(String date) throws ParseException{
		return dateFormat.parse(date);
	}
	public String datetoString(Date date) {
		return timeFormat.format(date);
	}

	public String datetoString(Date date, String format) {
		SimpleDateFormat myTimeFormat = new SimpleDateFormat(format);
		return myTimeFormat.format(date);
	}

	public static String backString(String str) {
		if (str == null || str.equals("")) {
			return str;
		}
		String sys = str;
		sys = sys.replaceAll("&lt;", "<");
		sys = sys.replaceAll("&gt;", ">");
		sys = sys.replaceAll("\r\n", "<br/>");
		sys = sys.replaceAll("\n", "<br/>");
		sys = sys.replaceAll("&nbsp;", " ");
		sys = sys.replaceAll("&amp;'", "&");
		sys = sys.replaceAll("&quot;", "\"");
		sys = sys.replaceAll("&#039;", "'");
		sys = sys.replaceAll("&#040;", "(");
		sys = sys.replaceAll("&#041;", ")");
		sys = sys.replaceAll("&#064;", "@");
		return sys;
	}

	/**
	 * 将传进来的字符串的换行符替成 <br/>
	 * 
	 * @param str
	 * @return
	 */
	public static String nl2br(String str) {
		try {
			return str.replaceAll("\r\n", "<br/>").replaceAll("\n", "<br/>");
		} catch (Exception e) {
			return str;
		}
	}

	public String utf8toGBK(String inputStr) {
		try {
			String utf8Raw = URLEncoder.encode(inputStr, "GBK");
			String gbkStr = URLDecoder.decode(utf8Raw, "UTF-8");
			return gbkStr;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return "";
		}
	}

	public String subString(String inputStr, int start, int end) {
		int length = inputStr.length();
		if (end > length)
			end = length;
		if (start < 0)
			start = 0;
		if (start > length)
			start = (length - 1);
		return inputStr.substring(start, end);
	}

	public static String cutMixTitle(String text, int textMaxChar) {
		int size, index;
		String returnStringArray = "";
		textMaxChar = textMaxChar * 2;
		if (textMaxChar <= 0) {
			returnStringArray = text;
		} else {
			for (size = 0, index = 0; index < text.length() && size < textMaxChar; index++) {
				size += text.substring(index, index + 1).getBytes().length;
			}
			returnStringArray = text.substring(0, index);
		}
		return returnStringArray;
	}

	public static String urlEncode(String str, String chrSet) {
		try {
			return URLEncoder.encode(str, chrSet);
		} catch (Exception ex) {
			return "";
		}
	}

}
