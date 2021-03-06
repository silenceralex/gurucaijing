package com.caijing.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.caijing.dao.StockEarnDao;
import com.caijing.domain.StockEarn;
import com.caijing.domain.StockGain;
import com.caijing.domain.StockHQ;
import com.caijing.util.DateTools;
import com.caijing.util.FloatUtil;
import com.caijing.util.UrlDownload;

public class StockPrice {

	private static String hqurl = "http://q.stock.sohu.com/app2/history.up?method=history&res=js&code=cn_";

	private static String zshqurl = "http://q.stock.sohu.com/app2/history.up?method=history&code=zs_000300&t=d&res=js";

	private static String hexunurl = "http://quote.stock.hexun.com/stockdata/stock_quote.aspx?stocklist=";

	private static String sinaurl = "http://hq.sinajs.cn/?_=";
	private UrlDownload down = new UrlDownload();

	@Autowired
	@Qualifier("stockEarnDao")
	private StockEarnDao stockEarnDao = null;
	private Pattern periodPattern = Pattern
			.compile(
					"\\['20[01][0-9]-[01][0-9]-[0-3][0-9]至20[01][0-9]-[01][0-9]-[0-3][0-9]','(-?[0-9\\.]+)','(-?[0-9\\.]+)%','([0-9\\.]+)','([0-9\\.]+)','[0-9\\.]+','([0-9\\.]+)','([0-9\\.]+)%'\\]\\]\\)",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);

	private Pattern currentPattern = Pattern
			.compile(
					"dataArr = \\[\\['[0-9]{6}','.*?',([0-9\\.]+),(-?[0-9\\.]+),([0-9\\.]+),([0-9\\.]+),([0-9\\.]+),([0-9\\.]+),([0-9\\.]+),[0-9\\.]+,([0-9\\.]+),[0-9\\.]+,[0-9\\.]+\\]\\]",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);

	//	public StockHQ getPriceBySina(String stockcode, String today) {
	//		String stockurl = "";
	//		if (stockcode.startsWith("6") || stockcode.startsWith("9")) {
	//			stockurl = sinaurl + System.currentTimeMillis() + "&list=sh" + stockcode;
	//		} else {
	//			stockurl = sinaurl + System.currentTimeMillis() + "&list=sz" + stockcode;
	//		}
	//		System.out.println("stockurl: " + stockurl);
	//		return null;
	//	}

