package com.caijing.web.controller;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.DiscountStock;
import com.caijing.domain.RecommendStock;
import com.caijing.model.StockPrice;
import com.caijing.util.Discount;
import com.caijing.util.Vutil;

@Controller
public class SearchController {

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	@Autowired
	@Qualifier("vutil")
	private Vutil vutil = null;

	@Autowired
	@Qualifier("stockPrice")
	private StockPrice sp = null;

	@Autowired
	@Qualifier("discount")
	private Discount discount = null;

	@RequestMapping("/admin/search.htm")
	public String searchRecommend(HttpServletResponse response, @RequestParam(value = "stockcode", required = true)
	String stockcode, HttpServletRequest request, ModelMap model) {
		Pattern stockcodePattern = Pattern.compile("(((002|000|300|600)[\\d]{3})|60[\\d]{4})", Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.UNIX_LINES);
		Matcher m = stockcodePattern.matcher(stockcode);

		List<RecommendStock> recommendlist = null;
		if (m != null && m.find()) {
			System.out.println("search by stockcode:" + stockcode);
			recommendlist = recommendStockDao.getRecommendStocksByStockcode(stockcode);
		} else {
			System.out.println("search by stockname:" + stockcode);
			recommendlist = recommendStockDao.getRecommendStocksByStockname(stockcode);
		}

		model.put("vutil", vutil);
		model.put("recommendlist", recommendlist);
		return "/admin/searchrecommend.htm";
	}

	@RequestMapping("/admin/discount.htm")
	public String discount(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		List<DiscountStock> discounts = discount.process();
		model.put("discountlist", discounts);
		return "/admin/discount.htm";
	}

}
