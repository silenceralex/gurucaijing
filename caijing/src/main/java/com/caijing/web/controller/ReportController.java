package com.caijing.web.controller;

import java.util.ArrayList;
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
import com.caijing.dao.ReportDao;
import com.caijing.domain.RecommendStock;
import com.caijing.domain.Report;
import com.caijing.util.Config;
import com.caijing.util.DateTools;
import com.caijing.util.Paginator;
import com.caijing.util.TopicNameConfig;
import com.caijing.util.Vutil;

@Controller
public class ReportController {
	@Autowired
	@Qualifier("reportDao")
	private ReportDao reportDao = null;

	@Autowired
	@Qualifier("TopicNameConfig")
	private TopicNameConfig topicNameMap = null;

	@Autowired
	@Qualifier("config")
	private Config config = null;

	@Autowired
	@Qualifier("vutil")
	private Vutil vutil = null;

	@Autowired
	@Qualifier("recommendStockDao")
	private RecommendStockDao recommendStockDao = null;

	@RequestMapping("/admin/showColumn.htm")
	public String showColomn(HttpServletResponse response,
			@RequestParam(value = "saname", required = false) String saname,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "type", required = false) Integer type, HttpServletRequest request, ModelMap model) {
		Paginator<Report> paginator = new Paginator<Report>();
		paginator.setPageSize(20);

		int total = 0;
		// 分页显示时，标识当前第几页
		if (page == null || page < 1) {
			page = 1;
		}
		paginator.setCurrentPageNumber(page);
		String urlPattern = "";
		System.out.println("saname:" + saname);
		List<Report> reportlist = new ArrayList();
		if (saname != null) {
			if (type == null) {
				total = reportDao.getAllReportsCountBySaname(saname);
				paginator.setTotalRecordNumber(total);
				reportlist = reportDao.getReportsBySaname(saname, (page - 1) * 20, 20);
				urlPattern = "/admin/showColumn.htm?saname=" + saname + "&page=$number$";
				model.put("saname", saname);
			} else {
				System.out.println("type: " + type);
				total = reportDao.getReportsCountBySanameType(saname, type);
				paginator.setTotalRecordNumber(total);
				reportlist = reportDao.getReportsBySanameType(saname, type, (page - 1) * 20, 20);
				urlPattern = "/admin/showColumn.htm?saname=" + saname + "&type=" + type + "&page=$number$";
				model.put("saname", saname);
			}
		} else {
			total = reportDao.getAllReportsCount();
			paginator.setTotalRecordNumber(total);
			reportlist = reportDao.getAllReports((page - 1) * 20, 20);
			urlPattern = "/admin/showColumn.htm?page=$number$";
		}

		paginator.setUrl(urlPattern);
		model.put("topicNameMap", topicNameMap);
		model.put("vutil", vutil);
		model.put("reportlist", reportlist);
		model.put("paginatorLink", paginator.getPageNumberList());

		return "/admin/reportlist.htm";

	}

	@RequestMapping("/report/searchreport.htm")
	public String searchReport(HttpServletResponse response,
			@RequestParam(value = "query", required = true) String query,
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

		String urlPattern = "/search/reportlab.htm?page=$number$&type=" + type + "&query=" + query;
		List<RecommendStock> recommendlist = new ArrayList<RecommendStock>();
		if (type == 3) {
			String saname = query;
			System.out.println("saname:" + saname);
			total = recommendStockDao.getAllRecommendCountBySaname(saname);
			paginator.setTotalRecordNumber(total);
			recommendlist = recommendStockDao.getRecommendStocksBySaname(saname, (page - 1) * 20, page * 20);
		} else if (type == 2) {
			String aname = query;
			System.out.println("aname:" + aname);
			total = recommendStockDao.getRecommendStockCountsByAnalyzer(aname);
			paginator.setTotalRecordNumber(total);
			recommendlist = recommendStockDao.getRecommendStocksByAnalyzer(aname, (page - 1) * 20, page * 20);
		} else if (type == 0) {
			String stockcode = query;
			Pattern stockcodePattern = Pattern.compile("(((002|000|300|600)[\\d]{3})|60[\\d]{4})",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNIX_LINES);
			Matcher m = stockcodePattern.matcher(stockcode);
			if (m != null && m.find()) {
				System.out.println("search by stockcode:" + stockcode);
				recommendlist = recommendStockDao.getRecommendStocksByStockcode(stockcode, (page - 1) * 20, page * 20);
				total = recommendStockDao.getRecommendStocksCountByStockcode(stockcode);
				paginator.setTotalRecordNumber(total);
			}
		} else if (type == 1) {
			System.out.println("search by stockname:" + query);
			recommendlist = recommendStockDao.getRecommendStocksByStockname(query, (page - 1) * 20, page * 20);
			total = recommendStockDao.getRecommendStocksCountByStockname(query);
			paginator.setTotalRecordNumber(total);
		}
		paginator.setUrl(urlPattern);
		model.put("query", query);
		model.put("type", type);
		System.out.println("type:" + type + "  page:" + page + "  total:" + total);
		model.put("vutil", vutil);
		model.put("reportList", recommendlist);
		model.put("dateTools", new DateTools());
		System.out.println("recommendlist size:" + recommendlist.size());
		model.put("paginatorLink", paginator.getPageNumberList());
		return "/search/reportlab.htm";

	}

}