	/**
	 * 判断是否是工作日，是否需要进行本地存储的更新
	 * @return
	 */
	public boolean isWorkDay() {
		Date date = new Date();
		String today = DateTools.transformYYYYMMDDDate(date);
		String stockurl = sinaurl + System.currentTimeMillis() + "&list=sh000300";
		try {
			String content = down.load(stockurl);
			System.out.println("content: " + content);
			content = content.substring(content.indexOf('"'), content.lastIndexOf('"'));
			System.out.println("content: " + content);
			String[] strs = content.split(",");
			System.out.println("strs length: " + strs.length);
			System.out.println("strs length: " + strs.length);
			System.out.println("strs[" + (strs.length - 2) + "]: " + strs[strs.length - 2].trim());
			System.out.println("today： " + today);
			if (today.endsWith(strs[strs.length - 2].trim())) {
				return true;
			} else {
				return false;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return true;
		}
	}

	public StockHQ currentPrice(String stockcode) {
		String stockurl = hexunurl + stockcode;
		System.out.println("stockurl: " + stockurl);
		StockHQ hq = new StockHQ();
		hq.setStockcode(stockcode);
		try {
			String content = down.load(stockurl);
			Matcher startm = currentPattern.matcher(content);
			if (startm != null && startm.find()) {
				System.out.println("Content:" + startm.groupCount());
				System.out.println(stockcode + "  开盘价：" + startm.group(4));
				hq.setOpenprice(Float.parseFloat(startm.group(4).trim()));
				System.out.println(stockcode + "  收盘价：" + startm.group(1));
				hq.setEndprice(Float.parseFloat(startm.group(1).trim()));
				System.out.println(stockcode + "  最低价：" + startm.group(6));
				hq.setLowest(Float.parseFloat(startm.group(6).trim()));
				System.out.println(stockcode + "  最高价：" + startm.group(5));
				hq.setHighest(Float.parseFloat(startm.group(5).trim()));
				System.out.println(stockcode + "  成交额：" + startm.group(7));
				hq.setVolum(Float.parseFloat(startm.group(7).trim()));
				System.out.println(stockcode + "  换手率：" + startm.group(8));
				hq.setChangerate(Float.parseFloat(startm.group(8).trim()));
				System.out.println(stockcode + "  涨跌率：" + startm.group(2));
				hq.setGainrate(Float.parseFloat(startm.group(2).trim()));

				StockEarn se = new StockEarn();
				se.setStockcode(stockcode);
				se.setDate(new Date());
				se.setPrice(Float.parseFloat(startm.group(1).trim()));
				se.setRatio(Float.parseFloat(startm.group(2).trim()));
				stockEarnDao.insert(se);
			}

			return hq;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public StockHQ fetchhq(String stockcode, String date) {
		StockHQ hq = new StockHQ();
		hq.setStockcode(stockcode);
		String stockurl = hqurl + stockcode + "&sd=" + date + "&ed=" + date + "&t=d";
		System.out.println("stockurl: " + stockurl);
		try {
			String content = down.load(stockurl);

			if (!content.contains("至")) {
				stockurl = hqurl + stockcode + "&sd=" + date + "&ed=" + date + "&t=r";
				content = down.load(stockurl);
			}
			Pattern startPattern = Pattern
					.compile(
							"\\['"
									+ date
									+ "','([0-9\\.]+)','([0-9\\.]+)','-?[0-9\\.]+','(-?[0-9\\.]+)%','([0-9\\.]+)','([0-9\\.]+)','[0-9\\.]+','([0-9\\.]+)','([0-9\\.]+)%'\\]",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
			Matcher startm = startPattern.matcher(content);
			if (startm != null && startm.find()) {
				//				System.out.println("Content:" + startm.groupCount());
				//				System.out.println(stockcode + "在日期：" + date + "  开盘价：" + startm.group(1));
				hq.setOpenprice(Float.parseFloat(startm.group(1).trim()));
				//				System.out.println(stockcode + "在日期：" + date + "  收盘价：" + startm.group(2));
				hq.setEndprice(Float.parseFloat(startm.group(2).trim()));
				//				System.out.println(stockcode + "在日期：" + date + "  最低价：" + startm.group(4));
				hq.setLowest(Float.parseFloat(startm.group(4).trim()));
				//				System.out.println(stockcode + "在日期：" + date + "  最高价：" + startm.group(5));
				hq.setHighest(Float.parseFloat(startm.group(5).trim()));
				//				System.out.println(stockcode + "在日期：" + date + "  成交额：" + startm.group(6));
				hq.setVolum(Float.parseFloat(startm.group(6).trim()));
				//				System.out.println(stockcode + "在日期：" + date + "  换手率：" + startm.group(7));
				hq.setChangerate(Float.parseFloat(startm.group(7).trim()));
				//				System.out.println(stockcode + "在日期：" + date + "  涨跌率：" + startm.group(3));
				hq.setGainrate(Float.parseFloat(startm.group(3).trim()));
			}
			return hq;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public StockGain getZSGainByPeriod(String startdate, String enddate) {
		String stockurl = zshqurl + "&sd=" + startdate + "&ed=" + enddate;
		System.out.println("stockurl: " + stockurl);
		try {
			String content = down.load(stockurl);
			Pattern startPattern = Pattern
					.compile(
							"\\['(20[01][0-9]-[01][0-9]-[0-3][0-9])','([0-9\\.]+)','([0-9\\.]+)','-?[0-9\\.]+','(-?[0-9\\.]+)%','([0-9\\.]+)','([0-9\\.]+)','[0-9\\.]+','([0-9\\.]+)','(-)'\\]",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
			Matcher startm = startPattern.matcher(content);
			int i = 0;
			StockHQ starthq = new StockHQ();
			StockHQ endhq = new StockHQ();
			StockHQ hq = new StockHQ();
			List<String> dates = new ArrayList<String>();
			List<Float> peroidprice = new ArrayList<Float>();
			List<Float> periodratio = new ArrayList<Float>();
			List<Float> periodearn = new ArrayList<Float>();
			float weight = 1;
			while (startm != null && startm.find()) {
				hq.setDate(startm.group(1).trim());
				hq.setOpenprice(Float.parseFloat(startm.group(2).trim()));
				hq.setEndprice(Float.parseFloat(startm.group(3).trim()));
				hq.setLowest(Float.parseFloat(startm.group(5).trim()));
				hq.setHighest(Float.parseFloat(startm.group(6).trim()));
				hq.setVolum(Float.parseFloat(startm.group(7).trim()));
				//				hq.setChangerate(Float.parseFloat(startm.group(8).trim()));
				hq.setGainrate(Float.parseFloat(startm.group(4).trim()));
				dates.add(startm.group(1).trim());
				peroidprice.add(Float.parseFloat(startm.group(3).trim()));
				float rtio = Float.parseFloat(startm.group(4).trim());
				periodratio.add(rtio);
				if (i == 0) {
					endhq.setDate(hq.getDate());
					endhq.setEndprice(hq.getEndprice());
					endhq.setOpenprice(hq.getOpenprice());
					endhq.setGainrate(hq.getGainrate());
				}
				i++;
			}
			starthq = hq;
			Pattern zsperiodPattern = Pattern
					.compile(
							"\\['20[01][0-9]-[01][0-9]-[0-3][0-9]至20[01][0-9]-[01][0-9]-[0-3][0-9]','(-?[0-9\\.]+)','(-?[0-9\\.]+)%','([0-9\\.]+)','([0-9\\.]+)','[0-9\\.]+','([0-9\\.]+)','(-)'\\]\\]\\)",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
			Matcher m = zsperiodPattern.matcher(content);
			StockGain gain = new StockGain();
			gain.setStartdate(starthq.getDate());
			gain.setEnddate(endhq.getDate());
			gain.setStartprice(starthq.getEndprice());
			gain.setEndprice(endhq.getEndprice());
			gain.setStockcode("000300");
			gain.setDealdays(i);

			//除去推荐日当天的行情
			dates.remove(dates.size() - 1);
			peroidprice.remove(peroidprice.size() - 1);
			periodratio.remove(periodratio.size() - 1);
			gain.setPerioddate(dates);
			gain.setPeriodprice(peroidprice);
			gain.setPeriodratio(periodratio);
			for (int j = periodratio.size() - 1; j >= 0; j--) {
				weight = weight * (1 + periodratio.get(j) / 100);
				periodearn.add(FloatUtil.getTwoDecimal((weight - 1) * 100));
			}
			gain.setPeriodearn(periodearn);
			System.out.println("回报率：" + (endhq.getEndprice() - starthq.getEndprice()) * 100 / starthq.getEndprice()
					+ "%");
			System.out.println("回报率2：" + (weight - 1) * 100 + "%");
			if (m != null && m.find()) {
				gain.setGain(FloatUtil.getTwoDecimal((weight - 1) * 100));
				// System.out.println("区间最低价格：" + m.group(3));
				gain.setLowest(Float.parseFloat(m.group(3).trim()));
				// System.out.println("区间最高价格：" + m.group(4));
				gain.setHighest(Float.parseFloat(m.group(4).trim()));
				// System.out.println("区间成交额：" + m.group(5));
				gain.setVolume(Float.parseFloat(m.group(5).trim()));
				// System.out.println("区间换手率：" + m.group(6));
				//				gain.setChangerate(Float.parseFloat(m.group(6).trim()));
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

	public void storeStockPrice(String stockcode, int type, String startdate, String enddate) {
		String stockurl = "";
		Pattern startPattern = null;
		if (type == 1) {
			stockurl = zshqurl + "&sd=" + startdate + "&ed=" + enddate;
			startPattern = Pattern
					.compile(
							"\\['(20[01][0-9]-[01][0-9]-[0-3][0-9])','([0-9\\.]+)','([0-9\\.]+)','-?[0-9\\.]+','(-?[0-9\\.]+)%','([0-9\\.]+)','([0-9\\.]+)','[0-9\\.]+','([0-9\\.]+)','(-)'\\]",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		} else {
			stockurl = hqurl + stockcode + "&sd=" + startdate + "&ed=" + enddate + "&t=d";
			startPattern = Pattern
					.compile(
							"\\['(20[01][0-9]-[01][0-9]-[0-3][0-9])','([0-9\\.]+)','([0-9\\.]+)','-?[0-9\\.]+','(-?[0-9\\.]+)%','([0-9\\.]+)','([0-9\\.]+)','[0-9\\.]+','([0-9\\.]+)','([0-9\\.]+)%'\\]",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
		}
		System.out.println("stockurl: " + stockurl);
		try {
			String content = down.load(stockurl);

			if (!content.contains("至")) {
				stockurl = hqurl + stockcode + "&sd=" + startdate + "&ed=" + enddate + "&t=r";
				content = down.load(stockurl);
			}

			Matcher startm = startPattern.matcher(content);
			while (startm != null && startm.find()) {
				StockEarn se = new StockEarn();
				se.setStockcode(stockcode);
				se.setDate(DateTools.parseYYYYMMDDDate(startm.group(1).trim()));
				se.setPrice(Float.parseFloat(startm.group(3).trim()));
				se.setRatio(Float.parseFloat(startm.group(4).trim()));
				stockEarnDao.insert(se);
			}
			System.out.println("end!!! " + stockurl);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// &code=cn_600016&sd=2010-07-30&ed=2010-07-30
	public StockGain getStockGainByPeriod(String stockcode, String startdate, String enddate) {
		String stockurl = hqurl + stockcode + "&sd=" + startdate + "&ed=" + enddate + "&t=d";
		System.out.println("stockurl: " + stockurl);
		try {
			String content = down.load(stockurl);

			if (!content.contains("至")) {
				stockurl = hqurl + stockcode + "&sd=" + startdate + "&ed=" + enddate + "&t=r";
				content = down.load(stockurl);
			}

			// System.out.println("Content:"+content);
			Pattern startPattern = Pattern
					.compile(
							"\\['(20[01][0-9]-[01][0-9]-[0-3][0-9])','([0-9\\.]+)','([0-9\\.]+)','-?[0-9\\.]+','(-?[0-9\\.]+)%','([0-9\\.]+)','([0-9\\.]+)','[0-9\\.]+','([0-9\\.]+)','([0-9\\.]+)%'\\]",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
			Matcher startm = startPattern.matcher(content);
			int i = 0;
			StockHQ starthq = new StockHQ();
			StockHQ endhq = new StockHQ();
			StockHQ hq = new StockHQ();
			List<String> dates = new ArrayList<String>();
			List<Float> peroidprice = new ArrayList<Float>();
			List<Float> periodratio = new ArrayList<Float>();
			List<Float> periodearn = new ArrayList<Float>();
			float weight = 1;
			while (startm != null && startm.find()) {
				hq.setDate(startm.group(1).trim());
				hq.setOpenprice(Float.parseFloat(startm.group(2).trim()));
				hq.setEndprice(Float.parseFloat(startm.group(3).trim()));
				hq.setLowest(Float.parseFloat(startm.group(5).trim()));
				hq.setHighest(Float.parseFloat(startm.group(6).trim()));
				hq.setVolum(Float.parseFloat(startm.group(7).trim()));
				hq.setChangerate(Float.parseFloat(startm.group(8).trim()));
				hq.setGainrate(Float.parseFloat(startm.group(4).trim()));
				dates.add(startm.group(1).trim());
				peroidprice.add(Float.parseFloat(startm.group(3).trim()));
				float rtio = Float.parseFloat(startm.group(4).trim());
				periodratio.add(rtio);
				if (i == 0) {
					endhq.setDate(hq.getDate());
					endhq.setEndprice(hq.getEndprice());
					endhq.setOpenprice(hq.getOpenprice());
					endhq.setGainrate(hq.getGainrate());
				}
				i++;
			}
			starthq = hq;
			Matcher m = periodPattern.matcher(content);
			StockGain gain = new StockGain();

			gain.setStartdate(starthq.getDate());
			gain.setEnddate(endhq.getDate());

			gain.setStartprice(starthq.getEndprice());
			gain.setEndprice(endhq.getEndprice());
			gain.setStockcode(stockcode);
			gain.setDealdays(i);

			gain.setGain(FloatUtil.getTwoDecimal((weight - 1) * 100));
			//除去推荐日当天的行情
			dates.remove(dates.size() - 1);
			peroidprice.remove(peroidprice.size() - 1);
			periodratio.remove(periodratio.size() - 1);
			gain.setPerioddate(dates);
			gain.setPeriodprice(peroidprice);
			gain.setPeriodratio(periodratio);
			for (int j = periodratio.size() - 1; j >= 0; j--) {
				weight = weight * (1 + periodratio.get(j) / 100);
				periodearn.add(FloatUtil.getTwoDecimal((weight - 1) * 100));
			}
			gain.setPeriodearn(periodearn);
			System.out.println("回报率：" + (endhq.getEndprice() - starthq.getEndprice()) * 100 / starthq.getEndprice()
					+ "%");
			System.out.println("回报率2：" + (weight - 1) * 100 + "%");
			if (m != null && m.find()) {
				// gain.setGain(Float.parseFloat(m.group(2).trim()));
				// System.out.println("区间最低价格：" + m.group(3));
				gain.setGain(FloatUtil.getTwoDecimal((weight - 1) * 100));
				gain.setLowest(Float.parseFloat(m.group(3).trim()));
				// System.out.println("区间最高价格：" + m.group(4));
				gain.setHighest(Float.parseFloat(m.group(4).trim()));
				// System.out.println("区间成交额：" + m.group(5));
				gain.setVolume(Float.parseFloat(m.group(5).trim()));
				// System.out.println("区间换手率：" + m.group(6));
				gain.setChangerate(Float.parseFloat(m.group(6).trim()));
			}
			return gain;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StockPrice sp = new StockPrice();
		//		StockEarnDao stockEarnDao = (StockEarnDaoImpl) ContextFactory.getBean("stockEarnDao");
		//		sp.setStockEarnDao(stockEarnDao);
		//		sp.getStockGainByPeriod("002477", "2010-08-27", "2010-10-21");
		//		StockHQ hq = sp.fetchhq("000001", "2010-10-21");
		//		StockHQ hq = sp.currentPrice("000001");
		//		System.out.println("收盘价：" + hq.getEndprice());
		//		System.out.println("收益率：" + hq.getGainrate());
		//		StockEarnDao stockEarnDao = (StockEarnDao) ContextFactory.getBean("stockEarnDao");
		//		sp.setStockEarnDao(stockEarnDao);
		//		sp.storeStockPrice("000300", 1, "2010-10-27", "2010-10-27");
		if (sp.isWorkDay()) {
			System.out.println("Is working Day!");
		} else {
			System.out.println("Tody Is NOT working Day!");
		}
	}

	public StockEarnDao getStockEarnDao() {
		return stockEarnDao;
	}

	public void setStockEarnDao(StockEarnDao stockEarnDao) {
		this.stockEarnDao = stockEarnDao;
	}

}
