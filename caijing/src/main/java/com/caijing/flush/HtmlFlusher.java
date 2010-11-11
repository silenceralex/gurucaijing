package com.caijing.flush;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.GroupEarnDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.dao.StockEarnDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.DiscountStock;
import com.caijing.model.StockPrice;
import com.caijing.util.ContextFactory;
import com.caijing.util.DateTools;
import com.caijing.util.Discount;

public class HtmlFlusher {
	public static String ADMINDIR = "/home/html/admin/static/";
	public static String TemplateDIR = "/home/html/admin/";

	public boolean flushDiscount() {
		try {
			Discount gg = new Discount();
			RecommendStockDao recommendStockDao = (RecommendStockDao) ContextFactory.getBean("recommendStockDao");
			StockPrice sp = (StockPrice) ContextFactory.getBean("stockPrice");
			gg.setRecommendStockDao(recommendStockDao);
			gg.setSp(sp);
			List<DiscountStock> discounts = gg.process();
			VMFactory vmf = new VMFactory();
			vmf.setTemplate("/admin/discount.htm");
			vmf.put("discountlist", discounts);
			vmf.save(ADMINDIR + "discount.html");
			return true;
		} catch (Exception e) {
			System.out.println("===> exception !!");
			System.out.println("While generating discount stock html --> GET ERROR MESSAGE: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public void flushStarGuruDetailIntro() {
		AnalyzerDao analyzerDao = (AnalyzerDao) ContextFactory.getBean("analyzerDao");
		List<Analyzer> analyzerList = analyzerDao.getAllAnalyzers();
		if (analyzerList != null && analyzerList.size() > 0) {
			VMFactory vmf = new VMFactory();
			for (Analyzer analyzer : analyzerList) {
				String aid = analyzer.getAid();
				GroupStockDao groupStockDao = (GroupStockDao) ContextFactory.getBean("groupStockDao");
				Date startDate = groupStockDao.getEarliestIntimeByAid(aid);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(startDate);
				Date today = DateTools.getToday();
				List<String> dateList = new ArrayList<String>();
				while (calendar.getTime().compareTo(today) <= 0) {
					dateList.add(DateTools.transformMMDDDate(calendar.getTime()));
					calendar.add(Calendar.DAY_OF_YEAR, 1);
				}

				GroupEarnDao groupEarnDao = (GroupEarnDao) ContextFactory.getBean("groupEarnDao");
				List<Float> weightList = groupEarnDao.getWeightList(aid, startDate);

				StockEarnDao stockEarnDao = (StockEarnDao) ContextFactory.getBean("groupEarnDao");
				float startprice = stockEarnDao.getStockEarnByCodeDate("000300",
						DateTools.transformYYYYMMDDDate(startDate)).getPrice();
				List<Float> priceList = stockEarnDao.getPriceByCodeDate("000300", DateTools
						.transformYYYYMMDDDate(startDate));

				vmf.setTemplate("/admin/starintro.htm");
				vmf.put("analyzer", analyzer);
				vmf.put("dateList", dateList);
				vmf.put("weightList", weightList);
				vmf.put("startprice", startprice);
				vmf.put("priceList", priceList);
				vmf.save(ADMINDIR + analyzer.getAid() + "_intro.html");
			}
		}
	}

	public static void main(String[] args) {
		HtmlFlusher flusher = new HtmlFlusher();
		flusher.flushDiscount();
	}
}
