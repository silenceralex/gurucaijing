package com.caijing.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.caijing.dao.AnalyzerDao;
import com.caijing.dao.GroupStockDao;
import com.caijing.dao.RecommendStockDao;
import com.caijing.domain.Analyzer;
import com.caijing.domain.DiscountStock;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;
import com.caijing.model.StockPrice;
import com.caijing.util.DateTools;
import com.caijing.util.Discount;
import com.caijing.util.FloatUtil;
import com.caijing.util.Paginator;
import com.caijing.util.Vutil;

@Controller
public class SearchController {
	private Log logger = LogFactory.getLog(SearchController.class);

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	@Autowired
	@Qualifier("groupStockDao")
	private GroupStockDao groupStockDao = null;

	@Autowired
	@Qualifier("vutil")
	private Vutil vutil = null;

	@Autowired
	@Qualifier("stockPrice")
	private StockPrice sp = null;

	@Autowired
	@Qualifier("discount")
	private Discount discount = null;

	@Autowired
	@Qualifier("analyzerDao")
	private AnalyzerDao analyzerDao = null;

	@RequestMapping("/admin/search.htm")
	public String searchRecommend(HttpServletResponse response,
			@RequestParam(value = "stockcode", required = true) String stockcode, HttpServletRequest request,
			ModelMap model) {
		Pattern stockcodePattern = Pattern.compile("(((002|000|300|600)[\\d]{3})|60[\\d]{4})", Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.UNIX_LINES);
		Matcher m = stockcodePattern.matcher(stockcode);

		List<RecommendStock> recommendlist = null;
		if (m != null && m.find()) {
			logger.debug("search by stockcode:" + stockcode);
			recommendlist = recommendStockDao.getRecommendStocksByStockcode(stockcode, 0, 20);
		} else {
			try {
				stockcode = URLDecoder.decode(stockcode, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("关键词utf-8解码失败：" + e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			logger.debug("search by stockname:" + stockcode);
			recommendlist = recommendStockDao.getRecommendStocksByStockname(stockcode, 0, 20);
		}
		logger.debug("size of recommendlist:" + recommendlist.size());
		model.put("vutil", vutil);
		model.put("recommendlist", recommendlist);
		return "/admin/searchrecommend.htm";
	}

	@RequestMapping("/admin/searchreport.htm")
	public String searchReport(HttpServletResponse response, @RequestParam(value = "q", required = true) String query,
			@RequestParam(value = "type", required = true) int type,
			@RequestParam(value = "page", required = false) Integer page, HttpServletRequest request, ModelMap model) {

		Paginator<Report> paginator = new Paginator<Report>();
		paginator.setPageSize(20);
		int total = 0;
		// 分页显示时，标识当前第几页
		if (page == null || page < 1) {
			page = 1;
		}
		paginator.setCurrentPageNumber(page);
		String urlPattern = "";
		List<RecommendStock> recommendlist = new ArrayList<RecommendStock>();
		if (type == 1) {
			String saname = query;
			System.out.println("saname:" + saname);
			total = recommendStockDao.getAllRecommendCountBySaname(saname);
			paginator.setTotalRecordNumber(total);
			recommendlist = recommendStockDao.getRecommendStocksBySaname(saname, (page - 1) * 20, page * 20);
			urlPattern = "/admin/searchreport.htm?q=" + saname + "&type=1&page=$number$";
		} else if (type == 2) {
			String aname = query;
			System.out.println("aname:" + aname);
			total = recommendStockDao.getRecommendStockCountsByAnalyzer(aname);
			paginator.setTotalRecordNumber(total);
			recommendlist = recommendStockDao.getRecommendStocksByAnalyzer(aname, (page - 1) * 20, page * 20);
			urlPattern = "/admin/searchreport.htm?q=" + aname + "&type=2&page=$number$";
		} else if (type == 0) {
			String stockcode = query;
			Pattern stockcodePattern = Pattern.compile("(((002|000|300|600)[\\d]{3})|60[\\d]{4})",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
			Matcher m = stockcodePattern.matcher(stockcode);
			if (m != null && m.find()) {
				System.out.println("search by stockcode:" + stockcode);
				recommendlist = recommendStockDao.getRecommendStocksByStockcode(stockcode, (page - 1) * 20, page * 20);
			} else {
				System.out.println("search by stockname:" + stockcode);
				recommendlist = recommendStockDao.getRecommendStocksByStockname(stockcode, (page - 1) * 20, page * 20);
			}
		}
		paginator.setUrl(urlPattern);
		model.put("vutil", vutil);
		model.put("recommendlist", recommendlist);
		model.put("paginatorLink", paginator.getPageNumberList());
		return "/admin/searchreport.htm";

	}

	@RequestMapping("/searchstock.htm")
	public String searchStock(HttpServletResponse response, HttpServletRequest request,
			@RequestParam(value = "stockcode", required = true) String stockcode,
			@RequestParam(value = "type", required = true) int type,
			@RequestParam(value = "page", required = false) Integer page, ModelMap model) {
		Paginator<RecommendStock> paginator = new Paginator<RecommendStock>();
		paginator.setPageSize(20);
		int total = 0;
		// 分页显示时，标识当前第几页
		if (page == null || page < 1) {
			page = 1;
		}
		total = recommendStockDao.getRecommendCountsByStockcodeAndStatus(stockcode, type);
		List<RecommendStock> recommendstocks = recommendStockDao.getRecommendStockByStatus(stockcode, type,
				(page - 1) * 20, 20);
		logger.debug("recommendstocks size :" + recommendstocks.size());
		String urlPattern = "/searchstock.htm?type=" + type + "&stockcode=" + stockcode + "&page=$number$";
		System.out.println("search stockcode:" + stockcode + "  type:" + type + "  page:" + page);
		paginator.setUrl(urlPattern);
		model.put("recommendstocks", recommendstocks);
		model.put("paginatorLink", paginator.getPageNumberList());
		model.put("dateTools", new DateTools());
		return "/search/stocksearch.htm";
	}

	//	@RequestMapping("/admin/flushdiscount.htm")
	//	public void flushdiscount(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
	//		try {
	//			List<DiscountStock> discounts = discount.process();
	//			velocityEngine.init();
	//			Template tpl = velocityEngine.getTemplate("/admin/discount.htm", "GBK");
	//			VelocityContext context = new VelocityContext();
	//			context.put("discountlist", discounts);
	//			StringWriter out = new StringWriter();
	//			tpl.merge(context, out);
	//			FileUtil.write("D:\\discount.html", out.toString(), "GB2312");
	//
	//		} catch (Exception e) {
	//			System.out.println("===> exception !!");
	//			System.out.println("While generating discount stock html --> GET ERROR MESSAGE: " + e.getMessage());
	//			e.printStackTrace();
	//		}
	//	}

	@RequestMapping("/admin/discount.htm")
	public String discount(HttpServletResponse response, HttpServletRequest request, ModelMap model) {
		Discount discount = new Discount();
		discount.setRecommendStockDao(recommendStockDao);
		discount.setGroupStockDao(groupStockDao);
		List<DiscountStock> discounts = discount.getDiscountStocks();
		model.put("discountlist", discounts);
		return "/admin/discount.htm";
	}

	@RequestMapping("/search/report.htm")
	public String searchReport(@RequestParam(value = "stockcode", required = true) String stockcode,
			@RequestParam(value = "aid", required = true) String aid, HttpServletResponse response,
			HttpServletRequest request, ModelMap model) {
		System.out.println("search stockcode:" + stockcode + "  aid:" + aid);
		List<Analyzer> analyzerList = analyzerDao.getStarAnalyzers();
		Analyzer analyzer = (Analyzer) analyzerDao.select(aid);
		List<RecommendStock> stockList = recommendStockDao.getStocksByAidAndStock(aid, stockcode, 10);
		model.put("floatUtil", new FloatUtil());
		model.put("dateTools", new DateTools());
		model.put("analyzer", analyzer);
		model.put("analyzerList", analyzerList);
		model.put("stockList", stockList);
		return "/template/starreport.htm";

	}
}
