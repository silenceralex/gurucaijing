package com.caijing.util;

import java.text.SimpleDateFormat;

/**
 * ��Ŵ���html����ת������Ч������һЩ������
 * @author chenjun <jun-chen@corp.netease.com>
 * 
 */
public class HtmlUtils {

	public final static SimpleDateFormat DATE_OUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd");
	public final static SimpleDateFormat YMD_FORMAT = new SimpleDateFormat("yyyyMMdd");
	public final static SimpleDateFormat MON_FORMAT = new SimpleDateFormat("MM");

	//�ж��Ƿ�Ϊ������
	public static boolean isSymbol(char ch) {
		return (0x0020 < ch) && (ch < 0x007F) || (0x2000 < ch) && (ch < 0x206F) //��ǡ�ͨ��
				|| (ch < 0x303f) && (ch > 0x3000) || (ch < 0xFFEF) //ȫ��
				&& (ch > 0xFF00);
	}

	public static String stripTags(String html) {
		if (html != null) {
			String tmp = html.replaceAll("<.*?>", " ").replaceAll("&nbsp;", " ").replaceAll("  > \"> ", "").replaceAll(
					"\\s+", " ");
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

		char c = '��';
		char d = 0x0020;
		System.out.println(d);
		System.out.println(Integer.toHexString((int) c));
		if (HtmlUtils.isSymbol(c)) {
			System.out.println("c is a Symbol!");
		} else {
			System.out.println("c is not Symbol!");
		}
		String md5 = MD5Utils.hash("http://www.caijing.com.cn/rss/column.xml");
		System.out.println("md5: " + md5);
		// try {
		// Date dt1 = HtmlUtils.DATE_OUT_FORMAT.parse("2008-01-02 00:00:00");
		// Date dt2 = HtmlUtils.DATE_OUT_FORMAT.parse("2008-01-01 00:00:00");
		// if (dt1.getTime() > dt2.getTime()) {
		// System.out.println("dt1 ��dt2ǰ");
		// } else {
		// System.out.println("dt1��dt2��");
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
