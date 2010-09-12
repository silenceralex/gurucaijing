package com.caijing.model;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;

import com.caijing.domain.StockGain;
import com.caijing.domain.StockHQ;
import com.caijing.util.UrlDownload;

public class StockPrice {

	private static String hqurl = "http://q.stock.sohu.com/app2/history.up?method=history&res=js&code=cn_";

	private UrlDownload down = new UrlDownload();

	private Pattern periodPattern = Pattern
			.compile(
					"\\['20[01][0-9]-[01][0-9]-[0-3][0-9]至20[01][0-9]-[01][0-9]-[0-3][0-9]','(-?[0-9\\.]+)','(-?[0-9\\.]+)%','([0-9\\.]+)','([0-9\\.]+)','[0-9\\.]+','([0-9\\.]+)','([0-9\\.]+)%'\\]\\]\\)",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL
							| Pattern.UNIX_LINES);

	public StockHQ fetchhq(String stockcode, String date) {
		StockHQ hq = new StockHQ();
		hq.setStockcode(stockcode);
		String stockurl = hqurl + stockcode + "&sd=" + date + "&ed=" + date;
		try {
			String content = down.load(stockurl);
			Pattern startPattern = Pattern
					.compile(
							"\\['"
									+ date
									+ "','([0-9\\.]+)','([0-9\\.]+)','-?[0-9\\.]+','(-?[0-9\\.]+)%','([0-9\\.]+)','([0-9\\.]+)','[0-9\\.]+','([0-9\\.]+)','([0-9\\.]+)%'\\]",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL
									| Pattern.UNIX_LINES);
			Matcher startm = startPattern.matcher(content);
			if (startm != null && startm.find()) {
				System.out.println("Content:" + startm.groupCount());
				System.out.println(stockcode + "在日期：" + date + "  开盘价："
						+ startm.group(1));
				hq.setOpenprice(Float.parseFloat(startm.group(1).trim()));
				System.out.println(stockcode + "在日期：" + date + "  收盘价："
						+ startm.group(2));
				hq.setEndprice(Float.parseFloat(startm.group(2).trim()));
				System.out.println(stockcode + "在日期：" + date + "  最低价："
						+ startm.group(4));
				hq.setLowest(Float.parseFloat(startm.group(4).trim()));
				System.out.println(stockcode + "在日期：" + date + "  最高价："
						+ startm.group(5));
				hq.setHighest(Float.parseFloat(startm.group(5).trim()));
				System.out.println(stockcode + "在日期：" + date + "  成交额："
						+ startm.group(6));
				hq.setVolum(Float.parseFloat(startm.group(6).trim()));
				System.out.println(stockcode + "在日期：" + date + "  换手率："
						+ startm.group(7));
				hq.setChangerate(Float.parseFloat(startm.group(7).trim()));
				System.out.println(stockcode + "在日期：" + date + "  涨跌率："
						+ startm.group(3));
				hq.setGainrate(Float.parseFloat(startm.group(3).trim()));
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// &code=cn_600016&sd=2010-07-30&ed=2010-07-30
	public StockGain getStockGainByPeriod(String stockcode, String startdate,
			String enddate) {
		String stockurl = hqurl + stockcode + "&sd=" + startdate + "&ed="
				+ enddate+"&t=d";
		System.out.println("stockurl: "+stockurl);
		try {
			String content = down.load(stockurl);
			
			if(!content.contains("至")){
				stockurl = hqurl + stockcode + "&sd=" + startdate + "&ed="
				+ enddate+"&t=r";
				content = down.load(stockurl);
			}
					
			// System.out.println("Content:"+content);
			Pattern startPattern = Pattern
					.compile(
							"\\['(20[01][0-9]-[01][0-9]-[0-3][0-9])','([0-9\\.]+)','([0-9\\.]+)','-?[0-9\\.]+','(-?[0-9\\.]+)%','([0-9\\.]+)','([0-9\\.]+)','[0-9\\.]+','([0-9\\.]+)','([0-9\\.]+)%'\\]",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL
									| Pattern.UNIX_LINES);
			Matcher startm = startPattern.matcher(content);
			int i = 0;
			StockHQ starthq = new StockHQ();
			StockHQ endhq = new StockHQ();
			StockHQ hq = new StockHQ();
			while (startm != null && startm.find()) {
				hq.setDate(startm.group(1).trim());
				hq.setOpenprice(Float.parseFloat(startm.group(2).trim()));
				hq.setEndprice(Float.parseFloat(startm.group(3).trim()));
				// System.out.println("时间点：" + startm.group(1).trim()+"
				// 收盘价："+startm.group(3).trim()+" ");
				hq.setLowest(Float.parseFloat(startm.group(5).trim()));
				hq.setHighest(Float.parseFloat(startm.group(6).trim()));
				hq.setVolum(Float.parseFloat(startm.group(7).trim()));
				hq.setChangerate(Float.parseFloat(startm.group(8).trim()));
				hq.setGainrate(Float.parseFloat(startm.group(4).trim()));
				if (i == 0) {
					endhq.setDate(hq.getDate());
					endhq.setEndprice(hq.getEndprice());
					endhq.setOpenprice(hq.getOpenprice());
					endhq.setGainrate(hq.getGainrate());
				}
				i++;
			}
			starthq = hq;
			System.out.println("开始时间点：" + starthq.getDate() + "  开盘价："
					+ starthq.getOpenprice() + "  ");
			System.out.println("结束时间点：" + endhq.getDate() + "  开盘价："
					+ endhq.getOpenprice() + "  ");
			System.out.println("开始时间点：" + starthq.getDate() + "  收盘价："
					+ starthq.getEndprice() + "  ");
			System.out.println("结束时间点：" + endhq.getDate() + "  收盘价："
					+ endhq.getEndprice() + "  ");
			System.out.println("回报率："
					+ (endhq.getEndprice() - starthq.getEndprice())*100
					/ starthq.getEndprice() + "%");

			Matcher m = periodPattern.matcher(content);
			StockGain gain = new StockGain();
			gain.setStartdate(starthq.getDate());
			gain.setEnddate(endhq.getDate());
			gain.setStartprice(starthq.getEndprice());
			gain.setEndprice(endhq.getEndprice());
			gain.setStockcode(stockcode);
			gain.setDealdays(i);
			if (m != null && m.find()) {
				System.out.println("Content:" + m.groupCount());
				System.out.println("涨跌价格：" + m.group(1));
				System.out.println("回报率：" + m.group(2));
				gain.setGain(Float.parseFloat(m.group(2).trim()));
				System.out.println("区间最低价格：" + m.group(3));
				gain.setLowest(Float.parseFloat(m.group(3).trim()));
				System.out.println("区间最高价格：" + m.group(4));
				gain.setHighest(Float.parseFloat(m.group(4).trim()));
				System.out.println("区间成交额：" + m.group(5));
				gain.setVolume(Float.parseFloat(m.group(5).trim()));
				System.out.println("区间换手率：" + m.group(6));
				gain.setChangerate(Float.parseFloat(m.group(6).trim()));
			}
			return gain;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StockPrice sp = new StockPrice();
		sp.getStockGainByPeriod("601939", "2010-07-30", "2010-09-11");
		// sp.fetchhq("601939", "2010-07-30");
	}

}
