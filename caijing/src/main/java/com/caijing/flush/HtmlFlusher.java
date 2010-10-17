package com.caijing.flush;

import java.util.List;

import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.DiscountStock;
import com.caijing.model.StockPrice;
import com.caijing.util.ContextFactory;
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

	public static void main(String[] args) {
		HtmlFlusher flusher = new HtmlFlusher();
		flusher.flushDiscount();
	}
}
