package com.caijing.util;

import java.text.SimpleDateFormat;

/**
 * 存放处理html或者转换的有效函数的一些公共类
 * @author chenjun <jun-chen@corp.netease.com>
 * 
 */
public class HtmlUtils {

	public final static SimpleDateFormat DATE_OUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd");
	public final static SimpleDateFormat YMD_FORMAT = new SimpleDateFormat("yyyyMMdd");
	public final static SimpleDateFormat MON_FORMAT = new SimpleDateFormat("MM");

	//判断是否为标点符号
	public static boolean isSymbol(char ch) {
		return (0x0020 < ch) && (ch < 0x007F) || (0x2000 < ch) && (ch < 0x206F) //半角、通用
				|| (ch < 0x303f) && (ch > 0x3000) || (ch < 0xFFEF) //全角
				&& (ch > 0xFF00);
	}

	public static String stripTags(String html) {
		if (html != null) {
			String tmp = html.replaceAll("<.*?>", " ").replaceAll("&nbsp;", " ").replaceAll("  > \"> ", "")
					.replaceAll("\\s+", " ");
			String ret = "";
			char[] chars = tmp.toCharArray();
			for (char c : chars) {
				if (Character.isLetterOrDigit(c) || Character.isWhitespace(c) || isSymbol(c)) {
					ret += c;
				}
			}
			return ret.trim();
		}
		return null;
	}

	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public String getSubStr(String str, int length, String more) {
		if (str.length() <= length)
			return str;
		int threshold = length * 2 - more.getBytes().length;
		int count = 0;
		int pos = 0;
		boolean out = false;
		if (str == null)
			return null;
		char[] chars = str.toCharArray();
		for (char c : chars) {
			int len = String.valueOf(c).getBytes().length;
			count += len;
			if (count > threshold) {
				out = true;
				break;
			} else if (count == threshold) {
				out = false;
			}
			pos++;
		}
		//		System.out.println("total lenth:" + count + "  pos:" + pos + "  out:" + (out ? "true" : "false")
		//				+ "  threshold:" + threshold);
		//		String retStr = str.substring(0, out ? pos : (pos + 1)) + more;
		//		System.out.println("return string length:" + retStr.getBytes().length);
		return str.substring(0, out ? pos : (pos + 1)) + more;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// String str =
		// "http://www.ahtv.cn/vod/2008-07/09/cms87048article.shtml";
		// String str2 = "/../../10/cms87047article.shtml";
		// try {
		// URL url = new URL(new URL(str), str2);
		// System.out.println("url:" + url.toString());
		// } catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println("match is NULL!");
		String test = "11月份的中国P“数据并未减轻市场对通胀的顾虑，还需要CPI和PPI数据的支持。在此之前，市场难以打消对紧缩政策的担忧，这也会造成市场持续的弱势表现和惨淡成交。 ";
		HtmlUtils htmlUtils = new HtmlUtils();
		String out = htmlUtils.getSubStr(test, 40, "...");
		System.out.println("out:" + out);

		//		char c = '。';
		//		char d = 0x0020;
		//		System.out.println(d);
		//		System.out.println(Integer.toHexString((int) c));
		//		if (HtmlUtils.isSymbol(c)) {
		//			System.out.println("c is a Symbol!");
		//		} else {
		//			System.out.println("c is not Symbol!");
		//		}
		//		String md5 = MD5Utils.hash("http://www.caijing.com.cn/rss/column.xml");
		//		System.out.println("md5: " + md5);
		// try {
		// Date dt1 = HtmlUtils.DATE_OUT_FORMAT.parse("2008-01-02 00:00:00");
		// Date dt2 = HtmlUtils.DATE_OUT_FORMAT.parse("2008-01-01 00:00:00");
		// if (dt1.getTime() > dt2.getTime()) {
		// System.out.println("dt1 在dt2前");
		// } else {
		// System.out.println("dt1在dt2后");
		// }
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// UrlDownload loader=new UrlDownload();
		// str
		// =loader.load("http://www.ahtv.cn/lanmu/dysj/vod/2009-08/31/cms133668article.shtml");
		// String regexp =
		// "http://www\\.ahtv\\.cn/.*?vod/\\d{4}-\\d{2}/\\d{2}/cms\\d+?article\\.shtml";
		// System.out.println("regexp :" + regexp);
		// Pattern HREF = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE
		// | Pattern.DOTALL | Pattern.UNIX_LINES);
		// Matcher m = HREF.matcher(str);
		// if (m != null) {
		// // if(!m.find())
		// // {
		// // System.out.println("Not match!");
		// // }
		// while (m.find()) {
		// String outlink = m.group(1);
		// // System.out.println("link :");
		// System.out.println("link :" + outlink);
		// // System.out.println("inner :");
		// // String inner = m.group(2);
		// // System.out.println("inner :" + inner);
		// }
		// } else {
		// System.out.println("match is NULL!");
		// }
	}
}
